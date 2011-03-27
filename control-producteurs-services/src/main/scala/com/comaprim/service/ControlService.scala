package com.comaprim
package service

trait ControlRepositoryComponent {
  val controlRepository: ControlRepository

  trait ControlRepository {
    def addControl(producer:IProducer, culture:ICulture, production:Double, temperature:Double, localPrice:Double, exportPrice:Double, `type`:ControlType.Value=ControlType.Unknown, date:java.util.Calendar):IControl
    def removeControl(id:String)
    def findControl(id:String): Option[IControl]
    def getControls:List[IControl]
    def getControls(producer:Option[IProducer]=None, culture:Option[ICulture]=None):List[IControl]
  }
}

trait ControlService {
    def addControl(producer:IProducer, culture:ICulture, production:Double, temperature:Double, localPrice:Double, exportPrice:Double, `type`:ControlType.Value=ControlType.Unknown, date:java.util.Calendar):IControl
    def removeControl(id:String)
    def findControl(id:String): Option[IControl]
    def getControls:List[IControl]
    def getControls(producer:Option[IProducer]=None, culture:Option[ICulture]=None):List[IControl]
}

trait ControlServiceComponent { self: ControlRepositoryComponent =>
  val controlService: ControlService

  class DefaultControlService extends ControlService {
    def addControl(producer:IProducer, culture:ICulture, production:Double, temperature:Double, localPrice:Double, exportPrice:Double, `type`:ControlType.Value=ControlType.Unknown, date:java.util.Calendar) = controlRepository.addControl(producer, culture, production, temperature, localPrice, exportPrice, `type`, date)
    def removeControl(id: String)  { controlRepository.removeControl(id) }
    def findControl(id: String) = controlRepository.findControl(id)
    def getControls = controlRepository.getControls
    def getControls(producer:Option[IProducer]=None, culture:Option[ICulture]=None) = controlRepository.getControls(producer, culture)
  }
}