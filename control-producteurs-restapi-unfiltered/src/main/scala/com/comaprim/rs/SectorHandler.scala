package com.comaprim
package rs

import unfiltered.request._
import unfiltered.response._
import net.liftweb.common.LazyLoggable
import model.Sector

class SectorHandler extends unfiltered.filter.Plan with LazyLoggable{
  import QParams._
  import Assembly._

  def intent = {
    case req@GET(Path(Seg("sectors" :: id :: Nil))) =>
      logger.info("get /sectors/"+id)
      req match {
        case Accepts.Json(_)  => Ok ~> Json(sectorService.getSectors)
        case Accepts.Xml(_)   => Ok ~> Xml(sectorService.getSectors)
        case _ => UnsupportedMediaType
      }
    case req@PUT(Path(Seg("sectors" :: id :: Nil))) =>
      sectorService.findSector(id) match {
        case None => NotFound ~> ResponseString("sector with id=(%s) not found" format(id))
        case Some(sector:Sector) =>
          logger.info("casting from isector to sector works as expected")
          req match {
            case JsonRead(json) =>
              logger.info("@ json put /sectors/" + id)
              val expected = for{
                name <- lookup[String]("name") is optional
                description <- lookup("description")  is optional
              } yield {
                try {
                  logger.info("modifying sector:{farmId="+sector.farmId+",name="+name.get+",description="+description.get)

                  name.get foreach sector.name.set
                  description foreach sector.description.set

                  req match {
                    case Accepts.Json(_)  => Ok ~> Json(sectorService.changeSector(sector))
                    case Accepts.Xml(_)   => Ok ~> Xml(sectorService.changeSector(sector))
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
            case XmlRead(xml) => NotImplemented ~> ResponseString("@ xml put /sectors/"+id)
            case _ => UnsupportedMediaType
          }
      }
    case DELETE(Path(Seg("sectors" :: id :: Nil))) =>
      logger.info("delete /sectors/"+id)
      try {
        sectorService.removeSector(id)
        NoContent
      }
      catch {
        case _ => InternalServerError ~> ResponseString("there was an error while deleting the entity")
      }
  }
}