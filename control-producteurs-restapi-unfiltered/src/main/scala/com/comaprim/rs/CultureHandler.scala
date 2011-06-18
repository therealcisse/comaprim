package com.comaprim
package rs

import unfiltered.request._
import unfiltered.response._
import unfiltered.filter.Plan
import net.liftweb.common.LazyLoggable
import model.{Culture}

class CultureHandler extends Plan with LazyLoggable{
  import QParams._
  import Assembly._

  def intent =  route1 orElse route2  orElse route3

  def route1:Plan.Intent = {
    case req@GET(Path(Seg("cultures" :: Nil))) =>
      logger.info("@ get /cultures")
      req match {
        case Accepts.Json(_)  => Ok ~> Json(cultureService.getCultures)
        case Accepts.Xml(_)   => Ok ~> Xml(cultureService.getCultures)
        case _ => UnsupportedMediaType
    }
    case req@POST(Path(Seg("cultures" :: Nil))) =>
      req match {
        case JsonRead(json) =>
          logger.info("@ json post /cultures")
          val expected = for{
            designation <- lookup("designation") is required(requiredMsg("designation")) is trimmed is nonempty(requiredMsg("designation"))
            description <- lookup("description") is optional
          } yield {
            try {
              logger.info("Adding culture:{designation="+designation.get+",description="+description.get)
              lazy val culture = cultureService.addCulture(designation.get, description.get)
              req match {
                case Accepts.Json(_) => Created ~> Json(culture)
                case Accepts.Xml(_) => Created ~> Xml(culture)
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
        case XmlRead(xml) => ResponseString("@ xml post /cultures") ~> NotImplemented
        case _ => UnsupportedMediaType
      }
    case req@GET(Path(Seg("cultures" :: id :: Nil))) =>
      logger.info("@ get /cultures/" + id)
      cultureService.findCulture(id) match {
        case Some(culture) => req match {
              case Accepts.Json(_)  => Ok ~> Json(culture)
              case Accepts.Xml(_)   => Ok ~> Xml(culture)
              case _ => UnsupportedMediaType
           }
        case _ => NotFound ~> ResponseString("culture:" + id+ " not found")
      }
  }

  def route2:Plan.Intent = {
    case req@GET(Path(Seg("cultures" :: id :: "varieties" :: Nil))) =>
      logger.info("@ get /cultures/" + id + "/varieties")
      req match {
        case Accepts.Json(_)  => Ok ~> Json(varietyService.getVarietiesByCulture(id))
        case Accepts.Xml(_)   => Ok ~> Xml(varietyService.getVarietiesByCulture(id))
        case _ => UnsupportedMediaType
    }
    case req@POST(Path(Seg("cultures" :: id :: "varieties" :: Nil))) =>
      cultureService.findCulture(id) match {
        case None => NotFound ~> ResponseString("culture with id=(%s) not found" format(id))
        case Some(culture) =>
          req match {
            case JsonRead(json) =>
              logger.info("@ json post /cultures/" + id + "/varieties")
              val expected = for{
                name <- lookup("name") is required(requiredMsg("name")) is trimmed is nonempty(requiredMsg("name"))
                description <- lookup("description") is optional
              } yield {
                try {
                  logger.info("Adding variety:{cultureId="+id+",name="+name.get+",description="+description.get)

                  lazy val variety = varietyService.addVariety(culture, name.get, description.get)
                  req match {
                    case Accepts.Json(_)  => Ok ~> Json(variety)
                    case Accepts.Xml(_)   => Ok ~> Xml(variety)
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
            case XmlRead(xml) => NotImplemented ~> ResponseString("@ xml post /cultures/" + id + "/varieties")
            case _ => UnsupportedMediaType
          }
      }
    case req@PUT(Path(Seg("cultures" :: id :: Nil))) =>
      cultureService.findCulture(id) match {
        case None => NotFound ~> ResponseString("producer with id=(%s) not found" format(id))
        case Some(culture:Culture) =>
          logger.info("casting from iculture to culture works as expected")
          req match {
            case JsonRead(json) =>
              logger.info("@ json put /cultures/"+id)
              val expected = for{
                designation <- lookup[String]("designation") is optional
                description <- lookup("description")  is optional
              } yield {
                try {
                  logger.info("modifying culture:{id="+id+",designation="+designation.get+",description="+description.get)

                  designation.get foreach culture.designation.set
                  description foreach culture.description.set

                  req match {
                    case Accepts.Json(_)  => Ok ~> Json(cultureService.changeCulture(culture))
                    case Accepts.Xml(_)   => Ok ~> Xml(cultureService.changeCulture(culture))
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
            case XmlRead(xml) => NotImplemented ~> ResponseString("@ xml put /cultures/"+id)
            case _ => UnsupportedMediaType
          }
      }
  }

  def route3:Plan.Intent = {
    case DELETE(Path(Seg("cultures" :: id :: Nil))) => {
      logger.info("@ delete /cultures/" + id)
      try {
        cultureService.removeCulture(id)
        NoContent
      }
      catch {
        case _ => InternalServerError ~> ResponseString("there was an unknown error while deleting the entity")
      }
    }
  }
}