package com.comaprim
package model

import org.squeryl.annotations.Column

import net.liftweb.squerylrecord._
import net.liftweb.squerylrecord.RecordTypeMode._

import net.liftweb.record.field._
import net.liftweb.record.{MetaRecord, Record}

class Sector private () extends Record[Sector] with KeyedRecord[Long] { self =>
  override def meta = Sector

  @Column(name = "id")
  override val idField = new LongField(this)

  val varietyId   = new LongField(this)
  val farmId      = new LongField(this)

  lazy val valUniqueFindFunc:(String=>Boolean) =
      if(self.isPersisted)
        (value:String) => (from(MySchema.sectors)(s => where(s.id <> self.id and s.farmId === self.farmId and s.name === value) select(s))).headOption.isDefined
      else
        (value:String) => (from(MySchema.sectors)(s => where(s.farmId === self.farmId and s.name === value) select(s))).headOption.isDefined

  val name = new StringField(this, 30, "") {
    override lazy val validations = Utils.valUnique(valUniqueFindFunc, this, "sector name already exists in this farm") _ :: super.validations
    override lazy val setFilter = notNull _ :: trim _ :: toLower _ :: super.setFilter
  }

  val area        = new DecimalField(this, BigDecimal(0))
  val description = new OptionalTextareaField(this, 1000)

  val created = new DateTimeField(this)
  val updated = new OptionalDateTimeField(this)

  lazy val farm     = MySchema.farmToSectors.right(this)
  lazy val variety  = MySchema.varietyToSectors.right(this)
}

object Sector extends Sector with MetaRecord[Sector]