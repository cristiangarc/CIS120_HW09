public abstract class Zombie extends GameChar {
	protected static final int SIZE = 40;
	
	/** 
	 * Move zombie according to the position of the player.
	 */
	public abstract void move(Player player);
	
	/**
	 * Increase character's health by 15 * factor
	 * 
	 * @param factor
	 * A greater factor increases the character's health more
	 */
	public void increaseHealth(int factor) {
		health = Math.min(max_health, health  + 11 * factor);
	}
	
	public abstract int respawnPosX(int round);
}
