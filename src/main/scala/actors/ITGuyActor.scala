package actors

import akka.actor.{Actor, Props}
import messages.{Command, FYou, Sudo, YesMeLord}

class ITGuyActor(val rebelChances: Double) extends Actor {
  assert(rebelChances > 0.0)
  assert(rebelChances < 1.0)

  val SUDO_ATTEMPTS_THRESHOLD = 3

  // we are assuming only one actor sends sudo
  // messages... that is ok for our purposes
  // his holds state as you can see ;)
  var sudoAttempts = 0

  val actorName = self.path.name

  override def preStart() = {
    if (shouldGoRebel) {
      println(s"[$actorName] Going wild...")
      context.become(rebel)
    }
  }

  def receive: Receive = {
    case c @ Command(cmd) =>
      println(s"[$actorName]: received command [$c]...!")
      println(s"[$actorName]: I am so obedient I will do it NOW...")
      sender ! YesMeLord(s"[$actorName] I executed your command [$cmd], Me Lord")
    case s @ Sudo(c @ Command(cmd)) =>
      println(s"[$actorName]: received Sudo command [$s]...!")
      self ! c
  }

  def rebel: Receive = {
    case c @ Command(cmd) =>
      println(s"[$actorName]: received command [$c]... but FYou System!")
      sender ! FYou(c)

    case s @ Sudo(c @ Command(cmd)) =>
      if (sudoAttempts < SUDO_ATTEMPTS_THRESHOLD) {
        sudoAttempts += 1
        sender ! FYou(c)
      } else {
        sender ! YesMeLord(s"I executed your command [$cmd] after $sudoAttempts attempts. My family got the message, Me Lord")
        sudoAttempts = 0
        context.stop(self)
      }
  }

  private def shouldGoRebel: Boolean = {
    val r = scala.util.Random
    rebelChances > r.nextDouble
  }

}

object ITGuyActor {
  def props(rebelChances: Double) = Props[ITGuyActor](new ITGuyActor(rebelChances))
}
