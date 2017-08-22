package model

/** A User of the game.
  * It Identify a single player.
  *
  * @author manuBottax
  */
trait User {

  def name: String
  def username: String
  def password: String
  def mail: String

}

/** A simple implementation of [[User]] that cannot modify its information.
  *
  * @author manuBottax
  */
class ImmutableUser (override val name: String,
                     override val username: String,
                     override val password: String,
                     override val mail: String ) extends User
