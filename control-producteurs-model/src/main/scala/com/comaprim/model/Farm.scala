package com.comaprim
package model

import org.squeryl.annotations.Column

import net.liftweb.squerylrecord._
import net.liftweb.squerylrecord.RecordTypeMode._

import net.liftweb.record.field._
import net.liftweb.record.{MetaRecord, Record}

import service.{IFarm, IProducer}

import net.liftweb.util.FieldError

class Farm private () extends Record[Farm] with KeyedRecord[Long] {
  override def meta = Farm

  @Column(name = "id")
  override val idField = new LongField(this)

  val producerId = new LongField(this)

  lazy val valUniqueFindFunc:(String=>Boolean) =
      if(isPersisted)
        (value:String) => headOption(from(MySchema.farms)(f => where(f.id <> id and f.reference === value) select(f))).isDefined
      else
        (value:String) => Assembly.farmService.findFarmByRef(value).isDefined

  val reference = new StringField(this, 8, "") {
    override lazy val validations = valUnique(valUniqueFindFunc, this, "reference already exists") _ :: super.validations
    override lazy val setFilter = notNull _ :: trim _ :: toLower _ :: super.setFilter
  }

  val description = new OptionalTextareaField(this, 255){
    override def setFilter = toHtml _ :: super.setFilter
  }

  val created = new DateTimeField(this)
  val updated = new DateTimeField(this)

  lazy val producer = MySchema.producerToFarms.right(this)
  lazy val sectors  = MySchema.farmToSectors.left(this)
}

object Farm extends Farm with MetaRecord[Farm] { self =>
  def removeFarm(id:Long) { MySchema.farms.deleteWhere(_.id === id) }
  def changeFarm(id:Long, reference:String, description:Option[String]=None) = {
    def valUniqueReferenceOnUpdate(id:Long, value:String):Boolean =
      headOption(from(MySchema.farms)(f => where(f.id <> id and (f.reference === /*value should be cleaned*/value)) select(f))).isDefined

    val referenceCleaned = runFilters(reference, Farm.reference.setFilter)

    if(referenceCleaned.isEmpty)
      throw ValidationException(List(FieldError(Culture.designation, scala.xml.Text("reference is required"))))

    if(!valUniqueReferenceOnUpdate(id, referenceCleaned))
      throw ValidationException(List(FieldError(Farm.reference, scala.xml.Text("reference already exists"))))

    update(MySchema.farms)(farm =>
      where(farm.id === id)
      set(farm.reference := referenceCleaned, farm.description := description, farm.updated := net.liftweb.util.TimeHelpers.now)
    )

    MySchema.farms.lookup(id).get
  }
  def findFarm(id:Long) = MySchema.farms.lookup(id)
  def findFarmByRef(reference:String) = headOption(from(MySchema.farms)(farm => where(farm.reference === runFilters(reference, Farm.reference.setFilter)) select(farm)))
  def getFarms = MySchema.farms.toList
  def getFarmsByProducer(producerId:Long) = from(MySchema.farms)(farm => where(farm.producerId === producerId) select(farm)).toList

  def addSector(farm:IFarm, name:String, area:BigDecimal, description:Option[String]) = {
    val ret = Sector.createRecord
                    .name(name)
                    .farmId(farm.id)
                    .area(area)
                    .description(description)
    doValidate(ret, MySchema.sectors.insert(ret))
  }
  
  implicit def toIFarm(farm:Farm):IFarm =
    new IFarm {
      def addSector(name:String, area:BigDecimal, description:Option[String]=None) = self.addSector(this, name, area, description)

      val created = farm.created.is
      val updated = farm.updated.is

      val id = farm.id.toString
      val reference = farm.reference.is
      val producerId = farm.producerId.is.toString
      val description = farm.description.is
    }
}

//---------------------------------- IMPLEMENTATIONS ----------------------------------------------------------------------

import service.{FarmRepositoryComponent, FarmServiceComponent}

trait FarmAssembly extends FarmServiceComponent with FarmRepositoryComponent {
  
  implicit def toIFarm(it:List[Farm])(implicit toIFarm: Farm=>IFarm):List[IFarm] = it map toIFarm
  implicit def toIFarm(it:Option[Farm])(implicit toIFarm: Farm=>IFarm) = it map toIFarm  
  
  class DefaultFarmRepository extends FarmRepository {
    def addFarm(producer:IProducer, reference:String, description:Option[String]=None) = producer.addFarm(reference, description)
    def removeFarm(id:String)  { Farm.removeFarm(id) }
    def changeFarm(farm:IFarm) = Farm.changeFarm(farm.id, farm.reference, farm.description)
    def findFarmByRef(reference:String) = Farm.findFarmByRef(reference)
    def findFarm(id:String) = Farm.findFarm(id)
    def getFarms = Farm.getFarms
    def getFarmsByProducer(producerId:String) = Farm.getFarmsByProducer(producerId)
  }
}