package hibernate

/**
  * Created by chiaravarini on 10/08/17.
  */

import org.hibernate._

object Test extends App{


    val session: Session = Utils.getSessionFactory.openSession()

    session.beginTransaction()

    val link = new Link

    link.status = "provaStatus"

    session.save(link)

    session.getTransaction.commit()

    session.close()

  println("FINISHED")
}
