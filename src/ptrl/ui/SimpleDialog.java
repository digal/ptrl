package ptrl.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import ptrl.combat.PtrlConstants;

public class SimpleDialog implements IGameScreen
{
	public SimpleDialog(String[] opts, String h)
	{
		cur=0;
		options=opts;
		head=h;
		
	}
	
	public boolean getKeyEvent(KeyEvent ke)
	{
		boolean res=false;
		int code=ke.getKeyCode();
		char ch=ke.getKeyChar();
		if (code==KeyEvent.VK_UP&&cur>0) cur-=1;
		else if ((ch=='8'||code==KeyEvent.VK_UP)&&cur==0) cur=options.length-1;
		else if ((ch=='2'||code==KeyEvent.VK_DOWN)&&cur<options.length-1) cur+=1;
		else if (code==KeyEvent.VK_DOWN&&cur==options.length-1) cur=0;
		else if (code==KeyEvent.VK_ENTER) res=true;
		
		return res;
	}

	public void paint(Console c)
	{
		c.clear();
		int w=head.length();
		int h=options.length+2;
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
	
	public int getCurrentOption()
	{
		return cur;
	}
		
		private int cur; //no. of option selected
		private String[] options;
		private String head;
}
