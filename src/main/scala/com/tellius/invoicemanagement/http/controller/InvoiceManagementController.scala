package com.tellius.invoicemanagement.http.controller

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.tellius.invoicemanagement.http.directives.LogRequestTimesDirective
import com.tellius.invoicemanagement.utils.RouteFactory
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration}

class InvoiceManagementController()(
  implicit ec: ExecutionContext,
  timeout: FiniteDuration = FiniteDuration.apply(10, TimeUnit.SECONDS),
  requestValidationTimeout: Duration
) extends StrictLogging
    with LogRequestTimesDirective {

  def getRoutes(routeFactory: RouteFactory): Route =
    getHealthCheckRoute ~
      logRequestTimes().apply {
        routeFactory.aggregateRoutes
      }

  def getHealthCheckRoute: Route =
    pathPrefix("app_status") {
      complete(
        HttpEntity(ContentTypes.`application/json`, "{\"sucesss\": \"Yes\"}")
      )
    }
}
