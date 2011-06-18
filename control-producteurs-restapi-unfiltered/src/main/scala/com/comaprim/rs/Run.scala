package com.comaprim
package rs

import net.liftweb.common.LazyLoggable

object Run extends LazyLoggable{
  def main(args:Array[String]):Unit = {
    logger.info("starting unfiltered app at localhost on port %s" format 8080)
    unfiltered.jetty.Http(8080).context("/api") { builder =>
      builder.filter(new ProducerHandler)
      //builder.filter(new FarmHandler)
      //builder.filter(new SectorHandler)
      //builder.filter(new CultureHandler)
      //builder.filter(new VarietyHandler)
      //builder.filter(new ControlHandler)
    }.run
  }
}

