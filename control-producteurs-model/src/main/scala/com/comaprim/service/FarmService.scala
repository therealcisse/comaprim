package com.comaprim
package service

import model.{Farm, Producer}

trait FarmRepositoryComponent {
  val farmRepository: FarmRepository

  trait FarmRepository {
    def addFarm(producer: Producer, ref: String, description: String=""): Farm
    def removeFarm(id: String): Unit
    def changeFarm(farm: Farm): Farm
    def findFarm(id: String): Option[Farm]
    def findFarmByRef(ref: String): Option[Farm]
    def getFarms(): List[Farm]
    def getFarmsForProduer(producerId:String): List[Farm]
  }
}

trait FarmService {
  def addFarm(producer: Producer, ref: String, description: String=""): Farm
  def removeFarm(id: String): Unit
  def changeFarm(farm: Farm): Farm
  def findFarmByRef(ref: String): Option[Farm]
  def findFarm(id: String): Option[Farm]
  def getFarms(): List[Farm]
}

trait FarmServiceComponent { self: FarmRepositoryComponent =>
  val farmService: FarmService

  class DefaultFarmService extends FarmService {
    def addFarm(producer: Producer, ref: String, description: String=""): Farm = farmRepository.addFarm(producer, ref, description)
    def removeFarm(id: String): Unit = farmRepository.removeFarm(id)
    def changeFarm(farm: Farm): Farm = farmRepository.changeFarm(farm)
    def findFarmByRef(ref: String): Option[Farm] = farmRepository.findFarmByRef(ref)
    def findFarm(id: String): Option[Farm] = farmRepository.findFarm(id)
    def getFarms(): List[Farm] = farmRepository.getFarms
  }
}