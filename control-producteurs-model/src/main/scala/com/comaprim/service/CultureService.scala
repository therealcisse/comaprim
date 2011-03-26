package com.comaprim
package service

import model.Culture

trait CultureRepositoryComponent {
  val cultureRepository: CultureRepository

  trait CultureRepository {
    def addCulture(name: String, description: String=""): Culture
    def removeCulture(id: String): Unit
    def changeCulture(culture: Culture): Culture
    def findCulture(id: String): Option[Culture]
    def findCultureByName(name:String): Option[Culture]
    def getCultures(): List[Culture]
  }
}

trait CultureService {
  def addCulture(name: String, description: String=""): Culture
  def removeCulture(id: String): Unit
  def changeCulture(culture: Culture): Culture
  def findCulture(id: String): Option[Culture]
  def findCultureByName(name:String): Option[Culture]
  def getCultures(): List[Culture]
}

trait CultureServiceComponent { self: CultureRepositoryComponent =>
  val cultureService: CultureService

  class DefaultCultureService extends CultureService {
    def addCulture(name: String, description: String=""): Culture = cultureRepository.addCulture(name, description)
    def removeCulture(id: String): Unit = cultureRepository.removeCulture(id)
    def changeCulture(culture: Culture): Culture = cultureRepository.changeCulture(culture)
    def findCulture(id: String): Option[Culture] = cultureRepository.findCulture(id)
    def findCultureByName(name:String): Option[Culture] = cultureRepository.findCultureByName(name)
    def getCultures(): List[Culture] = cultureRepository.getCultures
  }
}