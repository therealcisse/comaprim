package com.comaprim

import service._

object Assembly extends ProducerAssembly with FarmAssembly with SectorAssembly with CultureAssembly with VarietyAssembly with ControlAssembly

trait ProducerAssembly extends ProducerServiceComponent with ProducerRepositoryComponent {
  val producerRepository = null.asInstanceOf[ProducerRepository]
  val producerService = new DefaultProducerService
}

trait FarmAssembly extends FarmServiceComponent with FarmRepositoryComponent {
  val farmRepository = null.asInstanceOf[FarmRepository]
  val farmService = new DefaultFarmService
}

trait SectorAssembly extends SectorServiceComponent with SectorRepositoryComponent {
  val sectorRepository = null.asInstanceOf[SectorRepository]
  val sectorService = new DefaultSectorService
}

trait CultureAssembly extends CultureServiceComponent with CultureRepositoryComponent {
  val cultureRepository = null.asInstanceOf[CultureRepository]
  val cultureService = new DefaultCultureService
}

trait VarietyAssembly extends VarietyServiceComponent with VarietyRepositoryComponent {
  val varietyRepository = null.asInstanceOf[VarietyRepository]
  val varietyService = new DefaultVarietyService
}

trait ControlAssembly extends ControlServiceComponent with ControlRepositoryComponent {
  val controlRepository = null.asInstanceOf[ControlRepository]
  val controlService = new DefaultControlService
}
