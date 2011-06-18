package com.comaprim
package rs

import unfiltered.request._
import unfiltered.response._
import net.liftweb.common.LazyLoggable
import model.{Variety}

class VarietyHandler extends unfiltered.filter.Plan with LazyLoggable{
  import QParams._
  import Assembly._

  def intent = {
    case req@GET(Path(Seg("varieties" :: id :: Nil))) =>
      logger.info("@ get /varieties/"+id)
      req match {
        case Accepts.Json(_)  => Ok ~> Json(varietyService.getVarieties)
        case Accepts.Xml(_)   => Ok ~> Xml(varietyService.getVarieties)
        case _ => UnsupportedMediaType
      }
    case req@PUT(Path(Seg("varieties" :: id :: Nil))) =>
      varietyService.findVariety(id) match {
        case None => NotFound ~> ResponseString("variety with id=(%s) not found" format(id))
        case Some(variety:Variety) =>
          logger.info("casting from ivariety to variety works as expected")
          req match {
            case JsonRead(json) =>
              logger.info("@ json put /varieties/" + id)
              val expected = for{
                name <- lookup[String]("name") is optional
                description <- lookup("description")  is optional
              } yield {
                try {
                  logger.info("modifying variety:{cultureId="+variety.cultureId+",name="+name.get+",description="+description.get)

                  name.get foreach variety.name.set
                  description foreach variety.description.set

                  req match {
                    case Accepts.Json(_)  => Ok ~> Json(varietyService.changeVariety(variety))
                    case Accepts.Xml(_)   => Ok ~> Xml(varietyService.changeVariety(variety))
                    case _ => UnsupportedMediaType
                  }
                }
                catch {
                  case ValidationException(errors) =>
                    logger.info("Got errors:"+errors.map(e=>"{%s,%s}" format(e.field.uniqueFieldId.toString,e.msg.text)))
                    req match {
                      case Accepts.Json(_)  => UnprocessableEntity ~> Json(errors)
                      case Accepts.Xml(_)   => UnprocessableEntity ~> Xml(errors)
                      case _ => UnsupportedMediaType
                    }
                }
              }
              expected(jsonToMap(json)) orFail { errors =>
                logger.info("Got errors:"+errors.map(e=>"{%s,%s}" format(e.name,e.error)))
                req match {
                  case Accepts.Json(_)  => UnprocessableEntity ~> Json(errors)
                  case Accepts.Xml(_)   => UnprocessableEntity ~> Xml(errors)
                  case _ => UnsupportedMediaType
                }
              }
            case XmlRead(xml) => NotImplemented ~> ResponseString("@ xml put /varieties/"+id)
            case _ => UnsupportedMediaType
          }
      }
    case DELETE(Path(Seg("varieties" :: id :: Nil))) =>
      logger.info("delete /varieties/"+id)
      try {
        varietyService.removeVariety(id)
        NoContent
      }
      catch {
        case _ => InternalServerError ~> ResponseString("there was an error while deleting the entity")
      }
  }
}