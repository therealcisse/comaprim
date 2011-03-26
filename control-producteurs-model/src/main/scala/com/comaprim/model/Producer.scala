package com.comaprim
package model

import org.squeryl.annotations.Column

import net.liftweb.squerylrecord._
import net.liftweb.record.field._
import net.liftweb.record.{MetaRecord, Record}

class Producer private () extends Record[Producer] with KeyedRecord[Long] {
  override def meta = Producer

  @Column(name = "id")
  override val idField = new LongField(this)

  val firstName = new StringField(this, 30, "") {
    override lazy val validations = valMinLen(1, "firstname is required")_ :: super.validations
    override lazy val setFilter = notNull _ :: trim _ :: toLower _ :: super.setFilter
  }

  val lastName = new StringField(this, 30, "") {
    override lazy val validations = valMinLen(1, "lastname is required")_ :: super.validations
    override lazy val setFilter = notNull _ :: trim _ :: toLower _ :: super.setFilter
  }

  val description = new OptionalTextareaField(this, 1000)

  val created = new DateTimeField(this)
  val updated = new OptionalDateTimeField(this)

  lazy val farms = MySchema.producerToFarms.left(this)
}

object Producer extends Producer with MetaRecord[Producer]