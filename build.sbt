import sbt._
import Keys._

enablePlugins(SbtOsgi)

organization := "org.geneontology"

name := "taxon-constraints"

version := "0.1"

licenses := Seq("BSD-3-Clause" -> url("https://opensource.org/licenses/BSD-3-Clause"))

// build with Java 8 for compatibility with Protégé
scalaVersion := "2.13.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

def isJarToEmbed(file: java.io.File): Boolean = file.getName match {
  case name if name startsWith "scala" => true
  case _                               => false
}

//OsgiKeys.embeddedJars := (Keys.externalDependencyClasspath in Compile).value.map(_.data).filter(
//  file => {
//    /*
//     * Find configs for file and return false if it includes "test" or "provided"
//     */
//    libraryDependencies.value.map(x => { (x.name.toLowerCase, x.configurations.map(_.toLowerCase)) })
//      .find { case (n, _) => file.getName.toLowerCase.contains(n) }
//      .flatMap {case (_, c) => c} match {
//      case x if x.contains("test") => false
//      case x if x.contains("provided") => false
//      case _ => true
//    }
//  })

// Bundle-Version is set to the version by default.
OsgiKeys.bundleSymbolicName := "org.geneontology.taxonconstraints;singleton:=true"
// Include the packages specified by privatePackage in the bundle.
OsgiKeys.privatePackage := Seq("org.geneontology.*")
OsgiKeys.exportPackage := Seq("!*")
OsgiKeys.importPackage := Seq("!org.hamcrest", "!sun.misc", "*", "sun.misc;resolution:=optional")
OsgiKeys.failOnUndecidedPackage := true
OsgiKeys.requireCapability := """osgi.ee;filter:="(&(osgi.ee=JavaSE)(version=1.8))""""
OsgiKeys.embeddedJars := (Keys.externalDependencyClasspath in Compile).value map (_.data) filter isJarToEmbed
//TODO
OsgiKeys.additionalHeaders := Map(
  "Update-Url" -> "https://github.com/geneontology/protege-taxon-constraints"
)

libraryDependencies ++= {
  Seq(
    "net.sourceforge.owlapi" % "owlapi-distribution" % "4.5.8" % Provided,
    "edu.stanford.protege" % "protege-editor-core" % "5.2.0" % Provided,
    "edu.stanford.protege" % "protege-editor-owl" % "5.2.0" % Provided
  )
}
