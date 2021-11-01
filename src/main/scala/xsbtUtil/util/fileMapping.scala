package xsbtUtil.util

import sbt._

import xsbtUtil.implicits._
import xsbtUtil.types._

object fileMapping {
	val getSource:FileMapping=>File	= _._1
	val getTarget:FileMapping=>File	= _._2

	def modifySource(func:File=>File):Endo[FileMapping]	= { case (source, target) => (func(source), target) }
	def modifyTarget(func:File=>File):Endo[FileMapping]	= { case (source, target) => (source, func(target)) }

	def flatTo(targetDir:File):File=>FileMapping	=
		_ firstBy (targetDir / _.getName)
}
