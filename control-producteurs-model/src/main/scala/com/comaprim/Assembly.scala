package com.comaprim

import model.{VarietyAssembly, CultureAssembly, ProducerAssembly, SectorAssembly, FarmAssembly, ControlAssembly}

object Assembly extends CultureAssembly
                with VarietyAssembly
                with ProducerAssembly
                with SectorAssembly
                with FarmAssembly
                with ControlAssembly {

  lazy val cultureRepository = new DefaultICultureRepository
  lazy val cultureService    = new DefaultCultureService

  lazy val producerRepository  = new DefaultProducerRepository
  lazy val producerService     = new DefaultProducerService

  lazy val sectorRepository  = new DefaultSectorRepository
  lazy val sectorService     = new DefaultSectorService

  lazy val farmRepository  = new DefaultFarmRepository
  lazy val farmService     = new DefaultFarmService

  lazy val controlRepository = new DefaultControlRepository
  lazy val controlService    = new DefaultControlService

  lazy val varietyRepository = new DefaultVarietyRepository
  lazy val varietyService    = new DefaultVarietyService
}
