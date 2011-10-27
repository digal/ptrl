package ptrl.combat;

import ptrl.creatures.Creature;

/**
 * Describes the attack. 
 * 
 * @author Digal
 */

public class Attack
{
	/**
	 * Creates an attack object
	 * 
	 * @param d damage.
	 * @param dt damage type (use DT_ constants).
	 * @param aid attacker id.
	 */
	public Attack(int d, int dt, Creature attacker, float th)
	{
		damage=d;
		damage_type=dt;
		this.attacker=attacker;
		//success=false;
		effects = new int[]{0,0,0,0,0,0};
		tohit=th;
	}
	
	/**
	 * Creates an attack object with spec. effects.
	 * 
	 * @param d damage.
	 * @param dt damage type.
	 * @param aid attacker id (use DT_ constants).
	 * @param e effects array (use E_ constants for indexes). 
	 */
	public Attack(int d, int dt, Creature attacker, float th, int[] e)
	{
		this(d,dt, attacker, th);
		effects = e;
	}
	
	public void setDamage(int d)
	{
		damage=d;
	}
	
	public int getDamage()
	{
		return damage;
	}
	public int getDamageType()
	{
		return damage_type;
	}
	
	public float getToHit()
	{
		return tohit;
	}
	
	
	
	private int damage_type;
	private int damage;
	//protected int from_x;
	//protected int from_y;
	private Creature attacker;
	private int source_x;
	private int source_y;	
	private int[] effects;
	private float tohit;
	public Creature getAttacker()
	{
		return attacker;
	}
}
