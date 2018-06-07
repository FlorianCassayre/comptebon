package me.cassayre.florian.comptebon.twitter

import akka.actor.{Actor, PoisonPill}
import com.danielasfregola.twitter4s.entities.{Tweet, User}
import com.danielasfregola.twitter4s.{TwitterRestClient, TwitterStreamingClient}
import me.cassayre.florian.comptebon.expression.{Expression, ExpressionParser, ExpressionValidator}
import me.cassayre.florian.comptebon.game._
import me.cassayre.florian.comptebon.twitter.Emojis._
import me.cassayre.florian.comptebon.twitter.Utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class TwitterBot extends Actor {

  type UserId = Long
  type TweetId = Long
  type AnswerScore = (Int, Int) // (distance, plates used)

  // Events

  case object StartRound

  case class UserMention(tweet: Tweet)
  case class CountdownEnd(forTweet: Tweet)
  case class ListenTo(gameRef: AnyRef, tweet: Tweet)

  // ---

  val maxRetries = 5 // Over this limit, the application stops

  val restClient = TwitterRestClient()
  val streamingClient = TwitterStreamingClient()

  println("Connecting...")

  val user: User = Await.result(restClient.verifyCredentials(), Duration.Inf).data
  println("Connection successfully established.")

  val (botId: Long, botUsername: String) = (user.id, user.screen_name)

  var (gamesPlayed, countsMade) = {
    val regex = generateDescription("([0-9]+)", "([0-9]+)").r
    user.description match {
      case Some(regex(played, counts)) => (played.toInt, counts.toInt)
    }
  }

  var gameRef: AnyRef = _

  var game: GameRound = _

  var usersScores: Map[UserId, AnswerScore] = _

  var tweetsToListen: Set[TweetId] = _

  var currentBestSolution: Option[(Tweet, Expression)] = _


  override def receive: Receive = awaitState

  /**
    * During this state the bot listens for replies
    * @return
    */
  def roundState: Receive = {

    case UserMention(tweet) if tweet.in_reply_to_status_id.isDefined && tweetsToListen.contains(tweet.in_reply_to_status_id.get) && tweet.user.forall(_.id != botId) =>

      val withoutMentions = removeMentions(tweet.text)

      ExpressionParser.parseExpression(withoutMentions)
        .filter(expression => expression.isValid && ExpressionValidator.usesPlates(expression, game.plates))
      match {
        case Some(expression) =>
          val user = tweet.user.get

          val value = expression.value
          val distance = Math.abs(game.goal - value)
          val platesUsed = expression.numbers.size

          val isPersonalImprovement = usersScores.get(user.id) match {
            case Some((a, b)) => distance < a || distance == a && platesUsed < b
            case None => true
          }

          val best =
            if (usersScores.isEmpty)
              None
            else
              Some(usersScores.groupBy(_._1).minBy(_._1)._2.minBy(_._2)._2)

          usersScores += user.id -> (distance, platesUsed)

          val isGlobalImprovement = best match {
            case Some((bestDistance, bestPlatesUsed)) => distance < bestDistance || distance == bestDistance && platesUsed < bestPlatesUsed
            case None => true
          }

          if (isPersonalImprovement) {
            val message = (if (distance == 0) s"Le compte est bon ! $party" else s"$value, compte approchant à $distance.") + "\n" + s"$platesUsed plaques utilisées."

            val countdown = if (distance == 0) 1 minute else 5 minutes

            val extra = {
              if (isGlobalImprovement) {
                val victoryExtra = s"Victoire dans ${countdown.toMinutes} minute${if (countdown.toMinutes > 1) "s" else ""} si personne ne propose mieux."

                if (currentBestSolution.nonEmpty && currentBestSolution.forall(_._1.user.get.id == user.id))
                  s"$checkmark Vous améliorez votre solution. $victoryExtra"
                else
                  s"$checkmark Félicitations, vous détenez à présent la meilleure solution. $victoryExtra"
              }
              else {
                val bestTweet = currentBestSolution.get._1

                s"${mention(bestTweet.user.get.screen_name)} détient toujours la meilleure solution.\n${tweetLink(bestTweet.user.get.screen_name, bestTweet.id)}"
              }
            }

            if (isGlobalImprovement) {

              currentBestSolution = Some(tweet, expression)

              context.system.scheduler.scheduleOnce(countdown)(self ! CountdownEnd(tweet))

            }

            withRecovery(restClient.createTweet(s"${mention(botUsername)} ${mention(user.screen_name)}\n$message\n\n$extra", in_reply_to_status_id = Some(tweet.id))).foreach(tweet => self ! ListenTo(gameRef, tweet))
          } // Else nothing

        case None =>
          withRecovery(restClient.favoriteStatus(tweet.id))

      }

      self ! ListenTo(gameRef, tweet)

    case CountdownEnd(forTweet) if currentBestSolution.forall(_._1 == forTweet) =>

      val (bestTweet, bestExpression) = currentBestSolution.get

      val username = bestTweet.user.get.screen_name

      val distance = Math.abs(bestExpression.value - game.goal)

      val text = s"La manche est remportée par ${mention(username)} avec un ${if (distance == 0) "bon compte" else s"compte approchant à $distance" } en ${bestExpression.numbers.size} plaques !\n${tweetLink(username, bestTweet.id)}"

      withRecovery(restClient.createTweet(text))

      gamesPlayed += 1
      countsMade += (if (distance == 0) 1 else 0)

      withRecovery(restClient.updateProfileDescription(generateDescription(gamesPlayed.toString, countsMade.toString)))


      context.system.scheduler.scheduleOnce(1 minute)(self ! StartRound)

      context.become(awaitState)

    case ListenTo(ref, tweet) if ref == gameRef =>
      tweetsToListen += tweet.id
  }

  /**
    * During this state the bot is idle (either the game hasn't started yet or the bot is waiting for next round).
    * It can only receive a `StartRound` message (other packets will be ignored).
    * @return
    */
  def awaitState: Receive = {
    case StartRound =>
      gameRef = new AnyRef
      game = GameGenerator.drawGame()
      usersScores = Map()
      tweetsToListen = Set()
      currentBestSolution = None

      val text = s"$bullseye ${intToEmojis(game.goal)} $bullseye\n\nPlaques : ${game.plates.mkString(", ")}"

      val currentRef = gameRef
      withRecovery(restClient.createTweet(text)).foreach(tweet => self ! ListenTo(currentRef, tweet))

      context.become(roundState)

    case _ => // Ignore
  }

  def withRecovery[T](task: => Future[T], attempts: Int = maxRetries): Future[T] = {
    task.recoverWith {
      case _ if attempts > 0 => withRecovery(task, attempts - 1)
      case _ =>
        self ! PoisonPill
        throw new IllegalStateException("Couldn't recover task")
    }
  }

  def generateDescription(played: String, counts: String): String = s"Jeu du compte est bon – $played parties jouées – $counts bons comptes réalisés"

  println("Starting game.")

  self ! StartRound // Bootstraps the game


  streamingClient.filterStatuses(follow = Seq(botId)) {
    case tweet: Tweet =>
      self ! UserMention(tweet)
  }

}
