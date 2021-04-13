package com.tellius.invoicemanagement.app

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}
import akka.stream.{Materializer, SystemMaterializer}
import com.google.inject.{Guice, Injector}
import com.tellius.invoicemanagement.common.InvoiceMngtConfig
import com.tellius.invoicemanagement.error.IMRejectionHandler
import com.tellius.invoicemanagement.exception.IMExceptionHandler
import com.tellius.invoicemanagement.http.controller.InvoiceManagementController
import com.tellius.invoicemanagement.injection.{
  PersistenceModule,
  RoutableModule
}
import com.tellius.invoicemanagement.persistance.DatabaseWrapper
import com.tellius.invoicemanagement.utils.RouteFactory
import com.typesafe.scalalogging.StrictLogging
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

abstract class InvoiceManagementServer extends StrictLogging {
  implicit val system: ActorSystem = ActorSystem("InvoiceManagement")
  implicit val materializer: Materializer =
    SystemMaterializer.get(system).materializer
  implicit val ec: ExecutionContext = system.dispatcher

  implicit val rejectionHandler: RejectionHandler =
    IMRejectionHandler.rejectionHandler
  implicit val exceptionHandler: ExceptionHandler =
    IMExceptionHandler.exceptionHandler

  implicit val databaseWrapper: DatabaseWrapper = new DatabaseWrapper()

  private val injector: Injector =
    Guice.createInjector(new PersistenceModule(), new RoutableModule())

  private val routeFactory: RouteFactory = injector.instance[RouteFactory]

  private val invoiceMGTConfig: InvoiceMngtConfig = new InvoiceMngtConfig()

  implicit val requestValidationTimeout: Duration =
    Duration.apply(10, TimeUnit.SECONDS)

  private val invoiceManagementController = new InvoiceManagementController()

  private val bindingFuture: Future[ServerBinding] =
    Http().bindAndHandle(
      invoiceManagementController.getRoutes(routeFactory),
      invoiceMGTConfig.Http.interface.get,
      invoiceMGTConfig.Http.port.get
    )

  sys.addShutdownHook({
    import scala.concurrent.duration._
    Await.result(bindingFuture.map(_.unbind()), 10.seconds)
  })

  logger.info(s"Starting HTTP server on port ${invoiceMGTConfig.Http.port.get}")
}

object Main extends InvoiceManagementServer with App {}
