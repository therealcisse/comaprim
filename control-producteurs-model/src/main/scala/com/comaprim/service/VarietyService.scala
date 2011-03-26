package com.comaprim
package service

import model.Variety

trait VarietyRepositoryComponent {
  val varietyRepository: VarietyRepository

  trait VarietyRepository {
    def addVariety(name: String, description: String=""): Variety
    def removeVariety(id: String): Unit
    def changeVariety(variety: Variety): Variety
    def findVariety(id: String): Option[Variety]
    def findVarietyByName(name: String): Option[Variety]
    def getVarieties(): List[Variety]
    def getVarietiesByCulture(cultureId:String): List[Variety]
  }
}

trait VarietyService {
  def addVariety(name: String, description: String=""): Variety
  def removeVariety(id: String): Unit
  def changeVariety(variety: Variety): Variety
  def findVariety(id: String): Option[Variety]
  def findVarietyByName(name: String): Option[Variety]
  def getVarieties(): List[Variety]
  def getVarietiesByCulture(cultureId:String): List[Variety]
}

trait VarietyServiceComponent { self: VarietyRepositoryComponent =>
  val varietyService: VarietyService

  class DefaultVarietyService extends VarietyService {
    def addVariety(name: String, description: String=""): Variety = varietyRepository.addVariety(name, description)
    def removeVariety(id: String): Unit = varietyRepository.removeVariety(id)
    def changeVariety(variety: Variety): Variety = varietyRepository.changeVariety(variety)
    def findVariety(id: String): Option[Variety] = varietyRepository.findVariety(id)
    def findVarietyByName(name: String): Option[Variety]  = varietyRepository.findVarietyByName(name)
    def getVarieties(): List[Variety] = varietyRepository.getVarieties
    def getVarietiesByCulture(cultureId:String): List[Variety] = varietyRepository.getVarietiesByCulture(cultureId)
  }
}