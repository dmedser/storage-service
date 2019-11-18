package app.source

import fs2.Stream

trait AbstractSource[F[_], A] {
  def source: Stream[F, A]
}
