package clientModel.model

import clientModel.model.gameElement.Eatable


/**
  * Created by margherita on 11/07/17.
  */
trait View {

  //controller notifica la view quando un personaggio muore (per cancellarne l'immagine)
  def deathOfACharacter(character: Character): Unit

  //controller notifica la view quando un eatable viene mangiato (per cancellarne l'immagine)
  def eatenObject(eaten: Eatable): Unit

  //controller notifica la view quando pacman muore -> fine del gioco PER TUTTI
  def pacmanDeath(character: Character): Unit

  //controller notifica la view sulle vite rimanenti del MAIN CHARACTER
  def remainingLives(lives: Int): Unit

  //controller notifica la view sullo score del MAIN CHARACTER
  def score(score: Int): Unit

  //controller notifica la view quando i ghosts diventano killable e pacman diventa il predatore (un personaggio alla vola)
  def setKillableOrNot(character: Character, isKillable: Boolean)

  //controller notifica la view quando il MAIN CHARACTER si sta muovendo (ovvero quando l'utente preme una delle frecce)
  def setMainCharacterDirection(/*direction: Direction*/)

  //come fa a capire che gli altri personaggi sistanno muovendo? Glielo dicono gli altri client? allora sarebbe carino avere due interfacce per la view:
  //  - ViewLocale
  //  - ViewDistribuita

}