package ptrl.map;

import java.io.Serializable;
import java.util.*;
import ptrl.items.*;
import ptrl.util.GameTime;
import ptrl.util.Message;
import ptrl.util.MessageStack;
import ptrl.combat.RangedAttack;
import ptrl.creatures.*;

public class Map implements Serializable 
{
	/**
	 * Creates a map
	 * @param width width of the map in tiles.
	 * @param height height of the map in tiles.
	 */
	public Map(int width, int height) 
	{
		tilemap = new Tile[width][height];
		kbp = new boolean[width][height];
		actor_shadows = new boolean[width][height];
		for (int i=0; i<kbp.length; i++) 
		{
			Arrays.fill(kbp[i], false);
			Arrays.fill(actor_shadows[i], false);
		} 
		actors = new ArrayList();
		items = new ArrayList();
		someone_dies=false;
		hits=false;
		current_actor=-1;
		turn_time=0;
		projectiles=new ArrayList();
	}	
	/**
	 * Creates a map and fills it with particular type of tile.
	 * @param width width of the map in tiles.
	 * @param height height of the map in tiles.
	 * @param default_tile tile which fills the map.
	 * 	 */
	public Map(int width, int height, Tile default_tile) 
	{
		this(width, height);
		fillWithTile(default_tile);
	}
	
	protected void setDescriptor(MapDescriptor desc)
	{
		this.descriptor = desc;
	}

	/**
	 * gets map width.
	 *
	 * @returns map width in tiles.
	 */	
	public int getWidth()
	{
		int width = tilemap.length;
		return width;
	}
	/**
	 * gets map height.
	 *
	 * @returns map height in tiles.
	 */	
	public int getHeight()
	{
		int height = tilemap[0].length;
		return height;
	}
    /**
     * gets particular tile from map.
     * 
     * @param x x coordinate of tile.
     * @param y y coordinate of tile.
     * @returns tile in position (x,y). 0<=x(y)<=width-1(height-1)
     */
	public Tile getTile(int x, int y)
	{
		if (x<0||x>=getWidth()||y<0||y>=getHeight()) return null;
		Tile t = tilemap[x][y];
		return t;
	}
	/**
	 * Sets map tile in position x,y. 0<=x(y)<=width-1(height-1).
	 *
     * @param x x coordinate of tile.
     * @param y y coordinate of tile.
     * @param t tile to set.
	 */
	public void setTile(int x, int y, Tile t)
	{
		tilemap[x][y] = t.copy();
	}
	/**
	 * Fills map with particular type of tile.
	 *
	 * @param t tile which fills the map.
	 */
	public void fillWithTile(Tile t)
	{
		for (int j=0; j<getHeight(); j++)
		{
		 for (int i=0; i<getWidth(); i++)
		 {
		  tilemap[i][j]=t.copy();	
		 }
		} 	
	}
	
	public void addItem(Item i)
	{
		items.add(i.copyAll());
	}

	public void addItem(Item i, int x, int y)
	{
		Item toadd = i.copyAll();
		toadd.setXY(x, y);
		items.add(toadd);
	}
	/**
	 * Destroys tile in pos. (x,y). For generating purposes only.
	 */
	protected void destroyTile(int x, int y)
	{
		Item i = getTile(x,y).destroyTile();
		if (i!=null) 
		{	
			addItem (i, x, y);
		}
	}
	
