package actors


/** A single client reference.
  * Used to keep information about the client that is connected to the server.
  *
  * @author manuBottax
  */
trait Client {

  /** the unique ID code for the client.
    */
  def id: String

  /** the net address of the client.
    */
  def ipAddress: String

}
