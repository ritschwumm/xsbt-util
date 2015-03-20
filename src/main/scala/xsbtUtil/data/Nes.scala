package xsbtUtil.data

import scala.collection.immutable.{ Seq => ISeq }

object Nes {
	def single[T](head:T):Nes[T]	=
			Nes(head, ISeq.empty)
		
	def many[T](head:T, tail:T*):Nes[T]	=
	 		Nes(head, tail.toVector)
		
	def fromISeq[T](it:ISeq[T]):Option[Nes[T]]	=
			if (it.nonEmpty)	Some(Nes(it.head, it.tail))
			else				None
}

case class Nes[+T](head:T, tail:ISeq[T]) {
	def last:T	=
			if (tail.isEmpty)	head
			else				tail.last
			
	def init:ISeq[T]	=
			if (tail.nonEmpty)	head +: tail.init
			else				Vector(head)
			
	def size:Int	= 1 + tail.size
	
	def containsIndex(index:Int):Boolean	=
			index >= 0 && index < size
		
	def get(index:Int):Option[T]	=
			if (index == 0)	Some(head)
			else			tail lift (index-1)
		
	def modifier[U>:T](index:Int):Option[(T=>U)=>Nes[U]]	=
			if (index == 0) {
				Some { func:(T=>U) =>
					Nes(func(head), tail)
				}
			 }
			 else if (index > 0 && index < size) {
				 Some(func => Nes(head, tail updated (index-1, func(tail apply index-1))))
			 }
			 else None
			
	def updater[U>:T](index:Int):Option[U=>Nes[U]]	=
			modifier[U](index) map { patch:((T=>U)=>Nes[U]) =>
				item:U => patch(_ => item)
			}
		
	def reverse:Nes[T]	=
			if (tail.nonEmpty)	Nes(tail.last, tail.init.reverse :+ head)
			else				this
	
	def concat[U>:T](that:Nes[U]):Nes[U]	=
			Nes(this.head, (this.tail :+ that.head) ++ that.tail)
	
	def prepend[U>:T](item:U):Nes[U]	=
			Nes(item, toISeq)
		
	def append[U>:T](item:U):Nes[U]	=
			Nes(this.head, this.tail :+ item)
		
	@inline
	def ++[U>:T](that:Nes[U]):Nes[U]	=
			this concat that
		
	@inline
	def :+[U>:T](item:U):Nes[U]	=
			this append item
		
	@inline
	def +:[U>:T](item:U):Nes[U]	=
			this prepend item
		
	def map[U](func:T=>U):Nes[U]	=
			Nes(func(head), tail map func)
		
	def flatMap[U](func:T=>Nes[U]):Nes[U]	= {
		val Nes(h, t)	= func(head)
		val tt			= tail flatMap { it => func(it).toISeq }
		Nes(h, t ++ tt)
	}
	
	def flatten[U](implicit ev:T=>Nes[U]):Nes[U]	=
			flatMap(ev)
		
	def zip[U](that:Nes[U]):Nes[(T,U)]	=
			Nes(
				(this.head, that.head),
				this.tail zip that.tail
			)
			
	def zipWith[U,V](that:Nes[U])(func:(T,U)=>V):Nes[V]	=
			Nes(
				func(this.head, that.head),
				(this.tail zip that.tail) map func.tupled
			)
				
	def zipWithIndex:Nes[(T,Int)]	=
			Nes(
				(this.head, 0),
				this.tail.zipWithIndex map { case (v,i) => (v,i+1) }
			)
	
	def foreach(effect:T=>Unit){
		effect(head)
		tail foreach effect
	}
	
	def toISeq:ISeq[T]	=
			head +: tail
		
	def toVector:Vector[T]	=
			head +: tail.toVector
		
	def toList:List[T]	=
			head :: tail.toList
	
	def mkString(separator:String):String	=
			toISeq mkString separator
	
	def mkString(prefix:String, separator:String, suffix:String):String	=
			toISeq mkString (prefix, separator, suffix)
}
