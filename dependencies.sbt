libraryDependencies ++= {
  import Dependencies._

  val compile =
    List(
      catsCore,
      catsEffect,
      alpakkaJms,
      ibmMqClient,
      streamzConverter,
      fs2Core,
      fs2Io,
      tofuCore,
      logback,
      log4CatsSlf4j,
      refined,
      steedsCore,
      steedsContextLogger,
      steedsRefined,
      mouse,
      monixCatnap,
      monixEval
    )

  compile
}

resolvers ++= List(
  Resolver.sonatypeRepo("releases"),
  Resolver.bintrayRepo("krasserm", "maven"),
  "Artifactory maven" at "https://artifactory.raiffeisen.ru/artifactory/elbrus-d-mvn",
  "Artifactory yum" at "https://artifactory.raiffeisen.ru/artifactory/elbrus-d-yum"
)
