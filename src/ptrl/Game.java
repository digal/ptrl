package ptrl;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.*;

import ptrl.map.*;
import ptrl.combat.AttackResult;
import ptrl.combat.PtrlConstants;
import ptrl.combat.RangedAttack;
import ptrl.combat.RangedAttackType;
import ptrl.combat.UnarmedAttackType;
import ptrl.creatures.*;
import ptrl.ui.*;
import ptrl.util.GameSettings;
import ptrl.util.GameTime;
import ptrl.util.MapEvent;
import ptrl.util.Message;
import ptrl.util.MessageStack;
import ptrl.items.*;
import org.xml.sax.*;

import javax.xml.parsers.*;

/**
 * Main game class
 */
public class Game implements IGameScreen, IAnimated, Serializable 
{
	
	private boolean draw_self;
	private int footer;
	private int header;
	private IGameScreen delegate;
	private GlobalMap gmap;
	private Player pc;
	private Map map;
	private int myCurrentLevel;
//	private int mapy;
//	private int mapz; //level: 0 - ground; <0 - underground
	private GameTime gt;
	private MessageStack mstack;
	private static Game myInstance;
	
	public static Game getInstance() {
		return myInstance;
	}
	
	
	public Game() throws ParserConfigurationException, SAXException, IOException 
	{
		header=2;
		footer=2;
		pc=new Player();
		draw_self=true;
		gt=new GameTime(0);
		myInstance = this;
	}
	
	public void start() throws ParserConfigurationException, SAXException, IOException
	{
		File file = new File("."+File.separator+"players"+File.separator+pc.getName());
		file.mkdir();
		gmap=new GlobalMap("global");
		GameSettings gset=GameSettings.getInstance();
		int map_x=gset.getMapWidth();
		int map_y=gset.getMapHeight();
		gt = new GameTime(0);
		mstack = new MessageStack(gset.getMessages());
		/*if (maptype.equalsIgnoreCase("forest")) 
			map = MapGenerator.horForest(map_x, map_y, tree, b1, b2, floortile, road, (float)0.5, (float)0.7);
		else if (maptype.equalsIgnoreCase("ruins"))
			map = MapGenerator.simpleRuins(map_x, map_y, (float)0.2, floortile, floortile, bld_floor, road, ruinwall, door1);
		else if (maptype.equalsIgnoreCase("cave"))
			map = MapGenerator.caverns(map_x, map_y, (int)map_x/2, (int)map_y/2, "default.tileset");*/
		MapDescriptor desc = gmap.getCurrentTile();
		map = desc.getMap();
		map.setGameTime(gt);
		map.setMessageStack(mstack);
		pc.setXY(map_x/2, map_y/2);
		map.addActor(pc, "player", "none");
//		Item cap=new Item("Items.xml", "'Sense PRO'™ mask");
//		map.addItem(cap, pc.getX()+1, pc.getY());
		Item tags=new Item((float)0.1, "name tag: '"+pc.getName()+"'", "heap of name tags: '"+pc.getName()+"'", 0, 0, '\'', PtrlConstants.LGRAY);
		tags.setType(Item.IS_NECK);

		pc.getEquipment().getSlot(Equipment.EQ_NECK).setItem(tags);
		
//		map.setTile(map.getWidth()/2-10, map.getHeight()/2, inwall);
//		map.setTile(map.getWidth()/2-11, map.getHeight()/2, inwall);
//		map.destroyTile(map.getWidth()/2-10, map.getHeight()/2);
//		map.destroyTile(map.getWidth()/2-11, map.getHeight()/2);

//		Creature c=new Creature("Creatures.xml", "rat", 1, "");
//		c.setXY(map.getWidth()/2+10, map.getHeight()/2-1);
//		map.addActor(c, "forest", "aimless");
//		
//		c=new Creature("Creatures.xml", "rat", 1, "");
//		c.setXY(map.getWidth()/2+10, map.getHeight()/2-1);
//		map.addActor(c, "forest", "aimless");
//		
//		c=new Creature("Creatures.xml", "rat", 1, "");
//		c.setXY(map.getWidth()/2+12, map.getHeight()/2-2);
//		map.addActor(c, "forest", "aimless");
//		
//		c=new Creature("Creatures.xml", "psi rat", 1, "");
//		c.setXY(map.getWidth()/2+10, map.getHeight()/2-3);
//		map.addActor(c, "forest", "aimless");
//		
//		c=new Creature("Creatures.xml", "psi rat", 1, "");
//		c.setXY(map.getWidth()/2-10, map.getHeight()/2-3);
//		map.addActor(c, "forest", "aimless");
		//MapGenerator.populate(map, "zombie", 0.002);
		//MapGenerator.populate(map, "imp", 0.0005);
		
		pc.refreshAttackTypes();
	}

