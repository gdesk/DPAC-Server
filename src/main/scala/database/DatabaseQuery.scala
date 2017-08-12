package database

import java.sql.Date
import java.util.Calendar

import model.{MatchResult, MatchResultImpl}

/**
  * Query from server to database
  *
  * @author Giulia Lucchi
  */
object DatabaseQuery {
  private val connection = ScalaJdbcConnect getConnection

  /**
    * Request to database to recover all the games played by the player in question.
    * @param username username of player who logged in
    *
    * @return all matches played by the player in question
    */
  def allMatches(username: String): List[MatchResult] = {
    var allMatches: List[MatchResult] = List.empty
    try {
      val query = "SELECT result, date, score FROM gamematch WHERE userId = ?"
      val statement = connection.prepareStatement(query)
      statement.setString(1, username)
      val resultSet = statement.executeQuery()

      while (resultSet.next()) {
        val userMatch = new MatchResultImpl()
        val result = resultSet.getBoolean("result")
        val date = resultSet.getDate("date")
        val score = resultSet.getInt("score")
        println("data: " +date + "  result: "+result+"  score: "+score)
        userMatch.result = result
        userMatch.date = toCalendar(date)
        userMatch.score = score
       allMatches = userMatch :: allMatches
      }
    } catch {
      case e => e.printStackTrace
    }
    allMatches
  }

  /**
    * Add the data of user to database.
    *
    * @param name name of user
    * @param username unique name of user (primary key)
    * @param email main of user
    * @param password password for the game
    *
    * @return true if the insertion took place
    *         false if the username is not valid.
    */
  def addUser(name: String, username: String, email: String, password: String): Boolean ={
    try {
      val query = "INSERT INTO user " + "VALUES (?,?,?,?)"
      val statement = connection.prepareStatement(query)
      statement.setString(1, username)
      statement.setString(2, name)
      statement.setString(3, email)
      statement.setString(4, password)
      statement.execute()
    }catch{
      case e =>println("username is not valid.")
        return false
    }
    println("Registration done.")
    true
  }

  /**
    * Check information of login.
    *
    * @param username username of player
    * @param password password of player
    *
    * @return logged                if login ended good
    *         passwordWrong         if the password is wrong
    *         unregisteredUsername  if the username is wrong or unregistered
    *
    */
  def checkLogin(username: String, password: String): String ={
    try {
      val query = "SELECT username, password FROM user WHERE username = ?"
      val statement = connection.prepareStatement(query)
      statement.setString(1, username)
      val resultSet = statement.executeQuery()
      while (resultSet.next()) {
        val username = resultSet.getString("username")
        val psw = resultSet.getString("password")
        if(password.equals(psw)){
          println("Login effettuato.")
          return "logged"
        }else{
          println("password sbagliata. Riprova.")
          return "passwordWrong"
        }
      }
    } catch {
      case e => e.printStackTrace
    }
    println("username sbagliato. Sei già registrato?")
    "unregisteredUsername"
  }

  /**
    * Adds the results of the game just finished.
    *
    * @param username id of player
    * @param matchResult the information of result of match
    *
    * @return true  if match added
    *         false if username is not valid.
    */
  def addMatchResult(username: String, matchResult: MatchResult): Boolean ={
    try {
      val query = "INSERT INTO gamematch " + "VALUES (?,?,?,?,?)"
      val statement = connection.prepareStatement(query)
      println("prepar")
      statement.setNull(1, 1)
      statement.setBoolean(2, matchResult.result)
      statement.setDate(3,new java.sql.Date(matchResult.date.getTimeInMillis()) )
      statement.setInt(4, matchResult.score)
      statement.setString(5, username)
      statement.execute()
    }catch{
      case e =>println("username is not valid.")
        return false
    }
    println("Match added.")
    true
  }

  /**
    * Convert sql.Date to Calendar object.
    *
    * @param date date requested from query in sql.Date
    * @return same darte in Calendar object
    */
 private def toCalendar(date: Date): Calendar = {
    val calendar = Calendar.getInstance
    calendar.setTime(date)
    calendar
  }
}
