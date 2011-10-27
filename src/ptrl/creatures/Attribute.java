package ptrl.creatures;

import java.io.Serializable;

public class Attribute implements Serializable
{
	//TODO: attrib effects removing
	/**
	 * Method Attribute
	 * Constructor.
	 * 
	 * @param v Basic Value;
	 */
	public Attribute(int v) 
	{
		val=v;
		mod=0;
	}

	/**
	 * @returns effective value of attribute;
	 */
	public int getValue() 
	{
		int ret=val+mod+permanent;
		if (ret<0) ret=0;
		return ret;
	}

	public void setBasicValue(int v) 
	{
		val=v;
	}
	
	public void setPermanentModifier(int v)
	{
		permanent=v;
	}

	public int getBasicValue() 
	{
		return val;
	}	
	
	public int getPermanentValue() 
	{
		return val+permanent;
	}	
	
	
	public void inc()
	{
		val++;
	}

	public void dec()
	{
		if (val>0) val--;
	}
	
	public void addMod(AttribModifier am)
	{
		if (am.isPermanent()) permanent+=am.getMod();
		else mod+=am.getMod();
	}
	
	public void cleanMod()
	{
		mod=0;
	}
	
	
	private int val;
	private int permanent;
	private int mod;

}
