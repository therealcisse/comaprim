package com.comaprim
package model

import org.squeryl.annotations.Column

import net.liftweb.squerylrecord._
import net.liftweb.squerylrecord.RecordTypeMode._

import net.liftweb.record.field._
import net.liftweb.record.{MetaRecord, Record}

import service.ISector

class Sector private () extends Record[Sector] with KeyedRecord[Long] {
  override def meta = Sector

  @Column(name = "id")
  override val idField = new LongField(this)

  val varietyId   = new LongField(this)
  val farmId      = new LongField(this)

  lazy val valUniqueFindFunc:(String=>Boolean) =
      if(isPersisted)
        (value:String) => (from(MySchema.sectors)(s => where(s.id <> id and s.farmId === farmId and s.name === value) select(s))).headOption.isDefined
      else
        (value:String) => (from(MySchema.sectors)(s => where(s.farmId === farmId and s.name === value) select(s))).headOption.isDefined

  val name = new StringField(this, 30, "") {
    override lazy val validations = valUnique(valUniqueFindFunc, this, "sector name already exists in this farm") _ :: super.validations
    override lazy val setFilter = notNull _ :: trim _ :: toLower _ :: super.setFilter
  }

  val area        = new DecimalField(this, BigDecimal(0))
  val description = new OptionalTextareaField(this, 1000)

  val created = new DateTimeField(this)
  val updated = new OptionalDateTimeField(this)

  lazy val farm     = MySchema.farmToSectors.right(this)
  lazy val variety  = MySchema.varietyToSectors.right(this)
}

object Sector extends Sector with MetaRecord[Sector]{

  def removeSector(id:Long) { MySchema.sectors.deleteWhere(_.id === id) }
  def changeSector(id:Long, name:String, description:Option[String]) = { update(MySchema.sectors)(sector => where(sector.id === id) set(sector.name := runFilters(name, sector.name.setFilter), sector.description := description)); MySchema.sectors.lookup(id).get }
  def findSector(id:Long) = MySchema.sectors.lookup(id)
  def findSectorsByFarm(farmId:Long) = from(MySchema.sectors)(sector => where(sector.farmId === farmId) select(sector)).toList
  def getSectors = MySchema.sectors.toList
  
  implicit def toISector(sector:Sector):ISector =
    new ISector {
      val id = sector.id
      val name = sector.name.is
      val area = sector.area.is
      val farmId = sector.farmId.is
      val varietyId = sector.varietyId.is
      val description = sector.description.is

      val created = sector.created.is
      val updated = sector.updated.is
    }  
}

//---------------------------------- IMPLEMENTATIONS ----------------------------------------------------------------------

import service.{SectorRepositoryComponent, SectorServiceComponent, IFarm}

trait SectorAssembly extends SectorServiceComponent with SectorRepositoryComponent {
  
  implicit def toISector(it:List[Sector])(implicit toISector: Sector=>ISector):List[ISector] = it map toISector
  implicit def toISector(it:Option[Sector])(implicit toISector: Sector=>ISector) = it map toISector  
  
  class DefaultSectorRepository extends SectorRepository {
    def addSector(farm:IFarm, name:String, area:BigDecimal, description:String="") = farm.addSector(name, area, Option(description))
    def removeSector(id:String) { Sector.removeSector(id) }
    def changeSector(sector:ISector) = Sector.changeSector(sector.id, sector.name, sector.description)
    def findSector(id:String) = Sector.findSector(id)
    def findSectorsByFarm(farmId:String) = Sector.findSectorsByFarm(farmId)
    def getSectors = Sector.getSectors
  }
}
