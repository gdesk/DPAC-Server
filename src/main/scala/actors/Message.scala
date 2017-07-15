package actors


/** A message to be sent to a client trough the net.
  * Used to keep information about the data to send to the client.
  *
  * @author manuBottax
  */
trait Message {

  /** @return the list of the object passed as parameter for the message
    */
  // todo: ha senso usare una cosa del genere ?
  def getParameterList: List[Object]

}
