import sbt._
import fi.jawsy.sbtplugins.jrebel.{JRebelJarPlugin, JRebelWebPlugin}

class project(info: ProjectInfo) extends ParentProject(info)  {

  //override def compileOptions = super.compileOptions :: Nil

  val scalaToolsSnapshots = ScalaToolsSnapshots
  lazy val jboss = "JBoss repository" at 
    "https://repository.jboss.org/nexus/content/groups/public/"  
  //val scalaTools = "scala-tools-releases-repo" at "http://scala-tools.org/repo-releases"
  /*val squerylRepo = "squeryl-repo" at "http://nexus.scala-tools.org/content/repositories/releases" */

  val LiftVersion        = "2.4-SNAPSHOT"
  val Specs2Version      = "1.0.1"
  val ScalaTestVersion   = "1.3"
  val H2Version          = "1.3.155"
  val Jetty7Version      = "7.3.1.v20110307" //"7.2.2.v20101205" // "7.2.1.v20101111"
  val LogbackVersion     = "0.9.26"
  val SLF4JVersion       = "1.6.1" 
  val DispatchVersion    = "0.8.0"
  val UnfilteredVersion  = "0.3.3" 
  val MockitoVersion     = "1.8.5"

  object Dependencies {

    //Testing
    lazy val scalaTest             = "org.scalatest" % "scalatest" % ScalaTestVersion % "test"
    lazy val specs2                = "org.specs2" %% "specs2" % Specs2Version % "test"
    lazy val mockito               = "org.mockito" % "mockito-all" % MockitoVersion % "test"
    lazy val junit                 = "junit" % "junit" % "4.8.1" % "test"    
    
    //Liftweb
    //lazy val lift_utils            = "net.liftweb" %% "lift-util" % LiftVersion % "compile"
    lazy val lift_webkit           = "net.liftweb" %% "lift-webkit" % LiftVersion % "compile"
    lazy val lift_squeryl_record   = "net.liftweb" %% "lift-squeryl-record" % LiftVersion % "compile"
    lazy val lift_textile          = "net.liftweb" %% "lift-textile" % LiftVersion % "compile"
    lazy val lift_json             = "net.liftweb" %% "lift-json" % LiftVersion % "compile"
    lazy val lift_json_ext         = "net.liftweb" %% "lift-json-ext" % LiftVersion % "compile"    
    //lazy val lift_mapper           = "net.liftweb" %% "lift-mapper" % LiftVersion % "compile" intransitive    
    //lazy val lift_imaging          = "net.liftweb" %% "lift-imaging" % LiftVersion % "compile"
    
    //lazy val lift_oauth            = "net.liftweb" %% "lift-oauth" % LiftVersion % "compile"    
    //lazy val lift_oauth_mapper     = "net.liftweb" %% "lift-oauth-mapper" % LiftVersion % "compile"    

    //Database backend
    lazy val h2database            = "com.h2database" % "h2" % H2Version % "runtime"

    //Hibernate validator
    //lazy val jsr303                = "javax.validation" % "validation-api" % "1.0.0.GA" % "compile"
    //lazy val hibernate_validator   = "org.hibernate" % "hibernate-validator" % "4.1.0.Final" % "compile"        
    
    //Logging
    lazy val slf4j                 = "org.slf4j" % "slf4j-api" % SLF4JVersion % "compile" //MIT
    lazy val logback               = "ch.qos.logback" % "logback-classic" % LogbackVersion % "compile" //LGPL 2.1
    lazy val logback_core          = "ch.qos.logback" % "logback-core" % LogbackVersion % "compile" //LGPL 2.1    

    lazy val dispatch_futures      = "net.databinder" %% "dispatch-futures" % DispatchVersion % "compile"
    lazy val dispatch_oauth        = "net.databinder" %% "dispatch-oauth" % DispatchVersion % "compile"
    lazy val dispatch_http         = "net.databinder" %% "dispatch-http" % DispatchVersion % "compile"
    
    lazy val unfiltered_jetty      = "net.databinder" %% "unfiltered-filter" % UnfilteredVersion % "compile"
    lazy val unfiltered_filter     = "net.databinder" %% "unfiltered-jetty" % UnfilteredVersion % "compile"
    lazy val unfiltered_json       = "net.databinder" %% "unfiltered-json" % UnfilteredVersion % "compile"    
    lazy val uf_spec               = "net.databinder" %% "unfiltered-spec" % UnfilteredVersion % "test"
    
    //lazy val apache_http_client = "org.apache" % "http-client" % DispatchVersion % "compile"
    
    //Jetty Server
    lazy val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % Jetty7Version % "test"
    
    //Squeryl
    //lazy val squeryl = "org.squeryl" % "squeryl-local" % "0.9.4-RC3" % "compile" from ("file://"+Path.userHome+"/.squeryl/squeryl.jar")
  }

  override def parallelExecution = true
    
  // Project Definitions
  lazy val controlProducteursServices             = project("control-producteurs-services", "services", info => new ControlProducteursService(info) )  
  lazy val controlProducteursModel                = project("control-producteurs-model", "model", info => new ControlProducteursModel(info) , controlProducteursServices)
  lazy val controlProducteursRestAPIUnfiltered    = project("control-producteurs-restapi-unfiltered", "restapi-unfiltered", info => new ControlProducteursRestAPIUnfiltered(info) , controlProducteursModel)
  lazy val controlProducteursWebapp               = project("control-producteurs-webapp", "webapp", info => new ControlProducteursWebapp(info) , controlProducteursModel)
    
  class ControlProducteursRestAPIUnfiltered(info: ProjectInfo) extends DefaultProject(info) with Tests with Logging with JRebelJarPlugin{
    val lift_squeryl_record = Dependencies.lift_squeryl_record
    val unfiltered_jetty    = Dependencies.unfiltered_jetty
    val unfiltered_filter   = Dependencies.unfiltered_filter
    val unfiltered_json     = Dependencies.unfiltered_json
    
    val uf_spec = Dependencies.uf_spec

    override def compileOptions = Deprecation :: Unchecked :: super.compileOptions.toList  
  }
  
  class ControlProducteursModel(info: ProjectInfo) extends DefaultProject(info) with Tests with Logging with JRebelJarPlugin{        
    val lift_squeryl_record = Dependencies.lift_squeryl_record
    val textile             = Dependencies.lift_textile

    override def compileOptions = Deprecation :: Unchecked :: super.compileOptions.toList
  }  
  
  class ControlProducteursService(info: ProjectInfo) extends DefaultProject(info) with Tests with Logging with JRebelJarPlugin{        
    override def packageRebelXml = true       
    override def compileOptions = Deprecation :: Unchecked :: super.compileOptions.toList
  }    
  
  class ControlProducteursWebapp(info: ProjectInfo) extends DefaultWebProject(info) with Tests with Logging with JRebelWebPlugin{
    val lift_webkit         = Dependencies.lift_webkit
    val lift_squeryl_record = Dependencies.lift_squeryl_record
    val h2database          = Dependencies.h2database    
    
    val jetty7 = Dependencies.jetty7
    
    //override def jettyWebappPath  = webappPath
    override def scanDirectories = Nil  
    override def compileOptions = Deprecation :: Unchecked :: super.compileOptions.toList
  }     
  
  trait Tests {
    val junit       = Dependencies.junit
    val specs2      = Dependencies.specs2
    val scalatest   = Dependencies.scalaTest
    val mockito     = Dependencies.mockito  
  }
  
  trait Logging{
    val slf4j         = Dependencies.slf4j
    val logback       = Dependencies.logback
    val logback_core  = Dependencies.logback_core  
  }
}

