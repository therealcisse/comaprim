package com.comaprim
package model

import org.squeryl.annotations.Column

import net.liftweb.squerylrecord.RecordTypeMode._
import net.liftweb.squerylrecord._
import net.liftweb.record.field._
import net.liftweb.record.{MetaRecord, Record}

import service.{IControl, IProducer, ICulture, ControlType}

class Control private () extends Record[Control] with KeyedRecord[Long] {
  override def meta = Control

  @Column(name = "id")
  override val idField = new LongField(this)

  val producerId  = new LongField(this)
  val cultureId   = new LongField(this)
  val temperature = new DecimalField(this, BigDecimal(0))
  val production  = new DecimalField(this, BigDecimal(0))
  val controlType = new EnumNameField(this, ControlType, ControlType.Unknown)
  val localPrice  = new DecimalField(this, BigDecimal(0))
  val exportPrice = new DecimalField(this, BigDecimal(0))
  val date        = new DateTimeField(this)
}

object Control extends Control with MetaRecord[Control] {

  def addControl(producer:IProducer, culture:ICulture, production:Double, temperature:Double, localPrice:Double, exportPrice:Double, controlType:ControlType.Value=ControlType.Unknown, date:java.util.Calendar) = MySchema.controls.insert(Control.createRecord.producerId(producer.id).cultureId(culture.id).production(production).temperature(temperature).controlType(controlType).localPrice(localPrice).exportPrice(exportPrice).date(date))
  def removeControl(id:Long) { MySchema.controls.deleteWhere(_.id === id) }
  def findControl(id:Long) = MySchema.controls.lookup(id)
  def getControls = MySchema.controls.toList
  def getControls(producer:Option[IProducer]=None, culture:Option[ICulture]=None) = from(MySchema.controls)(control => where(control.producerId === producer.map(_.id).? and control.cultureId === culture.map(_.id).?) select(control)).toList
  
  implicit def toIControl(control:Control):IControl =
    new IControl {
      val id = control.id
      val temperature = control.temperature.is
      val production = control.production.is
      //val controlType = control.controlType.is
      val localPrice = control.localPrice.is
      val exportPrice = control.exportPrice.is
      val date = control.date.is

      val producerId = control.producerId.is
      val cultureId = control.cultureId.is
    }  
}

//---------------------------------- IMPLEMENTATIONS ----------------------------------------------------------------------

import service.{ControlRepositoryComponent, ControlServiceComponent}

trait ControlAssembly extends ControlServiceComponent with ControlRepositoryComponent {
  
  implicit def toIControl(it:List[Control])(implicit toIControl: Control=>IControl):List[IControl] = it map toIControl
  implicit def toIControl(it:Option[Control])(implicit toIControl: Control=>IControl) = it map toIControl  

  class DefaultControlRepository extends ControlRepository {
    def addControl(producer:IProducer, culture:ICulture, production:Double, temperature:Double, localPrice:Double, exportPrice:Double, controlType:ControlType.Value=ControlType.Unknown, date:java.util.Calendar) = Control.addControl(producer, culture, production, temperature, localPrice, exportPrice, controlType, date)
    def removeControl(id:String)  { Control.removeControl(id) }
    def findControl(id:String) = Control.findControl(id)
    def getControls:List[IControl] = Control.getControls
    def getControls(producer:Option[IProducer]=None, culture:Option[ICulture]=None) = Control.getControls(producer, culture)
  }
}