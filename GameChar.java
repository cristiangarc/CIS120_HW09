import java.awt.Graphics;

public abstract class GameChar {
	/** Current position of the object (in terms of graphics coordinates)
	 *  
	 * Coordinates are given by the upper-left hand corner of the object.
	 * This position should always be within bounds.
	 *  0 <= pos_x <= max_x 
	 *  0 <= pos_y <= max_y 
	 */
	public int pos_x;
	public int pos_y;

	/** Size of object, in pixels */
	public int width;
	public int height;
	
	/** Velocity: number of pixels to move every time move() is called */
	public int v_x;
	public int v_y;

	/** Upper bounds of the area in which the object can be positioned.  
	 *    Maximum permissible x, y positions for the upper-left 
	 *    hand corner of the object
	 */
	public int max_x;
	public int max_y;
	
	/** Each character has health, maximum health and damage. Different zombie
	 * 	types may have greater health, max_health, or damage or all three.
	 * If these fields do not apply, then the value is set to -1.
	 */
	public int health;
	public int damage;
	public int max_health;
	
	/**
	 * Moves the object by its velocity.  Ensures that the object does
	 * not go outside its bounds by clipping.
	 */
	public void move(){
		pos_x += v_x;
		pos_y += v_y;

		clip();
	}
	
	/**
	 * Prevents the object from going outside of the bounds of the area 
	 * designated for the object. (i.e. Object cannot go outside of the 
	 * active area the user defines for it).
	 */ 
	public void clip(){
		if (pos_x < 0) {
			pos_x = 0;
		} else if (pos_x > max_x) pos_x = max_x;

		if (pos_y < 0) {
			pos_y = 0;
		} else if (pos_y > max_y) {
			pos_y = max_y;
		}
	}
	
	/**
	 * Determine whether this game character is currently intersecting
	 * another object.
	 * 
	 * Intersection is determined by comparing bounding boxes. If the 
	 * bounding boxes overlap, then an intersection is considered to occur.
	 * 
	 * @param obj : other object
	 * @return whether this object intersects the other object.
	 */
	public boolean intersects(GameChar obj){
		return (pos_x + width >= obj.pos_x
				&& pos_y + height >= obj.pos_y
				&& obj.pos_x + obj.width >= pos_x 
				&& obj.pos_y + obj.height >= pos_y);
	}
	
	/**
	 * Determine whether this game character will intersect another in the
	 * next time step, assuming that both objects continue with their 
	 * current velocity.
	 * 
	 * Intersection is determined by comparing bounding boxes. If the 
	 * bounding boxes (for the next time step) overlap, then an 
	 * intersection is considered to occur.
	 * 
	 * @param obj : other object
	 * @return whether an intersection will occur.
	 */
	public boolean willIntersect(GameChar obj) {
		int next_x = pos_x + v_x;
		int next_y = pos_y + v_y;
		int next_obj_x = obj.pos_x + obj.v_x;
		int next_obj_y = obj.pos_y + obj.v_y;
		return (next_x + width >= next_obj_x
				&& next_y + height >= next_obj_y
				&& next_obj_x + obj.width >= next_x 
				&& next_obj_y + obj.height >= next_y);
	}
	
	/**
	 * Default draw method that provides how the object should be drawn 
	 * in the GUI. This method does not draw anything. Subclass should 
	 * override this method based on how their object should appear.
	 * 
	 * @param g 
	 *	The <code>Graphics</code> context used for drawing the object.
	 * 	Remember graphics contexts that we used in OCaml, it gives the 
	 *  context in which the object should be drawn (a canvas, a frame, 
	 *  etc.)
	 */
	public abstract void draw(Graphics g);
	
	/**
	 * Get character's health
	 * 
	 * @return
	 * Character's health
	 */
	public int getHealth() {
		return health;
	}
		
	/**
	 * Decreases character's health
	 * 
	 * @param x
	 * Decrease health by this
	 */
	public void takeDamage(int x) {
		health = Math.max(0, health - x);
	}
	
	/**
	 * 
	 * @return
	 * True if character's health has depleted. Otherwise, false.
	 */
	public boolean isDead() {
		if (health == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Check if character is in the air (not on ground)
	 * 
	 * @return
	 * True if character is in the air (not on ground), otherwise false.
	 */
	public boolean inAir() {
		return pos_y < max_y;
	}
		
	/**
	 * Sets the character's x-velocity
	 * 
	 * @param vx
	 * Character's new x-velocity
	 */
	public void setVelX(int vx) {
		v_x = vx;
	}
	
	/**
	 * Sets the character's y-velocity
	 * 
	 * @param vy
	 * Character's new y-velocity
	 */
	public void setVelY(int vy) {
		v_y = vy;
	}
}