	public Player getPlayer()
	{
		return pc;
	}

	/**
	 * Draws a part of map.
	 * Trying to keep player in center of window
	*/
	public void drawMap(Console c)
	{
		int w=c.getSymWidth();
		int h=c.getSymHeight()-footer-header;
		int x=0;
		int y=0;
		
		int px=pc.getX(); 
		int py=pc.getY();

		if (map.getWidth()<c.getSymWidth())	x=(map.getWidth()-c.getSymWidth())/2;
		else if (px<=w/2) x=0;
		else if (px>=map.getWidth()-w/2) x=map.getWidth()-w;
		else x = px-w/2;

		if (map.getHeight()<c.getSymHeight()) y=(map.getHeight()-c.getSymHeight())/2;
		else if (py<=h/2) y=0;
		else if (py>=map.getHeight()-h/2) y=map.getHeight()-h;
		else y = py-h/2;
		
		drawMap(x,y,c);
		
		Message[] m=mstack.getMessages();
		String[] s= new String[header];
		for (int i=0; i<s.length; i++) 
		{
			s[i]="";
		}

		int i=m.length-1;
		int str=0;
		boolean full=false;
		while (i>=0&&m[i].getTurn()>=gt.getCurrentTurn()-1&&!m[i].isShown())
		{
			if (full)
			{
				m[i--].show(!needRefresh());
				continue;
			}
			full=false;
			Message msg = m[i];
			if (msg.isShown()) break;
			if (s[str].length()+m[i].show(!needRefresh()).length()+1<c.getSymWidth()) s[str]=msg.show(!needRefresh())+" "+s[str];
			else if (str<s.length-1) 
			{
				str++;
				if (s[str].length()+msg.show(!needRefresh()).length()+1<c.getSymWidth()) s[str]=msg.show(!needRefresh())+" "+s[str];
			}
			else 
			{
				s[str]=s[str].substring(0, s[str].length()-4);
				s[str]+="...";
				full=true;
			}
			i--;
		}
		int corr=0;
		for (int j=0; j<s.length; j++)
		{
			if (s[j]==null) corr--;
			else if (s[j]=="") corr--;
		}
		//System.out.println("corr="+corr+", s.length="+s.length);
		for (int j=0; j<s.length; j++)
		{
			if (s[s.length-j-1]!=null)
				if (s[s.length-j-1]!="")
					c.printString(s[s.length-j-1], header-j , j+corr, PtrlConstants.WHITE);
		}

		String s1="";
		String s2="";
		UnarmedAttackType ut = pc.getCurrentCloseAttackType();
		if (ut!=null) s1=ut.getInfoString();
		RangedAttackType rt = pc.getCurrentRangedAttackType();
		if (rt!=null) s2=rt.getShortInfoString();
		if (s1.length()+s2.length()+2>c.getSymWidth())
		{
			s1="["+ut.getName()+"]";
			s2="["+rt.getName()+"]";
		}
		c.printString(s1, 0, c.getSymHeight()-2, PtrlConstants.WHITE);
		c.printString(s2, c.getSymWidth()-s2.length(), c.getSymHeight()-2, PtrlConstants.LGRAY);
		//c.printString(map.getCreature(1).toString(),0,c.getSymHeight()-1,Tile.WHITE);
		String ts="; time: "+gt.getCurrentTimestamp();
		c.printString(pc.toString()+ts,0,c.getSymHeight()-1,PtrlConstants.WHITE);
		// c.printString(ts,c.getSymWidth()-ts.length()-1,c.getSymHeight()-1,Tile.WHITE);		
	}
	
