import sbt._

object Dependencies {

  object Versions {
    val cats = "2.0.0"
    val catsEffect = "2.0.0"
    val alpakka = "1.1.2"
    val ibmMqClient = "9.1.1.0"
    val streamz = "0.10-M2"
    val fs2 = "2.1.0"
    val tofu = "0.5.1.1"
    val logback = "1.2.3"
    val log4Cats = "1.0.1"
    val refined = "0.9.10"
    val steeds = "0.4.0"
    val mouse = "0.23"
    val monix = "3.0.0"
  }

  val catsCore = "org.typelevel"                %% "cats-core"               % Versions.cats
  val catsEffect = "org.typelevel"              %% "cats-effect"             % Versions.catsEffect
  val alpakkaJms = "com.lightbend.akka"         %% "akka-stream-alpakka-jms" % Versions.alpakka
  val ibmMqClient = "com.ibm.mq"                % "com.ibm.mq.allclient"     % Versions.ibmMqClient
  val streamzConverter = "com.github.krasserm"  %% "streamz-converter"       % Versions.streamz
  val fs2Core = "co.fs2"                        %% "fs2-core"                % Versions.fs2
  val fs2Io = "co.fs2"                          %% "fs2-io"                  % Versions.fs2
  val tofuCore = "ru.tinkoff"                   %% "tofu-core"               % Versions.tofu
  val logback = "ch.qos.logback"                % "logback-classic"          % Versions.logback
  val log4CatsSlf4j = "io.chrisdavenport"       %% "log4cats-slf4j"          % Versions.log4Cats
  val refined = "eu.timepit"                    %% "refined"                 % Versions.refined
  val steedsCore = "ru.raiffeisen.rbp"          %% "steeds-core"             % Versions.steeds
  val steedsContextLogger = "ru.raiffeisen.rbp" %% "steeds-context-logger"   % Versions.steeds
  val steedsRefined = "ru.raiffeisen.rbp"       %% "steeds-refined"          % Versions.steeds
  val mouse = "org.typelevel"                   %% "mouse"                   % Versions.mouse
  val monixCatnap = "io.monix"                  %% "monix-catnap"            % Versions.monix
  val monixEval = "io.monix"                    %% "monix-eval"              % Versions.monix
}
