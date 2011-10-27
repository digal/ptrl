package ptrl.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;

import ptrl.combat.PtrlConstants;
import ptrl.util.Message;
import ptrl.util.MessageStack;



/**
 * A dialog that shows a list of messages. 
 * 
 * @author Digal
 */
public class MessagesDialog implements IGameScreen
{
	public MessagesDialog (Message[] m, int t)
	{
		msgs=m;
		offset=0;
		highlight = new boolean[msgs.length];
		boolean white;
		int trn=t;
		if (msgs.length>0)
		{
			if (msgs[msgs.length-1].getTurn()==t) white=true;
			else white=false;
			for (int i=msgs.length-1; i>=0; i--)
			{
				if (msgs[i].getTurn()!=trn) white=!white;
				trn=msgs[i].getTurn();
				highlight[i]=white;
			}
			
		}
		start=true;
	}
	
	public MessagesDialog (MessageStack ms, int t)
	{
		this(ms.getMessages(), t);
	}
	
	
	public void paint(Console c)
	{
		c.clear();
		String head="Messages stack:";
		int h=c.getSymHeight();
		int w=c.getSymWidth();
		int x=1;
		int y1=2;
		int y2=h-2;
		short col=PtrlConstants.WHITE;
		c.printString(head, (w/2-8), 0, PtrlConstants.LCYAN);
		String line="";	
		for (int i=0; i<w; i++)
		{
			line=line+'-';
		}
		c.printString(line, 0, 1, PtrlConstants.WHITE);
		c.printString(line, 0, y2+1, PtrlConstants.WHITE);
		int i=0;
		if (start&&msgs.length>=(y2-y1)-1)
		{
			start=false;
			offset=msgs.length-(y2-y1)-1;
		}
		while (i<=(y2-y1)&&i+offset<msgs.length)
		{
			if (highlight[i+offset]) col=PtrlConstants.WHITE;
			else col=PtrlConstants.LGRAY;
			c.printString(msgs[i+offset].getText(), x, y1+i, col);
			i++;
		}
		//c.printString("Offset: "+offset, 0, 0, Tile.WHITE);
		
	}

	public boolean getKeyEvent(KeyEvent ke)
	{
		int page=20;
		int code=ke.getKeyCode();
		char ch=ke.getKeyChar();
		if (code==KeyEvent.VK_ENTER||code==KeyEvent.VK_ESCAPE) return true;
		else if ((ch=='2'||code==KeyEvent.VK_DOWN)&&offset<msgs.length-1) offset++;
		else if ((ch=='8'||code==KeyEvent.VK_UP)&&offset>0) offset--;
		else if (code==KeyEvent.VK_PAGE_DOWN&&offset<msgs.length-1)
		{
			if (offset+page<msgs.length-1) offset+=page;
			else offset=msgs.length-1;
		}
		else if (code==KeyEvent.VK_PAGE_UP&&offset>0)
		{
			if (offset>=page) offset-=page;
			else offset=0;
		}

		return false;
	}
	
	Message[] msgs;
	boolean [] highlight;
	int offset;
	boolean start;
}
