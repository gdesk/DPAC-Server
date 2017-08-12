package database

import java.util.Calendar

import model.MatchResultImpl

/**
  * Test to adjust the interaction with database.
  * This test can be execute on the database testDpac in resources.
  *
  * @author Giulia Lucchi
  */
object DatabaseTest extends App{

  /* Return the list current player' match     */
  assert(!DatabaseQuery.allMatches("giulia").isEmpty)
  assert(DatabaseQuery.allMatches("giulis").isEmpty)

  /*Add user in the database*/
  DatabaseQuery.addUser("sara", "sariiin", "bcuhkd@hefew.it","ciaociao") // registrazione avvenuta
  DatabaseQuery.addUser("sara", "sariiin", "bcuhkd@hefew.it","ciaociao") //username is not valid

  /*Check login*/
  DatabaseQuery.checkLogin("sariiin", "ciaociao") //login riuscito
  DatabaseQuery.checkLogin("giulia", "")          //login riuscito
  DatabaseQuery.checkLogin("giulia", "ciao")      //password sbagliata.
  DatabaseQuery.checkLogin("fede", "fede")        //username sbagliato.

  /*Add match in the database */
  var matchgame = new MatchResultImpl
  matchgame.score = 1234
  matchgame.date = Calendar.getInstance()
  matchgame.result = true
  DatabaseQuery.addMatchResult("giulia",matchgame)
  DatabaseQuery.addMatchResult("sariiin",matchgame)
  DatabaseQuery.addMatchResult("fede", matchgame)

}
