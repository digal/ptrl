package ptrl.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;

import ptrl.combat.PtrlConstants;
import ptrl.creatures.Creature;
import ptrl.creatures.Player;
import ptrl.items.EquipmentSlot;
import ptrl.items.Item;

public class InventoryScreen implements IGameScreen
{

	public InventoryScreen (Player p)
	{
		pc=p;
		sl=-1;
		items=pc.getInventory().getSections();
		cur=0;
		options=new String[getInvStings()];
		addrs=new int[getInvStings()][2];
		for (int i=0; i<items.length; i++)
		{
			if (items[i]!=null)
				if (items[i].length!=0)
				{
					options[cur]=Item.TYPE_NAMES[i]; //TODO: Add section names
					addrs[cur][0]=i;
					addrs[cur][1]=-1;
					cur++;
					for (int j=0; j<items[i].length; j++)
					{
						options[cur]=items[i][j].getInvString();
						addrs[cur][0]=i;
						addrs[cur][1]=j;
						cur++;
					}
				}
		}
		cur=1;
	}
	
	public InventoryScreen(Player p, int slot_n)
	{
		pc=p;
		items=pc.getInventory().getSections();
		cur=0;
		sl=slot_n;
		boolean[] filter = p.getEquipment().getSlot(sl).getAllowedTypes();

		/*int n=0;
		for (int i=0; i<filter.length; i++)
		{
			if (filter[i]&&items[i]!=null)
				if (items[i].length>0) n++;
		}*/
		options=new String[getInvStings(filter)];
		addrs=new int[getInvStings(filter)][2];
		for (int i=0; i<items.length; i++)
		{
			if (items[i]!=null)
				if (items[i].length!=0&&filter[i])
				{
					options[cur]=Item.TYPE_NAMES[i]; //TODO: Add section names
					addrs[cur][0]=i;
					addrs[cur][1]=-1;
					cur++;
					for (int j=0; j<items[i].length; j++)
					{
						options[cur]=items[i][j].getInvString();
						addrs[cur][0]=i;
						addrs[cur][1]=j;
						cur++;
					}
				}
		}
		cur=1;
	}
	
	public void paint(Console c)
	{
		short uclr;
		short dclr;
		short clr;
		c.clear();
		
		int sh=c.getSymHeight();
		int sw=c.getSymWidth();
		String head = "Inventory";
		int n=options.length;
		int y1=2;
		int y2=sh-4;
		int x=0;
		String line="";	
		for (int i=0; i<sw; i++)
		{
			line=line+'-';
		}
		c.printString(line, 0, 1, PtrlConstants.WHITE);
		c.printString(line, 0, y2+1, PtrlConstants.WHITE);
		if (sl<0) c.printString("Press [Esc], [Enter] or [Space] to exit.", sw/2-20, y2+2, PtrlConstants.WHITE);
		else c.printString("Press [Enter] to select, [Esc] or [Space] to cancel.", sw/2-25, y2+2, PtrlConstants.WHITE);
		if (window>0) uclr = PtrlConstants.LGRAY;
		else uclr=PtrlConstants.BLACK;
		if (window<n-(y2-y1)-1) dclr=PtrlConstants.LGRAY;
		else dclr=PtrlConstants.BLACK;
		//c.printString ("[UP]", sw-6, y1, uclr);
		//c.printString ("[DOWN]", sw-6, y2, dclr);
		c.printString (head, sw/2 - 4, y1-2, PtrlConstants.LCYAN);
		if (cur<window) //cursor is above the window 
		{   
			window=cur;
			if (cur>0&&addrs[cur-1][1]==-1)
				window=cur-1;
		} 
		else if (cur>window+y2-y1) {window=cur-(y2-y1);}	  //cursor is below the window
		if (options.length==0) return;
		int i=0;
		String s="";
		while (i<=(y2-y1)&&i+window<options.length)
		{
			if (addrs[i+window][1]<0)
			{
				clr = PtrlConstants.DCYAN;
				String s1="[--"+options[i+window]+"--]";
				line="";	
				for (int j=0; j<(sw-s1.length())/2+1; j++)
				{
					line=line+' ';
				}
				s=line+s1+line;

			}
			else if (i+window==cur) 
			{
				clr = PtrlConstants.WHITE;
				s=' '+options[i+window];
			}
			else 
			{
				clr=PtrlConstants.LGRAY;
				s=' '+options[i+window];
			}
			c.printString(s, x, i+y1/*-window*/, clr);
			i++;
		}
	}

	public boolean getKeyEvent(KeyEvent ke)
	{
		int page=20;
		int code = ke.getKeyCode();
		char ch=ke.getKeyChar();
		if ((ch=='8'||code==KeyEvent.VK_UP)&&options.length>2) cursorUp();
		else if ((ch=='2'||code==KeyEvent.VK_DOWN)&&options.length>2) cursorDown(); 
		else if (code==KeyEvent.VK_ENTER) 
		{
			if (options.length<=0) return true;
			if (sl>=0)
			{
				EquipmentSlot slot = pc.getEquipment().getSlot(sl);
				slot.setItem(pc.getInventory().removeItem(addrs[cur][0], addrs[cur][1]));
				//pc.getInventory()
			}
			return true;
		}
		else if (code==KeyEvent.VK_ESCAPE||code==KeyEvent.VK_SPACE)
		{
			return true;
		}
		return false;
	}
	public int getInvStings()
	{
		int sum=0;
		for (int i=0; i<items.length; i++)
		{
			if (items[i]!=null)
				if (items[i].length>0) sum+=items[i].length+1;
		}
		return sum;
	}
	
	public int getInvStings(boolean[] filter)
	{
		int sum=0;
		for (int i=0; i<items.length; i++)
		{
			if (items[i]!=null)
				if (items[i].length>0&&filter[i]) sum+=items[i].length+1;
		}
		return sum;
	}

	private void cursorDown()
	{
		do 
		{
			if (cur==options.length-1) cur=0;
			else cur++;
		}
		while (addrs[cur][1]<0);  
	}
	
	private void cursorUp()
	{
		do 
		{
			if (cur==0) cur=options.length-1;
			else cur--;
		}
		while (addrs[cur][1]<0);  
	}
	
	
	Item[][] items;	
	Player pc;
	int cur;
	String[] options;
	int [][] addrs;
	int window;
	int sl;
}
