package com.comaprim
package rs

import unfiltered.request._
import unfiltered.response._
import net.liftweb.common.LazyLoggable

class ControlHandler extends unfiltered.filter.Plan with LazyLoggable{
  import Assembly._

  def intent = {
    case req@GET(Path(Seg("controls" :: Nil))) => {
      logger.info("@ get /controls")
      req match {
        case Accepts.Json(_)  => Ok ~> Json(controlService.getControls)
        case Accepts.Xml(_)   => Ok ~> Xml(controlService.getControls)
        case _ => UnsupportedMediaType
      }
    }
    case POST(Path(Seg("controls" :: Nil))) => ResponseString("post /controls") ~> Ok
    case DELETE(Path(Seg("controls" :: id :: Nil))) => {
      logger.info("@ delete /controls/"+id)
      controlService.removeControl(id)
      NoContent
    }
  }
}