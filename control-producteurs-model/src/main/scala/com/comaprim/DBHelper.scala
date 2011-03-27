package com.comaprim

import org.squeryl.Session
import org.squeryl.adapters.H2Adapter

import net.liftweb.squerylrecord.RecordTypeMode._
import net.liftweb.squerylrecord.SquerylRecord

import java.sql.DriverManager

object DBHelper {

  def initSquerylRecord() {
    SquerylRecord.initWithSquerylSession {
      val session = Session.create(DriverManager.getConnection("jdbc:h2:tcp:localhost/comaprim;DB_CLOSE_DELAY=-1"), new H2Adapter)
      session.setLogger(println)
      session
    }
  }
  
  def main(args:Array[String]) {
    initSquerylRecord()
    createSchema()
  }

  /**
   * Creates the test schema in a new transaction. Drops an old schema if
   * it exists.
   */
  def createSchema() {
    inTransaction {
    	try {
	      //MySchema.printDdl
	      MySchema.dropAndCreate()
	      MySchema.createTestData()
    	} catch {
    		case e => e.printStackTrace()
    		  throw e;
    	}
    }
  }
}
