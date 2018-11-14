sbtPlugin		:= true

name			:= "xsbt-util"
organization	:= "de.djini"
version			:= "1.3.0"

scalacOptions	++= Seq(
	"-deprecation",
	"-unchecked",
	// "-language:implicitConversions",
	// "-language:existentials",
	// "-language:higherKinds",
	// "-language:reflectiveCalls",
	// "-language:dynamics",
	// "-language:postfixOps",
	// "-language:experimental.macros"
	"-feature",
	"-Xfatal-warnings"
)

conflictManager	:= ConflictManager.strict
libraryDependencies	++= Seq(
	"org.apache.commons"	% "commons-compress"	% "1.18"	% "compile"
)
