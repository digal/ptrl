package ptrl.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.ArrayList;

import ptrl.combat.PtrlConstants;
import ptrl.combat.RangedAttackType;
import ptrl.combat.UnarmedAttackType;
import ptrl.creatures.Creature;
import ptrl.creatures.Player;
import ptrl.items.Item;
import ptrl.map.Actor;
import ptrl.map.Map;
import ptrl.util.Message;

import static ptrl.ui.Controls.*;

public class TargetDialog implements IGameScreen 
{
	public TargetDialog(Map m, Player p)
	{
		map=m;
		pc=p;
		line=new int[0][];
		chances=new double[0];
		tx=pc.getX();
		ty=pc.getY();
		visibleActors = map.getPlayerActor().getVisibleActors();
		visibleActorsCursor=0;
		if (lastTarget!=null)  
			if (map.getPlayerActor().seeActor(lastTarget)>0&&lastTarget.getCreature().getHP()>0)
			{
				tx=lastTarget.getCreature().getX();
				ty=lastTarget.getCreature().getY();
				findTargLine();
				for (int i=0; i<visibleActors.size(); i++)
					if (visibleActors.get(i)==lastTarget)
					{
						visibleActorsCursor=i;
						break;
					}
			}
				
	}

	public void paint(Console c)
	{
		drawMap(c);
		
	}
	public boolean getKeyEvent(KeyEvent ke)
	{	
		if (ke.getKeyCode()==KeyEvent.VK_ESCAPE) return true;
		if (ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			map.getPlayerActor().rangedAttack(tx, ty);
			map.turn(pc.getCurrentRangedAttackType().getTime()*1000);
			Actor a = map.getActor(tx, ty);
			if (a!=null) lastTarget=a;
			return true;
		}
		
		int[] xy={0,0};
		char ch=ke.getKeyChar();
		int code=ke.getKeyCode();
		
		if (ch==';'||ch=='/')
		{
			pc.prevRangedAttackType();
		}
		else if (ch=='\''||ch=='*')
		{
			pc.nextRangedAttackType();
		}
		else if (ch=='+'&&visibleActors.size()>0)
		{
			visibleActorsCursor++;
			if (visibleActorsCursor>=visibleActors.size()) visibleActorsCursor=0;
			targetActor(visibleActorsCursor);
		}
		else if (ch=='-'&&visibleActors.size()>0)
		{
			visibleActorsCursor--;
			if (visibleActorsCursor<0) visibleActorsCursor=visibleActors.size()-1;
			targetActor(visibleActorsCursor);
		}
		else
		{
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
			if ((xy[0]!=0||xy[1]!=0)&&tx+xy[0]>=0&&tx+xy[0]<map.getWidth()&&ty+xy[1]>=0&&ty+xy[1]<map.getHeight()) 
			{
				tx+=xy[0];
				ty+=xy[1];
			}
		}

		findTargLine();
		return false;
	}

	/**
	 * Draws a part of map.
	 * Trying to keep target tile in center of window.
	*/
	public void drawMap(Console c)
	{
		int w=c.getSymWidth();
		int h=c.getSymHeight()-footer-header;
		int x=0;
		int y=0;
		
		if (map.getWidth()<c.getSymWidth())	x=(map.getWidth()-c.getSymWidth())/2;
		else if (tx<=w/2) x=0;
		else if (tx>=map.getWidth()-w/2) x=map.getWidth()-w;
		else x = tx-w/2;

		if (map.getHeight()<c.getSymHeight()) y=(map.getHeight()-c.getSymHeight())/2;
		else if (ty<=h/2) y=0;
		else if (ty>=map.getHeight()-h/2) y=map.getHeight()-h;
		else y = ty-h/2;
		
		drawMap(x,y,c);
		
/*		int x1=pc.getX()-tx;
		int y1=pc.getY()-ty;
		double tan=0;
		if (y1!=0) tan=(double)x1/y1;
		double atan=Math.atan(tan);*/
		
		double azimuth=0;
		double deltaerr=2;
		
		if (tx==pc.getX())
		{
			if (ty<=pc.getY()) azimuth=0;
			else azimuth=180; 
		}
		else if (ty==pc.getY())
		{
			if (tx<=pc.getX()) azimuth=90;
			else azimuth=270; 
		}
		else 
		{
			double tan=(double)(pc.getX()-tx)/(pc.getY()-ty);
			double atan=Math.toDegrees(Math.atan(tan));
			if (ty>=pc.getY()) atan=atan+180;
			else if (tx>=pc.getX()) atan=360+atan;
			azimuth=atan;
		}
		
		if (Math.abs(pc.getX()-tx)<=Math.abs(pc.getY()-ty))
		{
			deltaerr=-Math.tan(Math.toRadians(azimuth));
			if (ty>pc.getY()) deltaerr*=-1;
		}
		else 
		{
			deltaerr=-1/Math.tan(Math.toRadians(azimuth));
			if (tx>pc.getX()) deltaerr*=-1;
		}
		String s1="";
		String s2="";
		UnarmedAttackType ut = pc.getCurrentCloseAttackType();
		if (ut!=null) s1=ut.getShortInfoString();
		RangedAttackType rt = pc.getCurrentRangedAttackType();
		if (rt!=null) s2=rt.getInfoString();
		if (s1.length()+s2.length()+2>c.getSymWidth())
		{
			s1="["+ut.getName()+"]";
			s2="["+rt.getName()+"]";
		}
		c.printString(s1, 0, c.getSymHeight()-2, PtrlConstants.LGRAY);
		c.printString(s2, c.getSymWidth()-s2.length(), c.getSymHeight()-2, PtrlConstants.WHITE);
		boolean kbp[][] = map.getKbp();
		double d = Math.sqrt((pc.getX() - tx) * (pc.getX() - tx)
				+ (pc.getY() - ty) * (pc.getY() - ty));
		NumberFormat nf=NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		String sd=nf.format(d);
		String s="distance="+sd+":";
		s2="";
		if (!kbp[tx][ty]) s+="unknown";
		else 
		{
			s+=map.getTile(tx, ty).getName();
			Actor a = map.getActor(tx,ty);
			if (a!=null) s2=a.getCreature().getTheName(false)+"("+a.getCreature().getHP()+" hp)";
		}
		c.printString(s, 0,0,PtrlConstants.WHITE);
		c.printString(s2, 0,1,PtrlConstants.WHITE);
		//c.printString("angle="+azimuth+", deltaerr="+deltaerr, 0, c.getSymHeight()-2, Tile.WHITE);
		c.printString(pc.toString(),0,c.getSymHeight()-1,PtrlConstants.WHITE);
	}

