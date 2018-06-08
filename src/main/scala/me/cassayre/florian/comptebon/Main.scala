package me.cassayre.florian.comptebon

import akka.actor.{ActorSystem, Props}
import me.cassayre.florian.comptebon.twitter.TwitterBot
import me.cassayre.florian.comptebon.twitter.TwitterBot.StartGame

object Main extends App {

  val system = ActorSystem()

  val actor = system.actorOf(Props[TwitterBot]) // The actor starts automatically

  actor ! StartGame // Bootstraps the game

}
