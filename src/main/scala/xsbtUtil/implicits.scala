package xsbtUtil

import scala.collection.immutable.{ Seq => ISeq }

import sbt._

import xsbtUtil.types._
import xsbtUtil.data._
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

		def nes:Nes[T]	=
			Nes one peer
	}

	implicit class BooleanExt(peer:Boolean) {
		def safeGuard[F](problems: =>Nes[F]):Safe[F,Unit]	=
			if (peer)	Safe win (())
			else		Safe fail problems

		def safePrevent[F](problems: =>Nes[F]):Safe[F,Unit]	=
			if (peer)	Safe fail problems
			else		Safe win (())
	}

	implicit class OptionExt[T](peer:Option[T]) {
		def getOrError(s:String)	= peer getOrElse (sys error s)

		def cata[X](none: => X, some:T => X):X =
			peer match {
				case Some(x)	=> some(x)
				case None		=> none
			}

		def toSafe[F](problems: =>Nes[F]):Safe[F,T]	=
			peer match {
				case Some(x)	=> Safe win x
				case None		=> Safe fail problems
			}
	}

	implicit class EitherToSafeExt[E,T](peer:Either[Nes[E],T]) {
		def toSafe:Safe[E,T]	= Safe fromEither peer
	}

	implicit class ISeqExt[T](peer:ISeq[T]) {
		def preventing[W](value: =>W):Safe[T,W]	=
			Nes fromISeq peer map Safe.fail getOrElse (Safe win value)

		def traverseSafe[F,U](func:T=>Safe[F,U]):Safe[F,ISeq[U]]	=
			Safe traverseISeq func apply peer

		def sequenceSafe[F,U](implicit ev:T <:< Safe[F,U]):Safe[F,ISeq[U]]	=
			traverseSafe(ev)

		def toNesOption:Option[Nes[T]]	=
			Nes fromISeq peer

		def zipBy[U](func:T=>U):ISeq[(T,U)]	=
			peer map { it =>
				it	-> func(it)
			}
	}

	implicit class StringContextExt(peer:StringContext) {
		def so(args:String*):String		= peer.s(args:_*)
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

	// NOTE this is quite similar to Using.resource
	implicit class AutoCloseableExt[S<:AutoCloseable](peer:S) {
		def use[T](func:S=>T):T	= {
			var primary:Throwable	= null
			try {
				func(peer)
			}
			catch { case e:Throwable	=>
				primary	= e
				throw e
			}
			finally {
				try {
					peer.close()
				}
				catch { case e:Throwable	=>
					if (primary ne null)	primary addSuppressed e
					else					throw e
				}
			}
		}
	}
}
