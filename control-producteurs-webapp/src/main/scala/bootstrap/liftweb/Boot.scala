package bootstrap.liftweb

import _root_.net.liftweb.http._
import net.liftweb.db.{DefaultConnectionIdentifier, StandardDBVendor, DB}

import org.squeryl.adapters.H2Adapter

import net.liftweb.common.{Box, Loggable}
import net.liftweb.util.{Helpers, Props}

import net.liftweb.squerylrecord.SquerylRecord

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Loggable {

  def boot {
    logger.debug("Starting Boot.boot")

    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    //Props.whereToLook =
      //() => Tuple2[String, ()=>Box[java.io.InputStream]]("default.props", () => Helpers.tryo{getClass.getResourceAsStream("/etc/default.props")}.filter(_ ne null)) :: Nil

    if (!DB.jndiJdbcConnAvailable_?) {      
      DB.defineConnectionManager(DefaultConnectionIdentifier, Database)
      LiftRules.unloadHooks.append(() => Database.closeAllConnections_!)
      
      import net.liftweb.squerylrecord.RecordTypeMode._

      DB.use(DefaultConnectionIdentifier) { _ =>
        try {
          //TestSchema.drop // we normally *NEVER* do this !!
        } catch {
          case e:java.sql.SQLException => println("schema does not yet exist :" + e.getMessage)
        }
        
        //TestSchema.create
      }      
    }
    
    SquerylRecord.init(() => new H2Adapter)
    S.addAround(DB.buildLoanWrapper)
    
    logger.debug("Done Boot.boot")
  }
}

object Database extends
  StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
    Props.get("db.url") openOr "jdbc:h2:tcp://localhost/~/comaprim",
    Props.get("db.user"), Props.get("db.password"))
