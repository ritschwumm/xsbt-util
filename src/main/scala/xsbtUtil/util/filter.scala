package xsbtUtil.util

import sbt._

import xsbtUtil.types._

object filter {
	val JarManifestFilter	= new ExactFilter(jar.manifestPath)
	val NotDirectoryFilter	= -DirectoryFilter

	val isFile:Predicate[File]		= _.isFile
	val isDirectory:Predicate[File]	= _.isDirectory

	def filePredicate(filter:FileFilter):File=>Boolean	=
		filter.accept

	def fileFilter(predicate:File=>Boolean):FileFilter	=
		new SimpleFileFilter(predicate)
}
