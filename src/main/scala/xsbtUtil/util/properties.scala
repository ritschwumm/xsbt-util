package xsbtUtil.util

import java.util.Properties
import java.io._

import scala.collection.JavaConverters._

object properties {
	def loadFile(file:File):Map[String,String]	= {
		val ist	= new FileInputStream(file)
		try {
			val out	= new Properties
			out load ist
			out.asScala.toMap
		}
		finally {
			ist.close()
		}
	}
}
