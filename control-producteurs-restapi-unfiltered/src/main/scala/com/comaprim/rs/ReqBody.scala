package com.comaprim
package rs

import unfiltered.request.{RequestContentType, HttpRequest, InStream}
import net.liftweb.common.LazyLoggable

trait ReqBody[ContentType] extends LazyLoggable{
  def contentType:String
  def parse(payload:java.io.InputStream):ContentType

  def unapply[T](req: HttpRequest[T]):Option[ContentType] = req match {
    case RequestContentType(contentTypes) if contentTypes.exists { contentType startsWith _  } =>
      req match {
            case InStream(inputStream) =>
              try { Some(parse(inputStream)) } catch { case _ => None }
            case _ => None
       }
    case _ => None
  }
}

object XmlRead extends ReqBody[scala.xml.Elem]{
  val contentType = "text/xml; charset=utf-8"
  def parse(payload:java.io.InputStream) = scala.xml.XML.load(payload)
}

object JsonRead extends ReqBody[net.liftweb.json.JValue]{
  val contentType = "application/json; charset=utf-8"
  def parse(payload:java.io.InputStream) = net.liftweb.json.JsonParser.parse(new java.io.InputStreamReader(payload))
}