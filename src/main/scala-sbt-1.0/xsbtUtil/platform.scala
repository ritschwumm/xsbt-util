package xsbtUtil

// Compatibility layer for newer SBT versions
private[xsbtUtil] object platform {

  val Using = sbt.io.Using
}
