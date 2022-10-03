=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README
PennKey: 68651736
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an approprate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. Collections - I use LinkedLists for storing the Zombies and the KiBlasts on the field, as
  		well as storing the high scores at the start of the game.

  2. I/O - At the beginning of the game I read through the highscores file and store
  		each name and corresponding score to a Score class. When the user asks to see
  		the scores at the end, I provide the top 15 scores and give the user the option
  		to store his/her score. The highscores are then updated with that user's score.

  3. Inheritance/Subtyping - I create a GameChar abstract class (similar to GameObj) but adding
  		additional methods and fields (such as health, damage and max_health). Both the
  		Player, zombies, and KiBlasts extend the GameChar class, with their own health,
  		max_health, and damage (if they apply) and their own unique methods as well. Two
  		types of zombies - RegularZombie and PirateZombie - extend the Zombie abstract class.

  4. AI - I made a simple algorithm for the zombies (wherever they are on playing field),
  		to chase the player as the player moves.


=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
  
	Player - this creates the player that the user controls.
	Zombie - abstract class for creating a zombie; implements the shared method
			increaseHealth(int x), which increases the zombie's health when the
			zombie spawns.
	RegularZombie - a type of zombie with low damage and starting health.
	PirateZombie - a zombie with greater damage and starting health than
			a RegularZombie
	KiBlast - the player uses KiBlasts as a weapon to attack the zombies. Once a
			blast hits a zombie, the zombie is damaged and it disappears.
	HealthBar - draws the player's health as a filled red rectangle. The rectangle
			gets smaller as the player health decreases.
	Score - keeps track of the highscores in the text file containing the highscores
			of players that have played the game. Adds the current player's score to
			the highscores text if the player decides to be added.
			

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
  
  	When I was extending the Zombie abstract class, it was difficult to give each type
  of zombie its own image while not interfering with the image of the other zombie instances.
  
  Implementing the I/O also took a good amount of time, since I needed a way for the
  user to request his/her score to be saved and for the user to input a username.
  Furthermore, I needed to format that username in order to limit the character length
  of the username and to be able to parse through the username to get the name and score.
  I also needed to prevent the user from storing the same score twice and informing the
  user that the score has already been saved.


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

	I think there is a good amount of separation of functionality. The player,
	zombie and KiBlast classes all have their own drawComponents() implementation
	and the GameCourt was the object to call those methods. Furthermore, for a lot
	of player movement, the gamecourt is able to call on the player's methods to make
	the player either change direction or jump. The KeyListeners enable the user to
	change the state of the game, and as a result, the gamecourt uses that new state
	to make necessary changes.
	
	I think making a lot of the fields in GameCourt private helps encapsulate the state. However,
	I did not make all of the fields for every class private, so I would make as many of those
	fields private (if possible) if I were to refactor my code.


========================
=: External Resources :=
========================

- Cite any external resources (libraries, images, tutorials, etc.) that you may
  have used while implementing your game.
  
  	All of the images I used for the Player, PirateZombie, RegularZombie, and KiBlast
  	classes were found through Google image searches:
  		https://www.google.com/
  	
  	I referred often to the JavaDocs for information about the Java Swing library,
  	methods, and general information about classes, interfaces, etc:
  		https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html
  	
  	I also referred
  	to Java website's tutorial on Java Swing:
  		https://docs.oracle.com/javase/tutorial/uiswing/

