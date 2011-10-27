package ptrl.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;

import ptrl.combat.PtrlConstants;
import ptrl.creatures.Player;

public class GenderDialog implements IGameScreen
{

	public GenderDialog(Player p)
	{
		pc=p;
		cur=0;
		options = new String[] {"Male", "Female"}; 
	}
	
	public void paint(Console c)
	{
		c.clear();
		int sh=c.getSymHeight();
		int sw=c.getSymWidth();
		String head = "Select your sex:";
		int w=head.length();
		int h=4;
		int y = (int)c.getSymHeight()/2 - (int)h/2;			
		int x = (int)c.getSymWidth()/2 - (int)w/2;
		c.printString(head, x, y, PtrlConstants.LCYAN);
		short col;
		for (int i=0;i<options.length;i++)
		{
			if (i==cur) col=PtrlConstants.WHITE;
			else col=PtrlConstants.LGRAY;
		
			c.printString(options[i], x, y+i+2, col); 
		}
	}
	public boolean getKeyEvent(KeyEvent ke)
	{
		char ch=ke.getKeyChar();
		if ((ch=='8'||ke.getKeyCode()==KeyEvent.VK_UP)&&cur>0) cur--;
		else if ((ch=='2'||ke.getKeyCode()==KeyEvent.VK_DOWN)&&cur<options.length-1) cur++;
		else if (ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			pc.setSex(cur);
			return true;
		}

		return false;	
	}
	
	private Player pc;
	private int cur;
	private String[] options;
}
