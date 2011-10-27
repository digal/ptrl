package ptrl.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;

import ptrl.combat.PtrlConstants;
import ptrl.creatures.Creature;
import ptrl.creatures.Player;
import ptrl.creatures.Skill;

public class SkillsDialog implements IGameScreen
{

	public SkillsDialog(Player p)
	{
		pc=p;
		cur=0;
		oldvals=new int[pc.getSkills().length];
		for (int i=0; i<oldvals.length; i++)
		{
			oldvals[i]=pc.getSkills()[i].getValue();
		}
		window=0;
		
	}
			
	
	public void paint(Console c)
	{
		short uclr;
		short dclr;
		short clr;
		c.clear();
		
		int sh=c.getSymHeight();
		int sw=c.getSymWidth();
		String head = "Skills";
		int n=pc.getSkills().length;
		int y1=0;
		int y2=0;
		if (n<=sh-11) {y1=(sh-n)/2; y2=y1+n-1;} //Full list on screen
		else  {y1=3; y2=sh-8;}		  		    //Part of list on screen: scroll needed
		int x1 = (int)sw/2 - 20;
		int x2 = (int)sw/2 + 10;
		if (cur<window) {window=cur;} //cursor is above the window
		else if (cur>window+y2-y1) {window=cur-(y2-y1);}	  //cursor is below the window
		if (window>0) uclr = PtrlConstants.LGRAY;
		else uclr=PtrlConstants.BLACK;
		if (window<n-(y2-y1)-1) dclr=PtrlConstants.LGRAY;
		else dclr=PtrlConstants.BLACK;
		c.printString ("[UP]", x2+12, y1, uclr);
		c.printString ("[DOWN]", x2+12, y2, dclr);
		c.printString (head, x1+17, y1-2, PtrlConstants.LCYAN);
		c.printString("Press [+/-] to distribute points", x1+5, y2+4, PtrlConstants.LGRAY);
		c.printString("[Enter] to continue", x1+10, y2+5, PtrlConstants.LGRAY);  

		for (int i=window; i<=(window+y2-y1); i++)
		{
			if (i==cur) clr = PtrlConstants.WHITE;
			else if (pc.getSkills()[i].getValue()==0) clr = PtrlConstants.DGRAY;
			else clr = PtrlConstants.LGRAY;
			//c.printString("                                     ", x1-4, i+y1-window, Tile.BLACK);
			c.printString(Creature.SKILL_NAMES[i], x1, i+y1-window, clr);
			c.printString(Integer.toString(pc.getSkills()[i].getValue()), x2, i+y1-window, clr);
			if (pc.getSkills()[i].isPrimary()) c.printString("(P)", x1-4, i+y1-window, clr);
			c.printString("(of "+Integer.toString(pc.getSkills()[i].getPotential())+") ", x2+3, i+y1-window, clr);
			c.printString("Free points: " + Integer.toString(pc.getSkillpoints()) + "  ", x1+14, y2+2, PtrlConstants.WHITE); 
		}
	}

	public boolean getKeyEvent(KeyEvent ke)
	{
		int page=20;
		int code = ke.getKeyCode();
		char ch=ke.getKeyChar();
		if ((ch=='8'||code==KeyEvent.VK_UP)&&cur>0) cur-=1;
		else if ((ch=='2'||code==KeyEvent.VK_DOWN)&&cur<pc.getSkills().length-1) cur+=1;
		else if (code==KeyEvent.VK_PAGE_UP&&cur>0)
		{
			if (cur>=page) cur-=page;
			else cur=0;
		}
		else if (code==KeyEvent.VK_PAGE_DOWN&&cur<pc.getSkills().length-1)
		{
			if (cur+page<pc.getSkills().length-1) cur+=page;
			else cur=pc.getSkills().length-1;
		}
		else if (ch=='+'&&pc.getSkillpoints()>0&&pc.getSkills()[cur].getPotential()>pc.getSkills()[cur].getValue())
		{
			pc.getSkills()[cur].inc();
			pc.decSkillPoints();
		}
		else if (ch=='-'&&pc.getSkills()[cur].getValue()>oldvals[cur])
		{
			pc.getSkills()[cur].dec();
			pc.incSkillPoints();;
		}
		else if (code==KeyEvent.VK_ENTER||code==KeyEvent.VK_SPACE||code==KeyEvent.VK_ESCAPE) return true;
		return false;
	}

	private int[] oldvals;
	private Player pc;
	private int cur;
	int window;
}
