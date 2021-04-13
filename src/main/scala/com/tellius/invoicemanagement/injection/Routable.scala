package com.tellius.invoicemanagement.injection

import akka.http.scaladsl.server.Route

trait Routable {
  def getRoutes: Route
}
