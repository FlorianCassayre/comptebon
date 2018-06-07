package me.cassayre.florian.comptebon

import akka.actor.{ActorSystem, Props}
import com.danielasfregola.twitter4s.{TwitterRestClient, TwitterStreamingClient}
import me.cassayre.florian.comptebon.twitter.TwitterBot

object Main extends App {

  val system = ActorSystem()

  val actor = system.actorOf(Props[TwitterBot]) // The actor starts automatically

}
