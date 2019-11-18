package app.source

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.jms.JmsConsumerSettings
import akka.stream.alpakka.jms.scaladsl.JmsConsumer
import akka.stream.scaladsl.RestartSource
import app.config.AppConfig
import app.source.JmsTextMessage.{JmsId, JmsTextPayload}
import cats.effect.{Async, ContextShift, Resource, Sync}
import cats.syntax.apply._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.option._
import com.ibm.mq.jms.MQQueueConnectionFactory
import com.ibm.msg.client.wmq.common.CommonConstants
import fs2.{INothing, Stream}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import javax.jms.{ConnectionFactory, TextMessage}
import monix.catnap.syntax._
import mouse.any._
import cats.instances.option._
import steeds.refined.syntax.functorFilter._
import steeds.syntax.resource._
import streamz.converter._

import scala.concurrent.duration._

trait JmsSource[F[_]] extends AbstractSource[F, JmsTextMessage] {
  def source: Stream[F, JmsTextMessage]
}

object JmsSource {

  def resource[F[_] : Async : ContextShift](jmsConfig: AppConfig.Jms): Resource[F, JmsSource[F]] =
    for {
      log                              <- Slf4jLogger.create.toResource
      implicit0(as: ActorSystem)       <- mkActorSystemResource(log)
      implicit0(am: ActorMaterializer) <- mkActorMaterializerResource(log)
      connectionFactory                <- mkConnectionFactory(jmsConfig).toResource
      jmsConsumerSettings = JmsConsumerSettings(as, connectionFactory).withQueue(jmsConfig.queueName)
    } yield new Impl(jmsConsumerSettings, log)

  private def mkActorSystemResource[F[_]](log: Logger[F])(implicit F: Async[F]): Resource[F, ActorSystem] = {
    val name = "akka-streams"
    val acquire: F[ActorSystem] =
      log.info(s"Starting akka system: $name") *> F.delay(ActorSystem(name))
    def release(actorSystem: ActorSystem): F[Unit] =
      log.info(s"Terminating akka system: $name") *>
        F.delay(actorSystem.terminate()).futureLift.void <*
        log.info(s"Akka system terminated: $name")
    Resource.make(acquire)(release)
  }

  private def mkActorMaterializerResource[F[_]](
    log: Logger[F]
  )(implicit F: Sync[F], as: ActorSystem): Resource[F, ActorMaterializer] = {
    val acquire: F[ActorMaterializer] = F.delay(ActorMaterializer())
    def release(actorMaterializer: ActorMaterializer): F[Unit] =
      log.info("Shutting down akka stream materializer") *>
        F.delay(actorMaterializer.shutdown()) <*
        log.info("Akka stream materializer shut down")
    Resource.make(acquire)(release)
  }

  private def mkConnectionFactory[F[_]](jmsConfig: AppConfig.Jms)(implicit F: Sync[F]): F[ConnectionFactory] =
    F.delay {
      import jmsConfig._
      new MQQueueConnectionFactory() <|
        (_.setHostName(hostname)) <|
        (_.setPort(port)) <|
        (_.setQueueManager(queueManagerName)) <|
        (_.setChannel(channelName)) <|
        (_.setTransportType(CommonConstants.WMQ_CM_BINDINGS_THEN_CLIENT)) // TODO
    }

  private final class Impl[F[_]](jmsConsumerSettings: JmsConsumerSettings, log: Logger[F])(
    implicit F: Async[F],
    cs: ContextShift[F],
    am: ActorMaterializer
  ) extends JmsSource[F] {

    private val restartableAkkaSource = RestartSource.onFailuresWithBackoff(
      minBackoff = 3 seconds,
      maxBackoff = 30 seconds,
      randomFactor = 0.2,
      maxRestarts = -1
    )(() => JmsConsumer(jmsConsumerSettings))

    def source: Stream[F, JmsTextMessage] =
      restartableAkkaSource.toStream[F]().evalTap(_ => cs.shift) >>= {
        case message: TextMessage =>
          Stream.eval(extract(message)).handleErrorWith(errorHandler)
        case _ =>
          errorHandler(SourceException("unsupported message format"))
      }

    private def extract(message: TextMessage): F[JmsTextMessage] =
      for {
        id_? <- F.delay(message.getJMSMessageID)
        id <- Option(id_?).trimFilter
          .map(JmsId.apply)
          .liftTo[F](SourceException("null or empty message id"))
        text_? <- F.delay(message.getText)
        text <- Option(text_?).trimFilter
          .map(JmsTextPayload.apply)
          .liftTo[F](SourceException("null or empty message text"))
      } yield JmsTextMessage(id, text)

    private def errorHandler(t: Throwable): Stream[F, INothing] =
      Stream.eval_(log.error(t)("Failed to extract JMS message"))
  }
}
