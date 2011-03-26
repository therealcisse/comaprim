package com.comaprim
package model

import org.squeryl.annotations.Column

import net.liftweb.squerylrecord._
import net.liftweb.squerylrecord.RecordTypeMode._

import net.liftweb.record.field._
import net.liftweb.record.{MetaRecord, Record}

class Culture private () extends Record[Culture] with KeyedRecord[Long] { self =>
  override def meta = Culture

  @Column(name = "id")
  override val idField = new LongField(this)

  lazy val valUniqueFindFunc:(String=>Boolean) =
      if(self.isPersisted)
        (value:String) => (from(MySchema.cultures)(c => where(c.id <> self.id and (c.designation === value)) select(c))).headOption.isDefined
      else
        (value:String) => Assembly.cultureService.findCultureByName(value).isDefined

  val designation = new StringField(this, 30, "") {
    override lazy val validations = Utils.valUnique(valUniqueFindFunc, this, "culture name already exists") _ :: super.validations
    override lazy val setFilter = notNull _ :: trim _ :: toLower _ :: super.setFilter
  }

  val description  = new OptionalTextareaField(this, 1000)

  val created = new DateTimeField(this)
  val updated = new OptionalDateTimeField(this)

  lazy val varieties = MySchema.cultureToVarieties.left(this)
}
object Culture extends Culture with MetaRecord[Culture]