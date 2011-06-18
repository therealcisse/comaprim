package com.comaprim
package service

trait CultureRepositoryComponent {
  val cultureRepository: CultureRepository

  trait CultureRepository {
    def addCulture(name:String, description:Option[String]=None): ICulture
    def removeCulture(id: String)
    def changeCulture(culture: ICulture): ICulture
    def findCulture(id: String): Option[ICulture]
    def findCultureByDesignation(designation:String): Option[ICulture]
    def getCultures: List[ICulture]
  }
}

trait CultureService {
  def addCulture(name:String, description:Option[String]=None): ICulture
  def removeCulture(id:String)
  def changeCulture(culture:ICulture):ICulture
  def findCulture(id:String):Option[ICulture]
  def findCultureByDesignation(designation:String): Option[ICulture]
  def getCultures:List[ICulture]
}

trait CultureServiceComponent { self: CultureRepositoryComponent =>
  val cultureService: CultureService

  class DefaultCultureService extends CultureService {
    def addCulture(name:String, description:Option[String]=None) = cultureRepository.addCulture(name, description)
    def removeCulture(id: String) { cultureRepository.removeCulture(id) }
    def changeCulture(culture:ICulture) = cultureRepository.changeCulture(culture)
    def findCulture(id: String) = cultureRepository.findCulture(id)
    def findCultureByDesignation(designation:String) = cultureRepository.findCultureByDesignation(designation)
    def getCultures = cultureRepository.getCultures
  }
}