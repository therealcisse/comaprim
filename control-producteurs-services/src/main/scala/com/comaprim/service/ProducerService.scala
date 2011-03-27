package com.comaprim
package service

trait ProducerRepositoryComponent {
  val producerRepository: ProducerRepository

  trait ProducerRepository {
    def addProducer(firstName:String, lastName:String, description:String):IProducer
    def removeProducer(id: String)
    def changeProducer(producer:IProducer):IProducer
    def findProducer(id:String):Option[IProducer]
    def getProducers:List[IProducer]
  }
}

trait ProducerService {
  def addProducer(firstName:String, lastName:String, description:String):IProducer
  def removeProducer(id: String)
  def changeProducer(producer:IProducer):IProducer
  def findProducer(id:String):Option[IProducer]
  def getProducers:List[IProducer]
}

trait ProducerServiceComponent { self: ProducerRepositoryComponent =>
  val producerService: ProducerService

  class DefaultProducerService extends ProducerService {
    def addProducer(firstName:String, lastName:String,  description:String) = producerRepository.addProducer(firstName, lastName, description)
    def removeProducer(id:String) { producerRepository.removeProducer(id) }
    def changeProducer(producer:IProducer) = producerRepository.changeProducer(producer)
    def findProducer(id:String) = producerRepository.findProducer(id)
    def getProducers = producerRepository.getProducers
  }
}