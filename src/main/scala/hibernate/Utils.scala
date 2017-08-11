package hibernate

/**
  * Created by chiaravarini on 10/08/17.
  */

import java.io.File

import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration

object Utils {

  private def buildSessionFactory: SessionFactory = {
    try{
  val configuration = new Configuration()
  configuration.configure(new File("src/main/resources/hibernate.cfg.xml"))
  val session = configuration.buildSessionFactory()
   // val sessionFactory = new cfg.Configuration().configure.buildSessionFactory
    // var configuration = new AnnotationConfiguration().configure("src/main/resources/hibernate.cfg.xml")
    //configuration.buildSessionFactory()
    /*val configuration = new Configuration().configure(new File("src/main/resources/hibernate.cfg.xml"))
   var serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties).build()
    var sessionFactory = configuration.buildSessionFactory(serviceRegistry)
    println("creata sessione internal" + sessionFactory)*/
      return session
  }catch {
      case ex: Throwable => {
        // Make sure you log the exception, as it might be swallowed
        System.err.println("Initial SessionFactory creation failed." + ex)
        throw new ExceptionInInitializerError(ex)
      }
    }
  }

  def getSessionFactory: SessionFactory ={
    var sessionFactory: SessionFactory = buildSessionFactory
    sessionFactory
  }


  def shutdown {
    // Close caches and connection pools
    getSessionFactory.close()
  }
}
