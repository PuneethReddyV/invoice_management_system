package com.tellius.invoicemanagement.http.directives

import akka.http.scaladsl.model.{HttpRequest, StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RouteResult.{Complete, Rejected}
import akka.http.scaladsl.server.{Directive0, RouteResult}
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import com.tellius.invoicemanagement.exception._
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

trait LogRequestTimesDirective extends StrictLogging {

  def logRequestTimes(
    currentTimeMillis: () => Long = () => System.currentTimeMillis()
  )(implicit executionContext: ExecutionContext): Directive0 =
    aroundRequest(timeRequest(_, currentTimeMillis))

  private def aroundRequest(
    onRequest: HttpRequest => Try[RouteResult] => Unit
  )(implicit ec: ExecutionContext): Directive0 = {
    extractRequestContext.flatMap { ctx =>
      {
        val onDone = onRequest(ctx.request)
        mapInnerRoute { inner =>
          inner.andThen { resultFuture =>
            resultFuture
              .map {
                case c @ Complete(response) =>
                  Complete(response.mapEntity { entity =>
                    if (entity.isKnownEmpty()) {
                      onDone(Success(c))
                      entity
                    } else {
                      entity.transformDataBytes(
                        Flow[ByteString].watchTermination() {
                          case (m, f) =>
                            f.map(_ => c).onComplete(onDone)
                            m
                        }
                      )
                    }
                  })
                case other =>
                  onDone(Success(other))
                  other
              }
              .andThen {
                case Failure(ex) =>
                  onDone(Failure(ex))
              }
          }
        }
      }
    }
  }

  private def timeRequest(
    ctx: HttpRequest,
    currentTimeMillis: () => Long
  ): Try[RouteResult] => Unit = {
    val start: Long = currentTimeMillis.apply()

    def logDuration(statusCode: StatusCode): Unit = {
      val d = currentTimeMillis.apply() - start
      logger.info(
        s"Completed: ${d}ms - [${ctx.method.name}:${statusCode}] - Path: ${ctx.uri.path}"
      )
    }

    {
      case Success(Complete(resp)) =>
        logDuration(resp.status)
      case Failure(_: IMResourceNotFoundException | _: CreateFailedException) =>
        logDuration(StatusCodes.NotFound)
      case Failure(_: MalformedEntityException | _: IMValidationException) =>
        logDuration(StatusCodes.UnprocessableEntity)
      case Failure(_: ResourceAlreadyExistsException) =>
        logDuration(StatusCodes.Conflict)
      case Failure(_: RuntimeException | _: Exception) =>
        logDuration(StatusCodes.InternalServerError)
      case Success(Rejected(_)) =>
        logDuration(StatusCodes.UnprocessableEntity)
      case _ =>
    }
  }
}
