package hibernate

/**
  * Created by chiaravarini on 10/08/17.
  */
import javax.persistence._

@Entity
@Table(name = "links")
class Link(url: String) {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Int = _

  var status: String = _

  override def toString = id + ")" + url
}
