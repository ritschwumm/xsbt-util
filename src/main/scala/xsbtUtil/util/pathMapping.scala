package xsbtUtil.util

import sbt._

import xsbtUtil.implicits._

import xsbtUtil.types._

object pathMapping {
	val getFile:PathMapping=>File	= _._1
	val getPath:PathMapping=>Pth	= _._2

	def modifyFile(func:Endo[File]):Endo[PathMapping]	= { case (file, path) => (func(file), path) }
	def modifyPath(func:Endo[Pth]):Endo[PathMapping]	= { case (file, path) => (file, func(path)) }

	// this is Mapper.flat, but total
	val flatFile:File=>PathMapping	=
		_ firstBy (_.getName)

	def anchorTo(targetDir:File):PathMapping=>FileMapping	=
		{ case (file, path) => (file, targetDir / path) }

	def prefixPath(prefix:String):Endo[PathMapping]	=
		modifyPath(prefix + "/" + _)
}
