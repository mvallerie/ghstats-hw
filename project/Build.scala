import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "ghstats-hw"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
	"org.webjars" %% "webjars-play" % "2.1.0-3",
	"org.webjars" % "bootstrap" % "3.0.0",
	"org.webjars" % "jquery" % "1.10.2",
	"org.webjars" % "jquery-ui" % "1.10.2-1",
	"org.webjars" % "d3js" % "3.1.5"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings()

}
