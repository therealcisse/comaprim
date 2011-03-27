package com.comaprim
package model

import org.squeryl.annotations.Column

import net.liftweb.squerylrecord.RecordTypeMode._
import net.liftweb.squerylrecord._
import net.liftweb.record.field._
import net.liftweb.record.{MetaRecord, Record}

import service.{IProducer}

class Producer private () extends Record[Producer] with KeyedRecord[Long] {
  override def meta = Producer

  @Column(name = "id")
  override val idField = new LongField(this)

  val firstName = new StringField(this, 30, "") {
    override lazy val validations = valMinLen(1, "firstname is required")_ :: super.validations
    override lazy val setFilter = notNull _ :: trim _ :: toLower _ :: super.setFilter
  }

  val lastName = new StringField(this, 30, "") {
    override lazy val validations = valMinLen(1, "lastname is required")_ :: super.validations
    override lazy val setFilter = notNull _ :: trim _ :: toLower _ :: super.setFilter
  }

  val description = new OptionalTextareaField(this, 1000)

  val created = new DateTimeField(this)
  val updated = new OptionalDateTimeField(this)

  lazy val farms = MySchema.producerToFarms.left(this)
}

object Producer extends Producer with MetaRecord[Producer]{ self =>
  def addProducer(firstName:String, lastName:String, description:String) = MySchema.producers.insert(Producer.createRecord.firstName(firstName).lastName(lastName).description(description))
  def removeProducer(id:Long) { MySchema.producers.deleteWhere(_.id === id) }
  def changeProducer(id:Long, firstName:String, lastName:String, description:Option[String]) = { update(MySchema.producers)(producer => where(producer.id === id) set(producer.firstName := runFilters(firstName, Producer.firstName.setFilter), producer.lastName := runFilters(lastName, Producer.lastName.setFilter), producer.description := description)); MySchema.producers.lookup(id).get }
  def findProducer(id:Long) = MySchema.producers.lookup(id)
  def getProducers = MySchema.producers.toList
  
  def addFarm(producer:IProducer, reference:String, description:Option[String]=None) = MySchema.farms.insert(Farm.createRecord.reference(reference).producerId(producer.id).description(description))

  implicit def toIProducer(producer:Producer):IProducer =
    new IProducer {
      def addFarm(reference:String, description:Option[String]) = self.addFarm(this, reference, description)

      val created = producer.created.is
      val updated = producer.updated.is

      val id = producer.id
      val firstName = producer.firstName.is
      val lastName = producer.lastName.is
      val description = producer.description.is
    }
}

//---------------------------------- IMPLEMENTATIONS ----------------------------------------------------------------------

import service.{ProducerRepositoryComponent, ProducerServiceComponent}

trait ProducerAssembly extends ProducerServiceComponent with ProducerRepositoryComponent {
  
  implicit def toIProducer(it:List[Producer])(implicit toIProducer: Producer=>IProducer):List[IProducer] = it map toIProducer
  implicit def toIProducer(it:Option[Producer])(implicit toIProducer: Producer=>IProducer) = it map toIProducer  
  
  class DefaultProducerRepository extends ProducerRepository {
    def addProducer(firstName:String, lastName:String,  description:String) = Producer.addProducer(firstName, lastName, description)
    def removeProducer(id:String) { Producer.removeProducer(id) }
    def changeProducer(producer:IProducer) = Producer.changeProducer(producer.id, producer.firstName, producer.lastName, producer.description)
    def findProducer(id:String) = Producer.findProducer(id)
    def getProducers = Producer.getProducers
  }
}

