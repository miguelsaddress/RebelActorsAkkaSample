package actors

import akka.actor.{Actor, PoisonPill, Props}
import messages._

import scala.concurrent.Await
import scala.concurrent.duration._

class BossActor extends Actor {

  val actorName = self.path.name
  var waitingForActors = 0

  def receive = {
    case SendCommandTo(cmd, actor) =>
      println(s"[$actorName] Sending command [$cmd] to [$actor]")
      waitingForActors += 1
      actor ! cmd

    case f @ FYou(c @ Command(cmd)) => {
      println (s"[$actorName]: What, [${sender.path.name}]??? F* to me? F* To you!")
      println(s"[$actorName]: Sending Sudo version... of [$c] to [${sender.path.name}]")
      sender ! Sudo(c)
    }
    case YesMeLord(response) => {
      waitingForActors -= 1
      println(s"[$actorName]: Good boy [${sender.path.name}]... I got your response [$response]")
      if (waitingForActors == 0) {
        println("Terminating...")
        context.system.terminate()
      }
    }
  }
}

object BossActor {
  def props() = Props[BossActor]
}
