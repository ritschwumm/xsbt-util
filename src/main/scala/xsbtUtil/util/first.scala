package xsbtUtil.util

object first {
	def get[A,B](it:(A,B)):A					= it._1
	def modify[A,B,X](func:A=>X):((A,B))=>(X,B)	= it => (func(it._1), it._2)
	def put[A,B,X](value:X):((A,B))=>(X,B)		= it => (value, it._2)
}
