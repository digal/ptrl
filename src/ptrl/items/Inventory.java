package ptrl.items;

import java.io.Serializable;
import java.util.ArrayList;

public class Inventory implements Serializable
{

	public Inventory()
	{
			items = new Item[Item.TYPE_NAMES.length][];
			for (int i=0; i<items.length; i++)
			{
				items[i]=new Item[0];
			}
	}
	
	public int getMaxWeight()
	{
		return max_weight;
	}

	public void setMaxWeight(int mw)
	{
		max_weight=mw;
	}

	public void AddItem(Item i)
	{
		if (i.getType()>=items.length) return; 
		addItemToSection(i, i.getType());
	}
	
	private void addItemToSection(Item it, int s)
	{
		if (it==null) return;
		int l=1;
		if (items[s]!=null)	l = items[s].length;
		else 
		{
			items[s]=new Item[]{it};
			return;
		}
		for (int i=0; i<l; i++)
		{
			if (it.equals(items[s][i]))
			{
				items[s][i].add(it.getQty());
				return;
			}
		}
		Item[] newsec= new Item[l+1];
		for (int i=0; i<l; i++)
		{
			newsec[i]=items[s][i];
		}
		newsec[l]=it;
		items[s]=newsec;
	}

	public Item removeItem(int section, int n)
	{
		Item itm = items[section][n];
		Item[] newsec=new Item[items[section].length-1];
		for (int i=0; i<n; i++) newsec[i]=items[section][i];
		for (int i=n+1; i<items[section].length; i++)
		{
			newsec[i-1]=items[section][i];
		}
		items[section]=newsec;
		return itm;
	}
	
	public Item[][] getSections()
	{
		return items;
	}
	
	public void clean(int sect)
	{
		ArrayList new_sec=new ArrayList();
		for (int i=0; i<items[sect].length; i++)
		{
			if (items[sect][i].getQty()>0) new_sec.add(items[sect][i]); 
		}
		Item[] newSecArr = new Item[new_sec.size()];
		for (int i=0; i<newSecArr.length; i++)
		{
			newSecArr[i]=(Item)new_sec.get(i);
		}
		items[sect]=newSecArr;
	}
	
	private Item[][] items;
	private int max_weight;
	private int current_weight;
	
	//public static final int SECTIONS=18; //last section is for invalid type items  
}
