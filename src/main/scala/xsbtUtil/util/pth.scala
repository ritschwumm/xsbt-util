package xsbtUtil.util

import sbt._

import xsbtUtil.types._

object pth {
	def prefix(pth:Pth):Endo[Pth]	= pth + "/" + _
}
