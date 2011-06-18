package com.comaprim

import java.util.Locale
import java.text.SimpleDateFormat

import net.liftweb.util.Helpers.tryo

trait DateUtils {
  //This is for formatting purposes

  def calcDateTimeFormatter = {
    val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", sysLocale() getOrElse Locale.getDefault)
    formatter.setTimeZone(utc) //todo: calculate timezone based on the currnent user
    formatter
  }

  def calcDateFormatter = {
    val formater = new SimpleDateFormat("yyyy-MM-dd'", sysLocale() getOrElse Locale.getDefault)
    formater.setTimeZone(utc) //todo: calculate timezone based on the currnent user
    formater
  }
  
  def formatDate(timestamp: _root_.java.util.Date) = calcDateTimeFormatter.format(timestamp)

  def formatDate(timestamp: Long): String = formatDate(new _root_.java.util.Date(timestamp))

  private def fixDateString(dateString: String) = {
    val l = dateString.length
    if (dateString.charAt(l - 3) == ':')
      dateString.substring(0, l - 3) + dateString.substring(l - 2, l)
    else dateString
  }

  def parseDate(dateString: String): Option[_root_.java.util.Date] = {
    val fixedDateString = fixDateString(dateString)
    dateFormats.view.flatMap(df => tryo { df.parse(fixedDateString) }).headOption
  }

  def parseDateTime(dateString: String): Option[_root_.java.util.Date] = {
    val fixedDateString = fixDateString(dateString)
    dateTimeFormats.view.flatMap(df => tryo { df.parse(fixedDateString) }).headOption
  }  

  //This is for parsing purposes

  lazy val dateFormats =
    List(
      new SimpleDateFormat("yyyy-MM-dd", sysLocale() getOrElse Locale.getDefault),
      new SimpleDateFormat("yyyy/MM/dd", sysLocale() getOrElse Locale.getDefault))

  lazy val dateTimeFormats =
    List(
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",       sysLocale() getOrElse Locale.getDefault),
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",     sysLocale() getOrElse Locale.getDefault),
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",   sysLocale() getOrElse Locale.getDefault),
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sysLocale() getOrElse Locale.getDefault))

  //The TimeZone
  lazy val utc = java.util.TimeZone.getTimeZone("UTC")
}