	/**
	 * Draws a part of map, specified by a window.
	 * 
	 * @param x x coord of a window.
	 * @param y y coord of a window.
	 */
	public void drawMap(int x, int y, Console c)
	{
		//Graphics g = buf_image.getGraphics();
		
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
		int creatures_cur=c.getSymWidth()-1;
		//-----Draw creatures-----
		ArrayList<Actor> viz = map.getPlayerActor().getVisibleActors();
		for (int i=0; i<viz.size(); i++)
		{
			cr = viz.get(i).getCreature();
			int cx=cr.getX();
			int cy=cr.getY();
			if (cx!=pc.getX()||cy!=pc.getY())
			{
				if (cx-x>=x1&&cx-x<x2&&cy-y+header>=y1&&cy-y+header<y2)
				{
					c.showBlackChar(cx-x, cy-y+header);
					c.showChar(cx-x, cy-y+header, cr.getSymbol(), cr.getColor());
				}
				c.showChar(creatures_cur, c.getSymHeight()-1, cr.getSymbol(), cr.getColor());
				creatures_cur--;
			}

		}
		//-----Draw projectiles-----
		ArrayList projs = map.getProjectiles();
		for (int i=0; i<projs.size(); i++)
		{
			RangedAttack ra = (RangedAttack)projs.get(i);
			if (ra.getAttackCounter()<=0) 
			{
				if (ra.isActive()) col=PtrlConstants.WHITE;
				else col=PtrlConstants.LRED;
				//char chr=map.getTile(ra.getCurrentX(), ra.getCurrentY()).getSymbol();
				c.showBlackChar(ra.getCurrentX()-x, ra.getCurrentY()-y+header);
				c.showChar(ra.getCurrentX()-x, ra.getCurrentY()-y+header, ra.getSymbol(), ra.getColor());		
			}
		}
		if (pc.getHP()>0)
		{
			c.showBlackChar(pc.getX()-x, pc.getY()-y+header);
			//c.showRectChar(pc.getX()-x, pc.getY()-y+header, Tile.LYELLOW);
			c.showChar(pc.getX()-x, pc.getY()-y+header, pc.getSymbol(), pc.getColor());		
		}
	}
	

	
	private void dialog(IGameScreen dialog)
	{
		delegate=dialog;
		draw_self=false;
	}
	
	private void question(IGameScreen q)
	{
		delegate=q;
		draw_self=true;
	}
	
	private void unDialog()
	{
		delegate=null;
	}
	
	public void paint(Console c)
	{
		if (delegate!=null&&!draw_self) 
		{
			delegate.paint(c);
		}
		else if (delegate!=null&&draw_self)
		{
			drawMap(c);
			delegate.paint(c);
		}
		else 
		{
			drawMap(c);
		}
	}

	public void message(String text)
	{
		if (mstack!=null) mstack.addMessage(new Message(text, gt));
	}
	
