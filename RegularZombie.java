import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RegularZombie extends Zombie {
	public static String img_l = "Regular_Zombie.png";
	public static String img_r = "Regular_Zombie_Flipped.png";
	
	public static final int INIT_HEALTH = 4;
	public static final int MAX_HEALTH = 100;
	public static final int DAMAGE = 2;
	
	public static final int INIT_X = GameCourt.COURT_WIDTH + 30 - SIZE;
	public static final int INIT_X_2 = -20;
	public static final int INIT_Y = GameCourt.COURT_HEIGHT - 7 * SIZE / 4;
	public static final int INIT_VEL_X = -1;
	public static final int INIT_VEL_Y = 0;
	
	private BufferedImage img;
	private static BufferedImage img_left;
	private static BufferedImage img_right;
	
	public RegularZombie() {
		// Initialize variables
		this.v_x = INIT_VEL_X;
		this.v_y = INIT_VEL_Y;
		this.pos_x = INIT_X;
		this.pos_y = INIT_Y;
		this.width = SIZE;
		this.height = 7 * SIZE / 4;
		
		// take the width and height into account when setting the 
		// bounds for the upper left corner of the object.
		this.max_x = GameCourt.COURT_WIDTH - width;
		this.max_y = GameCourt.COURT_HEIGHT - height;
		
		this.health = INIT_HEALTH;
		this.max_health = MAX_HEALTH;
		this.damage = DAMAGE;
				
		// Initialize player state images
		try {
			if (img_left == null || img_right == null) {
				img_left = ImageIO.read(new File(img_l));
				img_right = ImageIO.read(new File(img_r));
				img = img_left;
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}
	
	/** 
	 * Move zombie towards player. If player jumps over zombie, 
	 * zombie waits until player gets back down.
	 */
	public void move(Player player) {
		if (player.pos_y == player.max_y) {
			if (pos_x > player.pos_x) {
				v_x = -Math.abs(INIT_VEL_X);
				img = img_left;
			} else if (pos_x < player.pos_x) {
				v_x = Math.abs(INIT_VEL_X);
				img = img_right;
			}
		} else {
			if ((pos_x >= player.pos_x && v_x > 0) ||
				(pos_x <= player.pos_x && v_x < 0)) {
				v_x = 0;
			}
		}
		pos_x += v_x;
		pos_y += v_y;
	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(img, pos_x, pos_y, width, height, null);
	}
	
	public int respawnPosX(int round) {
		if (round > 1) {
			if (Math.random() > 0.75) {
				return INIT_X_2;
			} else {
				return INIT_X + 20;
			}
		} else {
			return INIT_X + 20;
		}
	}
}
