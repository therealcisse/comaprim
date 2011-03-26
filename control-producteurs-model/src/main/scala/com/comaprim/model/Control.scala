package com.comaprim
package model

import org.squeryl.annotations.Column

import net.liftweb.squerylrecord._
import net.liftweb.record.field._
import net.liftweb.record.{MetaRecord, Record}

class Control private () extends Record[Control] with KeyedRecord[Long] {
  override def meta = Control

  @Column(name = "id")
  override val idField = new LongField(this)

  val producerId  = new LongField(this)
  val cultureId   = new LongField(this)
  val temperature = new DecimalField(this, BigDecimal(0))
  val production  = new DecimalField(this, BigDecimal(0))
  val `type`      = new EnumNameField(this, ControlType, ControlType.Unknown)
  val localPrice  = new DecimalField(this, BigDecimal(0))
  val exportPrice = new DecimalField(this, BigDecimal(0))
  val date        = new DateTimeField(this)
}

object Control extends Control with MetaRecord[Control]

object ControlType extends Enumeration {
  val Gross, Petit, Unknown = Value
}