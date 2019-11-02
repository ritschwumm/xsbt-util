package xsbtUtil

import sbt._

package object types {
	type Pth			= String
	type PathMapping	= (File, Pth)
	type FileMapping	= (File, File)

	type Predicate[T]	= T=>Boolean
}
