/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;


/**
 * GameCourt
 *
 * This class holds the primary game logic for how different objects interact
 * with one another. Take time to understand how the timer interacts with the
 * different methods and how it repaints the GUI on every tick().
 *
 */
@SuppressWarnings("serial")
public class GameCourt extends JPanel {

	// the state of the game logic
	//private Circle snitch; // the Golden Snitch
	private Player player;
	private List<Zombie> zombies;
	private List<KiBlast> kiBlasts;

	private List<Score> scores;

	// player motion state: player can be moving, jumping,
	// not moving, or bump into a zombie...
	public static enum playerState {
		NORMAL,
		MOVE_L,
		MOVE_R,
		JUMP,
		MOVE_JUMP_L,
		MOVE_JUMP_R,
		BUMP,
	}

	// number of jumps so far
	// resets when player touches the ground after a jump
	private int jump_count;

	private boolean playing = false; // whether the game is running
	private JLabel status; // Current status text (i.e. Running...)
	private JLabel health_status; // player health
	private JLabel player_time;
	private HealthBar health_bar; // player health bar
	private JLabel score_bar; // player score
	private JPanel reset_control;
	private JLabel roundLabel;

	private boolean round_end = false; // if round has ended

	private playerState player_state; // the player state
	private int round; // current round
	private int score = 0; // player score
	private int time;
	private int kill_count = 0; // zombies killed this round
	private int max_zombies; // maximum zombies this round
	private int zombies_spawned = 0;

	private String userName;
	private String high_scores = "HighScores.txt";

	// Game constants
	public static final int COURT_WIDTH = 500;
	public static final int COURT_HEIGHT = 300;
	public static final int GRAVITY = 1;
	public static final int INIT_TIME = 61;
	public static final String DEFAULT_NAME = "AAAAAA";
	public static final char DEFAULT_CHAR = '_';

	// Update interval for timer, in milliseconds
	// originally 35
	public static final int INTERVAL1 = 35;
	public static final int INTERVAL2 = 1000;

	private int count2 = 0; // used for counting seconds in tick2()

	public GameCourt(JLabel status, JLabel health_status, HealthBar health_bar, JLabel score_bar,
			JLabel player_time, JPanel reset_control, JLabel roundLabel) {
		// creates border around the court area, JComponent method
		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// The timer is an object which triggers an action periodically
		// with the given INTERVAL. One registers an ActionListener with
		// this timer, whose actionPerformed() method will be called
		// each time the timer triggers. We define a helper method
		// called tick() that actually does everything that should
		// be done in a single timestep.
		Timer timer1 = new Timer(INTERVAL1, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		timer1.start(); // MAKE SURE TO START THE TIMER!

		Timer timer2 = new Timer(INTERVAL2, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick2();
			}
		});
		timer2.start();

		// Enable keyboard focus on the court area.
		// When this component has the keyboard focus, key
		// events will be handled by its key listener.
		setFocusable(true);

