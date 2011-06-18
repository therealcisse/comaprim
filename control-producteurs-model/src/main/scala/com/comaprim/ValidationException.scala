package com.comaprim

import net.liftweb.util.FieldError

case class ValidationException(errors:List[FieldError]) extends Exception