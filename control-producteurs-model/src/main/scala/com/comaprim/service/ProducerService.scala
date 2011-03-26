package com.comaprim
package service

import model.Producer

trait ProducerRepositoryComponent {
  val producerRepository: ProducerRepository

  trait ProducerRepository {
    def addProducer(firstName: String, lastName: String): Producer
    def removeProducer(id: String): Unit
    def changeProducer(producer: Producer): Producer
    def findProducer(id: String): Option[Producer]
    def getProducers(): List[Producer]
  }
}

trait ProducerService {
  def addProducer(firstName: String, lastName: String): Producer
  def removeProducer(id: String): Unit
  def changeProducer(producer: Producer): Producer
  def findProducer(id: String): Option[Producer]
  def getProducers(): List[Producer]
}

trait ProducerServiceComponent { self: ProducerRepositoryComponent =>
  val producerService: ProducerService

  class DefaultProducerService extends ProducerService {
    def addProducer(firstName: String, lastName: String): Producer = producerRepository.addProducer(firstName, lastName)
    def removeProducer(id: String): Unit = producerRepository.removeProducer(id)
    def changeProducer(producer: Producer): Producer = producerRepository.changeProducer(producer)
    def findProducer(id: String): Option[Producer] = producerRepository.findProducer(id)
    def getProducers(): List[Producer] = producerRepository.getProducers
  }
}