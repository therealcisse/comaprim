package com.comaprim
package rs

import unfiltered.response._

case class Xml(node:scala.xml.NodeSeq) extends ChainResponse(TextXmlContent ~> ResponseString(node.toString))