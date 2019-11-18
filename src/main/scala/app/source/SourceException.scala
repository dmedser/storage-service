package app.source

import scala.util.control.NoStackTrace

final case class SourceException(message: String) extends RuntimeException(message) with NoStackTrace
