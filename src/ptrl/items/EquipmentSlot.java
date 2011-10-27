package ptrl.items;

import java.io.Serializable;

public class EquipmentSlot implements Serializable
{
	public EquipmentSlot(int[] types)
	{
		itm=null;
		types_allowed=new boolean[Item.TYPE_NAMES.length];
		for (int i=0; i<types_allowed.length; i++)
			types_allowed[i]=false;
	
		for (int i=0; i<types.length; i++)
			types_allowed[types[i]]=true;
	}

	public Item getItem()
	{
		return itm;
	}
	
	public boolean setItem(Item i)
	{
		if (types_allowed[i.getType()]) 
		{
			itm=i;
			return true;
		}
		else return false;
	}
	
	public void makeEmpty()
	{
		itm=null;
	}
	
	public boolean[] getAllowedTypes()
	{
		return types_allowed;
	}
	
	public void setAllowedTypes(int[] types)
	{
		types_allowed=new boolean[Item.TYPE_NAMES.length];
		for (int i=0; i<types_allowed.length; i++)
			types_allowed[i]=false;
		for (int i=0; i<types.length; i++)
			types_allowed[types[i]]=true;
	}
	
	//private String name;
	
	private Item itm;
	private boolean[] types_allowed;
}
