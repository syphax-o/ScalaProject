ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"
lazy val pekkoHTTPVersion    = "1.0.0"
lazy val pekkoVersion    = "1.0.2"

lazy val root = (project in file("."))
  .settings(
    name := "PMR_Trivia",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-http" % pekkoHTTPVersion,
      "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHTTPVersion,
      "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
      "ch.qos.logback" % "logback-classic" % "1.4.14",
      "com.typesafe" % "config" % "1.4.3",

      "com.typesafe.slick" %% "slick" % "3.5.0-M4",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.5.0-M4",
//      "org.postgresql" % "postgresql" % "42.2.14",
      "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
      "com.github.tminglei" %% "slick-pg" % "0.22.0-M4",

      "org.apache.pekko" %% "pekko-http-testkit" % pekkoHTTPVersion % Test,
      "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test
    )
  )
