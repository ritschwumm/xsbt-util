package xsbtUtil.data

import scala.collection.immutable.{ Seq => ISeq }

object Safe {
	def win[F,W](value:W):Safe[F,W]	=
		new Safe[F,W] {
			def cata[X](fail:Nes[F]=>X, win:W=>X):X	= win(value)
		}

	def fail[F,W](problems:Nes[F]):Safe[F,W]	=
		new Safe[F,W] {
			def cata[X](fail:Nes[F]=>X, win:W=>X):X	= fail(problems)
		}

	/*
	def catchException[W](block: =>W):Safe[Exception,W]	=
		try { win(block) }
		catch { case e:Exception => fail(e.nes) }
	*/

	def traverseISeq[F,S,T](func:S=>Safe[F,T]):ISeq[S]=>Safe[F,ISeq[T]]	=
		ss	=> {
			(ss map func foldLeft win[F,ISeq[T]](Vector.empty[T])) { (old, cur) =>
				old zip cur cata (
					(zipFail:Nes[F])		=> fail(zipFail),
					{ case (oldWin, curWin)	=> win(oldWin :+ curWin) }
				)
			}
		}
}

sealed trait Safe[+F,+W] {
	def cata[X](fail:Nes[F]=>X, win:W=>X):X

	def isWin:Boolean	= cata(_ => false, _ => true)
	def isFail:Boolean	= !isWin

	def foreach(func:W=>Unit) {
		cata(_ => (), func)
	}

	def map[U](func:W=>U):Safe[F,U]	=
		cata(
			Safe.fail,
			func andThen Safe.win
		)

	def flatMap[FF>:F,U](func:W=>Safe[FF,U]):Safe[FF,U]	=
		cata(
			Safe.fail,
			func
		)

	def zip[FF>:F,U](that:Safe[FF,U]):Safe[FF,(W,U)]	=
		this cata (
			thisProblems	=> {
				that cata (
					thatProblems	=> Safe fail (thisProblems ++ thatProblems),
					thatResult		=> Safe fail (thisProblems)
				)
			},
			thisResult	=> {
				that cata (
					thatProblems	=> Safe fail (thatProblems),
					thatResult		=> Safe win ((thisResult, thatResult))
				)
			}
		)

	def toOption:Option[W]	=
		cata(
			_ => None,
			Some.apply
		)
}
