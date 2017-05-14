
import actors.{BossActor, ITGuyActor}
import messages._
import akka.actor._

import scala.concurrent.duration._
import scala.concurrent.Await

object Main extends App {
  val system = ActorSystem("RebelAkktorsSystem")

  val boss = system.actorOf(BossActor.props(), name = "TheBoss")

  val obedientITGuy = system.actorOf(ITGuyActor.props(0.01), name = "ObedientItGuy")
  val rebelITGuy = system.actorOf(ITGuyActor.props(0.95), name = "RebelItGuy")

  println( "Here we go")
  boss ! SendCommandTo(Command("Make me a Sandwich"), obedientITGuy)
  boss ! SendCommandTo(Command("Make me a Sandwich"), rebelITGuy)

  Await.ready(system.whenTerminated, 5 second)

}
