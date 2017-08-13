package model

/**
  * Created by Manuel Bottax on 05/08/2017.
  */
trait Client {

  def ip: String

  def username: String

}

class ClientImpl (val ip: String, val username: String) extends Client