	public boolean getKeyEvent(KeyEvent ke)
	{
		if (delegate!=null) 
		{
			if (delegate.getKeyEvent(ke)) 
			{
				if (delegate instanceof ReloadDialog)
				{
					ReloadDialog rd=(ReloadDialog)delegate;
					if (rd.isReloaded()) map.turn(3000);
				}
				else if (delegate instanceof GlobalMapScreen)
				{
					reloadMap();
				}
				unDialog();
			}
			
			return false;
		}
		char ch=ke.getKeyChar();
		//pc.turn();
		if (ch=='Q') 
		{
			return true;
		}
		else if (ch=='s')
		{
			dialog(new SkillsDialog(pc));
		}
		else if (ch=='o')
		{
			question(new DoorQuestion(null, false)); 
		}
		else if (ch=='c')
		{
			question(new DoorQuestion(null, true));
		}
		else if (ch=='m')
		{
			if (mstack==null) System.out.println("Mstack is null"); 
			else 
			{
				dialog(new MessagesDialog(mstack, gt.getCurrentTurn()));
			}
		}
		else if (ch=='e')
		{   
			
			String s=map.getTile(pc.getX(), pc.getY()).getName();
			message(s);
		}
		else if (ch=='['||ch=='0')
		{
			pc.prevCloseAttackType();
		}
		else if (ch==']'||ch=='.')
		{
			pc.nextCloseAttackType();
		}
		else if (ch==';'||ch=='/')
		{
			pc.prevRangedAttackType();
		}
		else if (ch=='\''||ch=='*')
		{
			pc.nextRangedAttackType();
		}
		else if (ch=='a')
		{
			//message("---Aviable attacks:---");
			UnarmedAttackType[] at = pc.getCloseAttackTypes();
			for (int i=0; i<at.length; i++)
			{
				String s=at[i].getInfoString();
				message(s);
			}
		}
		else if (ch=='g')
		{
			int[] items = map.getItemsIndexesXY(pc.getX(), pc.getY());
			if (items.length!=0)
			{
				if (items.length==1)
				{
					Item i=map.removeItem(items[0], map.getItem(items[0]).getQty());
					pc.getInventory().AddItem(i);
				}
				else question(new PickupQuestion(items));
			}
		}
		else if (ch=='5')
		{
			map.movePlayer(new int[]{0,0});
		}
		else if (ch=='i')
		{
  			dialog(new EquipmentScreen(pc));
		}
		else if (ch=='I')
		{
			dialog(new InventoryScreen(pc));
		}
		else if (ch=='t'||ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			if (pc.getCurrentRangedAttackType()!=null) dialog(new TargetDialog(map, pc));
		}
		else if (ch=='r') //fast reload
		{
			RangedAttackType rat = pc.getCurrentRangedAttackType();
			if (rat!=null)
			{
				ReloadDialog rd=new ReloadDialog(pc, mstack, gt);
				rd.simpleReload();
			}
		}
		else if (ch=='R') //fast reload
		{
			RangedAttackType rat = pc.getCurrentRangedAttackType();
			if (rat!=null)
				if (rat.getCaliber()!=null)
				{
					ReloadDialog rd=new ReloadDialog(pc, mstack, gt);
					if (rd.getVariantsN()>0) question(rd);
				}
		}
		else if (ch=='M')
		{
			dialog(new GlobalMapScreen(gmap, pc));
		}
		else if (ch=='S')
		{
			save();
		}
		
		else 
		{
			int[] xy={0,0};
			if (ch=='7'||ch=='8'||ch=='9')
			{
				xy[1]=-1;
			}
			else if (ch=='1'||ch=='2'||ch=='3')
			{
				xy[1]=1;
			}
			
			if (ch=='7'||ch=='4'||ch=='1')
			{
				xy[0]=-1;
			}
			else if (ch=='9'||ch=='6'||ch=='3')
			{
				xy[0]=1;
			}
			if ((xy[0]!=0||xy[1]!=0)&&pc.getX()+xy[0]>=0&&pc.getX()+xy[0]<map.getWidth()&&pc.getY()+xy[1]>=0&&pc.getY()+xy[1]<map.getHeight()) 
			{
				Tile tl=map.getTile(pc.getX()+xy[0], pc.getY()+xy[1]);
				Actor a=map.getActor(pc.getX()+xy[0], pc.getY()+xy[1]);
				//TODO: check fraction
				if (a!=null)
				{
					AttackResult[] ar =map.getPlayerActor().closeAttack(xy);
					if (ar==null) message ("You miss!");
					else if (ar.length==0) message ("You miss!");
					else 
					{
						for (int i=0; i<ar.length; i++)
						{
							if (ar[i]==null) message ("You miss!");
							//else message ("You hit "+a.getCreature().getTheName(false)+" for "+ar[i].getDamage()+" hp.");
						}
						if (a.getCreature().getHP()<=0) message (a.getCreature().getTheName(true)+" dies.");
						int t=pc.getCurrentCloseAttackType().getTime()*1000;
						map.turn(t);
					}
				}
				else if (tl instanceof DoorTile) 
				{
					DoorTile dtl=(DoorTile)tl;
					if (dtl.isOpen()) 
					{
						map.movePlayer(xy);
						messageItems();
					}
					else question(new DoorQuestion(xy, false));
					
					//TODO: message "There is no door" else
				}
				else 
				{
					if (tl.getTimeMultiplier()!=0)
					{
						map.movePlayer(xy);
						messageItems();	
					}
					else message("Your path is blocked!");
				}
			}
		}
		showEvents();
		if (pc.getHP()<=0) dialog (new DeathQuestion());
		return false;
	}
	
	
	private void reloadMap()
	{
		Actor pl = map.getActor(0);
		map = gmap.getMap();
		map.setPlayerActor(pl);
		
	}

