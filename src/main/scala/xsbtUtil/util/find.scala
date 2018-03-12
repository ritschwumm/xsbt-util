package xsbtUtil.util

import sbt._

import xsbtUtil.types._
import xsbtUtil.util.filter._
import xsbtUtil.platform._

object find {
	/** like allSubpaths but without directories or flattening */
	def filesMapped(dir:File):Seq[PathMapping]	=
			files(dir) pair (Path relativeTo dir)
		
	/** only files */
	def files(dir:File):Seq[File]	=
			(dir ** NotDirectoryFilter).get
		
	/** like allSubpaths but without flattening */
	def allMapped(dir:File):Seq[PathMapping]	=
			all(dir) pair (Path relativeTo dir)
		
	/** everything but the root directory */
	def all(dir:File):Seq[File]	=
			(PathFinder(dir).allPaths --- PathFinder(dir)).get
}
