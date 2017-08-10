package hibernate

/**
  * Created by chiaravarini on 10/08/17.
  */
import org.hibernate._

object Test extends App{

  val session: Session = Utils.getSessionFactory.openSession()

  session.beginTransaction()
  val link= new Link("abc")
  link.status = "1"
  session.save(link)
  session.getTransaction().commit()
  println("FINISHED")
}
