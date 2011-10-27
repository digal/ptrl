package ptrl.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;

import ptrl.combat.PtrlConstants;
import ptrl.creatures.Creature;
import ptrl.creatures.Player;

public class AttribsDialog implements IGameScreen
{
	public AttribsDialog(Player p, int ap)
	{
		points=ap;
		pc=p;
		cur=0;
	}
	
	public void paint(Console c)
	{
		c.clear();
		int sh=c.getSymHeight();
		int sw=c.getSymWidth();
		String head = "Attributes";
		int n=Creature.ATTR_NAMES.length;
		int w=head.length();
		int h=n+2;
		int y = (int)sh/2 - (int)h/2 - 2;			
		int x = (int)sw/2 - (int)w/2;
		c.printString(head, x, y, PtrlConstants.LCYAN);
		short col;
		for (int i=0;i<n;i++)
		{
			if (i==cur) col=PtrlConstants.WHITE;
			else col=PtrlConstants.LGRAY;
			c.printString(Creature.ATTR_NAMES[i]+" "+pc.getAttributeValue(i), x+2, y+i+2, col); 
		}
		c.printString("Free points: " + Integer.toString(points), x-2, y+n+3, PtrlConstants.WHITE);
		c.printString("Press [+/-] to distribute points", x-11, y+n+5, PtrlConstants.LGRAY);
		c.printString("[Enter] to continue", x-5, y+n+6, PtrlConstants.LGRAY);

	}

	public boolean getKeyEvent(KeyEvent ke)
	{
		int code = ke.getKeyCode();
		char ch=ke.getKeyChar();
		if ((ch=='8'||code==KeyEvent.VK_UP)&&cur>0) cur-=1;
		else if ((ch=='2'||code==KeyEvent.VK_DOWN)&&cur<Creature.ATTR_NAMES.length-1) cur+=1;
		else if (ch=='+'&&pc.getAttributeValue(cur)<10&&points>0)
		{
			pc.getAttribute(cur).inc();
			points--;
		}
		else if (ch=='-'&&pc.getAttributeValue(cur)>1)
		{
			pc.getAttribute(cur).dec();
			points++;
		}
		else if (code==KeyEvent.VK_ENTER) 
		{
			pc.countAll();
			return true;
			
		}
		return false;
	}
/*
	public static int[] attribsDialog()
	{
		int points=7; //free attribs points
		int cur=0; //current option
	
		
		int code='0';
		do
		{
			Toolkit.printString("  "+Creature.ATTR_NAMES[cur]+" "+Integer.toString(values[cur])+" ", x, y+cur+2, SELECTCOLOR); 
			if (cur>0) Toolkit.printString("  "+Creature.ATTR_NAMES[cur-1]+" "+Integer.toString(values[cur-1]), x, y+cur+1, REGULARCOLOR); 
			if (cur<n-1) Toolkit.printString("  "+Creature.ATTR_NAMES[cur+1]+" "+Integer.toString(values[cur+1]), x, y+cur+3, REGULARCOLOR); 
 
		
			InputChar ch = Toolkit.readCharacter();
			code = ch.getCode();
			if (code==InputChar.KEY_UP&&cur>0) cur-=1;
			if (code==InputChar.KEY_DOWN&&cur<n-1) cur+=1;
			if (!ch.isSpecialCode())
			{
				if (ch.getCharacter()=='+'&&points>0&&values[cur]<10)
				{
					values[cur]+=1;
					points-=1;
				}
				if (ch.getCharacter()=='-'&&values[cur]>1)
				{
					values[cur]-=1;
					points+=1;
				}
			}
			
		}while (code!=10);
		return values;
	}
*/
	private int cur;
	private int points;
	private Player pc;
}
