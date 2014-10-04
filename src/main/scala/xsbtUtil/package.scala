import sbt._

import java.util.jar.{ Manifest => JarManifest }
import java.util.jar.Attributes.{ Name => AttrName }

import Keys.TaskStreams

package object xsbtUtil {
	//------------------------------------------------------------------------------
	//## pair
	
	def getFirst[A,B](it:(A,B)):A	= it._1
	def getSecond[A,B](it:(A,B)):B	= it._2
	
	def modifyFirst[A,B,X](func:A=>X):((A,B))=>(X,B)	=
			it => (func(it._1), it._2)
		
	def modifySecond[A,B,X](func:B=>X):((A,B))=>(A,X)	=
			it => (it._1, func(it._2))
		
	//------------------------------------------------------------------------------
	//## task
	
	def chainTasks[T](key:SettingKey[Seq[TaskKey[T]]]):Def.Initialize[Task[Seq[T]]]	= {
		def sequence[T](keys:Seq[TaskKey[T]], accu:Seq[T] = Vector.empty):Def.Initialize[Task[Seq[T]]]	=
				Def.taskDyn { 
					keys match {
						case head +: tail	=> Def.taskDyn { sequence(tail, accu :+ head.value) }
						case _				=> Def.task { accu }
					}
				}
		Def.taskDyn { sequence(key.value) }
	}
	
	//------------------------------------------------------------------------------
	//## error
	
	object FailureException extends FeedbackProvidedException
	
	def failWithError(streams:TaskStreams, message:String):Nothing	= {
		streams.log error message
		throw FailureException
	}
	
	//------------------------------------------------------------------------------
	//## filter
	
	val JarManifestFilter	= new ExactFilter("META-INF/MANIFEST.MF")
	val NotDirectoryFilter	= -DirectoryFilter
	
	def fileFilterToPredicate(filter:FileFilter):File=>Boolean	=
			filter.accept
		
	def predicateToFileFilter(predicate:File=>Boolean):FileFilter	=
			new FileFilter {
				def accept(it:File)	= predicate(it)
			}
			
	/*
	implicit class FileFilter(peer:FileFilter) {
		def toPredicate:File=>Boolean	= fileFilterToPredicate(peer)
	}
	*/
	
	//------------------------------------------------------------------------------
	//## find
	
	/** like allSubpaths but without directories or flattening */
	def filePathMappingsIn(dir:File):Seq[PathMapping]	=
			filesIn(dir) pair (Path relativeTo dir)
		
	/** only files */
	def filesIn(dir:File):Seq[File]	=
			(dir ** NotDirectoryFilter).get
		
	/** like allSubpaths but without flattening */
	def allPathMappingsIn(dir:File):Seq[PathMapping]	=
			allIn(dir) pair (Path relativeTo dir)
		
	/** everything but the root directory */
	def allIn(dir:File):Seq[File]	=
			(dir.*** --- PathFinder(dir)).get
	
	//------------------------------------------------------------------------------
	//## mapping
	
	type Path	= String
	
	type PathMapping	= (File, Path)
	object PathMapping {
		val getFile:PathMapping=>File	= _._1
		val getPath:PathMapping=>Path	= _._2
		
		def modifyFile(func:File=>File):PathMapping=>PathMapping	= modifyFirst(func)
		def modifyPath(func:Path=>Path):PathMapping=>PathMapping	= modifySecond(func)
		
		def flat(it:File):PathMapping	=
				(it, it.getName)
		
		def anchorTo(targetDir:File):PathMapping=>FileMapping	=
				modifySecond(targetDir / _)
	}
	
	type FileMapping	= (File, File)
	object FileMapping {
		val getSource:FileMapping=>File	= _._1
		val getTarget:FileMapping=>File	= _._2
		
		def modifySource(func:File=>File):FileMapping=>FileMapping	= modifyFirst(func)
		def modifyTarget(func:File=>File):FileMapping=>FileMapping	= modifySecond(func)
		
		def flatTo(targetDir:File)(it:File):FileMapping	=
				(it, targetDir / it.getName)
	}
	
	//------------------------------------------------------------------------------
	//## file
	
	implicit class FileExt(peer:File) {
		def mkParentDirs():Unit			= peer.getParentFile.mkdirs()
		def guardExists:Option[File]	= if (peer.exists)	Some(peer)	else None
	}
	
	//------------------------------------------------------------------------------
	//## string
	
	implicit class StringExt(peer:String) {
		private val Strip	= """^\s*\|\t(.*)$""".r
		
		def strippedLines:String	=
				getLines collect { case Strip(it) => it } mkString "\n"
		
		def getLines:Seq[String]	=
				(peer:scala.collection.immutable.WrappedString).lines.toVector
	}
	
	//------------------------------------------------------------------------------
	//## jar
	
	def jarManifest(attrs:(String,String)*):JarManifest	= {
		val manifest	= new JarManifest
		attrs foreach { case (k, v) =>
			manifest.getMainAttributes put (new AttrName(k), v)
		}
		manifest
	}
	
	//------------------------------------------------------------------------------
	//## resource
	
	def resourceURL(path:Path):URL	=
			Option(getClass getResource path) getOrElse { sys error s"resource not found: ${path}" }
		
	def resourceBytes(path:Path):Array[Byte]	=
			(Using urlInputStream resourceURL(path))(IO.readBytes)
		
	//------------------------------------------------------------------------------
	//## scripting
	
	def unixHardQuote(s:String):String	= 
			"'" + (s replace ("'", "\\'")) + "'"
			
	def unixSoftQuote(s:String):String	= 
			"\"" + (s replace ("\"", "\\\"")) + "\""
			
	def windowsQuote(s:String):String	= 
			"\"" + (s replace ("\"", "\"\"")) + "\""
		
	def windowsLF(s:String):String	= 
			s replace ("\n", "\r\n")
		
	def scriptSystemPropertyMap(it:Map[String,String]):Seq[String]	=
			it.toVector map scriptSystemProperty
	
	val scriptSystemProperty:((String,String))=>String	= 
			{ case (key:String, value:String) => s"-D${key}=${value}" }
		
	//------------------------------------------------------------------------------
	//## permissions
	
	def parseOctal(s:String):Int	= Integer parseInt (s, 8)
}
