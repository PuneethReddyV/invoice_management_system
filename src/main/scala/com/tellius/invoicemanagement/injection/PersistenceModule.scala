package com.tellius.invoicemanagement.injection

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.tellius.invoicemanagement.persistance.DatabaseWrapper
import com.tellius.invoicemanagement.persistance.repository._
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext

class PersistenceModule()(implicit
                          materializer: Materializer,
                          actorSytem: ActorSystem,
                          executor: ExecutionContext,
                          databaseWrapper: DatabaseWrapper)
    extends ScalaModule {

  override def configure(): Unit = {
    bind[DatabaseWrapper].toInstance(databaseWrapper)

    bind[BusinessRepository].toInstance(
      new BusinessRepositoryImpl(databaseWrapper.database)
    )

    val businessToCustomerRepo = new BusinessToCustomerRepositoryImpl(
      databaseWrapper.database
    )
    bind[BusinessToCustomerRepository].toInstance(businessToCustomerRepo)

    bind[CustomerRepository].toInstance(
      new CustomerRepositoryImpl(databaseWrapper.database)
    )

    bind[InvoiceRepository].toInstance(
      new InvoiceRepositoryImpl(
        databaseWrapper.database,
        businessToCustomerRepo
      )
    )

    bind[ItemsRepository].toInstance(
      new ItemsRepositoryImpl(databaseWrapper.database)
    )

    bind[ItemsToInvoiceRepository].toInstance(
      new ItemsToInvoiceRepositoryImpl(databaseWrapper.database)
    )

  }
}
