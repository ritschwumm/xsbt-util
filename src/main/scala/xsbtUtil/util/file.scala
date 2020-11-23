package xsbtUtil.util

import java.nio.charset.Charset

import sbt._
import xsbtUtil.implicits._
import xsbtUtil.types._

object file {
	val guardExists:File=>Option[File]	=
		_ guardBy { _.exists }

	def putName(it:String):Endo[File]	=
		_.getParentFile / it

	def modifyName(func:Endo[String]):Endo[File]	=
		file => putName(func(file.getName))(file)

	def appendSuffix(it:String):Endo[File]	=
		modifyName(_ + it)

	def mkParentDirs(file:File):Unit	=
		parentDir(file) foreach { _.mkdirs() }

	def parentDir(file:File):Option[File]	=
		Option(file.getParentFile)

	/** ancestor chain without the given file itself, from leaf to root */
	def parentDirs(file:File):Seq[File]	= {
		val parent	= file.getParentFile
		if (parent != null)	parent +: parentDirs(parent)
		else				Vector.empty
	}

	/** mirror files into a target directory removing everything not there before */
	def mirror(targetDir:File, assets:Traversable[PathMapping]):Traversable[FileMapping]	= {
		val toCopy	= assets map (pathMapping anchorTo targetDir)
		val copied	= IO copy toCopy
		cleanupDir(targetDir, copied)
		toCopy
	}

	/** deletes everything from dir that is not in the keep set or one of its ancestors */
	def cleanupDir(dir:File, keepFiles:Set[File]):Set[File]	= {
		val all			= (find all dir).toSet
		val keep		= keepFiles filter { _.isFile } flatMap parentDirs
		val obsolete	= all -- keep -- keepFiles
		IO delete obsolete
		obsolete
	}

	def patch(from:File, to:File, charset:Charset = IO.defaultCharset)(func:String=>String):Unit	=
		IO write (to, func(IO read (from, charset)), charset)
}
