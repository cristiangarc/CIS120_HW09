import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class HealthBar extends JPanel {
	
	private int health = Player.INIT_HEALTH;
	
	public HealthBar() {
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g, health);
	}
	
	public void draw(Graphics g, int health) {
		g.setColor(Color.RED);
		g.fillRect(0, 0, health, 10);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(100, 10);
	}
	
	public void setHealth(int h) {
		health = h;
	}
}
