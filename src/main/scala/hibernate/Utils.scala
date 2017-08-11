package hibernate

/**
  * Created by chiaravarini on 10/08/17.
  */
import org.hibernate.cfg.{AnnotationConfiguration}
import org.hibernate.SessionFactory

object Utils {
  private val sessionFactory = buildSessionFactory

  private def buildSessionFactory: SessionFactory = {
    try {
      var configuration = new AnnotationConfiguration().configure("/hibernate.cfg.xml")
      configuration.buildSessionFactory()

    } catch {
      case ex: Throwable => {
        // Make sure you log the exception, as it might be swallowed
        System.err.println("Initial SessionFactory creation failed." + ex)
        throw new ExceptionInInitializerError(ex)
      }
    }
  }

  def getSessionFactory = sessionFactory


  def shutdown {
    // Close caches and connection pools
    getSessionFactory.close()
  }
}
