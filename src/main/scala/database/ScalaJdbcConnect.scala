package database

import java.sql.{Connection, DriverManager}

/**
  * A Scala JDBC connection example by Alvin Alexander,
  * http://alvinalexander.com
  */
object ScalaJdbcConnect{

  // connect to the database named "mysql" on the localhost
  private val driver = "com.mysql.jdbc.Driver"
  private val url = "jdbc:mysql://localhost/d-pac"
  private val username = "root"
  private val password = ""

  // there's probably a better way to do this
  Class.forName(driver)
  private val connection :Connection = DriverManager.getConnection(url, username, password)

  def getConnection: Connection = connection


  /*   try {
       // make the connection

       connection =


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
     connection.close()*/

}