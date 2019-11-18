package app.config

import app.config.AppConfig.Jms

final case class AppConfig(jms: Jms)

object AppConfig {
  final case class Jms(hostname: String, port: Int, queueManagerName: String, channelName: String, queueName: String)
}
