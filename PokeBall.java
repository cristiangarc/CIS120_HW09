import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PokeBall extends GameChar {
  // TODO: Update img
	public static String img = "";

  public static final int SIZE = 40;

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
}
