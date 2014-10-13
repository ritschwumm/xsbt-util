package xsbtUtil.util

import java.nio.charset.Charset

import sbt._

object file {
	def patch(from:File, to:File, charset:Charset = IO.defaultCharset)(func:String=>String):Unit	=
			IO write (to, func(IO read (from, charset)), charset)
}
