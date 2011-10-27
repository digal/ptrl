package ptrl.creatures;

import java.io.Serializable;

public class Skill implements Serializable
{
	/**
	 * Creates a skill
	 *
	 * @param n name
	 * @param v value
	 * @param l learning curve
	 */
	public Skill(int v, int a)
	{
		//name=n;
		value=v;
		lcurve=10;
		cur_pot=0;
		cur_marks=0;
		if (v==0) potential=0;
		else potential=v+1;
		main_attrib=a;
		primary=false;
	}
	
	public void makePrimary()
	{
		lcurve=7;
		primary=true;
	}
	
	/**
	 * Proves (rolls) a skill
	 * 1d[value]>=diff -> true (success)
	 * 1d[value]<diff ->false (failed)
	 *
	 * @param d difficulty
	 */
	public boolean prove(int d)
	{
		if (Math.round(Math.random()*getValue())>=d)
		{
			use();
			return true;
		}
		else return false;
	}
	
	/**
	 * @returns skill value
	 */
	public int getValue()
	{
		return value;
	}
	
	public void setValue(int v)
	{
		value=v;
		potential=v+1;
	}

	/**
	 * @returns skill potential (max aviable)
	 */
	
	public int getPotential()
	{
		return potential;
	}
	
	public int getAttrib()
	{
		return main_attrib;
	}
	/**
	 * Uses skill. Increments succsful marks counter.
	 */
	public void use()
	{
		if (++cur_marks>=Math.round(lcurve*(potential+cur_pot)))
		{
			cur_pot++;
		}
	}
	
	
	/**
	 * Increments skill value.
	 *
	 * @returns true if success.
	 */
	public boolean inc()
	{
		if (getValue()<getPotential()) 
		{
			value++;
			return true;
		}
		else return false;
	}
	
	public void dec()
	{
		value--;
	}
	
	public void refreshPotential()
	{
		if (cur_pot+getValue()>getPotential())
		{
			potential=cur_pot+getValue();
			cur_pot=0;
		}
	}
	
	public boolean isPrimary()
	{
		return primary;
	}
	
	//public static int MAIN_ATTRIB;	
	private int value;
	private int potential;
	private int cur_marks;
	private int cur_pot;
	private float lcurve;
	private int main_attrib;
	private boolean primary;
}
