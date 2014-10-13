package xsbtUtil.util

object second {
	def get[A,B](it:(A,B)):B					= it._2
	def modify[A,B,X](func:B=>X):((A,B))=>(A,X)	= it => (it._1, func(it._2))
	def put[A,B,X](value:X):((A,B))=>(A,X)		= it => (it._1, value)
}
