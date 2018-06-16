organization := "io.kontainers"

name := "rollbar-scala"

version := "1.2.0-SNAPSHOT"

description := "Notifier library for integrating Scala apps with the Rollbar service."

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", scalaVersion.value)

val slf4jVersion = "1.7.25"
val dispatchVersion = "0.14.0"
val log4jVersion = "1.2.17"
val logbackVersion = "1.2.3"
val json4sVersion = "3.5.4"
val scalatestVersion = "3.0.5"

libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % slf4jVersion,
    "org.dispatchhttp" %% "dispatch-core" % dispatchVersion,
    "org.json4s" %% "json4s-jackson" % json4sVersion,
    "log4j" % "log4j" % log4jVersion % "provided",
    "ch.qos.logback" % "logback-classic" % logbackVersion % "provided",
    "org.scalatest" %% "scalatest" % scalatestVersion % "test"
)

// Plugins configuration

publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
    else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra in Global := {
    <url>https://github.com/kontainers/rollbar-scala</url>
    <licenses>
        <license>
            <name>MIT License</name>
            <url>https://opensource.org/licenses/MIT</url>
        </license>
    </licenses>
    <scm>
        <connection>scm:git@github.com:kontainers/rollbar-scala.git</connection>
        <developerConnection>scm:git:git@github.com:kontainers/rollbar-scala.git</developerConnection>
        <url>git@github.com:kontainers/rollbar-scala</url>
    </scm>
    <developers>
        <developer>
            <id>acidghost</id>
            <name>Andrea Jemmett</name>
            <url>https://github.com/acidghost</url>
            <organization>Storecove</organization>
            <organizationUrl>http://www.storecove.com</organizationUrl>
        </developer>
        <developer>
            <id>pjfanning</id>
            <name>PJ Fanning</name>
            <url>https://github.com/pjfanning</url>
        </developer>
    </developers>
}
