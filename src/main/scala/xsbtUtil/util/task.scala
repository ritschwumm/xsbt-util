package xsbtUtil.util

import sbt._

object task {
	def chain[T](key:SettingKey[Seq[TaskKey[T]]]):Def.Initialize[Task[Seq[T]]]	= {
		def sequence[T](keys:Seq[TaskKey[T]], accu:Seq[T] = Vector.empty):Def.Initialize[Task[Seq[T]]]	=
			Def.taskDyn {
				keys match {
					case head +: tail	=> Def.taskDyn { sequence(tail, accu :+ head.value) }
					case _				=> Def.task { accu }
				}
			}
		Def.taskDyn {
			sequence(key.value)
		}
	}
}
