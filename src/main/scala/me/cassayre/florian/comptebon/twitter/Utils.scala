package me.cassayre.florian.comptebon.twitter

object Utils {

  def tweetLink(username: String, id: Long): String = s"https://twitter.com/$username/status/$id"

  def mention(username: String): String = s"@$username"

  def removeMentions(text: String): String = { // Their function is too complicated to actually distinguish all the cases
    text.replaceAll("@[A-Za-z0-9_]{1,20}", "")
  }

}
