import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Player extends GameChar {
	
	public static final String normal = "Super_Saiyan_Vegeta.png";
	public static final String moveR = "Vegeta_Moving.png";
	public static final String moveL = "Vegeta_Moving_Flipped.png";
	public static final String jump = "Vegeta_Jump.png";
	public static final int SIZE = 40;
	public static final int INIT_X = 0;
	public static final int INIT_Y = GameCourt.COURT_HEIGHT - 7 * SIZE / 4;
	public static final int INIT_VEL_X = 0;
	public static final int INIT_VEL_Y = 0;
	public static final int INIT_HEALTH = 100;
	public static final int RECOIL_VEL = 4;
	public static final int MOVE_VEL = 4;
	public static final int JUMP_VEL = 10;
	public static final int MAX_JUMPS = 2;

	private static BufferedImage img;
	private static BufferedImage img_normal;
	private static BufferedImage img_moveR;
	private static BufferedImage img_moveL;
	private static BufferedImage img_jump;

	public Player() {
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
		this.max_health = INIT_HEALTH;
		this.damage = -1;
		
		// Initialize player state images
		try {
			if (img == null) {
				img_normal = ImageIO.read(new File(normal));
				img_moveR = ImageIO.read(new File(moveR));
				img_moveL = ImageIO.read(new File(moveL));
				img_jump = ImageIO.read(new File(jump));
				img = img_normal;
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(img, pos_x, pos_y, width, height, null);
	}
	
	/**
	 * Make the player intersection with other characters be closer
	 */
	@Override
	public boolean intersects(GameChar obj){
		return (pos_x + width >= obj.pos_x + obj.width / 2
				&& pos_y + height >= obj.pos_y
				&& obj.pos_x + obj.width >= pos_x 
				&& obj.pos_y + obj.height >= pos_y);
	}
	
	public void changeImage(GameCourt.playerState state) {
		switch(state) {
			case NORMAL:
				img = img_normal;
				break;
			case MOVE_L: case MOVE_JUMP_L:
				img = img_moveL;
				break;
			case MOVE_R: case MOVE_JUMP_R:
				img = img_moveR;
				break;
			case JUMP:
				img = img_jump;
				break;
			default: break;
		}
	}
	
	public void jump() {
		v_y = -Math.abs(Player.JUMP_VEL);
	}
	
	public void runLeft() {
		v_x = -Math.abs(Player.MOVE_VEL);
	}
	
	public void runRight() {
		v_x = Math.abs(Player.MOVE_VEL);
	}
	
	public void recoilLeft() {
		v_x = -Math.abs(Player.RECOIL_VEL);
	}
	
	public void recoilRight() {
		v_x = Math.abs(Player.RECOIL_VEL);
	}
	
	public void recoilVertical() {
		v_y = -Math.abs(RECOIL_VEL);
	}
	
	public void recoil(int zpx) {
		if (pos_x < zpx) {
			recoilLeft();
		} else {
			recoilRight();
		}
		recoilVertical();
	}
}
