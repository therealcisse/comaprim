package com.comaprim
package model

import org.squeryl.annotations.Column

import net.liftweb.squerylrecord._
import net.liftweb.squerylrecord.RecordTypeMode._

import net.liftweb.record.field._
import net.liftweb.record.{MetaRecord, Record}
import service.{IVariety, ICulture}

class Culture private () extends Record[Culture] with KeyedRecord[Long]{
  override def meta = Culture

  @Column(name = "id")
  override val idField = new LongField(this)

  lazy val valUniqueFindFunc:(String=>Boolean) =
    if(isPersisted)
      (value:String) => (from(MySchema.cultures)(c => where(c.id <> id and (c.designation === /*setFilter is already ran here*/value)) select(c))).headOption.isDefined
    else
      (value:String) => Assembly.cultureService.findCultureByDesignation(value).isDefined

  val designation = new StringField(this, 30, "") {
    override lazy val validations = valUnique(valUniqueFindFunc, this, "culture name already exists") _ :: super.validations
    override lazy val setFilter = notNull _ :: trim _ :: toLower _ :: super.setFilter
  }

  val description  = new OptionalTextareaField(this, 1000)

  val created = new DateTimeField(this)
  val updated = new OptionalDateTimeField(this)

  lazy val varieties = MySchema.cultureToVarieties.left(this)
}

object Culture extends Culture with MetaRecord[Culture]  {  self =>
  def addVariety(culture:ICulture, name:String, description:String="") = MySchema.varieties.insert(Variety.createRecord.name(name).cultureId(culture.id).description(description))
  def addCulture(designation:String, description:String="") = MySchema.cultures.insert(Culture.createRecord.designation(designation).description(description))
  def removeCulture(id:Long)  { MySchema.cultures.deleteWhere(_.id === id) }
  def changeCulture(id:Long, designation:String, description:Option[String]) = { update(MySchema.cultures)(culture => where(culture.id === id) set(culture.designation := runFilters(designation, Culture.designation.setFilter), culture.description := description)); MySchema.cultures.lookup(id).get }
  def findCulture(id:Long) = MySchema.cultures.lookup(id)
  def findCultureByDesignation(designation:String) = headOption(from(MySchema.cultures)(culture => where(culture.designation === runFilters(designation, Culture.designation.setFilter)) select(culture)))
  def getCultures = MySchema.cultures.toList

  implicit def toICulture(culture:Culture):ICulture =
    new ICulture {
      val id = culture.id
      val designation = culture.designation.is
      val description = culture.description.is

      val created = culture.created.is
      val updated = culture.updated.is

      def addVariety(name:String, description:String="") = self.addVariety(this, name, description)
    }
}

//---------------------------------- IMPLEMENTATIONS ----------------------------------------------------------------------

import service.{CultureRepositoryComponent, CultureServiceComponent, ICulture}


trait CultureAssembly extends CultureServiceComponent with CultureRepositoryComponent {
  
  implicit def toICulture(it:List[Culture])(implicit toICulture: Culture=>ICulture):List[ICulture] = it map toICulture
  implicit def toICulture(it:Option[Culture])(implicit toICulture: Culture=>ICulture) = it map toICulture

  class DefaultICultureRepository extends CultureRepository {
    def addCulture(name:String, description:String="") = Culture.addCulture(name, description)
    def removeCulture(id:String)  { Culture.removeCulture(id) }
    def changeCulture(culture:ICulture) = Culture.changeCulture(culture.id, culture.designation, culture.description)
    def findCulture(id:String) = Culture.findCulture(id)
    def findCultureByDesignation(designation:String) = Culture.findCultureByDesignation(designation)
    def getCultures:List[ICulture] = Culture.getCultures
  }
}