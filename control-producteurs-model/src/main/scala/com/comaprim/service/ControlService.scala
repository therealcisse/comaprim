package com.comaprim
package service

import model.{Control, ControlType, Culture, Producer}

trait ControlRepositoryComponent {
  val controlRepository: ControlRepository

  trait ControlRepository {
    def addControl(producer:Producer, culture:Culture, production:Double, temperature:Double, localPrice:Double, exportPrice:Double, `type`:ControlType.Value=ControlType.Unknown, date:java.sql.Timestamp=new java.sql.Timestamp(System.currentTimeMillis)): Control
    def removeControl(id: String): Unit
    def findControl(id: String): Option[Control]
    def getControls(): List[Control]
    def getControls(producer:Option[Producer]=None, culture:Option[Culture]=None): List[Control]
  }
}

trait ControlService {
  def addControl(producer:Producer, culture:Culture, production:Double, temperature:Double, localPrice:Double, exportPrice:Double, `type`:ControlType.Value=ControlType.Unknown, date:java.sql.Timestamp=new java.sql.Timestamp(System.currentTimeMillis)): Control
  def removeControl(id: String): Unit
  def findControl(id: String): Option[Control]
  def getControls(): List[Control]
  def getControls(producer:Option[Producer]=None, culture:Option[Culture]=None): List[Control]
}

trait ControlServiceComponent { self: ControlRepositoryComponent =>
  val controlService: ControlService

  class DefaultControlService extends ControlService {
    def addControl(producer:Producer, culture:Culture, production:Double, temperature:Double, localPrice:Double, exportPrice:Double, `type`:ControlType.Value=ControlType.Unknown, date:java.sql.Timestamp=new java.sql.Timestamp(System.currentTimeMillis)): Control = controlRepository.addControl(producer, culture, production, temperature, localPrice, exportPrice, `type`, date)
    def removeControl(id: String): Unit = controlRepository.removeControl(id)
    def findControl(id: String): Option[Control] = controlRepository.findControl(id)
    def getControls(): List[Control] = controlRepository.getControls
    def getControls(producer:Option[Producer]=None, culture:Option[Culture]=None): List[Control] = controlRepository.getControls(producer, culture)
  }
}