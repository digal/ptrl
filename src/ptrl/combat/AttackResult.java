package ptrl.combat;

import ptrl.creatures.Creature;

/**
 * Describes results of the attack.
 * 
 * @author Digal
 */
public class AttackResult
{
	/**
	 * Creates a result.
	 * 
	 * @param tid target's ID.
	 * @param d damage caused.
	 * @param x target x.
	 * @param y target y.
	 */
	public AttackResult(Creature target, int d, int x, int y)
	{
		this.target=target;
		damage=d;
		hit_x=x;
		hit_y=y;
	}

	/**
	 * 
	 * @return damage caused.
	 */
	public int getDamage()
	{
		return damage;
	}
	
	/**
	 * @return target x.
	 */
	public int getX()
	{
		return hit_x;
	}

	/**
	 * @return target y.
	 */
	public int getY()
	{
		return hit_y;
	}
	
	public Creature getTarget()
	{
		return target;
	}
	
	private Creature target; //-1 if none
	private int damage;
	private int hit_x;
	private int hit_y;


}
