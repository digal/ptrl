package ptrl.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;

import ptrl.combat.PtrlConstants;
import ptrl.creatures.Player;
import ptrl.map.GlobalMap;
import ptrl.map.MapDescriptor;
import static ptrl.ui.Controls.*;

public class GlobalMapScreen implements IGameScreen
{
	private int HEADER=2;
	private int FOOTER=2;
	private GlobalMap map;
	private Player pc;
	//private boolean lockPlayerPos;
	
	public GlobalMapScreen(GlobalMap map, Player pc)
	{
		this.map=map;
		this.pc=pc;
	}
	
	public void drawMap(Console c)
	{
		int w=c.getSymWidth();
		int h=c.getSymHeight()-FOOTER-HEADER;
		int x=0;
		int y=0;
		
		if (map.getWidth()<w) x=(map.getWidth()-w)/2;
		else if (map.getPCX()<=w/2) x=0;
		else if (map.getPCX()>=map.getWidth()-w/2) x=map.getWidth()-w;
		else x = map.getPCX()-w/2;

		if (map.getHeight()<h) y=(map.getHeight()-h)/2;
		else if (map.getPCY()<=h/2) y=0;
		else if (map.getPCY()>=map.getHeight()-h/2) y=map.getHeight()-h;
		else y = map.getPCY()-h/2;
		
		drawMap(x,y,map.getPCZ(),c);
		c.printString("x="+map.getPCX()+"; y="+map.getPCY()+"; h="+map.getTerrainLevel(map.getPCX(), map.getPCY()), 0,0,PtrlConstants.WHITE);
		c.printString(map.getTile(map.getPCX(), map.getPCY(), map.getPCZ()).toString(), 0,1,PtrlConstants.WHITE);
	}
	
	public void drawMap(int x, int y, int z, Console c)
	{
		c.clear();
		int x1=0;
		int x2=c.getSymWidth()-1;
		int y1=HEADER;
		int y2=c.getSymHeight()-FOOTER;
		int ax=map.getPCX(); // actor x
		int ay=map.getPCY(); // actor y
		short  bgCol;
		short  fgCol;
		MapDescriptor tile;
		
		for (int i=x1; i<=x2; i++)
			for (int j=y1-HEADER; j<=y2-FOOTER-1; j++)
			{
				if (x+i>=map.getWidth()||y+j>=map.getHeight()||x+i<0||y+j<0) continue;
				tile=map.getTile(x+i,y+j,0);
				char ch = tile.getSymbol();
				if (!tile.isKnownByPlayer()) 
				{
					bgCol = PtrlConstants.BLACK;
					fgCol = PtrlConstants.BLACK;
				}
				else
				{
					bgCol = tile.getBgColor();
					fgCol = tile.getFgColor();
				}
				c.showRectChar(i, j+HEADER, bgCol);
				c.showChar(i, j+HEADER, ch, fgCol);
			}
		c.showRectChar(map.getPCX()-x, map.getPCY()-y+HEADER, map.getTile(map.getPCX(), map.getPCY(), map.getPCZ()).getBgColor());
		c.showChar(map.getPCX()-x, map.getPCY()-y+HEADER, pc.getSymbol(), pc.getColor());		


	}
	
	public boolean getKeyEvent(KeyEvent ke)
	{
		char ch=ke.getKeyChar();
		if (ke.getKeyCode()==KeyEvent.VK_ENTER||ch=='>') return true;
		int[] xy={0,0};
		if (ch=='r')
			map.makeRiver(map.getPCX(), map.getPCY());
		else if(ch=='p')
			map.polish();
		else if(ch=='c')
			map.citiesAndRoads();
		
		if (isUp(ke))
		{
			xy[1]=-1;
		}
		else if (isDown(ke))
		{
			xy[1]=1;
		}
		
		if (isLeft(ke))
		{
			xy[0]=-1;
		}
		else if (isRight(ke))
		{
			xy[0]=1;
		}
		if ((xy[0]!=0||xy[1]!=0)&&map.getPCX()+xy[0]>=0&&map.getPCX()+xy[0]<map.getWidth()&&map.getPCY()+xy[1]>=0&&map.getPCY()+xy[1]<map.getHeight()) 
		{
			map.setPCX(map.getPCX()+xy[0]);
			map.setPCY(map.getPCY()+xy[1]);
		}
		map.refreshKnown();
		
		return false;
	}

	public void paint(Console c)
	{
		drawMap(c);
	}
	
}
