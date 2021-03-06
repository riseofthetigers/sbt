import sbt.internal.inc.classpath.ClasspathUtilities

lazy val scalaOverride = taskKey[Unit]("Check that the proper version of Scala is on the classpath.")

lazy val root = (project in file(".")).
  settings(
    libraryDependencies <++= baseDirectory(dependencies),
    scalaVersion := "2.9.2",
    ivyScala := { ivyScala.value map {_.copy(overrideScalaVersion = sbtPlugin.value)} },
    autoScalaLibrary <<= baseDirectory(base => !(base / "noscala").exists ),
    scalaOverride <<= check("scala.App")
  )

def check(className: String): Def.Initialize[Task[Unit]] = fullClasspath in Compile map { cp =>
  val existing = cp.files.filter(_.getName contains "scala-library")
  println("Full classpath: " + cp.mkString("\n\t", "\n\t", ""))
  println("scala-library.jar: " + existing.mkString("\n\t", "\n\t", ""))
  val loader = ClasspathUtilities.toLoader(existing)
  Class.forName(className, false, loader)
}

def dependencies(base: File) =
  if( ( base / "stm").exists ) ("org.scala-tools" % "scala-stm_2.8.2" % "0.6") :: Nil
  else Nil
