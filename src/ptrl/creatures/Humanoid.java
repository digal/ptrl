package ptrl.creatures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import ptrl.combat.MeleeAttackType;
import ptrl.combat.RangedAttackType;
import ptrl.combat.UnarmedAttackType;
import ptrl.items.*;

/**
 * 
 * @author Digal
 */
public class Humanoid extends Creature implements Serializable
{
	public Humanoid(String n, String t)
	{
		super(n, t);
		inventory = new Inventory();
		equipment = new Equipment();
		countAll();
		hp=stats[MHP].getBasicValue();
	}

	public void countAll()
	{
		refreshEffects();
		super.countAll();
		refreshAttackTypes();
		if (getCurrentCloseAttackN()>=getCloseAttackTypes().length) setCurrentCloseAttackN(0);
		inventory.setMaxWeight(getAttribute(STR).getValue()*3);
	}
	
	private void refreshEffects()
	{
		for (int i=0; i<attribs.length; i++)
			attribs[i].cleanMod();
		for (int i=0; i<stats.length; i++)
			stats[i].cleanMod();
		
		for (int i=0; i<Equipment.SLOT_NAMES.length; i++)
		{
			Item itm = this.equipment.getSlot(i).getItem();
			if (itm==null) continue;
			ArrayList<AttribModifier> ams = itm.getAttrMods();
			Iterator<AttribModifier> it=ams.iterator();
			while (it.hasNext())
			{
				AttribModifier am = it.next();
				if (am.getAttrib()<ATTR_NAMES.length) 
					getAttribute(am.getAttrib()).addMod(am);
				else
					getStat(am.getAttrib()-ATTR_NAMES.length).addMod(am);

 			}
		}
	}
	
	public Inventory getInventory()
	{
		return inventory;
	}
	
	public Equipment getEquipment()
	{
		return equipment;
	}
	
	public void refreshAttackTypes()
	{
		close_attacks=new UnarmedAttackType[0];
		ranged_attacks=new RangedAttackType[0];
		Item itm=equipment.getSlot(Equipment.EQ_RIGHTHAND).getItem();
		if (itm	instanceof Weapon) 
		{
			Weapon w = (Weapon)itm;
			Iterator<MeleeAttackType> i = w.getMeleeAttackTypes().iterator();
			//Whiskey is done
			while (i.hasNext())
			{
				addCloseAttackType(i.next());
			}
		}
		if (itm instanceof RangedWeapon)
		{
			RangedWeapon w = (RangedWeapon)itm;
			Iterator<RangedAttackType> i = w.getRangedAttackTypes().iterator();
			//Whiskey is done
			while (i.hasNext())
			{
				addRangedAttackType(i.next());
			}
		}
	}

	public void reload(int n)
	{
		RangedAttackType rat = getCurrentRangedAttackType();
		if (rat==null) return;
		if (rat.getCaliber()==null) return;
		if (rat.getCaliber().equalsIgnoreCase("")) return;
		Item newi=inventory.getSections()[Item.IS_AMMO][n];
		if (!(newi instanceof Ammo)) return;
		Ammo new_ammo=(Ammo) newi;
		RangedWeapon weap =rat.getHost();
		if (weap==null) return;
		Ammo old_ammo=weap.loadAmmo(new_ammo);
		if (old_ammo!=null)
			if (old_ammo.getQty()>0) inventory.AddItem(old_ammo);
		inventory.clean(Item.IS_AMMO);
	}
	
	protected Inventory inventory;
	protected Equipment equipment;
	
	
	/*protected Item[] equipment;
	
	public static final int EQ_HEAD=0;
	public static final int EQ_FACE=1;
	public static final int EQ_NECK=2;
	public static final int EQ_CLOAK=3;
	public static final int EQ_BODY=4;
	public static final int EQ_RIGHTHAND=5;
	public static final int EQ_LEFTTHAND=6;
	public static final int EQ_RIGHTWRIST=7;
	public static final int EQ_LEFTWRIST=8;
	public static final int EQ_GIRDLE=9;
	public static final int EQ_LEGS=10;
	public static final int EQ_HOLSTER=11;
	public static final int EQ_FEET=12;*/
}
