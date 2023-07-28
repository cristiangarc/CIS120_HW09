import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PokeBall extends GameChar {
  // TODO: Update img
	public static String pic = "pokeball.png";

	public static final int SIZE = 40;
	public static final int INIT_X = 0;
	public static final int INIT_Y = GameCourt.COURT_HEIGHT - SIZE;
	public static final int INIT_VEL_X = 5;
	public static final int INIT_VEL_Y = 0; // TODO: update to negative (falling)
  public static final int INIT_HEALTH = 1;
  public static final int DAMAGE = 1;

	private BufferedImage img;

  public PokeBall() {
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
		this.damage = DAMAGE;

		// Initialize player state images
		try {
			if (img == null) {
				img = ImageIO.read(new File(pic));
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
	 * PokeBall can move anywhere on the screen, but if it ends up
	 * out of bounds, it will be removed by game court
	 */
	@Override
	public void clip() { }

	// returns whether the PokeBall is out of bounds
	public boolean outOfBounds() {
		if ((pos_x >= max_x + width || pos_x <= 0 - width) ||
			(pos_y >= max_y + height || pos_y <= 0 - height)) {
			return true;
		}
		return false;
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

	/**
	 * move the pokeball by a set amount horizontally and vertically
	 */
	public void move() {
		pos_x += v_x;
		pos_y += v_y;
	}
}
