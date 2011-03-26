package com.comaprim
package service

import model.{Sector, Farm}

trait SectorRepositoryComponent {
  val sectorRepository: SectorRepository

  trait SectorRepository {
    def addSector(farm: Farm, name: String, description: String=""): Sector
    def removeSector(id: String): Unit
    def changeSector(producer: Sector): Sector
    def findSector(id: String): Option[Sector]
    def findSectorsByFarm(farmId:String): List[Sector]
    def getSectors(): List[Sector]
  }
}

trait SectorService {
  def addSector(farm: Farm, name: String, description: String=""): Sector
  def removeSector(id: String): Unit
  def changeSector(sector: Sector): Sector
  def findSector(id: String): Option[Sector]
  def findSectorsByFarm(farmId:String): List[Sector]
  def getSectors(): List[Sector]
}

trait SectorServiceComponent { self: SectorRepositoryComponent =>
  val sectorService: SectorService

  class DefaultSectorService extends SectorService {
    def addSector(farm: Farm, name: String, description: String=""): Sector = sectorRepository.addSector(farm, name, description)
    def removeSector(id: String): Unit = sectorRepository.removeSector(id)
    def changeSector(sector: Sector): Sector = sectorRepository.changeSector(sector)
    def findSector(id: String): Option[Sector] = sectorRepository.findSector(id)
    def findSectorsByFarm(farmId:String): List[Sector] = sectorRepository.findSectorsByFarm(farmId)
    def getSectors(): List[Sector] = sectorRepository.getSectors
  }
}