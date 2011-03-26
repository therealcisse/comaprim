package com.comaprim
package model

import org.squeryl.annotations.Column

import net.liftweb.squerylrecord._
import net.liftweb.squerylrecord.RecordTypeMode._

import net.liftweb.record.field._
import net.liftweb.record.{MetaRecord, Record}

class Farm private () extends Record[Farm] with KeyedRecord[Long] { self =>
  override def meta = Farm

  @Column(name = "id")
  override val idField = new LongField(this)

  val producerId = new LongField(this)

  lazy val valUniqueFindFunc:(String=>Boolean) =
      if(self.isPersisted)
        (value:String) => (from(MySchema.farms)(f => where(f.id <> self.id and f.reference === value) select(f))).headOption.isDefined
      else
        (value:String) => Assembly.farmService.findFarmByRef(value).isDefined

  val reference = new StringField(this, 8, "") {
    override lazy val validations = Utils.valUnique(valUniqueFindFunc, this , "reference already exists") _ :: super.validations
    override lazy val setFilter = notNull _ :: trim _ :: toLower _ :: super.setFilter
  }

  val description  = new OptionalTextareaField(this, 1000)

  val created = new DateTimeField(this)
  val updated = new OptionalDateTimeField(this)

  lazy val producer = MySchema.producerToFarms.right(this)
  lazy val sectors  = MySchema.farmToSectors.left(this)
}

object Farm extends Farm with MetaRecord[Farm]