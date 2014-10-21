sbtPlugin		:= true

name			:= "xsbt-util"

organization	:= "de.djini"

version			:= "0.3.0"

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
	"-feature"
)

conflictManager	:= ConflictManager.strict

libraryDependencies	++= Seq(
	"org.apache.commons"	% "commons-compress"	% "1.8.1"	% "compile"
)