	public Item getItem(int n)
	{
		if (items.size()<=n) return null;
		else return (Item)items.get(n);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return array items indexes;
	 */
	public int[] getItemsIndexesXY(int x, int y)
	{
		ArrayList al = new ArrayList();
		for (int j=0; j<items.size(); j++)
		{
			Item i = (Item)items.get(j);
			if (i.getX()==x&&i.getY()==y)
				{al.add(new Integer(j));}
		}
		if (al.size()==0) return new int[0];
		int[] arr = new int[al.size()];
		for (int i=0; i<al.size(); i++)
		{
			arr[i]=((Integer)al.get(i)).intValue();
		}
		return arr;
	}
	
	public ArrayList getAllItems()
	{
		return items;
	}

	public ArrayList getProjectiles()
	{
		return projectiles;
	}
	public Creature getCreature(int i)
	{
		return ((Actor)actors.get(i)).getCreature();
	}
	
	public int getActorsN()
	{
		return actors.size();
	}

	public boolean findActorPath(int i, int dx, int dy)
	{
		Actor a = (Actor)actors.get(i);
		return a.findWavePath(dx, dy);
	}
	
	public void addActor(Creature c, String f, String act)
	{
		Actor a=new Actor(c,f,act);
		a.setMap(this);
		a.setTilemap(this.tilemap);
		actors.add(a);
		actor_shadows[a.getCreature().getX()][a.getCreature().getY()]=true;
	}

	public int[][] getPlayerFov()
	{
		Actor a = (Actor)actors.get(actors.size() - 1);
		a.calc360Fov();
		return a.getFov();
	}

	public Actor getActor(int x, int y)
	{
		if (actor_shadows[x][y]==false) return null;
		for (int i=0; i<actors.size(); i++)
		{
			Creature c=((Actor)actors.get(i)).getCreature();
			if (c.getX()==x&&c.getY()==y)
			{
				return (Actor)actors.get(i);
			}
		}
		return null;
	}
	
	public Actor getActor(int n)
	{
		if (n>=actors.size()) return null;
		else return (Actor)actors.get(n);
	}
	
	public Actor[] getActorsInRect(int x1, int x2, int y1, int y2)
	{
		if (x1>x2)
		{
			int tmpx = x1;
			x1 = x2;
			x2 = tmpx;
		}
		if (y1>y2)
		{
			int tmpy = y1;
			y1 = y2;
			y2 = tmpy;
		}
		ArrayList al=new ArrayList();
		Actor a;
		int x;
		int y;
		for (int i=0; i<actors.size(); i++)
		{
			a=(Actor)actors.get(i);
			x=a.getCreature().getX();
			y=a.getCreature().getY();
			if (x>=x1&&x<=x2&&y>=y1&&y<=y2)
			{
				al.add(a);
			}
		}
		Actor[] ret=new Actor[al.size()];
		for (int i=0; i<ret.length; i++)
		{
			ret[i]=(Actor)al.get(i);
		}
		return ret;
	}
	
	public Actor getPlayerActor()
	{
		if (actors!=null)
			if (actors.size()>0) return (Actor)actors.get(actors.size()-1);
		return null;
	}
	
	public long movePlayer(int[] xy)
	{
		Actor a = getPlayerActor();
		actor_shadows[a.getCreature().getX()][a.getCreature().getY()]=false;
		if (a==null) System.exit(0);
		long t=0;
		if (xy[0]==0&&xy[1]==0) t=1000;
		if (a.getCreature().getX()+xy[0]>=0&&a.getCreature().getX()+xy[0]<getWidth()&&a.getCreature().getY()+xy[1]>=0&&a.getCreature().getY()+xy[1]<getHeight())
		{
			t=(a.getCreature().move1sq(xy, getTile(a.getCreature().getX()+xy[0], a.getCreature().getY()+xy[1]).getTimeMultiplier()));
		}
		System.out.println("t="+t);
		actor_shadows[a.getCreature().getX()][a.getCreature().getY()]=true;
		turn(t);
		return t;
	}

	private void clearDead()
	{
		ArrayList new_actors=new ArrayList();
		for (int i=0; i<actors.size(); i++)
		{
			Actor a=(Actor)actors.get(i);
			if (a.getCreature().getHP()>0||i==0) new_actors.add(a);
			else //process actors' deaths  
			{
				//getTile(a.getCreature().getX(), a.getCreature().getY()).pour(1);
				
			}
		}
		actors=new_actors;
		someone_dies=false;
	}
	
	public void turn(long time)
	{
		this.turn_time=time;
		for (int i=1; i<actors.size(); i++)
		{
			Actor a = (Actor)actors.get(i);
			a.getCreature().turn();
		}
		if (someone_dies) clearDead();
		current_actor=1;
		turn();
	}
	
	public void turn()
	{
		while (!needAnimation()&&this.current_actor>0)
		{
			if (someone_dies) clearDead();
			if (current_actor>actors.size()-1) 
			{
				current_actor=-1; 
				return;
			}
			else
			{	
				Actor a = (Actor)actors.get(current_actor);
				actor_shadows[a.getCreature().getX()][a.getCreature().getY()]=false;
				if (a.turnInterrupted()) a.continueTurn();
				else a.startTurn(this.turn_time);
				actor_shadows[a.getCreature().getX()][a.getCreature().getY()]=true;
				if (!needAnimation()) current_actor++;
			}
		}
		if (someone_dies) clearDead();
	}
	
	public void refreshKnown()
	{
		Actor a = (Actor)actors.get(actors.size()-1);
		if (a==null) System.exit(0);
		int[][] fov = a.getFov();
		int x=a.getCreature().getX();
		int y=a.getCreature().getY();
		int l=(int)((fov.length-1)/2);
		for (int i=0; i<fov.length; i++)
			for (int j=0; j<fov[0].length; j++)
			{
				if (x+i-l<0||y+j-l<0||x+i-l>getWidth()-1||y+j-l>getHeight()-1) continue;
				if (fov[i][j]>0) kbp[x+i-l][y+j-l]=true;
			}
	}
	
	public boolean[][] getKbp()
	{
		return kbp;
	}
	/*
	 * Shows path on map, using slime drops
	 *
	public void demoPath(int n)
	{
		Actor a = (Actor)actors.get(n);
		
		int x=a.getCreature().getX();
		int y=a.getCreature().getY();			
		for (int i=0; i<a.current_path.length; i++)
		{
			getTile(x, y).pour(2);
			x+=a.current_path[i][0];
			y+=a.current_path[i][1];				
		}
	}*/
	
	public void setGameTime(GameTime gt)
	{
		this.gt = gt;
	}
	public void setMessageStack(MessageStack mstack)
	{
		this.mstack = mstack;
	}

	public void message(String text)
	{
		if (mstack!=null) mstack.addMessage(new Message(text, gt));
	}
	
	public Item removeItem (int in, int num)
	{
		//ArrayList newitems = new ArrayList(); 
		Item itm = (Item)items.get(in);
		if (num>=itm.getQty()) 
		{
			items.remove(in);
			return itm;
		}  
		Item itm2 = itm.copyAll();
		itm.setQty(itm.getQty()-num);
		itm2.setQty(num);
		return itm2;
	}

	public void addProjectile(RangedAttack ra)
	{
		ra.setMap(this);
		projectiles.add(ra);
	}
	
	public void deathEvent()
	{
		someone_dies=true;
	}
	
	public boolean needAnimation() //...to repaint shots and explosives
	{
		if (projectiles.size()>0) return true;
		else return false;
	}
	
	public void newFrame() //animation of shots and explosives
	{
			ArrayList new_proj=new ArrayList();
			for (int i=0; i<projectiles.size(); i++)
			{
				if (((RangedAttack)projectiles.get(i)).isActive()) new_proj.add(projectiles.get(i));
			}
			projectiles=new_proj;

		for (int i=0; i<projectiles.size(); i++)
		{
			RangedAttack ra = (RangedAttack)projectiles.get(i);
			ra.iterate();
		}
		if (someone_dies) clearDead();
		if (!needAnimation())
		{
			turn();
		}
	}
	
	public void setPlayerActor(Actor a)
	{
//		if (actors.size()==0)
    actors.add(a);
//		else
//			actors.set(0, a);
		a.setMap(this);
		a.setTilemap(this.tilemap);
	}
	
	private MapDescriptor descriptor;
	private boolean someone_dies;
	private GameTime gt;
	private MessageStack mstack;
	private Tile[][] tilemap;
	private ArrayList actors;
	private ArrayList items;
	private boolean kbp[][];	//known by player 	
	private ArrayList projectiles;
	private boolean hits;
	private int current_actor; //-1 if player turn
	private long turn_time;
	private boolean[][] actor_shadows;
	public MapDescriptor getDescriptor()
	{
		return descriptor;
	}
}