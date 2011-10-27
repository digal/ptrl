package ptrl.ui;

import java.awt.event.KeyEvent;

import ptrl.combat.PtrlConstants;
import ptrl.creatures.Player;

public class NameDialog implements IGameScreen
{

	public NameDialog(Player p)
	{
		pc=p;
		name="";
	}
	
	public void paint(Console c)
	{
		c.clear();
		int sh=c.getSymHeight();
		int sw=c.getSymWidth();
		String question = "Enter your name:";
		int y = (int)sh/2 - 1;
		int x = (int)sw/2 - (int)(question.length()/2);
		c.printString(question, x, y, PtrlConstants.LCYAN);
		c.printString(name, x, y+2, PtrlConstants.WHITE);
	}

	public boolean getKeyEvent(KeyEvent ke)
	{
		//TODO: improve symbol displayability check using regexps
		if (ke.getKeyChar()!=ke.CHAR_UNDEFINED&&ke.getKeyCode()!=KeyEvent.VK_ENTER&&ke.getKeyCode()!=KeyEvent.VK_BACK_SPACE) name+=ke.getKeyChar();
		else if (ke.getKeyCode()==KeyEvent.VK_BACK_SPACE&&name.length()>0) name=name.substring(0, name.length()-1);
		else if (ke.getKeyCode()==KeyEvent.VK_ENTER&&name.length()>0) 
		{
			pc.setName(name);
			return true;
		}
		return false;
	}
	
	private Player pc;
	private String name;
}
