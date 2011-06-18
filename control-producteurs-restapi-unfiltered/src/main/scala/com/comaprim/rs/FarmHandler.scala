package com.comaprim
package rs

import unfiltered.request._
import unfiltered.response._
import unfiltered.filter.Plan
import net.liftweb.common.LazyLoggable
import model.{Farm}

class FarmHandler extends Plan with LazyLoggable {
  import QParams._
  import Assembly._

  def intent =  route1 orElse route2  orElse route3

  def route1:Plan.Intent = {
    case req@GET(Path(Seg("farms" :: Nil))) =>
      logger.info("@ get /farms")
      req match {
        case Accepts.Json(_)  => Ok ~> Json(farmService.getFarms)
        case Accepts.Xml(_)   => Ok ~> Xml(farmService.getFarms)
        case _ => UnsupportedMediaType
      }
    case req@GET(Path(Seg("farms" :: id :: Nil))) =>
      logger.info("@ get /farms/" + id)
      farmService.findFarm(id) match {
        case Some(farm) =>
          req match {
            case Accepts.Json(_)  => Ok ~> Json(farm)
            case Accepts.Xml(_)   => Ok ~> Xml(farm)
            case _ => UnsupportedMediaType
          }
        case _ => ResponseString("farm:"+id+" not found") ~> NotFound
      }
  }

  def route2:Plan.Intent = {
    case req@GET(Path(Seg("farms" :: id :: "sectors" :: Nil))) =>
      logger.info("@ get /farms/" + id + "/sectors")
      req match {
        case Accepts.Json(_)  => Ok ~> Json(sectorService.findSectorsByFarm(id))
        case Accepts.Xml(_)   => Ok ~> Xml(sectorService.findSectorsByFarm(id))
        case _ => UnsupportedMediaType
      }
    case req@POST(Path(Seg("farms" :: id :: "sectors" :: Nil))) =>
      farmService.findFarm(id) match {
        case None => NotFound ~> ResponseString("farm with id=(%s) not found" format(id))
        case Some(farm) =>

          def bigDecimal[E](e: String => E) = watch[E,String,BigDecimal]((os:Option[String])=>try { os map { BigDecimal(_) } } catch { case _ => None }, e)

          req match {
            case JsonRead(json) =>
              logger.info("post /farms/" + id + "/sectors")
              val expected = for{
                name <- lookup("name") is required(requiredMsg("name")) is trimmed is nonempty(requiredMsg("name"))
                area <- lookup("area") is required(requiredMsg("area")) is bigDecimal(_ + " is decimal")
                description <- lookup("description") is optional
              } yield {
                try {
                  logger.info("Adding sector:{farmId="+id+",name="+name.get+",description="+description.get)

                  lazy val sector = sectorService.addSector(farm, name.get, area.get, description.get)
                  req match {
                    case Accepts.Json(_)  => Ok ~> Json(sector)
                    case Accepts.Xml(_)   => Ok ~> Xml(sector)
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
            case XmlRead(xml) => NotImplemented ~> ResponseString("xml post /farms/" + id + "/sectors")
            case _ => UnsupportedMediaType
          }
      }
    case req@PUT(Path(Seg("farms" :: id :: Nil))) =>
      farmService.findFarm(id) match {
        case None => NotFound ~> ResponseString("farm with id=(%s) not found" format(id))
        case Some(farm:Farm) =>
          logger.info("casting from ifarm to farm works as expected")
          req match {
            case JsonRead(json) =>
              logger.info("@ put /farms/" + id)
              val expected = for{
                reference <- lookup[String]("reference") is optional
                description <- lookup("description")  is optional
              } yield {
                try {
                  logger.info("modifying farm:{producerId="+farm.producerId+",reference="+reference.get+",description="+description.get)

                  reference.get foreach farm.reference.set
                  description foreach farm.description.set

                  req match {
                    case Accepts.Json(_)  => Ok ~> Json(farmService.changeFarm(farm))
                    case Accepts.Xml(_)   => Ok ~> Xml(farmService.changeFarm(farm))
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
            case XmlRead(xml) => NotImplemented ~> ResponseString("xml put /producers/"+id)
            case _ => UnsupportedMediaType
          }
      }
  }

  def route3:Plan.Intent = {
    case DELETE(Path(Seg("farms" :: id :: Nil))) =>
      logger.info("@ delete /farms/" + id)
      try {
        farmService.removeFarm(id)
        NoContent
      }
      catch {
        case _ => InternalServerError ~> ResponseString("there was an unknown error while deleting the entity")
      }
  }
}