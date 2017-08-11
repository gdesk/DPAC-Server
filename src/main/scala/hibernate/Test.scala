package hibernate

/**
  * Created by chiaravarini on 10/08/17.
  */

import org.hibernate._

object Test extends App{


    val session: Session = Utils.getSessionFactory.openSession()

    println("inizia reansazione")

    session.beginTransaction()

    val link = new Link

    link.status = "provaStatus"

    session.getTransaction.commit()

    session.close()

  println("FINISHED")
}