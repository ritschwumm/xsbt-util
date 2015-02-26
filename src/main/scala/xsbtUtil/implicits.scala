package xsbtUtil

import sbt._

import xsbtUtil.types._
import xsbtUtil.util.file

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
		def guardExists:Option[File]			= file guardExists	peer
		
		def putName(it:String):File				= file putName		it		apply peer
		def modifyName(func:Endo[String]):File	= file modifyName	func	apply peer
		def appendSuffix(it:String):File		= file appendSuffix	it		apply peer
		
		def parentDir:Option[File]				= file parentDir	peer
		def parentDirs:Seq[File]				= file parentDirs	peer
		
		def mkParentDirs():Unit					= file mkParentDirs	peer
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
