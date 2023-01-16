/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
	public void run() {
		// NOTE : recall that the 'final' keyword notes immutability
		// even for local variables.

		// Top-level frame in which game components live
		// Be sure to change "TOP LEVEL FRAME" to the name of your game
		final JFrame frame = new JFrame("Mini Zombie Shooter");
		frame.setLocation(300, 200);

		// Status panel
		final JPanel status_panel = new JPanel();
		frame.add(status_panel, BorderLayout.SOUTH);
		final JLabel status = new JLabel("Running...");
		status_panel.add(status);

		final JLabel roundLabel = new JLabel("Round " + 0);

		// Health points
		final JLabel player_health = new JLabel("Health: " + Player.INIT_HEALTH);
		// Player health bar
		HealthBar health_bar = new HealthBar();

		// Time count
		final JLabel player_time = new JLabel("Time: " + GameCourt.INIT_TIME + "s");

		// Player score
		final JLabel player_score = new JLabel("Score: " + 0);

		// Controls for after game ends
		final JPanel reset_control = new JPanel();

		// Stats for debugging the game
		final JPanel debugging_panel = new JPanel();
		final JLabel debugging_stats = new JLabel(
		"Velocity_x: " + 0 + "\n " +
		"Velocity_y: " + 0 + "\n " +
		"Zombies: " + 0 + "\n " +
		"Break Time: " + 0 + "s" + "\n " +
		"PokeBall Velocity_x: " + PokeBall.INIT_VEL_X);
		// final JLabel debugging_velx = new JLabel("Velocity_x: " + 0);
		// final JLabel debugging_vely = new JLabel("Velocity_y: " + 0);
		final JLabel debugging_zombies = new JLabel("Zombies: " + 0);

		// Main playing area
		final GameCourt court = new GameCourt(status, player_health, health_bar,
				player_score, player_time, reset_control, roundLabel, debugging_panel,
				debugging_stats);
		frame.add(court, BorderLayout.CENTER);

		// Control panel
		final JPanel control_panel = new JPanel();

		final JPanel player_stats = new JPanel();

		player_stats.add(roundLabel);
		player_stats.add(player_health);
		player_stats.add(health_bar);
		player_stats.add(player_time);
		player_stats.add(player_score);
		control_panel.add(player_stats, BorderLayout.NORTH);

		final JButton save_score = new JButton("Save Score");
		save_score.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				court.saveScore();
			}
		});
		final JButton see_scores = new JButton("See Highscores");
		see_scores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				court.displayScores();
			}
		});

		// Note here that when we add an action listener to the reset
		// button, we define it as an anonymous inner class that is
		// an instance of ActionListener with its actionPerformed()
		// method overridden. When the button is pressed,
		// actionPerformed() will be called.
		final JButton reset = new JButton("Play Again");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				court.reset();
			}
		});
		reset_control.add(reset);
		reset_control.add(save_score);
		reset_control.add(see_scores);
		reset_control.setVisible(false); // make invisible until game ends
		control_panel.add(reset_control, BorderLayout.SOUTH);
		control_panel.setPreferredSize(new Dimension (
			(int) control_panel.getPreferredSize().getWidth(),
			(int) (control_panel.getPreferredSize().getHeight() +
					reset_control.getPreferredSize().getHeight() //+
					// control_panel.getPreferredSize().getHeight()
					)));
		debugging_panel.add(debugging_stats);
		debugging_panel.setVisible(false); // make invisible until debugging starts
		court.add(debugging_panel, BorderLayout.NORTH);

		frame.add(control_panel, BorderLayout.NORTH);

		// Put the frame on the screen
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// Start game
		court.reset();

	}

	/*
	 * Main method run to start and run the game Initializes the GUI elements
	 * specified in Game and runs it IMPORTANT: Do NOT delete! You MUST include
	 * this in the final submission of your game.
	 */
	public static void main(String[] args) {
		String message =
				"Movement:\n" +
				"Move Left: Arrow Key Left\n" +
				"Move Right: Arrow Key Right\n" +
				"Jump: Arrow Key Up\n" +
				"Shoot: Spacebar\n\n" +
				"Objective:\nKill as many zombies before time runs out!";
		JOptionPane.showMessageDialog(null, message, "Controls", JOptionPane.INFORMATION_MESSAGE);
		SwingUtilities.invokeLater(new Game());
	}
}