	private void messageItems()
	{
		int[] items = map.getItemsIndexesXY(pc.getX(), pc.getY());
		if (items.length==0) return;
		else if (items.length==1)
		{
			Item i=map.getItem(items[0]);
			message("A "+i.getName()+" is lying here.");
			return;
		}
		else
		{
			message("Several items are lying here.");
		}
		
	}
	
	private void showEvents()
	{
		MapEvent[] me = pc.getEvents();
		if (me==null) return;
		for (int i=0; i<me.length; i++)
		{
			if (me[i].getType()==MapEvent.T_UNDER_ATTACK)
			{
				String who="nobody";
				Creature c = me[i].getSource();
				if (c!=null) who=c.getTheName(true);
				if (me[i].getName()=="miss")
				{
					message (who+" misses.");
				}
				else if (me[i].getName()=="hit")
				{
					message (who+" hits you for "+me[i].getValue()+" hp.");
				}
				else if (me[i].getName()=="you_hit")
				{
					if (c!=null) who=c.getTheName(false);
					message ("You hit "+who+" for "+me[i].getValue()+" hp.");
				}
				else if (me[i].getName()=="you_kill")
				{
					if (c!=null) who=c.getTheName(false);
					message ("You've killed "+who+".");
				}
				else if (me[i].getName()=="death")
				{
					message ("You die!");
				}
			}
		}
		pc.turn();
	}
	
	public boolean needRefresh()
	{
		if (delegate!=null)
			if (delegate instanceof IAnimated) 
				return ((IAnimated)delegate).needRefresh();
			else 
				return false;
		else if (map==null) 
			return false;
		else 
			return map.needAnimation();
	}

	public void refresh()
	{
		if (delegate!=null)
			if (delegate instanceof IAnimated) ((IAnimated)delegate).refresh();
		if (map!=null) 
		{
			showEvents();
			map.newFrame();
		}
	}
	
