package xsbtUtil.util

import java.io._
import java.nio.charset.Charset

import sbt._

import xsbtUtil.implicits._
import xsbtUtil.types._
import xsbtUtil.platform._

object classpath {
	private val defaultCharset	= IO.defaultCharset
	
	def url(path:Pth):URL	=
			Option(getClass getResource path) getOrElse { sys error s"resource not found: ${path}" }
		
	def stream(path:Pth):InputStream	=
			url(path).openStream
		
	def reader(path:Pth, charset:Charset = defaultCharset):BufferedReader	=
			new BufferedReader(new InputStreamReader(stream(path), charset))
		
	def bytes(path:Pth):Array[Byte]	=
			stream(path) use IO.readBytes
		
	def string(path:Pth, charset:Charset = defaultCharset):String	=
			 (Using urlInputStream url(path))(IO.readStream(_, charset))
		
	def lines(path:Pth, charset:Charset = defaultCharset):Seq[String]	=
			reader(path, charset) use (IO readLines (_))
}
