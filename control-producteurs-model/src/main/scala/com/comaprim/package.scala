package com

import net.liftweb.util.{FieldError, FieldIdentifier}

package object comaprim {
  def valUnique[T](findFunc: T => Boolean, field:FieldIdentifier, msg:String)(value:T):List[FieldError] =
    if(findFunc(value)) List(FieldError(field, scala.xml.Text(msg))) else Nil

  def headOption[X](coll:Iterable[X]) = {
    val it = coll.iterator
    if(it.hasNext) Some(it.next) else None
  }

  def runFilters[T](value:T, funcs:List[T=>T]):T = funcs match {
    case Nil => value
    case x :: xs => runFilters(x(value), xs)
  }

  implicit def toLong(str:String) = try { java.lang.Long.parseLong(str) } catch { case _:NumberFormatException => 0L }
}