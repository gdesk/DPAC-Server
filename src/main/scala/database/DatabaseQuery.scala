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
      val statement = connection.createStatement()
      val query = "SELECT result, date, score FROM gamematch WHERE userId = ?"
      val pstmt = connection.prepareStatement(query)
      pstmt.setString(1, username)
      val resultSet = pstmt.executeQuery()

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
      val statement = connection.createStatement()
      val query = "INSERT INTO user " + "VALUES (?,?,?,?)"
      val pstmt = connection.prepareStatement(query)
      pstmt.setString(1, username)
      pstmt.setString(2, name)
      pstmt.setString(3, email)
      pstmt.setString(4, password)
      pstmt.execute()
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
    * @return true if the login ended good
    *         false if username not exist or password is wrong.
    */
  def checkLogin(username: String, password: String): Boolean ={
    try {
      val statement = connection.createStatement()
      val query = "SELECT username, password FROM user WHERE username = ?"
      val pstmt = connection.prepareStatement(query)
      pstmt.setString(1, username)
      val resultSet = pstmt.executeQuery()
      while (resultSet.next()) {
        val username = resultSet.getString("username")
        val psw = resultSet.getString("password")
        if(password.equals(psw)){
          println("login riuscito.")
          return true
        }else{
          println("password sbagliata.")
          return false
        }
      }
    } catch {
      case e => e.printStackTrace
    }
    println("username sbagliato. Sei già registrato?")
    false
  }

  def addMatchResult(username: String, matchResult: MatchResult): Boolean ={
    try {
      val statement = connection.createStatement()
      val query = "INSERT INTO gamematch " + "VALUES (?,?,?,?,?)"
      val pstmt = connection.prepareStatement(query)
      println("prepar")
      pstmt.setNull(1, 1)
      pstmt.setBoolean(2, matchResult.result)
      pstmt.setDate(3,new java.sql.Date(matchResult.date.getTimeInMillis()) )
      pstmt.setInt(4, matchResult.score)
      pstmt.setString(5, username)
      pstmt.execute()
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
    val cal = Calendar.getInstance
    cal.setTime(date)
    cal
  }
}
