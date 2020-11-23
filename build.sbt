sbtPlugin		:= true

name			:= "xsbt-util"
organization	:= "de.djini"
version			:= "1.5.0"

scalacOptions	++= Seq(
	"-feature",
	"-deprecation",
	"-unchecked",
	"-Xfatal-warnings",
)

conflictManager		:= ConflictManager.strict withOrganization "^(?!(org\\.scala-lang|org\\.scala-js|org\\.scala-sbt)(\\..*)?)$"
libraryDependencies	++= Seq(
	"org.apache.commons"	% "commons-compress"	% "1.20"	% "compile"
)
