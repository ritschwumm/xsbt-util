package xsbtUtil

import sbt.PathFinder

// Compatibility layer for older SBT versions
private[xsbtUtil] object platform {

  implicit class PathFinderOps(val self: PathFinder) extends AnyVal {
    def allPaths: PathFinder = self.***
  }
}
