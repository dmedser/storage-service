package app.source

import app.source.JmsTextMessage.{JmsId, JmsTextPayload}
import steeds.refined.data.NonBlankString

final case class JmsTextMessage (id: JmsId, text: JmsTextPayload)

object JmsTextMessage {

  final case class JmsId(value: NonBlankString)

  final case class JmsTextPayload(value: NonBlankString)
}