	private void save()
	{
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("save.sav"));
			out.writeObject(map);
		}catch (Exception e){e.printStackTrace();}
		System.exit(0);
			
	}

	

	/**
	 * 
	 *  
	 * @author Digal
	 */
	private class DoorQuestion implements IGameScreen
	{
		public DoorQuestion(int[] d, boolean c)
		{
			dir=d;
			close=c;
			//TODO: if d==null check neighborhood tiles and choose dir if there is only one door
		}
		
		public void paint(Console c)
		{
			String q;
			if (dir==null) q="In which direction? [12346798]";
			else q="Open the door? [y/n]";
			c.printString(q, 0,0, PtrlConstants.WHITE);
			
		}

		public boolean getKeyEvent(KeyEvent ke)
		{
			char ch=ke.getKeyChar();
			if (dir==null)
			{
				dir=new int[]{0,0};
				if (ch=='7'||ch=='8'||ch=='9')
				{
					dir[1]=-1;
					open=true;
				}
				else if (ch=='1'||ch=='2'||ch=='3')
				{
					dir[1]=1;
					open=true;
				}
				
				if (ch=='7'||ch=='4'||ch=='1')
				{
					dir[0]=-1;
					open=true;
				}
				else if (ch=='9'||ch=='6'||ch=='3')
				{
					dir[0]=1;
					open=true;
				}
			}
			else 
			{
				if (ch=='Y'||ch=='y') 
				{
					map.turn(5000);
					open=true;	
				}
			}
			if (open&&pc.getX()+dir[0]>=0&&pc.getX()+dir[0]<map.getWidth()&&pc.getY()+dir[1]>=0&&pc.getY()+dir[1]<map.getHeight()) 
			{
				open();
			}
			if (open||ch=='n'||ch=='N') return true;
			else return false;
		}
		
		private void open()
		{
			Tile tl=map.getTile(pc.getX()+dir[0], pc.getY()+dir[1]);
			DoorTile dtl;
			if (tl instanceof DoorTile) 
			{
				dtl=(DoorTile)tl;
				if (dtl.isOpen()&&close) 
				{
					dtl.close();
					//gt.makeTurn(dtl.getOpenCloseTime());
					mstack.addMessage(new Message("You've closed the door",gt));
				}
				else if (!dtl.isOpen())
				{
					dtl.open();
					map.turn(1000);
					//gt.makeTurn(dtl.getOpenCloseTime());					
					mstack.addMessage(new Message("You've opened the door",gt));
				}
			}
			else mstack.addMessage(new Message("There's no door in this direction.",gt));
		}
		
		private int[] dir;
		private boolean open;
		private boolean close;
	}
	
	private class DeathQuestion implements IGameScreen
	{
		public void paint(Console c)
		{
			drawMap(c);
			String q="You are dead. Press space.";
			for (int i=0; i<c.getSymWidth(); i++)
			{
				c.showBlackChar(i, 0);
			}
			c.printString(q, 0,0, PtrlConstants.LRED);
			c.printString(pc.toString(),0,c.getSymHeight()-1,PtrlConstants.WHITE);
		}

		public boolean getKeyEvent(KeyEvent ke)
		{
			if (ke.getKeyCode()==KeyEvent.VK_SPACE)
			{
				System.out.println("R.I.P.");
				Message[] m = mstack.getMessages();
				for (int i=0; i<m.length; i++)
				{
					System.out.println(m[i].show());
				}
				System.exit(0);
				return true;
			}
			else return false;
		}
	}

	private class PickupQuestion implements IGameScreen
	{
		public PickupQuestion(int[] itms)
		{
			item_indexes = map.getItemsIndexesXY(pc.getX(), pc.getY());
			cur=0;
		}
		
		public void paint(Console c)
		{
			String q="Pick up the "+map.getItem(item_indexes[cur]).getInvString()+" [Y/N]?";
			c.printString(q, 0,0, PtrlConstants.WHITE);
		}

		public boolean getKeyEvent(KeyEvent ke)
		{
			if (ke.getKeyChar()=='y'||ke.getKeyChar()=='Y')
			{
				Item i=map.removeItem(item_indexes[cur], map.getItem(item_indexes[cur]).getQty());
				pc.getInventory().AddItem(i);
				item_indexes=map.getItemsIndexesXY(pc.getX(), pc.getY());
			}
			else if(ke.getKeyChar()=='n'||ke.getKeyChar()=='N')
			{
				cur++;
			}
			else return false;
			
			if (cur>=item_indexes.length) return true;
			else return false;
		}
		
		private int item_indexes[];
		private int cur;
	}



}
