package xsbtUtil.util

import java.io.FileInputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry

import org.apache.commons.compress.archivers.zip._

import sbt._

import xsbtUtil.implicits._
import xsbtUtil.types._

object zip {
	object mode {
		val file		= parse octal "100000"
		val directory	= parse octal "040000"
		val link		= parse octal "120000"
		
		val rwxr_xr_x	= parse octal "755"
		val rw_r__r__	= parse octal "644"
	}
	
	/** paths must use a forward slash, unix mode is optional, symlinks are ignored */
	def create(sources:Traversable[PathMapping], outputZip:File):Unit	=
			new ZipArchiveOutputStream(outputZip) use { outputStream => 
				// outputStream	setMethod	ZipOutputStream.DEFLATED
				// outputStream	setLevel	0
				val sourceDirectories		= sources filter (pathMapping.getFile andThen filter.isDirectory)
				val sourceFiles				= sources filter (pathMapping.getFile andThen filter.isFile)
				
				// ensure every file has a parent directory
				val sourceDirectoryPaths	= sourceDirectories map pathMapping.getPath map { _ + "/" }
				val sourceParentPaths		= sourceFiles		map pathMapping.getPath flatMap pathDirs
				val zipDirectories			= (sourceDirectoryPaths ++ sourceParentPaths).toVector.distinct
				
				val now			= System.currentTimeMillis
				val emptyCRC	= (new CRC32).getValue
				
				zipDirectories foreach { path =>
					val entry	= new ZipArchiveEntry(path)
					entry	setMethod	ZipEntry.STORED
					entry	setSize		0
					entry	setTime		now
					entry	setCrc		emptyCRC
					entry	setUnixMode	(mode.directory | mode.rwxr_xr_x)
					outputStream  	putArchiveEntry entry
					outputStream.closeArchiveEntry()
				}
				
				sourceFiles foreach { case (file, path) => 
					val entry	= new ZipArchiveEntry(path)
					entry	setMethod	ZipEntry.STORED
					entry	setSize		file.length
					entry	setTime		file.lastModified
					entry	setUnixMode	(mode.file | (if (file.canExecute) mode.rwxr_xr_x else mode.rw_r__r__))
					outputStream  	putArchiveEntry	entry
					
					new FileInputStream(file) use { inputStream =>
						IO transfer (inputStream, outputStream)
					}
					
					outputStream.closeArchiveEntry()
				}
				
				outputZip
			}
	
	def pathDirs(path:String):Seq[String]	= 
			(path split "/").init.inits.toList.init.reverse map { _ mkString ("", "/", "/") }
}                                                                         
