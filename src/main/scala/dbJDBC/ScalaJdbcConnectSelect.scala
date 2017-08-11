package dbJDBC

import java.sql.DriverManager
import java.sql.Connection

/**
 * A Scala JDBC connection example by Alvin Alexander,
 * http://alvinalexander.com
 */
object ScalaJdbcConnectSelect extends App{

    // connect to the database named "mysql" on the localhost
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://localhost/d-pac"
    val username = "root"
    val password = ""

    // there's probably a better way to do this
    var connection:Connection = null

    try {
      // make the connection
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)


      // create the statement, and run the select query
      val statement = connection.createStatement()
      println("si connette?")
      val resultSet = statement.executeQuery("SELECT username, email FROM user")

      while ( resultSet.next() ) {
        val host = resultSet.getString("username")
        val user = resultSet.getString("email")
        println("host, user = " + host + ", " + user)
      }
    } catch {
      case e => e.printStackTrace
    }
    connection.close()

}