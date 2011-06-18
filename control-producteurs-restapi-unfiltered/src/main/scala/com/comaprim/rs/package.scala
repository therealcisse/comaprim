package com.comaprim

import service.{IFarm, IProducer, ISector, IControl, ICulture, IVariety}
import net.liftweb.json.{JObject, JValue, JField, JString, JInt, JDouble, JBool, JNothing, JNull}
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Xml.{toXml => jsonToXml}

package object rs {

  def requiredMsg(what: String) = "%s is required." format (what)

  implicit def toJson(it:unfiltered.request.QParams.Fail[_]):JObject = ("field"->it.name) ~ ("msg"->it.error.toString)

  implicit def toJson(it:net.liftweb.util.FieldError):JObject = ("field"->it.field.uniqueFieldId.toOption) ~ ("msg"->it.msg.text)
  implicit def toJson(it:List[net.liftweb.util.FieldError]):JObject = ("errors"->it.map(toJson)) ~ ("success"->false)

  implicit def toJson(it:IProducer):JObject =
    ("id" -> it.id) ~
      ("firstName" -> it.firstName) ~
      ("lastName" -> it.lastName) ~
      ("description" -> it.description) ~
      ("created" -> it.created.getTimeInMillis) ~
      ("updated" -> it.created.getTimeInMillis)

  implicit def toJson(it:IFarm):JObject =
    ("id" -> it.id) ~
      ("reference" -> it.reference) ~
      ("producerId" -> it.producerId) ~
      ("description" -> it.description) ~
      ("created" -> it.created.getTimeInMillis) ~
      ("updated" -> it.created.getTimeInMillis)

  implicit def toJson(it:ISector):JObject =
    ("id" -> it.id) ~
      ("name" -> it.name) ~
      ("area" -> it.area) ~
      ("farmId" -> it.farmId) ~
      ("description" -> it.description) ~
      ("created" -> it.created.getTimeInMillis) ~
      ("updated" -> it.created.getTimeInMillis)

  implicit def toJson(it:ICulture):JObject =
    ("id" -> it.id) ~
      ("designation" -> it.designation) ~
      ("description" -> it.description) ~
      ("created" -> it.created.getTimeInMillis) ~
      ("updated" -> it.created.getTimeInMillis)

  implicit def toJson(it:IVariety):JObject =
    ("id" -> it.id) ~
      ("name" -> it.name) ~
      ("description" -> it.description) ~
      ("created" -> it.created.getTimeInMillis) ~
      ("updated" -> it.created.getTimeInMillis)

  implicit def toJson(it:IControl):JObject =
    ("id" -> it.id) ~
      ("temperature" -> it.temperature) ~
      ("production" -> it.production) ~
      ("cultureId" -> it.cultureId) ~
      ("producerId" -> it.producerId) ~
      ("localPrice" -> it.localPrice) ~
      ("exportPrice" -> it.exportPrice) ~
      ("date" -> it.date.getTimeInMillis)

  def jsonToMap(json:JValue) =
    json.fold(Map.empty[String,List[String]] withDefaultValue Nil) {
      case (map, nxt) =>

        // this pnly handles primitive JValue types
        object JValue{
          def unapply(it:JValue):Option[Any] =
            it match {
              case JString(v) => Some(v)
              case JInt(v) =>  Some(v)
              case JDouble(v) => Some(v)
              case JBool(v) => Some(v)
              case JNull|JNothing|_ => None
            }
        }

	def _addVal(name:String,value:Any) = map + (name->(value.toString::map(name)))
        nxt match {
          case JField(name, value) =>
            //def _addVal(value:Any) = map + (name -> (value.toString :: map(name)))
            value match {
              case JValue(v) => _addVal(name, v)
              case _ => map
            }
          case _ => map
        }
    }

  implicit def toJson[T](lst:List[T])(implicit toJson: T=>JObject):JObject = ("rows" -> lst.map(toJson)) ~ ("at" -> java.util.Calendar.getInstance.getTimeInMillis) ~ ("success"->true)

  implicit def toXml[T](it:T)(implicit toJson: T=>JObject):scala.xml.NodeSeq = <record>{jsonToXml(toJson(it))}</record>
  implicit def toXml[T](lst:List[T])(implicit toJson: T=>JObject):scala.xml.NodeSeq = <rows at={java.util.Calendar.getInstance.getTimeInMillis.toString}>{lst map (it => jsonToXml(toJson(it)))}</rows>
}
