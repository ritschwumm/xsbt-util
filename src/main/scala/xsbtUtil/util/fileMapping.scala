package xsbtUtil.util

import sbt._

import xsbtUtil.implicits._
import xsbtUtil.types._

object fileMapping {
	val getSource:FileMapping=>File	= first.get
	val getTarget:FileMapping=>File	= second.get

	def modifySource(func:File=>File):Endo[FileMapping]	= first		modify func
	def modifyTarget(func:File=>File):Endo[FileMapping]	= second	modify func

	def flatTo(targetDir:File):File=>FileMapping	=
		_ firstBy (targetDir / _.getName)
}