		// This key listener allows the square to move as long
		// as an arrow key is pressed, by changing the square's
		// velocity accordingly. (The tick method below actually
		// moves the square.)
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					switch(player_state) {
					case NORMAL:
						player_state = playerState.MOVE_L;
						syncVelocity();
						player.changeImage(player_state);
						break;
					case JUMP:
						player_state = playerState.MOVE_JUMP_L;
						syncVelocity();
						player.changeImage(player_state);
						break;
					case BUMP:
						if (player.inAir()) {
							player_state = playerState.MOVE_JUMP_L;
							syncVelocity();
						}
						break;
					default: break;
					}
				}
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					switch(player_state) {
					case NORMAL:
						player_state = playerState.MOVE_R;
						syncVelocity();
						player.changeImage(player_state);
						break;
					case JUMP:
						player_state = playerState.MOVE_JUMP_R;
						syncVelocity();
						player.changeImage(player_state);
						break;
					case BUMP:
						if (player.inAir()) {
							player_state = playerState.MOVE_JUMP_R;
							syncVelocity();
						}
						break;
					default: break;
					}
				}
				else if (e.getKeyCode() == KeyEvent.VK_UP) {
					switch(player_state) {
					case MOVE_L:
						player_state = playerState.MOVE_JUMP_L;
						player.jump();
						jump_count++;
						break;
					case MOVE_R:
						player_state = playerState.MOVE_JUMP_R;
						player.jump();
						jump_count++;
						break;
					case NORMAL:
						player_state = playerState.JUMP;
						player.jump();
						jump_count++;
						player.changeImage(player_state);
						break;
					case JUMP: case MOVE_JUMP_L: case MOVE_JUMP_R: case BUMP:
						if (jumpsRemain()) {
							player.jump();
							jump_count++;
						}
						break;

					default: break;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (kiBlasts.size() < 8) {
						KiBlast kb = new KiBlast(player);
						kb.changeImage(player_state);
						kb.changeVelocity(player_state);
						kb.increaseDamage(round);
						kiBlasts.add(kb);
					}
				} else if (e.getKeyCode() == KeyEvent.VK_F7) {
					// game state changes to debug mode
					debug_mode = true;
				}
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					switch(player_state) {
					case MOVE_L:
						player_state = playerState.NORMAL;
						syncVelocity();
						player.changeImage(player_state);
						break;
					case MOVE_JUMP_L:
						player_state = playerState.JUMP;
						syncVelocity();
						player.changeImage(player_state);
						break;
					default: break;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					switch(player_state) {
					case MOVE_R:
						player_state = playerState.NORMAL;
						syncVelocity();
						player.changeImage(player_state);
						break;
					case MOVE_JUMP_R:
						player_state = playerState.JUMP;
						syncVelocity();
						player.changeImage(player_state);
						break;
					default: break;
					}
				}
			}
		});

		this.status = status;
		this.health_status = health_status;
		this.health_bar = health_bar;
		this.score_bar = score_bar;
		this.player_time = player_time;
		this.reset_control = reset_control;
		this.roundLabel = roundLabel;
	}

	/**
	 * (Re-)set the game to its initial state.
	 */
	public void reset() {
		player = new Player();
		score = 0;
		time = INIT_TIME;
		score_bar.setText("Score: " + score);
		kill_count = 0;
		jump_count = 0;
		zombies_spawned = 0;

		kiBlasts = new LinkedList<>();

		playing = true;
		status.setText("Running...");
		round = 1;
		updateRoundLabel();
		zombies = new LinkedList<>();
		max_zombies = totalRoundZombies();
		initialRespawn();

		player_state = playerState.NORMAL;
		health_status.setText("Health: " + Player.INIT_HEALTH);
		health_bar.setHealth(Player.INIT_HEALTH);
		reset_control.setVisible(false);

		// Make sure that this component has the keyboard focus
		requestFocusInWindow();
		storeScores(); // store the old scores at the start of game
	}

	/**
	 * This method is called every time the timer defined in the constructor
	 * triggers.
	 */
	void tick() {
		if (playing) {
			// advance the player and zombies in their current direction.
			player.move();
			moveKiBlasts();
			moveZombies();

			// impose gravity on the player if in air and has been bumped by a zombie
			switch(player_state) {
			case BUMP:
				syncBump();
				break;
			default: break;
			}

			// damage the player if hit by a zombie and recoil the player
			// check for the game end conditions
			Zombie z = bumpZombie();
			if (z != null) {
				player.recoil(z.pos_x);
				damagePlayer(z.damage);
				jump_count = Math.max(0, jump_count - 1); // enable an extra jump
			}

			// damage zombies that are hit by KiBlasts and
			// update player score
			int killed = blastZombies();

			// remove all KiBlasts that are out of bounds
			kiBlasts.removeAll(kisOutOfBounds());

			// synchronize player's jumps so that player returns to a
			// non-JUMP state when ground is reached
			syncJump();

			// check if player is dead
			if (player.isDead()) {
				gameLost();
				reset_control.setVisible(true);
			}

			// spawn more zombies if round has not ended and
			// max zombie limit has not been reached
			if (!round_end && zombies_spawned < max_zombies) {
				respawnZombies(killed);
			}

			roundEnded(); // check if round ended

			// update the display
			repaint();
		}
	}

	/**
	 * Decrement time. If round has ended, increment round and
	 * reset game state to the start of a new round
	 */
	void tick2() {
		if (playing) {
			if (count2 == 0) {
				time--;
				player_time.setText("Time: " + time + "s");
				if (time == 0) {
					gameLost();
					reset_control.setVisible(true);
				}
				if (round_end) {
					round++; // next round
					updateRoundLabel();
					zombies_spawned = 0;
					max_zombies = totalRoundZombies();
					count2++; // count 1 second
				}
			} else {
				if (count2 > 0) {
					if (count2 == 6) {
						round_end = false;
						kill_count = 0;
						count2 = 0;
						initialRespawn();
					} else {
						count2++;
					}
				}
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawZombies(g);
		player.draw(g);
		drawKiBlasts(g);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(COURT_WIDTH, COURT_HEIGHT);
	}

	/**
	 * Change player state
	 * @param state
	 * the new player state
	 */
	public void setPlayerState(playerState state) {
		player_state = state;
	}

	/**
	 * move each kiblast on the field
	 */
	public void moveKiBlasts() {
		for (KiBlast k : kiBlasts) {
			k.move();
		}
	}

	/**
	 * draw each kiblast on the field
	 * @param g
	 */
	public void drawKiBlasts(Graphics g) {
		for (KiBlast k : kiBlasts) {
			k.draw(g);
		}
	}

	/**
	 * move each zombie on the field
	 */
	public void moveZombies() {
		for (Zombie z : zombies) {
			z.move(player);
		}
	}

	/**
	 * draw each zombie on the field
	 * @param g
	 */
	public void drawZombies(Graphics g) {
		for (Zombie z : zombies) {
			z.draw(g);
		}
	}

	/**
	 * Update the score displayed
	 */
	public void updateScore() {
		score_bar.setText("Score: " + score);
	}

	/**
	 * Returns the zombie that the player bumped into
	 * @return
	 * Zombie that player bumped into or null if all clear
	 */
	public Zombie bumpZombie() {
		for (Zombie z : zombies) {
			if (player.intersects(z)) {
				player_state = playerState.BUMP;
				return z;
			}
		}
		return null;
	}

	/**
	 * Damage the player according to which zombie hits the player, and
	 * update health bar with new health
	 */
	public void damagePlayer(int d) {
		player.takeDamage(d);
		health_status.setText("Health: " + player.getHealth());
		health_bar.setHealth(player.getHealth());
		health_bar.repaint();
	}

	/**
	 * inflicts damage on zombies based on which KiBlast hits
	 * @param k
	 * the ki blast that is fired
	 * @param kbs
	 * the set of kiblasts that will be removed from the field
	 * (removed if they touch a zombie)
	 * @return
	 * total number of zombies that are dead
	 */
	public List<Zombie> damageZombies(KiBlast ki, List<KiBlast> kbs) {
		List<Zombie> zbs = new LinkedList<>();
		for (Zombie zom : zombies) {
			if (ki.intersects(zom)) {
				zom.takeDamage(ki.damage);
				if (zom.isDead()) {
					zbs.add(zom);
					score += zom.damage * 10;
				}
				kbs.add(ki);
				break;
			}
		}
		return zbs;
	}

	/**
	 * Inflict damage on all zombies with the KiBlasts on the field (if there are any).
	 * Remove the KiBlasts that hit zombies as well as any dead zombies
	 * @return
	 * The number of zombies killed during this tick
	 */
	public int blastZombies() {
		int killed = 0;
		if (!kiBlasts.isEmpty()) {
			List<KiBlast> kbs = new LinkedList<>();
			List<Zombie> zbs = new LinkedList<>();
			for (KiBlast k : kiBlasts) {
				List<Zombie> hit = damageZombies(k, kbs);
				zbs.addAll(hit);
			}
			killed = zbs.size();
			if (killed != 0) {
				kill_count += killed;
				updateScore();
			}
			kiBlasts.removeAll(kbs);
			zombies.removeAll(zbs);
		}
		return killed;
	}

	/**
	 * Removes all ki blasts from the field if they are out of bounds
	 * @return
	 * the KiBlasts that are out of bounds
	 */
	public List<KiBlast> kisOutOfBounds() {
		List<KiBlast> kbs = new LinkedList<>();
		for (KiBlast k : kiBlasts) {
			if (k.outOfBounds()) {
				kbs.add(k);
			}
		}
		return kbs;
	}

	/**
	 * Algorithm for determining the total number of zombies for the
	 * current round
	 * @return
	 * the total number of zombies for the round
	 */
	public int totalRoundZombies() {
		if (round == 1) {
			return 5;
		}
		int i = Math.max(0, 5 * round);
		return i;
	}

	/**
	 * Determines the initial zombies spawned as a factor of the round
	 * @return
	 * the number of zombies spawned at the beginning of the round
	 */
	public int initialRespawn() {
		int i;
		for (i = 0; i < 2 * round; i++) {
			if (zombies.size() < 16 && zombies_spawned < max_zombies) {
				double random = (i + 1) * Math.random();
				if (random < i * round) {
					Zombie z = new RegularZombie();
					z.pos_x += i * 20;
					z.increaseHealth(round);
					zombies.add(z);
				} else {
					Zombie z = new PirateZombie();
					z.pos_x += i * 20;
					z.increaseHealth(round);
					zombies.add(z);
				}
				zombies_spawned++;
			} else {
				break;
			}
		}
		return i;
	}

	/**
	 * Respawn zombies (call this after the initial zombie respawn)
	 * pseudorandomly
	 * @param killed
	 * Respawn zombies according to the number of zombied killed
	 */
	public void respawnZombies(int killed) {
		if (killed > 0) {
			// int x = Math.max(1, killed / 2);
			for (int zbs = 0; zbs < killed; zbs++) {
				if (zombies_spawned + zbs < max_zombies) {
					randomSpawn();
					zombies_spawned++;
				}
			}
		}
	}

	/**
	 * Respawn one zombie at its respawn position and increase its health.
	 * Called after the initial respawn of zombies
	 */
	public void respawnRegular() {
		Zombie z = new RegularZombie();
		z.pos_x = z.respawnPosX(round);
		z.increaseHealth(round);
		zombies.add(z);
	}

	/**
	 * Respawn cowboy zombie
	 */
	public void respawnPirate() {
		Zombie z = new PirateZombie();
		z.pos_x = z.respawnPosX(round);
		z.increaseHealth(round);
		zombies.add(z);
	}

	/**
	 * Spawn a random zombie
	 */
	public void randomSpawn() {
		if (kill_count * 1.5 * Math.random() > max_zombies) {
			respawnPirate();
		} else {
			respawnRegular();
		}
	}

	/**
	 * Update player velocities according the current player state
	 */
	public void syncVelocity() {
		switch(player_state) {
		case NORMAL:
			player.v_x = 0;
			player.v_y = 0;
			break;
		case MOVE_L:
			player.runLeft();
			player.v_y = 0;
			break;
		case MOVE_R:
			player.runRight();
			player.v_y = 0;
			break;
		case JUMP:
			player.v_x = 0;
			break;
		case MOVE_JUMP_L:
			player.runLeft();
			break;
		case MOVE_JUMP_R:
			player.runRight();
			break;
		default: break;
		}
	}

	/**
	 * Once a damaged player hits the ground, return to normal state
	 */
	public void syncBump() {
		if (!player.inAir()) {
			player_state = playerState.NORMAL;
			syncVelocity();
			player.changeImage(player_state);
			jump_count = 0;
		}
	}

	/**
	 * Impose gravity on the player's jumps.
	 * If player has returned from a jump, update velocities accordingly
	 * and reset the player's jump count
	 */
		public void syncJump() {
			if (!player.inAir()) {
				switch(player_state) {
				case MOVE_JUMP_L:
					player_state = playerState.MOVE_L;
					syncVelocity();
					break;
				case MOVE_JUMP_R:
					player_state = playerState.MOVE_R;
					syncVelocity();
					break;
				case JUMP:
					player_state = playerState.NORMAL;
					syncVelocity();
					player.changeImage(player_state);
					break;
				default: break;
				}
				jump_count = 0;
			} else {
				player.v_y += Math.abs(GRAVITY);
			}
		}

	/**
	 * Check if player has used up all of his/her jumps in the air
	 * @return
	 * True if the player has remaining jumps in the air, false otherwise
	 */
	public boolean jumpsRemain() {
		return jump_count < Player.MAX_JUMPS;
	}

	/**
	 * The player has won so update the game state
	 */
	public void gameWon() {
		playing = false;
		status.setText("You Win!");
	}

	/**
	 * The player has lost so update the game state
	 */
	public void gameLost() {
		playing = false;
		if (time == 0) {
			status.setText("Time's Up!");
		} else {
			status.setText("You Lose!");
		}
	}

	/**
	 * check round ending conditions
	 * @return
	 * true if all zombies are killed. False otherwise;
	 */
	public void roundEnded() {
		if (kill_count >= max_zombies) {
			round_end = true;
		}
	}

	public void updateRoundLabel() {
		roundLabel.setText("Round " + round);
	}

	/*
	 * These methods are getter methods for getting characteristics about the player
	 */

	public int getPlayerPX() {
		return player.pos_x;
	}

	public int getPlayerPY() {
		return player.pos_y;
	}

	public int getPlayerVX() {
		return player.v_x;
	}

	public int getPlayerVY() {
		return player.v_y;
	}

	public int getPlayerMax_X() {
		return player.max_x;
	}

	/*
	 * These methods are for reading scores from the file HighScores.
	 * The scores are stored so as to be displayed if invoked.
	 *
	 */

	/**
	 * Display the current high scores. Call after the scores have been saved.
	 */
	public void displayScores() {
		Collections.sort(scores);

		int count = 1;
		String display = "";
		for (Score s : scores) {
			if (count < 16) {
				display += count + ". ";
				display += s.getName() + " ";
				display += s.getScore() + "\n";
				count++;
			}
		}
		JOptionPane.showMessageDialog(null, display, "Highscores (Name - Score)",
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Formats the user typed name to make File reading easier
	 * @param name
	 * The user typed name
	 * @return
	 * The user's formatted name
	 */
	public String clipUserName(String name) {
		String newName = "";
		int length = name.length();
		for (int i = 0; i < 6; i++) {
			if (i >= length || name.charAt(i) == ' ') {
				newName += DEFAULT_CHAR;
			} else {
				newName += name.charAt(i);
			}
		}
		return newName;
	}

	/**
	 * Save the player's name and score on the high score leaderboards.
	 * The top 15 scores will be saved.
	 */
	public void saveScore() {
		if (userName == null) {
			int confirmation = JOptionPane.showConfirmDialog(null, "Save score?");
			if (confirmation == 0) {
				userName = JOptionPane.showInputDialog("Create a Username " +
								"(Max 6 Characters Will Be Stored):");
				if (userName != null) {
					if (userName.length() == 0) {
						userName = DEFAULT_NAME;
					}
					userName = clipUserName(userName);
					try {
						BufferedWriter out = new BufferedWriter(new FileWriter(high_scores, true));
						out.newLine();
						out.append(userName);
						out.append(" " + score);
						out.flush();
						out.close();
						scores.add(new Score(userName, score));
					} catch (IOException e) {
						System.out.print("IOException: " + e);
					}
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "Score already saved");
		}
	}

	/**
	 * Store all of the high scores if not already done so.
	 */
	public void storeScores() {
		if (scores == null) {
			scores = new LinkedList<>();
			try {
				BufferedReader buff = new BufferedReader(new FileReader(high_scores));
				readScores(buff);
			} catch (IOException e) {
				System.out.println("IOException: "+ e);
			}
		}
	}

	/**
	 * Read all of the names and scores in the high scores leaderboards
	 * @param buff
	 * The buffer reader
	 * @throws IOException
	 */
	public void readScores(BufferedReader buff) throws IOException {
		String currName = "";
		int currScore = 0;
		String line = buff.readLine();
		while (line != null) {
			currName = readName(line);
			currScore = readScore(skipName(line));
			scores.add(new Score(currName, currScore));
			line = buff.readLine();
		}
	}

	/**
	 * Returns the name part of the file line
	 * @param line
	 * The current line
	 * @return
	 * The name
	 */
	public String readName(String line) {
		String word = "";
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (Character.isAlphabetic(c) || c == DEFAULT_CHAR) {
				word += c;
			}
		}
		return word;
	}

	/**
	 * Reads the score part of the file line
	 * @param word
	 * The current line
	 * @return
	 * The score
	 */
	public int readScore(String word) {
		String s = "";
		if (word.length() == 0) {
			return 0;
		} else {
			for (int i = 0; i < word.length(); i++) {
				char c = word.charAt(i);
				if (Character.isDigit(c)) {
					s += c;
				}
			}
			return Integer.parseInt(s);
		}
	}

	/**
	 * Returns the score part of the line read by the reader
	 * @param line
	 * The line that is read by the BufferedReader
	 * @return
	 * returns the score of the user
	 */
	public String skipName(String line) {
		int space = line.indexOf(' ');
		if (space != -1) {
			return line.substring(space + 1, line.length());
		} else {
			return "";
		}
	}

}
