import sbt._
import fi.jawsy.sbtplugins.jrebel._

class project(info: ProjectInfo) extends ParentProject(info) with IdeaProject {

  //override def compileOptions = super.compileOptions :: Nil

  val scalaToolsSnapshots = ScalaToolsSnapshots
  //val scalaTools = "scala-tools-releases-repo" at "http://scala-tools.org/repo-releases"
  /*val squerylRepo = "squeryl-repo" at "http://nexus.scala-tools.org/content/repositories/releases" */

  val LiftVersion        = "2.3-SNAPSHOT"
  val Specs2Version       = "1.0.1"
  val ScalaTestVersion   = "1.3"
  val H2Version          = "1.3.153"
  val Jetty7Version      = "7.3.1.v20110307" //"7.2.2.v20101205" // "7.2.1.v20101111"
  val LogbackVersion     = "0.9.26"
  val SLF4JVersion       = "1.6.1" 
  val DispatchVersion    = "0.8.0.Beta2" 
  val MockitoVersion     = "1.8.5"

  object Dependencies {

    //Testing
    lazy val scalaTest             = "org.scalatest" %% "scalatest" % ScalaTestVersion % "test"
    lazy val specs2                = "org.specs2" %% "specs2" % Specs2Version % "test"
    lazy val mockito               = "org.mockito" % "mockito-all" % MockitoVersion % "test"
    lazy val junit                 = "junit" % "junit" % "4.8.1" % "test"    
    
    //Liftweb
    //lazy val lift_utils            = "net.liftweb" %% "lift-util" % LiftVersion % "compile" withSources
    lazy val lift_webkit           = "net.liftweb" %% "lift-webkit" % LiftVersion % "compile" withSources
    lazy val lift_squeryl_record   = "net.liftweb" %% "lift-squeryl-record" % LiftVersion % "compile" withSources
    lazy val lift_json             = "net.liftweb" %% "lift-json" % LiftVersion % "compile" withSources
    lazy val lift_json_ext         = "net.liftweb" %% "lift-json-ext" % LiftVersion % "compile" withSources    
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
    
    //lazy val apache_http_client = "org.apache" % "http-client" % DispatchVersion % "compile"
    
    //Jetty Server
    lazy val jetty7                = "org.eclipse.jetty" % "jetty-webapp" % Jetty7Version % "test"
    
    //Squeryl
    //lazy val squeryl = "org.squeryl" % "squeryl-local" % "0.9.4-RC3" % "compile" from ("file://"+Path.userHome+"/.squeryl/squeryl.jar")
  }

  override def parallelExecution = true
    
  // Project Definitions
  lazy val controlProducteursModel = project("control-producteurs-model", "control-producteurs-model", info => new ControlProducteursModel(info) with IdeaProject)
  lazy val controlProducteurs      = project("control-producteurs-webapp", "control-producteurs-webapps", info => new ControlProducteursWebapp(info) with IdeaProject, controlProducteursModel)
  
  class ControlProducteursModel(info: ProjectInfo) extends DefaultProject(info){    
    val specs2               = Dependencies.specs2
    val mockito              = Dependencies.mockito
    
    val lift_squeryl_record = Dependencies.lift_squeryl_record
    
    val slf4j         = Dependencies.slf4j
    val logback       = Dependencies.logback
    val logback_core  = Dependencies.logback_core

    override def compileOptions = Unchecked :: super.compileOptions.toList
  }  
  
  class ControlProducteursWebapp(info: ProjectInfo) extends DefaultWebProject(info) with JRebelWebPlugin{
    val specs2              = Dependencies.specs2
    val mockito             = Dependencies.mockito

    val lift_webkit         = Dependencies.lift_webkit
    val lift_squeryl_record = Dependencies.lift_squeryl_record
    val h2database          = Dependencies.h2database    

    val junit               = Dependencies.junit
    val jetty7              = Dependencies.jetty7
    
    val slf4j         = Dependencies.slf4j
    val logback       = Dependencies.logback
    val logback_core  = Dependencies.logback_core
    
    //override def jettyWebappPath  = webappPath
    override def scanDirectories = Nil  

    override def compileOptions = Deprecation :: Unchecked :: super.compileOptions.toList
  }      
}

