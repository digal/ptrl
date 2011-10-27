package ptrl.combat;

import java.io.Serializable;

/**
 * Describes the creature's resistance to attacks.
 *  
 * @author Digal
 */
public class Protection implements Serializable
{
	/**
	 * Creates the Protection object.
	 * 
	 * @param a armor - abilities to decrease damage taken, from attacks of various types. See Attack DT_ constants for possible types.
	 * @param d defence - decreases chance to be hit. 
	 */
	public Protection(int[] a, float d)
	{
		armor=a;
		defence=d;
	}
	
	/**
	 * Determines, if the creature will be hit by attack. 
	 * 
	 * @param a attack taken.
	 * @return true if hit.
	 */
	public boolean isHit(Attack a)
	{
		if (Math.random()*a.getToHit()>Math.random()*defence) return true;
		else return false;
	}
	
	/**
	 * Determines damage taken.
	 * 
	 * @param a attack taken.
	 * @return damage taken.
	 */
	public int getAttackDamage(Attack a)
	{
		return a.getDamage()-armor[a.getDamageType()];
	}

	public int[] getArmor()
	{
		return armor;
	}

	public float getDefence()
	{
		return defence;
	}
	
	private int[] armor;
	private float defence;

}
