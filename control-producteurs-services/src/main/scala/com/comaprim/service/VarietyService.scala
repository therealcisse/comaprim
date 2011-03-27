package com.comaprim
package service

trait IVariety {
  def id:Long
  def name:String
  def description:Option[String]
  def cultureId:Long

  def created: java.util.Calendar
  def updated: Option[java.util.Calendar]
}

trait ICulture {
  def id:Long
  def designation:String
  def description:Option[String]

  def created: java.util.Calendar
  def updated: Option[java.util.Calendar]

  def addVariety(name:String, description:String):IVariety
}

trait IFarm {
  def id:Long
  def reference:String
  def description:Option[String]

  def created: java.util.Calendar
  def updated: Option[java.util.Calendar]

  def addSector(name:String, area:BigDecimal, description:Option[String]=None):ISector
}

trait ISector {
  def id:Long
  def name:String
  def area:BigDecimal
  def farmId:Long
  def description:Option[String]

  def created: java.util.Calendar
  def updated: Option[java.util.Calendar]
}

trait IProducer {
  def id:Long
  def firstName:String
  def lastName:String
  def description:Option[String]

  def created: java.util.Calendar
  def updated: Option[java.util.Calendar]

  def addFarm(reference:String, description:Option[String]=None):IFarm
}

trait IControl {
  def id:Long
  def temperature:BigDecimal
  def production:BigDecimal
  //def controlType:ControlType.Value //TODO: implement this
  def localPrice:BigDecimal
  def exportPrice:BigDecimal
  def date: java.util.Calendar

  def producerId:Long
  def cultureId:Long
}

object ControlType extends Enumeration {
  val Gross, Petit, Unknown = Value
}

trait VarietyRepositoryComponent {
  val varietyRepository:VarietyRepository

  trait VarietyRepository {
    def addVariety(culture:ICulture, name:String, description:String=""):IVariety
    def removeVariety(id:String)
    def changeVariety(variety:IVariety):IVariety
    def findVariety(id:String):Option[IVariety]
    def findVarietyByName(name:String):Option[IVariety]
    def getVarieties:List[IVariety]
    def getVarietiesByCulture(cultureId:String):List[IVariety]
  }
}

trait VarietyService {
  def addVariety(culture:ICulture, name:String, description:String=""):IVariety
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
    def addVariety(culture:ICulture, name: String, description: String="") = varietyRepository.addVariety(culture, name, description)
    def removeVariety(id: String) { varietyRepository.removeVariety(id) }
    def changeVariety(variety:IVariety) = varietyRepository.changeVariety(variety)
    def findVariety(id: String) = varietyRepository.findVariety(id)
    def findVarietyByName(name: String) = varietyRepository.findVarietyByName(name)
    def getVarieties = varietyRepository.getVarieties
    def getVarietiesByCulture(cultureId:String) = varietyRepository.getVarietiesByCulture(cultureId)
  }
}

