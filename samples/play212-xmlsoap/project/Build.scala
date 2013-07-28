import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play212-xmlsoap"
  val appVersion      = "0.2-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "play2.tools.xml" %% "xmlsoap-ersatz" % "0.2-SNAPSHOT"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
   resolvers += ("mandubian-mvn snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots")
  
  )

}
