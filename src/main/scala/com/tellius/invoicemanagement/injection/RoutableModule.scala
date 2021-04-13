package com.tellius.invoicemanagement.injection

import com.tellius.invoicemanagement.http.controller.{
  BusinessController,
  BusinessInvoiceController,
  CustomerController,
  CustomerInvoiceController,
  InvoiceController,
  ItemController,
  NotificationsController
}
import com.tellius.invoicemanagement.services._
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

class RoutableModule extends ScalaModule {

  override def configure(): Unit = {
    val routableMultiBinder = ScalaMultibinder.newSetBinder[Routable](binder)

    routableMultiBinder.addBinding.to[BusinessController]
    bind[BusinessService].to[BusinessServiceImpl]

    routableMultiBinder.addBinding.to[CustomerController]
    bind[CustomerService].to[CustomerServiceImpl]

    routableMultiBinder.addBinding.to[ItemController]
    bind[ItemService].to[ItemServiceImpl]

    routableMultiBinder.addBinding.to[InvoiceController]
    bind[InvoiceService].to[InvoiceServiceImpl]

    routableMultiBinder.addBinding.to[NotificationsController]
    bind[NotificationsService].to[NotificationsServiceImpl]

    routableMultiBinder.addBinding.to[BusinessInvoiceController]
    routableMultiBinder.addBinding.to[CustomerInvoiceController]
  }
}
