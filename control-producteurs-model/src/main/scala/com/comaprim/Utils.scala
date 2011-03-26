package com.comaprim

import net.liftweb.util.{FieldError, FieldIdentifier}

object Utils {
  def valUnique[T](findFunc: T => Boolean, field:FieldIdentifier, msg:String)(value:T):List[FieldError] =
    if(findFunc(value)) List(FieldError(field, scala.xml.Text(msg))) else Nil
}