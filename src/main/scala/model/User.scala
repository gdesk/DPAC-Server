package model

/**
  * Created by Manuel Bottax on 15/07/2017.
  */
trait User {

  def name: String
  def username: String
  def password: String
  def mail: String

}

class ImmutableUser (override val name: String,
                     override val username: String,
                     override val password: String,
                     override val mail: String ) extends User
