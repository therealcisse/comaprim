package com.comaprim
package service

trait IVariety {
  val id:String
  val name:String
  val description:Option[String]
  val cultureId:String

  val created: java.util.Calendar
  val updated: java.util.Calendar
}

trait ICulture {
  val id:String
  val designation:String
  val description:Option[String]

  val created: java.util.Calendar
  val updated: java.util.Calendar

  def addVariety(name:String, description:Option[String]=None):IVariety
}

trait IFarm {
  val id:String
  val reference:String
  val producerId:String
  val description:Option[String]

  val created: java.util.Calendar
  val updated: java.util.Calendar

  def addSector(name:String, area:BigDecimal, description:Option[String]=None):ISector
}

trait ISector {
  val id:String
  val name:String
  val area:BigDecimal
  val farmId:String
  val description:Option[String]

  val created: java.util.Calendar
  val updated: java.util.Calendar
}

trait IProducer {
  val id:String
  val firstName:String
  val lastName:Option[String]
  val description:Option[String]

  val created: java.util.Calendar
  val updated: java.util.Calendar

  def addFarm(reference:String, description:Option[String]=None):IFarm
}

trait IControl {
  val id:String
  val temperature:BigDecimal
  val production:BigDecimal
  //val controlType:ControlType.Value //TODO: implement this
  val localPrice:BigDecimal
  val exportPrice:BigDecimal
  val date: java.util.Calendar

  val producerId:String
  val cultureId:String
}

object ControlType extends Enumeration {
  val Gross, Petit, Unknown = Value
}

trait VarietyRepositoryComponent {
  val varietyRepository:VarietyRepository

  trait VarietyRepository {
    def addVariety(culture:ICulture, name: String, description:Option[String]=None):IVariety
    def removeVariety(id:String)
    def changeVariety(variety:IVariety):IVariety
    def findVariety(id:String):Option[IVariety]
    def findVarietyByName(name:String):Option[IVariety]
    def getVarieties:List[IVariety]
    def getVarietiesByCulture(cultureId:String):List[IVariety]
  }
}

trait VarietyService {
  def addVariety(culture:ICulture, name: String, description:Option[String]=None):IVariety
  def removeVariety(id:String)
  def changeVariety(variety:IVariety):IVariety
  def findVariety(id:String):Option[IVariety]
  def findVarietyByName(name:String):Option[IVariety]
  def getVarieties:List[IVariety]
  def getVarietiesByCulture(cultureId:String):List[IVariety]
}

trait VarietyServiceComponent { this:VarietyRepositoryComponent =>
  val varietyService:VarietyService

  class DefaultVarietyService extends VarietyService {
    def addVariety(culture:ICulture, name: String, description:Option[String]=None) = varietyRepository.addVariety(culture, name, description)
    def removeVariety(id: String) { varietyRepository.removeVariety(id) }
    def changeVariety(variety:IVariety) = varietyRepository.changeVariety(variety)
    def findVariety(id: String) = varietyRepository.findVariety(id)
    def findVarietyByName(name: String) = varietyRepository.findVarietyByName(name)
    def getVarieties = varietyRepository.getVarieties
    def getVarietiesByCulture(cultureId:String) = varietyRepository.getVarietiesByCulture(cultureId)
  }
}

