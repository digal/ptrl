package ptrl.items;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import ptrl.combat.MeleeAttackType;
import ptrl.combat.RangedAttackType;
import ptrl.creatures.AttribModifier;

public class RangedWeapon extends Weapon implements Serializable
{
	public RangedWeapon()
	{
		super();
		ranged_attacks=new ArrayList<RangedAttackType>();
	}
	
	public RangedWeapon(Element e) throws ParserConfigurationException, SAXException, IOException
	{
		this();
		fillFromElement(e);
	}
	
	protected boolean parseElement(Element e)
	{
		if (super.parseElement(e)) 
			return true;
		String value="";
		Text tnode = (Text)e.getFirstChild();
		if (tnode!=null) 
			value = tnode.getData().trim();
		else 
			return true;
		if (e.getTagName().equals("ranged_attack_type"))
			addRangedAttackType(new RangedAttackType(e,null));
		else if (e.getTagName().equals("ammo"))
		{
			String typ=e.getAttribute("type");
			if (typ!="") addAmmoType(typ, Integer.parseInt(value));
		}
		else 
			return false;
		return true;
	}

	public void addRangedAttackType (RangedAttackType rat)
	{
		rat.setHost(this);
		ranged_attacks.add(rat);
	}
	
	public void addAmmoType (String cal, int max)
	{
		if (ammoTypes==null)
		{
			ammoTypes=new String[]{cal};
		}
		else 
		{
			String[] new_at = new String[ammoTypes.length+1];
			for (int i=0; i<ammoTypes.length; i++)
			{
				new_at[i]=ammoTypes[i];
			}
			new_at[new_at.length-1]=cal;
			ammoTypes=new_at;
		}
		
		if (maxAmmo==null)
		{
			maxAmmo=new int[]{max};
		}
		else 
		{
			int[] new_ma = new int[maxAmmo.length+1];
			for (int i=0; i<maxAmmo.length; i++)
			{
				new_ma[i]=maxAmmo[i];
			}
			new_ma[new_ma.length-1]=max;
			maxAmmo=new_ma;
		}	
		if (ammos==null)
		{
			ammos=new Ammo[]{null};
		}
		else 
		{
			Ammo[] new_a = new Ammo[ammos.length+1];
			for (int i=0; i<ammos.length; i++)
			{
				new_a[i]=ammos[i];
			}
			new_a[new_a.length-1]=null;
			ammos=new_a;
		}	
	}
	private String ammoTypes[]; //calibers
	private int maxAmmo[];
	private Ammo[] ammos; //Ammo
	private ArrayList<RangedAttackType> ranged_attacks; //Ranged
	
    public Item copySingle()
	{
		RangedWeapon cloned=new RangedWeapon();
		cloned.color=color;
		cloned.weight=weight;
		cloned.name=name;
		cloned.many_name=many_name;
		cloned.price=price;
		cloned.level=level;
		cloned.symbol=symbol;
		cloned.inventory_section=inventory_section;

		Iterator<MeleeAttackType>  i= melee_attacks.iterator();
	 	while (i.hasNext())
	 		cloned.melee_attacks.add(i.next());

	 	Iterator<AttribModifier>  i2= attrMods.iterator();
	 	while (i2.hasNext())
	 		cloned.attrMods.add(i2.next());

	 	Iterator<RangedAttackType>  i3= ranged_attacks.iterator();
	 	while (i3.hasNext())
	 		cloned.ranged_attacks.add(i3.next());

	 	if (ammoTypes!=null) cloned.ammoTypes=this.ammoTypes;
		if (ammos!=null) cloned.ammos=this.ammos;
		if (maxAmmo!=null) cloned.maxAmmo=this.maxAmmo;
		return cloned;
	}
    
    public Ammo getAmmo(String cal)
    {
    	if (ammos==null) return null;
    	else 
    	{
    		for (int i=0; i<ammos.length; i++)
    		{
    			if (cal.equalsIgnoreCase(ammoTypes[i])) return ammos[i];
    		}
    		return null;
    	}
    }
    
    public int getMaxAmmo(String cal)
    {
	if (ammos==null) return 0;
	else 
	{
		for (int i=0; i<ammos.length; i++)
		{
			if (cal.equals(ammoTypes[i])) return maxAmmo[i];
		}
		return 0;
	}
}
    
    /**
     * Reloads weapon.
     * @param load ammo to load.
     * @return old ammo.
     */
    public Ammo loadAmmo(Ammo load)
    {
    	if (ammos==null) return null;
    	else 
    	{
    		for (int i=0; i<ammos.length; i++)
    		{
    			if (load.getCaliber().equalsIgnoreCase(ammoTypes[i])) 
    			{
    				if (ammos[i]!=null)
    					if (load.getSingleName().equalsIgnoreCase(ammos[i].getSingleName()))
    					{
    						Ammo load2=(Ammo)load.copyAll();
    						load2.setQty(load2.getQty()+ammos[i].getQty());
    						load.setQty(load2.getQty());
    						if (load2.getQty()>=maxAmmo[i]) 
    						{
    							load2.setQty(maxAmmo[i]);
    							load.setQty(load.getQty()-load2.getQty());
    						}
    						else 
    						{
    							load2.setQty(load.getQty());
    							load.setQty(0);
    						}
    						ammos[i]=load2;
    						return null;
    					}
    				Ammo ret=null;
    				if (ammos[i]!=null) 
    					if (ammos[i].getQty()>0) 
    						ret=ammos[i];

    				Ammo load2=(Ammo)load.copyAll();
    				if (load2.getQty()>=maxAmmo[i]) 
    				{
    					load2.setQty(maxAmmo[i]);
    					load.setQty(load.getQty()-load2.getQty());
    				}
    				else 
    				{
    					load2.setQty(load.getQty());
    					load.setQty(0);
    				}
    				ammos[i]=load2;
    				return ret;
    			}
    		}
    		return null;
    	}
    }
    
    public ArrayList<RangedAttackType> getRangedAttackTypes()
    {
    	return ranged_attacks;
    }
}
