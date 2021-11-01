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

	def fromEither[F,W](it:Either[Nes[F],W]):Safe[F,W]	=
		it match {
			case Left(e)	=> fail(e)
			case Right(w)	=> win(w)
		}

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

	def zip[FF>:F,U](that:Safe[FF,U]):Safe[FF,(W,U)]	=
		zipWith(that)((_,_))

	def zipWith[FF>:F,U,X](that:Safe[FF,U])(func:(W,U)=>X):Safe[FF,X]	=
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
					thatResult		=> Safe win func(thisResult, thatResult)
				)
			}
		)

	// NOTE this is a hack -there is no proper Monad instance for Safe
	def flatMap[FF>:F,U](func:W=>Safe[FF,U]):Safe[FF,U]	=
		cata(
			Safe.fail,
			func
		)

	def toOption:Option[W]	=
		cata(
			_ => None,
			Some.apply
		)

	def toEither:Either[Nes[F],W]	=
		cata(
			Left.apply,
			Right.apply
		)
}
