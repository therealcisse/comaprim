package com.comaprim
package model

import org.squeryl.annotations.Column

import net.liftweb.squerylrecord.RecordTypeMode._
import net.liftweb.squerylrecord._
import net.liftweb.record.field._
import net.liftweb.record.{MetaRecord, Record}

import service.{IProducer}

import net.liftweb.util.FieldError

class Producer private () extends Record[Producer] with KeyedRecord[Long] {
  override def meta = Producer

  @Column(name = "id")
  override val idField = new LongField(this)

  lazy val valUniqueFindFunc:(String=>Boolean) =
      if(isPersisted)
        (value:String) => headOption(from(MySchema.producers)(p => where(p.id <> id and p.firstName === value) select(p))).isDefined
      else
        (value:String) => Assembly.producerService.findProducerByName(value).isDefined

  val firstName = new StringField(this, 30, "") {
    override lazy val validations = valMinLen(5, "firstName requires at least 5 characters")_ :: valUnique(valUniqueFindFunc, this, "firstName already taken")_ :: super.validations
    override lazy val setFilter = notNull _ :: trim _ :: toLower _ :: net.liftweb.util.StringHelpers.clean _ :: super.setFilter
  }

  val lastName = new OptionalTextareaField(this, 255)

  val description = new OptionalTextareaField(this, 255){
    //override def setFilter = toHtml _ :: super.setFilter
  }

  val created = new DateTimeField(this)
  val updated = new DateTimeField(this)

  lazy val farms = MySchema.producerToFarms.left(this)
}

object Producer extends Producer with MetaRecord[Producer]{ self =>
  def addProducer(firstName:String, lastName:Option[String]=None, description:Option[String]=None) = {
    val ret = Producer.createRecord
                      .firstName(firstName)
                      .lastName(lastName)
                      .description(description)

    doValidate(ret, MySchema.producers.insert(ret))
  }
  def removeProducer(id:Long) { MySchema.producers.deleteWhere(_.id === id) }
  def changeProducer(id:Long, firstName:String, lastName:Option[String]=None, description:Option[String]=None) = {
    def valUniqueFirstnameOnUpdate(id:Long, value:String):Boolean =
      headOption(from(MySchema.producers)(p => where(p.id <> id and (p.firstName === /*value should be cleaned*/value)) select(p))).isDefined

    val firstNameCleaned = runFilters(firstName, Producer.firstName.setFilter)

    if(firstNameCleaned.isEmpty)
      throw ValidationException(List(FieldError(Producer.firstName, scala.xml.Text("firstName is required"))))

    if(!valUniqueFirstnameOnUpdate(id, firstNameCleaned))
      throw ValidationException(List(FieldError(Producer.firstName, scala.xml.Text("firstName already taken"))))

    update(MySchema.producers)(producer =>
      where(producer.id === id)
      set(producer.firstName := firstNameCleaned, producer.lastName := lastName, producer.description := description, producer.updated := net.liftweb.util.TimeHelpers.now)
    )

    MySchema.producers.lookup(id).get
  }
  def findProducer(id:Long) = MySchema.producers.lookup(id)
  def findProducerByName(name:String) = headOption(from(MySchema.producers)(producer => where(producer.firstName === runFilters(name, Producer.firstName.setFilter)) select(producer)))
  def getProducers = MySchema.producers.toList
  
  def addFarm(producer:IProducer, reference:String, description:Option[String]=None) = {
    val ret = Farm.createRecord
                  .reference(reference)
                  .producerId(producer.id)
                  .description(description)

    doValidate(ret, MySchema.farms.insert(ret))
  }

  implicit def toIProducer(producer:Producer):IProducer =
    new IProducer {
      def addFarm(reference:String, description:Option[String]) = self.addFarm(this, reference, description)

      val created = producer.created.is
      val updated = producer.updated.is

      val id = producer.id.toString
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
    def addProducer(firstName:String, lastName:Option[String]=None,  description:Option[String]=None) = Producer.addProducer(firstName, lastName, description)
    def removeProducer(id:String) { Producer.removeProducer(id) }
    def changeProducer(producer:IProducer) = Producer.changeProducer(producer.id, producer.firstName, producer.lastName, producer.description)
    def findProducer(id:String) = Producer.findProducer(id)
    def findProducerByName(name:String) = Producer.findProducerByName(name)
    def getProducers = Producer.getProducers
  }
}

