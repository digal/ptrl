package ptrl.items;

import java.io.Serializable;

public class Equipment implements Serializable
{
	public Equipment()
	{
		slots=new EquipmentSlot[]
		          {
					new EquipmentSlot(new int[]{Item.IS_HEAD}), 	//head
					new EquipmentSlot(new int[]{Item.IS_FACE}), 	//face
					new EquipmentSlot(new int[]{Item.IS_NECK}), 	//neck
					new EquipmentSlot(new int[]{Item.IS_CLOAK}),	//cloak
					new EquipmentSlot(new int[]{Item.IS_BODYARMOR}),//body
					new EquipmentSlot(new int[]{Item.IS_MATERIAL,	//right hand
												   Item.IS_HEAD, 
												   Item.IS_FACE, 
												   Item.IS_NECK, 
												   Item.IS_CLOAK, 
												   Item.IS_BODYARMOR, 
												   Item.IS_WRIST,
												   Item.IS_GIRDLE,
												   Item.IS_PANTS,
												   Item.IS_BOOTS,
												   Item.IS_MELEE,
												   Item.IS_SMALL_FIREARMS,
												   Item.IS_MEDIUM_FIREARMS,
												   Item.IS_BIG_FIREARMS,
												   Item.IS_AMMO,
												   Item.IS_MODS,
												   Item.IS_TOOLS,
												   Item.IS_EXPLOSIVES,
												   Item.IS_MEDIA,
												   Item.IS_DRUGS}),
					new EquipmentSlot(new int[]{Item.IS_MATERIAL,	//left hand
												   Item.IS_HEAD, 
												   Item.IS_FACE, 
												   Item.IS_NECK, 
												   Item.IS_CLOAK, 
												   Item.IS_BODYARMOR, 
												   Item.IS_WRIST,
												   Item.IS_GIRDLE,
												   Item.IS_PANTS,
												   Item.IS_BOOTS,
												   Item.IS_AMMO,
												   Item.IS_MODS,
												   Item.IS_TOOLS,
												   Item.IS_EXPLOSIVES,
												   Item.IS_MEDIA,
												   Item.IS_DRUGS}),
					new EquipmentSlot(new int[]{Item.IS_SMALL_FIREARMS}),//holster					
					new EquipmentSlot(new int[]{Item.IS_WRIST}),   //r_wrist					
					new EquipmentSlot(new int[]{Item.IS_WRIST}),   //l_wrist					
					new EquipmentSlot(new int[]{Item.IS_GIRDLE}),  //girdle					
					new EquipmentSlot(new int[]{Item.IS_PANTS}),   //legs					
					new EquipmentSlot(new int[]{Item.IS_BOOTS})};  //feet
	}
	
	/*public Item getItem(int sl)
	{
		return slots[sl].getItem();
	}*/
	
	public boolean putItem(Item i, int sl)
	{
		return slots[sl].setItem(i);
	}
	
	public Item removeItem(int sl)
	{
		Item i=slots[sl].getItem();
		slots[sl].makeEmpty();
		return i;
	}
	
	public EquipmentSlot getSlot(int n)
	{
		return slots[n];
	}
	
	private EquipmentSlot[] slots;
	
	public static final int EQ_HEAD=0;
	public static final int EQ_FACE=1;
	public static final int EQ_NECK=2;
	public static final int EQ_CLOAK=3;
	public static final int EQ_BODY=4;
	public static final int EQ_RIGHTHAND=5;
	public static final int EQ_LEFTTHAND=6;
	public static final int EQ_HOLSTER=7;
	public static final int EQ_RIGHTWRIST=8;
	public static final int EQ_LEFTWRIST=9;
	public static final int EQ_GIRDLE=10;
	public static final int EQ_LEGS=11;
	public static final int EQ_FEET=12;
	
	public static final String[] SLOT_NAMES = new String[]
	                              {"Head",
								  "Face",
								  "Neck",
								  "Cloak",
								  "Body",
								  "Right hand",
								  "Left hand",
								  "Holster",
								  "Right wrist",
								  "Left wrist",
								  "Girdle",
								  "Legs",
								  "Feet"};
}
