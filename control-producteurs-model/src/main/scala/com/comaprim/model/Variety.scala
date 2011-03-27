package com.comaprim
package model

import org.squeryl.annotations.Column

import net.liftweb.util.FieldError

import net.liftweb.squerylrecord._
import net.liftweb.squerylrecord.RecordTypeMode._

import net.liftweb.record.field._
import net.liftweb.record.{MetaRecord, Record}
import com.comaprim.MySchema
import service.{IVariety, ICulture}

class Variety extends Record[Variety] with KeyedRecord[Long]{
  override def meta = Variety

  @Column(name = "id")
  override val idField = new LongField(this)

  val cultureId = new LongField(this)

  lazy val valUniqueFindFunc:(String=>Boolean) =
      if(isPersisted)
        (value:String) => (from(MySchema.varieties)(v => where(v.id <> id and v.name === value) select(v))).headOption.isDefined
      else
        (value:String) => Assembly.varietyService.findVarietyByName(value).isDefined

  val name = new StringField(this, 30, "") {
    override lazy val validations: List[String => List[FieldError]] = valUnique(valUniqueFindFunc, this, "variety name already exists") _ :: super.validations
    override lazy val setFilter = notNull _ :: trim _ :: toLower _ :: super.setFilter
  }

  val description = new OptionalTextareaField(this, 255)

  val created = new DateTimeField(this)
  val updated = new OptionalDateTimeField(this)

  lazy val culture = MySchema.cultureToVarieties.right(this)
  lazy val sectors = MySchema.varietyToSectors.left(this)
}

object Variety extends Variety with MetaRecord[Variety] {
  def removeVariety(id:Long) { MySchema.varieties.deleteWhere(_.id === id) }
  def changeVariety(id:Long, name:String, description:Option[String]=None):Variety = { update(MySchema.varieties)(variety => where(variety.id === id) set(variety.name := runFilters(name, Variety.name.setFilter), variety.description := description)); MySchema.varieties.lookup(id).get }
  def findVariety(id:Long):Option[Variety] = MySchema.varieties.lookup(id)
  def findVarietyByName(name:String):Option[Variety] = headOption(from(MySchema.varieties)(variety => where(variety.name === runFilters(name, Variety.name.setFilter)) select(variety)))
  def getVarieties:List[Variety] = MySchema.varieties.toList
  def getVarietiesByCulture(cultureId:Long):List[Variety] = from(MySchema.varieties)(variety => where(variety.cultureId === cultureId) select(variety)).toList

  implicit def toIVariety(variety:Variety):IVariety =
    new IVariety {
      val id = variety.id
      val cultureId = variety.cultureId.is
      val description = variety.description.is
      val name = variety.name.is

      val created = variety.created.is
      val updated = variety.updated.is
    }
}

//---------------------------------- IMPLEMENTATIONS ----------------------------------------------------------------------

import service.{VarietyRepositoryComponent, VarietyServiceComponent}

trait VarietyAssembly extends VarietyRepositoryComponent with VarietyServiceComponent{

  implicit def toIVariety(it:List[Variety])(implicit toIVariety: Variety=>IVariety):List[IVariety] = it map toIVariety
  implicit def toIVariety(it:Option[Variety])(implicit toIVariety: Variety=>IVariety) = it map toIVariety

  class DefaultVarietyRepository extends VarietyRepository {
    def addVariety(culture:ICulture, name:String, description:String="") = culture.addVariety(name, description)
    def removeVariety(id:String) { Variety.removeVariety(id) }
    def changeVariety(variety:IVariety) = Variety.changeVariety(variety.id, variety.name, variety.description)
    def findVariety(id:String) = Variety.findVariety(id)
    def findVarietyByName(name:String) = Variety.findVarietyByName(name)
    def getVarieties = Variety.getVarieties
    def getVarietiesByCulture(cultureId:String) = Variety.getVarietiesByCulture(cultureId)
  }
}