package com.comaprim
package service

trait FarmRepositoryComponent {
  val farmRepository: FarmRepository

  trait FarmRepository {
    def addFarm(producer:IProducer, reference:String, description:String=""):IFarm
    def removeFarm(id:String)
    def changeFarm(farm:IFarm):IFarm
    def findFarm(id: String):Option[IFarm]
    def findFarmByRef(reference:String):Option[IFarm]
    def getFarms:List[IFarm]
    def getFarmsByProducer(producerId:String):List[IFarm]
  }
}

trait FarmService {
  def addFarm(producer:IProducer, reference:String, description:String=""):IFarm
  def removeFarm(id:String)
  def changeFarm(farm:IFarm):IFarm
  def findFarm(id: String):Option[IFarm]
  def findFarmByRef(reference:String):Option[IFarm]
  def getFarms:List[IFarm]
  def getFarmsByProducer(producerId:String):List[IFarm]
}

trait FarmServiceComponent { self: FarmRepositoryComponent =>
  val farmService: FarmService

  class DefaultFarmService extends FarmService {
    def addFarm(producer:IProducer, reference:String, description:String="") = farmRepository.addFarm(producer, reference, description)
    def removeFarm(id:String)  { farmRepository.removeFarm(id) }
    def changeFarm(farm:IFarm) = farmRepository.changeFarm(farm)
    def findFarmByRef(reference:String) = farmRepository.findFarmByRef(reference)
    def findFarm(id:String) = farmRepository.findFarm(id)
    def getFarms = farmRepository.getFarms
    def getFarmsByProducer(producerId:String) = farmRepository.getFarmsByProducer(producerId)
  }
}