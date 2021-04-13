package com.tellius.invoicemanagement.utils

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.google.inject.Singleton
import com.tellius.invoicemanagement.injection.Routable
import javax.inject.Inject

@Singleton
class RouteFactory @Inject()(routableServices: Set[Routable]) {

  def aggregateRoutes: Route = {
    routableServices.map(_.getRoutes).reduce(_ ~ _)
  }
}
