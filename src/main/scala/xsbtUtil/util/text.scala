package xsbtUtil.util

import scala.annotation.tailrec

import sbt._

import xsbtUtil.implicits._
import xsbtUtil.types._

object text {
	private val StripRE	= """^\s*\|\t(.*)$""".r

	def stripped(s:String):String	=
		s.getLines collect { case StripRE(it) => it } mkString "\n"

	def untab(width:Int):Endo[String]	=
		s => {
			@tailrec
			def loop(text:String, width:Int, index:Int, col:Int, out:String):String =
				if (index == text.length) out
				else text charAt index match {
					case '\r'	=> loop(text, width, index+1, 0,		out + "\r")
					case '\n'	=> loop(text, width, index+1, 0,		out + "\n")
					case '\t'	=> loop(text, width, index+1, 0, 		out + (" " * (width-(col%width))))
					case x		=> loop(text, width, index+1, col+1,	out + x.toString)
				}
			loop(s, width, 0, 0, "")
		}
}
