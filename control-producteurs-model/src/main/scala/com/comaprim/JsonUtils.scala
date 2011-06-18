package com.comaprim

import net.liftweb.json._
import net.liftweb.util.Helpers

trait JsonUtils {

  def getDate(date:String) = parseDate(date)

  def getDateTime(date:String) = parseDateTime(date)

  def getDate(json:JValue, field:String):Option[_root_.java.util.Date] =
    (json \ field) match {
      case JString(date) => getDate(date)
      case _ => None
    }

  def getDateTime(json:JValue, field:String): Option[_root_.java.util.Date] =
    (json \ field) match {
      case JString(date) => getDateTime(date)
      case _ => None
    }

  /*
  *  JValue.map returns List[JValue]
  * */
    def getInt(json:JValue, field:String): Option[Int] =
    (json \ field) match {
      case JInt(value) => Some(value.toInt)
      case _ => None
    }

  def getLong(json:JValue, field:String): Option[Long] =
    (json \ field) match {
      case JInt(value) => Some(value.toLong)
      case JString(Helpers.AsLong(value)) => Some(value)
      case _ => None
    }

  def getString(json:JValue, field:String): Option[String] =
    (json \ field) match {
      case JString(value) => Some(value) //TODO: maybe trim & check for emptiness
      case _ => None
    }

  /*
  *  Spec: JField(field, JArray(strings))
  * */
  def getStrings(json:JValue, field:String) =
    (json \ field) match {  //todo: test this method
      case JNothing => Nil
      case JArray(l) => l collect {case JString(value) => value}
      case _ => Nil //don't know what to do
    }

  /*
  *  Spec: JField(field, JArray(longs))
  * */
  def getLongs(json:JValue, field:String) =
    (json \ field) match {  //todo: test this method
      case JNothing => Nil
      case JArray(l) => l collect {case JInt(value) => value.toLong; case JString(Helpers.AsLong(long)) => long}
      case _ => Nil //don't know what to do
    }

  def getEnum[E <: Enumeration](enum: E, json:JValue, field:String): Option[E#Value] =
    (json \ field) match {
    case JInt(value) => try { Some(enum(value.toInt)) } catch { case _ => None }
    case JString(value) => try { Option(enum.withName(value)) orElse Some(enum(Helpers.toInt(value))) } catch { case _ => None }
    case _ => None
  }
} 