package xsbtUtil.util

import sbt._

import xsbtUtil.implicits._

import xsbtUtil.types._

object pathMapping {
	val getFile:PathMapping=>File	= first.get
	val getPath:PathMapping=>Pth	= second.get

	def modifyFile(func:Endo[File]):Endo[PathMapping]	= first	modify	func
	def modifyPath(func:Endo[Pth]):Endo[PathMapping]	= second	modify	func

	// this is Mapper.flat, but total
	val flatFile:File=>PathMapping	=
		_ firstBy (_.getName)

	def anchorTo(targetDir:File):PathMapping=>FileMapping	=
		second modify (targetDir / _)

	def prefixPath(prefix:String):Endo[PathMapping]	=
		modifyPath(prefix + "/" + _)
}
