package com.comaprim
package model

import org.squeryl.annotations.Column

import net.liftweb.util.FieldError

import net.liftweb.squerylrecord._
import net.liftweb.squerylrecord.RecordTypeMode._

import net.liftweb.record.field._
import net.liftweb.record.{MetaRecord, Record}

class Variety extends Record[Variety] with KeyedRecord[Long] { self =>
  override def meta = Variety

  @Column(name = "id")
  override val idField = new LongField(this)

  val cultureId   = new LongField(this)

  def valUniqueFindFunc:(String=>Boolean) =
      if(self.isPersisted)
        (value:String) => (from(MySchema.varieties)(v => where(v.id <> self.id and v.name === value) select(v))).headOption.isDefined
      else
        (value:String) => Assembly.varietyService.findVarietyByName(value).isDefined

  val name = new StringField(this, 30, "") {
    override def validations: List[String => List[FieldError]] = Utils.valUnique(valUniqueFindFunc, this, "variety name already exists") _ :: super.validations
    override def setFilter = notNull _ :: trim _ :: toLower _ :: super.setFilter
  }

  val description = new OptionalTextareaField(this, 255)

  val created = new DateTimeField(this)
  val updated = new OptionalDateTimeField(this)

  lazy val culture = MySchema.cultureToVarieties.right(this)
  lazy val sectors = MySchema.varietyToSectors.left(this)
}

object Variety extends Variety with MetaRecord[Variety]