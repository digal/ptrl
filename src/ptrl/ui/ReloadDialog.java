package ptrl.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;

import ptrl.combat.PtrlConstants;
import ptrl.combat.RangedAttackType;
import ptrl.creatures.Player;
import ptrl.items.Ammo;
import ptrl.items.Item;
import ptrl.util.GameTime;
import ptrl.util.MessageStack;
import ptrl.util.Message;


public class ReloadDialog implements IGameScreen
{
	public ReloadDialog(Player pc, MessageStack ms, GameTime gt)
	{
		this.pc=pc;
		this.rat=pc.getCurrentRangedAttackType();
		this.ms=ms;
		this.gt=gt;
		Item[] a1=pc.getInventory().getSections()[Item.IS_AMMO];
		int vars=0;
		for (int i=0; i<a1.length; i++)
		{
			if (a1[i] instanceof Ammo)
			{
				Ammo a = (Ammo)a1[i];
				if (a.getCaliber().equals(rat.getCaliber())) vars++;
			}
		}
		variants=new Ammo[vars];
		indexes=new int[vars];
		vars=0;
		for (int i=0; i<a1.length; i++)
		{
			if (a1[i] instanceof Ammo)
			{
				Ammo a = (Ammo)a1[i];
				if (a.getCaliber().equals(rat.getCaliber())) 
				{
					variants[vars]=(Ammo)a1[i];
					indexes[vars]=i;
					vars++;
				}
			}
		}
		cursor=0;
		reloaded=false;
	}

	public void simpleReload()
	{
		if (rat.getCaliber()==null)
		{
			ms.addMessage(new Message("No ammo needed.", gt));
			return;
		}
		if (getVariantsN()==0)
		{
			ms.addMessage(new Message("No ammo.", gt));
			return;
		}
		//ammo unknown loading first
		if (rat.getHost().
				getAmmo(
						rat.
						getCaliber())==null)
		{
			pc.reload(getIndexes()[0]);
			ms.addMessage(new Message("Loaded: "+variants[0].getSingleName()+".", gt));
			reloaded=true;
			return;
		}
		else //(rat.getHost().getAmmo(rat.getCaliber()).getQty()>0)
		{
			int needed=-1;
			for (int i=0; i<getVariantsN(); i++)
			{
				if (variants[i].getSingleName().equalsIgnoreCase(rat.getHost().getAmmo(rat.getCaliber()).getSingleName())) 
					needed=i; 
			}
			if (needed>=0&&variants[needed].getQty()>0) 
			{
				pc.reload(getIndexes()[needed]);
				ms.addMessage(new Message("Loaded: "+variants[needed].getSingleName()+".", gt));
				reloaded=true;
				return;
			}
			else 
			{
				pc.reload(getIndexes()[0]);
				ms.addMessage(new Message("Loaded: "+variants[0].getSingleName()+".", gt));
				reloaded=true;
				return;
			}
				
		}
		
//		else if (rd.getVariantsN()==1)
//			pc.reload(rd.getIndexes()[0]);
	}
	
	
	public void paint(Console c)
	{
		if (cursor>=getVariantsN()) cursor=0;
		int x1=c.getSymWidth()-50;
		int x2=x1+40;
		int y1=5;
		int y2=y1+getVariantsN()+1;
		short bordercol=PtrlConstants.LCYAN;
		for (int x=x1; x<=x2; x++)
		{
			c.showBlackChar(x ,y1);
			c.showBlackChar(x ,y2);
			c.showChar(x ,y1, '*', bordercol);
			c.showChar(x ,y2, '*', bordercol);
			for (int y=y1+1; y<y2-1; y++)
			{
				c.showBlackChar(x ,y);
			}
		}
		for (int y=y1; y<=y2; y++)
		{
			c.showBlackChar(x1 ,y);
			c.showBlackChar(x2 ,y);
			c.showChar(x1 ,y, '*', bordercol);
			c.showChar(x2 ,y, '*', bordercol);
		}
		short col=PtrlConstants.LGRAY;
		for (int i=0; i<getVariantsN(); i++)
		{
			if (i==cursor) col=PtrlConstants.WHITE;
			else col=PtrlConstants.LGRAY;
			c.printString(variants[i].getName(), x1+2, y1+1+i, col);
		}
		
	}

	public boolean getKeyEvent(KeyEvent ke)
	{
		char ch=ke.getKeyChar();
		if (ke.getKeyCode()==KeyEvent.VK_ESCAPE) return true;
		else if ((ch=='8'||ke.getKeyCode()==KeyEvent.VK_UP)&&cursor>0) cursor--;
		else if ((ch=='2'||ke.getKeyCode()==KeyEvent.VK_DOWN)&&cursor<getVariantsN()-1) cursor++;
		else if (ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			pc.reload(getIndexes()[cursor]);
			ms.addMessage(new Message("Loaded: "+variants[cursor].getSingleName()+".", gt));
			reloaded=true;
			return true;	
		}
		return false;
	}
	
	public int getVariantsN()
	{
		return variants.length;
	}
	
	public Ammo[] getVariants()
	{
		return variants;
	}
	
	public int[] getIndexes()
	{
		return indexes;
	}

	
	private Player pc;
	private RangedAttackType rat;
	private Ammo[] variants;
	private int[] indexes;
	private MessageStack ms;
	private GameTime gt;
	private int cursor;
	private boolean reloaded;
	public boolean isReloaded()
	{
		return reloaded;
	}
}