	/**
	 * Draws a part of map, specified by a window.
	 * 
	 * @param x x coord of a window.
	 * @param y y coord of a window.
	 */
	public void drawMap(int x, int y, Console c)
	{
		c.clear();
		
		int x1=0;
		int x2=c.getSymWidth()-1;
		int y1=header;
		int y2=c.getSymHeight()-footer;
		
		Creature cr = pc;
		int[][] fov = map.getPlayerFov();
		map.refreshKnown();
		
		boolean kbp[][] = map.getKbp();
		int l=((fov.length-1)/2);
		
		int ax=cr.getX(); // actor x
		int ay=cr.getY(); // actor y
		short col;
		char ch;
		
		for (int i=x1; i<=x2; i++)
			for (int j=y1-header; j<=y2-footer-1; j++)
			{
				if (x+i>=map.getWidth()||y+j>=map.getHeight()||x+i<0||y+j<0) continue;
				ch = map.getTile(x+i, y+j).getSymbol();
				if (!kbp[x+i][y+j]) col = PtrlConstants.BLACK;
				else if (x+i>=ax-l&&x+i<=ax+l&&y+j>=ay-l&&y+j<=ay+l)
				{
					if (fov[x+i-ax+l][y+j-ay+l] > 0) col = map.getTile(x+i, y+j).getCurrentColor();
					else col=PtrlConstants.DGRAY;
					//if (x+i==tx&&y+j==ty) c.printString("fov="+fov[x+i-ax+l][y+j-ay+l], 50, 0, Tile.WHITE);
				}
				else col=PtrlConstants.DGRAY;
				//col = map.getTile(x+i, y+j).getCurrentColor(); //View all
				c.showChar(i, j+header, ch, col);
			}
		//-------Draw items-------
		ArrayList items=map.getAllItems();
		
		for (int i=0; i<items.size(); i++)
		{
			Item it = (Item)items.get(i);
			int ix=it.getX();
			int iy=it.getY();
			if (ix>=ax-l&&ix<=ax+l&&iy>=ay-l&&iy<=ay+l)
				if 	(fov[ix-ax+l][iy-ay+l]>0&&ix-x>=x1&&ix-x<x2&&iy-y+header>=y1&&iy-y+header<y2) 
				{
					c.showBlackChar(ix-x, iy-y+header);
					c.showChar(ix-x, iy-y+header, it.getSymbol(), it.getColor());		
				}
		}
		//-----Draw creatures-----
		int creatures_cur=c.getSymWidth()-1;
		for (int i=0; i<visibleActors.size(); i++)
		{
			cr = visibleActors.get(i).getCreature();
			int cx=cr.getX();
			int cy=cr.getY();
			if (cx!=pc.getX()||cy!=pc.getY())
			{
				if (cx==tx&&cy==ty) 
				{
					c.showRectChar(creatures_cur, c.getSymHeight()-1, PtrlConstants.WHITE);
					c.showChar(creatures_cur, c.getSymHeight()-1, cr.getSymbol(), PtrlConstants.BLACK);
				}
				else 
					c.showChar(creatures_cur, c.getSymHeight()-1, cr.getSymbol(), cr.getColor());
				creatures_cur--;
				if (cx-x>=x1&&cx-x<x2&&cy-y+header>=y1&&cy-y+header<y2)
				{
					c.showBlackChar(cx-x, cy-y+header);
					c.showChar(cx-x, cy-y+header, cr.getSymbol(), cr.getColor());
				}
				
			}
		}
		if (pc.getHP()>0&&pc.getX()-x>=x1&&pc.getX()-x<x2&&pc.getY()-y+header>=y1&&pc.getY()-y+header<y2)
		{
			c.showBlackChar(pc.getX()-x, pc.getY()-y+header);
			c.showChar(pc.getX()-x, pc.getY()-y+header, pc.getSymbol(), pc.getColor());
		}
		
		//-----Draw target line-----
		int xx=pc.getX()-x;
		int yy=pc.getY()-y;
		ch='x';
		col=PtrlConstants.LGREEN;
		for (int i=0; i<line.length; i++)
		{
			xx+=line[i][0];
			yy+=line[i][1];
			if (ch!='?') col=PtrlConstants.LGREEN;
			if (ch!='?'&&chances[i]<=0.5) col=PtrlConstants.LYELLOW;
			if (ch!='?'&&chances[i]<=0) col=PtrlConstants.LRED;
			if (!kbp[xx+x][yy+y]) 
			{
				ch='?';
				col=PtrlConstants.LRED;			
			}
			if (xx>=x1&&xx<x2&&yy+header>=y1&&yy+header<y2)
			{
				c.showBlackChar(xx, yy+header);
				c.showChar(xx, yy+header, ch, col);		
			}
		}
	}

