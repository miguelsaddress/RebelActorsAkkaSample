package messages

import akka.actor.ActorRef

case class SendCommandTo(cmd: Command, to: ActorRef)
