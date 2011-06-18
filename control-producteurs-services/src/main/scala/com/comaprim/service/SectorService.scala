package com.comaprim
package service

trait SectorRepositoryComponent {
  val sectorRepository: SectorRepository

  trait SectorRepository {
    def addSector(farm:IFarm, name:String, area:BigDecimal, description:Option[String]=None):ISector
    def removeSector(id:String)
    def changeSector(producer:ISector):ISector
    def findSector(id:String):Option[ISector]
    def findSectorsByFarm(farmId:String):List[ISector]
    def getSectors:List[ISector]
  }
}

trait SectorService {
  def addSector(farm:IFarm, name:String, area:BigDecimal, description:Option[String]=None):ISector
  def removeSector(id:String)
  def changeSector(sector:ISector):ISector
  def findSector(id:String): Option[ISector]
  def findSectorsByFarm(farmId:String):List[ISector]
  def getSectors:List[ISector]
}

trait SectorServiceComponent { self: SectorRepositoryComponent =>
  val sectorService: SectorService

  class DefaultSectorService extends SectorService {
    def addSector(farm:IFarm, name:String, area:BigDecimal, description:Option[String]=None) = sectorRepository.addSector(farm, name, area, description)
    def removeSector(id:String) { sectorRepository.removeSector(id) }
    def changeSector(sector:ISector) = sectorRepository.changeSector(sector)
    def findSector(id:String) = sectorRepository.findSector(id)
    def findSectorsByFarm(farmId:String) = sectorRepository.findSectorsByFarm(farmId)
    def getSectors = sectorRepository.getSectors
  }
}