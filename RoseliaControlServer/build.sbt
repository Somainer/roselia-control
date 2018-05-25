name := "RoseliaControlServer"
 
version := "1.0"
      
lazy val `roseliacontrolserver` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

// libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.6" % "test"

scalaJSUseMainModuleInitializer := true


// libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.5"
