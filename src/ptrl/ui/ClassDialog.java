package ptrl.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;

import ptrl.combat.PtrlConstants;
import ptrl.creatures.Creature;
import ptrl.creatures.Player;

public class ClassDialog implements IGameScreen
{
	public ClassDialog(Player p)
	{
		pc=p;
		cur=0;
	}
	
	public void paint(Console c)
	{
		c.clear();
		int sh=c.getSymHeight();
		int sw=c.getSymWidth();
		String head = "Choose your class";
		String head2 = "Primary skills:";
		String head3 = "Secondary skills:";
		int n=Player.CLASSNAMES.length;
		int w=head.length();
		int h=n+2;
		int y = (int)sh/2 - (int)h/2;			
		int x = (int)sw/4 - (int)w/2;
		int x2 = x+(int)sw/2;
		int y2 = y - 6;
		int y3 = y2 + 8;
		c.printString(head, x, y, PtrlConstants.LCYAN); 
		c.printString(head2, x2, y2, PtrlConstants.LCYAN);
		c.printString(head3, x2, y3, PtrlConstants.LCYAN);

		short col;
		for (int i=0;i<n;i++)
		{
			if (i==cur) col=PtrlConstants.WHITE;
			else col=PtrlConstants.LGRAY;
			c.printString(Player.CLASSNAMES[i], x, y+i+2, col); 
		}
		for (int i=0; i<Player.CLASS_PRIMARY_SKILLS[cur].length;i++)
		{
			c.printString(Creature.SKILL_NAMES[Player.CLASS_PRIMARY_SKILLS[cur][i]], x2, y2+i+2, PtrlConstants.LGRAY);
		}
		for (int i=0; i<Player.CLASS_SECONDARY_SKILLS[cur].length;i++)
		{
			c.printString(Creature.SKILL_NAMES[Player.CLASS_SECONDARY_SKILLS[cur][i]], x2, y3+i+2, PtrlConstants.LGRAY);
		}


		
	}

	public boolean getKeyEvent(KeyEvent ke)
	{
		char ch=ke.getKeyChar();
		if ((ch=='8'||ke.getKeyCode()==KeyEvent.VK_UP)&&cur>0) cur--;
		else if ((ch=='2'|ke.getKeyCode()==KeyEvent.VK_DOWN)&&cur<Player.CLASSNAMES.length-1) cur++;
		else if (ke.getKeyCode()==KeyEvent.VK_ENTER) 
		{
			pc.setClass(cur);
			return true;
		}
		return false;
	}
	
	private int cur;
	private Player pc;

}
