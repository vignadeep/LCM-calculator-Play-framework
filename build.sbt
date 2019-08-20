name := """other-project"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.0"
libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += javaForms

enablePlugins(JettyPlugin)
libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"