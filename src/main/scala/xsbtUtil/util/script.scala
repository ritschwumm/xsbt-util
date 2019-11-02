package xsbtUtil.util

object script {
	def unixHardQuote(s:String):String	=
			"'" + (s replace ("'", "\\'")) + "'"

	def unixSoftQuote(s:String):String	=
			"\"" + (s replace ("\"", "\\\"")) + "\""

	def windowsQuote(s:String):String	=
			"\"" + (s replace ("\"", "\"\"")) + "\""

	def windowsLF(s:String):String	=
			s replace ("\n", "\r\n")

	//------------------------------------------------------------------------------

	def systemProperties(it:Map[String,String]):Seq[String]	=
			it.toVector map systemProperty

	val systemProperty:((String,String))=>String	=
			{ case (key:String, value:String) => s"-D${key}=${value}" }
}
