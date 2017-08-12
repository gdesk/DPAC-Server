package database

import java.sql.{Connection, DriverManager}

/**
  * A Scala JDBC connection.
  *
  * @author Giulia Lucchi
  */
object ScalaJdbcConnect{
  private val driver = "com.mysql.jdbc.Driver"
  private val url = "jdbc:mysql://localhost/d-pac"
  private val username = "root"
  private val password = ""

  Class.forName(driver)
  private val connection :Connection = DriverManager.getConnection(url, username, password)

  def getConnection: Connection = connection
}