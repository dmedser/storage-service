name := "storage-service"

version := "0.1"

scalaVersion := "2.12.10"

credentials := Seq(
  Credentials(
    "Artifactory Realm",
    "artifactory.raiffeisen.ru",
    sys.env.getOrElse("ARTIFACTORY_USERNAME", ""),
    sys.env.getOrElse("ARTIFACTORY_PASSWORD", "")
  )
)