import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class KiBlast extends GameChar {
	public static final String normal = "Ki_Blast.png";
	public static final String normalf = "Ki_Blast_Flipped.png";
	public static final String rotated = "Ki_Blast_2_Rotated.png";
	public static final String rotatedf = "Ki_Blast_2_Rotated_Flipped.png";
	public static final int DAMAGE = 7;
	public static final int MAX_DAMAGE = 30;
	public static final int SIZE = 40;
	public static final int INIT_VEL_X = 5;
	public static final int INIT_VEL_Y = 4;
	
	private BufferedImage img;
	private static BufferedImage img_horz;
	private static BufferedImage img_diag;
	private static BufferedImage img_horzf;
	private static BufferedImage img_diagf;
	
	public KiBlast(Player player) {
		// Initialize variables
		this.v_x = INIT_VEL_X;
		this.pos_x = player.pos_x;
		this.pos_y = player.pos_y;
		this.width = SIZE;
		this.height = SIZE;
		
		// take the width and height into account when setting the 
		// bounds for the upper left corner of the object.
		this.max_x = GameCourt.COURT_WIDTH - width;
		this.max_y = GameCourt.COURT_HEIGHT - height;
		
		this.health = -1;
		this.max_health = -1;
		
		if (pos_y != player.max_y) {
			v_y = INIT_VEL_Y;
			damage = DAMAGE + 1;
		} else {
			this.damage = DAMAGE;
		}
		
		try {
			if (img == null) {
				img_horz = ImageIO.read(new File(normal));
				img_horzf = ImageIO.read(new File(normalf));
				img_diag = ImageIO.read(new File(rotated));
				img_diagf = ImageIO.read(new File(rotatedf));
				img = img_horz;
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
	 * KiBlast can move anywhere on the screen, but if it ends up
	 * out of bounds, it will be removed by game court
	 */
	@Override
	public void clip() { }
	
	// returns whether the KiBlast is out of bounds
	public boolean outOfBounds() {
		if ((pos_x >= max_x + width || pos_x <= 0 - width) ||
			(pos_y >= max_y + height || pos_y <= 0 - height)) {
			return true;
		}
		return false;
	}
	
	// change image of ki blast
	public void changeImage(GameCourt.playerState state) {
		switch(state) {
			case NORMAL: case MOVE_R:
				img = img_horz;
				break;
			case MOVE_L:
				img = img_horzf;
				break;
			case MOVE_JUMP_L:
				img = img_diagf;
				break;
			case MOVE_JUMP_R: case JUMP: case BUMP:
				img = img_diag;
				break;
			default: break;
		}
	}
	
	// change the velocity of ki blast
	public void changeVelocity(GameCourt.playerState state) {
		switch(state) {
		case NORMAL: case MOVE_R:
			v_x = Math.abs(INIT_VEL_X);
			break;
		case MOVE_L:
			v_x = -Math.abs(INIT_VEL_X);
			break;
		case MOVE_JUMP_L:
			v_x = -Math.abs(INIT_VEL_X);
			v_y = Math.abs(INIT_VEL_Y);
			break;
		case MOVE_JUMP_R: case JUMP: 
			v_x = Math.abs(INIT_VEL_X);
			v_y = Math.abs(INIT_VEL_Y);
			break;
		default: break;
		}
	}
	
	public void increaseDamage(int factor) {
		damage = Math.min(MAX_DAMAGE, factor * DAMAGE);
	}
	
}