	private void targetActor(int n)
	{
		Actor a = visibleActors.get(n);
		if (a==null) return;
		tx=a.getCreature().getX();
		ty=a.getCreature().getY();
		findTargLine();
	}
	
	private void findTargLine()
	{
		int x1=pc.getX();
		int y1=pc.getY();
		int x2=tx;
		int y2=ty;
		int y=y1;
		int x=x1;
		int prev_x;
		int prev_y;
		double chance=1.0;
		int[][] new_path = new int[Math.max(Math.abs(x2-x1), Math.abs(y2-y1))+1][2];
		int i=0;
		if (Math.abs(x2-x1)>=Math.abs(y2-y1))
		{
			line=new int[Math.abs(x2-x1)][2];
			chances=new double[Math.abs(x2-x1)];
			float deltaerr=(float)(y2-y1)/Math.abs(x2-x1); //(Math.abs(dy-py))/(Math.abs(dx-px));
			float err=0;
			int sgn=1;
			if (x1>x2) sgn=-1;
			for (x=x1+sgn; sgn*x<=sgn*x2; x+=sgn)
			{
				prev_x=x;
				prev_y=y;
				err+=deltaerr;
				if (err>=0.5)
				{
					y++;
					err-=1.0;
				}
				else if (err<=-0.5) 
				{
					y--;
					err+=1.0;
				}
				double h=(double)map.getTile(x, y).getHeight()/10;
				chance-=h;
				line[i][0]=sgn;
				line[i][1]=y-prev_y;
				if (Math.sqrt((x1-x)*(x1-x)+(y1-y)*(y1-y))>pc.getCurrentRangedAttackType().getRange()) chances[i]=0;
				else chances[i]=chance;
				i++;
			}
		}else
		{
			line=new int[Math.abs(y2-y1)][2];
			chances=new double[Math.abs(y2-y1)];
			//System.out.println("horiz");
			float deltaerr=(float)(x2-x1)/Math.abs(y2-y1); //(Math.abs(dy-py))/(Math.abs(dx-px));
			float err=0;
			int sgn=1;
			if (y1>y2) sgn=-1;
			for (y=y1+sgn; sgn*y<=sgn*y2; y+=sgn)
			{
				prev_x=x;
				prev_y=y;
				err+=deltaerr;
				if (err>=0.5)
				{
					x++;
					err-=1.0;
				}
				else if (err<=-0.5) 
				{
					x--;
					err+=1.0;
				}
				double h=(double)map.getTile(x, y).getHeight()/10;
				chance-=h;
				
				line[i][0]=x-prev_x;
				line[i][1]=sgn;
				if (Math.sqrt((x1-x)*(x1-x)+(y1-y)*(y1-y))>pc.getCurrentRangedAttackType().getRange()) chances[i]=0;
				else chances[i]=chance;
				i++;
			}
		}
	}
	
	private final int footer=2;
	private final int header=2;

	private int tx;
	private int ty;
	private int[][] line;
	private double[] chances;
	private Map map;
	private Player pc;
	private static Actor lastTarget=null;
	private ArrayList<Actor> visibleActors;
	private int visibleActorsCursor;
}	
