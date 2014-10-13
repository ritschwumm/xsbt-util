package xsbtUtil.util

import java.util.jar.{ Manifest => JarManifest }
import java.util.jar.Attributes.{ Name => AttrName }

import xsbtUtil.types._

object jar {
	val manifestPath:Pth	= "META-INF/MANIFEST.MF"
	
	def manifest(attrs:(String,String)*):JarManifest	= {
		val manifest	= new JarManifest
		attrs foreach { case (k, v) =>
			manifest.getMainAttributes put (new AttrName(k), v)
		}
		manifest
	}
}
