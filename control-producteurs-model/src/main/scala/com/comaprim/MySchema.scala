package com.comaprim

import net.liftweb.squerylrecord.RecordTypeMode._

import org.squeryl.Schema
import model._

object MySchema extends Schema {
  val producers   = table[Producer]
  val farms       = table[Farm]
  val sectors     = table[Sector]
  val cultures    = table[Culture]
  val varieties   = table[Variety]
  val controls    = table[Control]

  on(producers)(producer => declare(
    producer.firstName defineAs indexed("producer_firstname_index"),
    producer.firstName defineAs indexed("farm_lastname_index"),
    producer.created defineAs indexed("producer_created_index"),
    producer.updated defineAs indexed("producer_updated_index")
  ))

  on(farms)(farm => declare(
    farm.reference defineAs (unique, indexed("farm_ref_index")),
    farm.created defineAs indexed("farm_created_index"),
    farm.updated defineAs indexed("farm_updated_index")
  ))

  on(sectors)(sector => declare(
    sector.name defineAs (unique, indexed("sector_name_index")),
    sector.created defineAs indexed("sector_created_index"),
    sector.updated defineAs indexed("sector_updated_index")
  ))

  on(cultures)(culture => declare(
    culture.designation defineAs (unique, indexed("culture_name_index")),
    culture.created defineAs indexed("culture_created_index"),
    culture.updated defineAs indexed("culture_updated_index")
  ))

  on(varieties)(variety => declare(
    variety.name defineAs (unique, indexed("variety_name_index")),
    variety.created defineAs indexed("varieties_created_index"),
    variety.updated defineAs indexed("varieties_updated_index")
  ))

  on(controls)(control => declare(
    control.date defineAs indexed("control_date_index"),
    control.temperature defineAs indexed("control_temperature_index"),
    control.temperature defaultsTo BigDecimal(0),
    control.production defineAs indexed("control_production_index"),
    control.production defaultsTo BigDecimal(0),
    control.`type` defineAs indexed("control_type_index"),
    control.localPrice defineAs indexed("control_local_price"),
    control.localPrice defaultsTo BigDecimal(0),
    control.exportPrice defineAs indexed("control_export_price"),
    control.exportPrice defaultsTo BigDecimal(0)
  ))

  val producerToFarms =
    oneToManyRelation(producers, farms)
      .via((p, f) => p.id === f.producerId)

  val producerToControls =
    oneToManyRelation(producers, controls)
      .via((p, c) => p.id === c.producerId)

  val varietyToSectors =
    oneToManyRelation(varieties, sectors)
      .via((v, s) => v.id === s.varietyId)

  val farmToSectors =
    oneToManyRelation(farms, sectors)
      .via((f, s) => f.id === s.farmId)

  val cultureToVarieties =
    oneToManyRelation(cultures, varieties)
      .via((c, v) => c.id === v.cultureId)

  val cultureToControls =
    oneToManyRelation(cultures, controls)
      .via((v, c) => v.id === c.cultureId)

  /**
   * Drops an old schema if exists and then creates
   * the new schema.
   */
  def dropAndCreate {
    drop
    create
  }

  def createTestData {
    //create producers

    val producers =
      for(x <-  1 to 30) yield MySchema.producers.insert(Producer.createRecord.firstName("Producer:"+x).lastName("Producer:"+x))

    //create farms

    val farms =
      for(x <-  1 to 30) yield MySchema.farms.insert(Farm.createRecord.reference((x+2000L).toString).producerId(x))

    //create cultures
    //create varieties
    //create sectors
  }
}

