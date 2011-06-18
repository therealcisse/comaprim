package com.comaprim
package rs

import unfiltered.response._
import unfiltered.request._
import unfiltered.filter.Plan
import net.liftweb.common.LazyLoggable
import javax.servlet.{FilterChain, ServletResponse, ServletRequest, FilterConfig}

import model.Producer

class ProducerHandler extends Plan with LazyLoggable{
  import QParams._
  import Assembly._

  override def init(config: FilterConfig) {
    super.init(config)
    DBHelper.initSquerylRecord()
  }

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    net.liftweb.squerylrecord.RecordTypeMode.inTransaction { super.doFilter(request, response, chain) }
  }

  def intent = route1 orElse route2  orElse route3

  def route1:Plan.Intent = {
    case req@GET(Path(Seg("producers" :: Nil))) =>
      logger.info("@ get /producers")
      req match {
        case Accepts.Json(_) => Ok ~> Json(producerService.getProducers)
        case Accepts.Xml(_) => Ok ~> Xml(producerService.getProducers)
        case _ => UnsupportedMediaType
      }
    case req@POST(Path(Seg("producers" :: Nil))) =>
      req match {
        case JsonRead(json) =>
          logger.info("@ json post /producers")
          val expected = for{
            firstName <- lookup("firstName") is required(requiredMsg("firstName")) is trimmed is nonempty(requiredMsg("firstName"))
            lastName <- lookup("lastName") is optional
            description <- lookup("description") is optional
          } yield {
            try {
              logger.info("Adding producer:{firstName="+firstName.get+",lastName="+lastName.get+",description="+description.get)
              lazy val producer = producerService.addProducer(firstName.get, lastName.get, description.get)
              req match {
                case Accepts.Json(_) => Created ~> Json(producer)
                case Accepts.Xml(_) => Created ~> Xml(producer)
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
        case XmlRead(xml) => ResponseString("@ xml post /producers") ~> NotImplemented
        case _ => UnsupportedMediaType
      }
    case req@GET(Path(Seg("producers" :: id :: Nil))) =>
      logger.info("get /producers/"+id)
      producerService.findProducer(id) match {
        case Some(producer) => req match {
                case Accepts.Json(_)  => Ok ~> Json(producer)
                case Accepts.Xml(_)   => Ok ~> Xml(producer)
                case _ => UnsupportedMediaType
              }
        case _ => ResponseString("producer:"+id+" not found") ~> NotFound
      }
  }

  def route2:Plan.Intent = {
    case req@GET(Path(Seg("producers" :: id :: "farms" :: Nil))) =>
      logger.info("@ get /producers/"+id+"/farms")
      req match {
        case Accepts.Json(_)  => Ok ~> Json(farmService.getFarmsByProducer(id))
        case Accepts.Xml(_)   => Ok ~> Xml(farmService.getFarmsByProducer(id))
        case _ => UnsupportedMediaType
      }
    case req@POST(Path(Seg("producers" :: id :: "farms" :: Nil))) =>
      producerService.findProducer(id) match {
        case None => NotFound ~> ResponseString("producer with id=(%s) not found" format(id))
        case Some(producer) =>
          req match {
            case JsonRead(json) =>
              logger.info("json put /producers/"+id+"/farms")
              val expected = for{
                reference <- lookup("reference") is required(requiredMsg("reference")) is trimmed is nonempty(requiredMsg("reference"))
                description <- lookup("description") is optional
              } yield {
                try {
                  logger.info("Adding farm:{producerId="+id+",reference="+reference.get+",description="+description.get)

                  req match {
                    case Accepts.Json(_)  => Ok ~> Json(farmService.addFarm(producer, reference.get, description.get))
                    case Accepts.Xml(_)   => Ok ~> Xml(farmService.addFarm(producer, reference.get, description.get))
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
            case XmlRead(xml) => NotImplemented ~> ResponseString("xml put /producers/"+producer.id+"/farms")
            case _ => UnsupportedMediaType
          }
      }
    case req@PUT(Path(Seg("producers" :: id :: Nil))) =>
      producerService.findProducer(id) match {
        case None => NotFound ~> ResponseString("producer with id=(%s) not found" format(id))
        case Some(producer:Producer) =>
          logger.info("casting from iproducer to producer works as expected")
          req match {
            case JsonRead(json) =>
              logger.info("json put /producers/"+id)
              val expected = for{
                firstName <- lookup[String]("firstName") is optional
                lastName <- lookup("lastName")  is optional
                description <- lookup("description")  is optional
              } yield {
                try {
                  logger.info("Adding producer:{id="+id+",firstName="+firstName.get+",lastName="+lastName.get+",description="+description.get)

                  firstName.get foreach producer.firstName.set
                  lastName foreach producer.lastName.set
                  description foreach producer.description.set

                  req match {
                    case Accepts.Json(_)  => Ok ~> Json(producerService.changeProducer(producer))
                    case Accepts.Xml(_)   => Ok ~> Xml(producerService.changeProducer(producer))
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
            case XmlRead(xml) => NotImplemented ~> ResponseString("xml put /producers/"+producer.id)
            case _ => UnsupportedMediaType
          }
      }
  }

  def route3:Plan.Intent = {
    case DELETE(Path(Seg("producers" :: id :: Nil))) =>
      logger.info("@ delete /producers/"+id)
      try {
        producerService.removeProducer(id)
        NoContent
      }
      catch {
        case _ => InternalServerError ~> ResponseString("there was an unknown error while deleting the entity")
      }
  }
}
