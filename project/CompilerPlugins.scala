import sbt._

object CompilerPlugins {

  object Versions {
    val macroParadise = "2.1.0"
    val betterMonadicFor = "0.3.1"
    val kindProjector = "0.11.0"
  }

  val macroParadise = compilerPlugin("org.scalamacros" % "paradise"            % Versions.macroParadise cross CrossVersion.full)
  val betterMonadicFor = compilerPlugin("com.olegpy"   %% "better-monadic-for" % Versions.betterMonadicFor)
  val kindProjector = compilerPlugin(
    "org.typelevel" %% "kind-projector" % Versions.kindProjector cross CrossVersion.full
  )
}
