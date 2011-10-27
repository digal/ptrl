package ptrl.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;

import ptrl.combat.PtrlConstants;
import ptrl.creatures.Player;
import ptrl.items.Equipment;
import ptrl.items.Item;

public class EquipmentScreen implements IGameScreen
{
	public EquipmentScreen(Player p)
	{
		pc=p;
		is=null;
		cur=0;
	}
	
	public void paint(Console c)
	{
		if (is!=null) 
		{
			is.paint(c);
			return;
		}
		c.clear();
		int sh=c.getSymHeight();
		int sw=c.getSymWidth();
		String head = "Equipment";
		String s;
		short col;
		if (pc.getEquipment().getSlot(cur).getItem()==null)
			c.printString("Press [Enter] to equip, [Esc] or [Space] to exit.", sw/2-25, sh-2, PtrlConstants.WHITE);
		else c.printString("Press [Enter] to unequip, [Esc] or [Space] to exit.", sw/2-25, sh-2, PtrlConstants.WHITE);
		
		//String item_names[] = new String[Equipment.SLOT_NAMES.length];
		
		int y1=(sh-Equipment.SLOT_NAMES.length)/2;
		int x=sw/2-15;
		c.printString (head, sw/2 - 5, y1-2, PtrlConstants.LCYAN);
		for (int i=0; i<Equipment.SLOT_NAMES.length; i++)
		{
			c.printString (Equipment.SLOT_NAMES[i]+":", x, y1+i, PtrlConstants.DCYAN);
			s="[none]";
			col=PtrlConstants.DGRAY;
			Item it = pc.getEquipment().getSlot(i).getItem();
			if (it!=null) 
			{
				s=it.getInvString();
				col=PtrlConstants.LGRAY;
			}
			if (i==cur) col=PtrlConstants.WHITE;
			c.printString (s, x+13, y1+i, col);
		}
	}

	public boolean getKeyEvent(KeyEvent ke)
	{
		if (is!=null)
		{
			if (is.getKeyEvent(ke)) is=null;
			pc.refreshAttackTypes();
			return false;
		}
		char ch=ke.getKeyChar();
		if ((ch=='8'||ke.getKeyCode()==KeyEvent.VK_UP)&&cur>0) cur--;
		else if ((ch=='2'||ke.getKeyCode()==KeyEvent.VK_DOWN)&&cur<Equipment.SLOT_NAMES.length-1) cur++;
		else if (ke.getKeyCode()==KeyEvent.VK_ESCAPE||ke.getKeyCode()==KeyEvent.VK_SPACE) 
		{	
			pc.countAll();
			return true;
		}
		else if (ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			if (pc.getEquipment().getSlot(cur).getItem()!=null)
			{
				Item itm=pc.getEquipment().getSlot(cur).getItem();
				pc.getEquipment().getSlot(cur).makeEmpty();
				pc.getInventory().AddItem(itm);
			}
			else is=new InventoryScreen(pc, cur); 
		}
		return false;
	}
	
	
	private Player pc;
	private InventoryScreen is;
	private int cur;


}
