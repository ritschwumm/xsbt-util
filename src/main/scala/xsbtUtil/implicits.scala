package xsbtUtil

import sbt._

import xsbtUtil.types._

object implicits {
	implicit class AnyExt[T](peer:T) {
		def firstBy[U](func:T=>U):(T,U)		= (peer, func(peer))
		def secondBy[U](func:T=>U):(U,T)	= (func(peer), peer)
		
		def guardBy(pred:Predicate[T]):Option[T]	=
				if (pred(peer))	Some(peer)
				else			None
			
		def preventBy(pred:Predicate[T]):Option[T]	=
				guardBy { it => !pred(it) }
	}
	
	implicit class StringExt(peer:String) {
		def getLines:Seq[String]	=
				(peer:scala.collection.immutable.WrappedString).lines.toVector
	}
	
	implicit class FileExt(peer:File) {
		def putName(it:String):File				= peer.getParentFile / it
		def modifyName(func:Endo[String]):File	= putName(func(peer.getName))
		
		def appendSuffix(it:String):File		= modifyName(_ + it)
		
		def mkParentDirs():Unit			= peer.getParentFile.mkdirs()
		def guardExists:Option[File]	= peer guardBy { _.exists }
	}
	
	// TODO this is Using.resource
	implicit class AutoCloseableExt[S<:AutoCloseable](peer:S) {
		def use[T](func:S=>T):T	= {
			var thrown	= false
			try {
				func(peer)
			}
			catch { case e:Throwable	=> 
				thrown	= true
				throw e
			}
			finally {
				try { 
					peer.close()
				}
				catch { case e:Throwable	=> 
					if (!thrown)	throw e
				}
			}
		}
	}
}
