package ptrl.map;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ptrl.creatures.Creature;
import ptrl.items.Ammo;
import ptrl.items.Item;
import ptrl.items.RangedWeapon;
import ptrl.items.Weapon;
import ptrl.util.GameSettings;

/**
 * Class used to generate maps.
 */

//TODO: Implement tilesets
public class MapGenerator 
{
	public static Map generate(MapDescriptor desc)
	{
		TileSet ts = new TileSet(desc.getTileSetName());
		int w=GameSettings.getInstance().getMapWidth();
		int h=GameSettings.getInstance().getMapHeight();
		Map m = new Map(w, h);
		m.setDescriptor(desc);
		obstacleFill(m, ts, 0.00);
		if (desc instanceof SettlementMapDescriptor)
		{
			SettlementMapDescriptor sDesc = (SettlementMapDescriptor) desc;
			generateSettlement(m);
		} else if ("mountains".equals(desc.getSurface())) {
      caverns(m, w,h, w/2, h/2, "default");
    } else {
      obstacleFill(m, ts, 0.1);
    }
    arsenal(m);
		populate(m, "zombie", 0.0005);
		populate(m, "imp", 0.00001);

		return m;
	}
	
	 private void parseLevels(String filename, int number, Map m) throws ParserConfigurationException, SAXException, IOException
	 {
	 	File f = new File("."+File.separator+filename);

		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);
			//System.out.println(doc);
			Element root = doc.getDocumentElement();
			NodeList nl = root.getChildNodes();
		    for (int i=0; i<nl.getLength(); i++)
	    	{
	    		Node n = nl.item(i);
	    		if (n instanceof Element)
	    		{
	    			Element e = (Element)n;
	    			int num= Integer.parseInt(e.getAttribute("number"));
	    			if (num==number&&e.getTagName().equals("level")) parseLevelElement(e, m);
	    		}
	  		}
		}
		catch (SAXParseException e)
		{
			System.out.println("oops!");
			System.out.println("line: "+e.getLineNumber());
			System.out.println("col: "+e.getColumnNumber());
			e.printStackTrace();
		}
	 }
	 
	 protected void parseLevelElement(Element e, Map m) throws ParserConfigurationException, SAXException, IOException
	 {
		 NodeList nl = e.getChildNodes();
		 for (int i=0; i<nl.getLength(); i++)
		 {
		    	Node n_child = nl.item(i);
		    	if (n_child instanceof Element)
		    	{
		    		Element e_child = (Element)n_child;
		    		Text tnode = (Text)e_child.getFirstChild();
		    		if (e_child.getTagName().equals("shop"))
		    		{
		    			makeShopFromElement(m, e_child);
		    		}
		    		
		    	}
		 }
		 
	 }

	private static void makeShopFromElement(Map m, Element e)
	{
		String s = e.getAttribute("chance");
		double chance = s.equals("")?1d:Double.parseDouble(s);
		if (chance<Math.random())
		{
			return;
		}
		s=e.getAttribute("level");
		int level = s.equals("")?0:Integer.parseInt(s);
		s=e.getAttribute("type");
		StringTokenizer st = new StringTokenizer(s, "|");
		List<String> types = new ArrayList<String>();
		while (st.hasMoreElements())
		{
			types.add(st.nextToken());
		}
		s=e.getAttribute("pricemodifier");
		double priceModifier = s.equals("")?0d:Double.parseDouble(s);
		s=e.getAttribute("width");
		int w = s.equals("")?3:Integer.parseInt(s);
		s=e.getAttribute("height");
		int h = s.equals("")?3:Integer.parseInt(s);
		
		makeShop(level, types, priceModifier, w, h, m);
	}
	 
	private static void makeShop(int level, List<String> types, double priceModifier, int w, int h, Map m)
	{
		int x = (int)(Math.random()*m.getWidth()-w);
		int y = (int)(Math.random()*m.getHeight()-h);
		TileSet ts = new TileSet(m.getDescriptor().getTileSetName());
		for (int i=x; i<=x+w; i++)
		{
			for (int j=y; j<=y+h; j++)
			{
				m.setTile(i,j,ts.getPiece(ts.P_OUTER_WALL));	
			}
		}
		
		
	}

	private static void generateSettlement(Map map)
	{
		MapDescriptor md = map.getDescriptor();
		if (!(md instanceof SettlementMapDescriptor))
			return;
		SettlementMapDescriptor desc = (SettlementMapDescriptor)md;
		TileSet ts = new TileSet(desc.getTileSetName());
		obstacleFill(map, ts, 0.01);
		int w=map.getWidth();
		int h=map.getHeight();
		//Map newmap = new Map(w,h,ts.getPiece(TileSet.P_GROUND));
		int roadW = 2;
		float d = desc.getDamage();
		//Horiz. road
	    drawHorizRoad(2, d, map, ts);
	    //Vert. road
	    drawVertRoad(2, d, map, ts);
	    //add buildings
	    int sector_w = (w-(2*roadW+1))/2;
		int sector_h = (h-(2*roadW+1))/2;

	    //TODO: add unusual buidlings here?
		//nw sector	    
				int x1 = 0;
				int y1 = 0;
				int x2 = 0+sector_w-1;
				int y2 = 0+sector_h-1;
				drawSmthInRect(map, d, x1+1, y1+1, x2-1, y2-1, ts);
		//ne sector
				x1=x2+roadW*2+2;	
				x2=w-1;
				drawSmthInRect(map, d, x1+1, y1+1, x2-1, y2-1, ts);
		//se sector		
				y1=y2+roadW*2+3;	
				y2=h-1;
				drawSmthInRect(map, d, x1+1, y1+1, x2-1, y2-1, ts);

		//sw sector		
				x1 = 0;
				x2 = 0+sector_w-1;
				drawSmthInRect(map, d, x1+1, y1+1, x2-1, y2-1, ts);
	}

	private boolean[][] roadMap2DirMap(boolean[][] roadMap)
	{
		boolean[][] dirMap = new boolean[3][3];
		for (boolean[] arr: dirMap) {
			for (int i=0; i<arr.length; i++) {
				arr[i] = false;
			}
		}
		dirMap[0][1]=roadMap[0][1]; //N
		dirMap[1][0]=roadMap[1][0]; //W
		dirMap[1][2]=roadMap[1][2]; //E
		dirMap[2][1]=roadMap[2][1]; //S
			
		if (roadMap[0][0] && !dirMap[0][1] && !dirMap[1][0]) { //NW
			dirMap[0][0]=true;
		}
		if (roadMap[0][2] && !dirMap[0][1] && !dirMap[1][2]) { //NE
			dirMap[0][2]=true;
		}
		if (roadMap[2][0] && !dirMap[1][0] && !dirMap[2][1]) { //SW
			dirMap[2][0]=true;
		}
		if (roadMap[2][2] && !dirMap[1][2] && !dirMap[2][1]) { //SE
			dirMap[2][2]=true;
		}
		return dirMap;
	}
	
	private void generateRoads(Map m, boolean[][] roadMap)
	{
		boolean[][] dirMap = roadMap2DirMap(roadMap);
		
	}

	private static void obstacleFill (Map map, TileSet ts, double obstacleChance) { 
		Tile ground = ts.getPiece(TileSet.P_GROUND);
		for (int i=0; i<map.getWidth(); i++) {
			for (int j=0; j<map.getHeight(); j++) {
				if (Math.random()>obstacleChance) {
					map.setTile(i,j,ground);
				} else {
					map.setTile(i,j,ts.getPiece(TileSet.P_OBSTACLE));
				}
			}
		}
	}
	
	/**
	 * Generates new map.
	 */
	private static Map simpleFill(int width, 
						   		 int height, 
						   		 Tile floor)
	{
	    return new Map(width, height, floor);	
	}
	
	private static Map simpleBordered(int width, 
						   		     int height, 
						   		     Tile floor,
						   		     Tile border_wall)
	{
		Map map = new Map(width, height, floor);	
	    for (int i=0; i<width; i++)
	    {
	    	map.setTile(i, 0, border_wall);
	    	map.setTile(i, height-1, border_wall);
	    } 
   	    for (int i=1; i<height-1; i++)
	    {
	    	map.setTile(0, i, border_wall);
	    	map.setTile(width-1, i, border_wall);
	    } 
	    return map;
	}
	
	/**
	 * Creates simple ruins of town with damaged crossroad.
	 */
	
	/*public static Map simpleRuins(int width,
								  int height,
								  float d,
								  Tile ground,
								  Tile ground2,
								  Tile floor,
								  Tile road,
								  Tile wall,
								  DoorTile door)
									
	{
		Map newmap = simpleFill(width, height, ground); //Fill with ground
		int xcenter = width / 2;
		int ycenter = height / 2;
		int w = 2;
		//Horiz. road
	    drawHorizRoad(2, d, newmap, road, ground);
	    //Vert. road
	    drawVertRoad(2, d, newmap, road, ground);
	    //add buildings
	    int sector_w = (width-(2*w+1))/2;
		int sector_h = (height-(2*w+1))/2;

	    //TODO: add unusual buidlings here?
		//nw sector	    
				int x1 = 0;
				int y1 = 0;
				int x2 = 0+sector_w-1;
				int y2 = 0+sector_h-1;
				drawSmthInRect(newmap, d, x1+1, y1+1, x2-1, y2-1, wall, floor, ground, door);
		//ne sector
				x1=x2+w*2+2;	
				x2=width-1;
				drawSmthInRect(newmap, d, x1+1, y1+1, x2-1, y2-1, wall, floor, ground, door);
		//se sector		
				y1=y2+w*2+3;	
				y2=height-1;
				drawSmthInRect(newmap, d, x1+1, y1+1, x2-1, y2-1, wall, floor, ground, door);

		//sw sector		
				x1 = 0;
				x2 = 0+sector_w-1;
				drawSmthInRect(newmap, d, x1+1, y1+1, x2-1, y2-1, wall, floor, ground, door);

		arsenal(newmap);		
		return newmap;
	}*/

	private static void populate(Map m, String name, double chance)
	{
		for (int x=0; x<m.getWidth(); x++)
			for (int y=0; y<m.getHeight(); y++)
			{
				if (m.getTile(x,y).getHeight()==0&&m.getActor(x, y)==null&&chance>=Math.random())
				{
					Creature c=new Creature("Creatures.xml", name, 1, "");
					c.setXY(x, y);
					m.addActor(c, "forest", "aimless");
				}
			}
	}
	
/*	public static Map horForest(int width, int height, Tile tree, Tile b1, Tile b2, Tile ground, Tile road, float road_d, float forest_d)
	{
		Map newmap = simpleFill(width, height, ground); //Fill with ground
		//int xcenter = width / 2;
		int ycenter = height / 2;
		//int w = 2;
		//Horiz. road
	    drawHorizRoad(2, road_d, newmap, road, ground);
	    forestXY(newmap, 0, ycenter-3, width-1, ycenter-5, forest_d-(float)0.4, tree, b1, b2);
	    forestXY(newmap, 0, ycenter-5, width-1, ycenter-7, forest_d-(float)0.2, tree, b1, b2);
	    forestXY(newmap, 0, 0, width-1, ycenter-7, forest_d, tree, b1, b2);
	    forestXY(newmap, 0, ycenter+3, width-1, ycenter+5, forest_d-(float)0.4, tree, b1, b2);
		forestXY(newmap, 0, ycenter+5, width-1, ycenter+7, forest_d-(float)0.2, tree, b1, b2);
		forestXY(newmap, 0, ycenter+7, width-1, height-1, forest_d, tree, b1, b2);
		arsenal(newmap);
	    return newmap;
	}*/
	
	private static void forestXY(Map m, int x1, int y1, int x2, int y2, float d, Tile tree, Tile b1, Tile b2)
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
		for (int i=x1; i<=x2; i++)
			for (int j=y1; j<=y2; j++)
			{
				if (Math.random()<=d)
				{
					float dice=(float)Math.random();
					if (dice<=0.4) m.setTile(i, j, tree);
					else if (dice<=0.7) m.setTile(i, j, b1);
					else m.setTile(i, j, b2);
				}
			}
	}
	
	
	private static void arsenal (Map m)
	{
		ArrayList weaps = new ArrayList();
		ArrayList ammos = new ArrayList();
		try
		{
			File f = new File("."+File.separator+"Weapons.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);
			//System.out.println(doc);
			Element root = doc.getDocumentElement();
			NodeList nl = root.getChildNodes();
			for (int i=0; i<nl.getLength(); i++)
	    	{
	    		Node n = nl.item(i);
	    		if (n instanceof Element)
	    		{
	    			Element e = (Element)n;
	    			String ename = e.getAttribute("name");
	    			if (e.getTagName().equalsIgnoreCase("weapon")) weaps.add(new Weapon(e));
	    			else if (e.getTagName().equalsIgnoreCase("rangedweapon")) weaps.add(new RangedWeapon(e));
	    		}
	    	}
			f = new File("."+File.separator+"Ammo.xml");
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.parse(f);
			//System.out.println(doc);
			root = doc.getDocumentElement();
			nl = root.getChildNodes();
			for (int i=0; i<nl.getLength(); i++)
	    	{
	    		Node n = nl.item(i);
	    		if (n instanceof Element)
	    		{
	    			Element e = (Element)n;
	    			String ename = e.getAttribute("name");
	    			if (e.getTagName().equalsIgnoreCase("ammo")) ammos.add(new Ammo(e));
	    		}
	    	}
		}	
		catch (SAXParseException e)
		{
			System.out.println("oops!");
			System.out.println("line: "+e.getLineNumber());
			System.out.println("col: "+e.getColumnNumber());
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		
		int x1=2;
		int y1=(int)(m.getHeight()/2-2);
		int x2=(int)(5+Math.max(ammos.size(), weaps.size()));
		int y2=(int)(m.getHeight()/2+2);
		try
		{
			Tile floor = new Tile ("Tiles.xml", "stone floor"); 
			Tile ground = new Tile ("Tiles.xml", "ground");
			for (int i=x1-2; i<=x2+1; i++)
				for (int j=y1-2; j<=y2+1; j++)
				{
					m.setTile(i, j ,ground);
				}

			//System.out.println("domik: x1="+x1+"; y1="+y1);
//			drawBuildingInRect(m, (float)0.0, x1, y1, x2, y2, new TileSet(), false);

			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		for (int i=0; i<Math.max(ammos.size(), weaps.size()); i++)
		{
			if (i<ammos.size())
			{
				Item itm=(Item)ammos.get(i);
				itm.setQty(100);
				m.addItem(itm, x1+i+2, y1+1);
			}
			if (i<weaps.size())
			{
				Item itm=(Item)weaps.get(i);
				m.addItem(itm, x1+i+2, y2-1);
			}
		}
	}
	
	/**
	 * Draws horizontal road on map.
	 * @param w w*2+1=(width of road).
	 * @param d - damage (0.0 - 1.0)
	 * @param map map to draw.
	 * @param road road tile.
	 * @param ground ground (damaged road) tile.
	 */
	private static void drawHorizRoad(int w, float d, Map map, TileSet ts)
	{
		int ycenter = map.getHeight() / 2;
		Tile road=ts.getPiece(TileSet.P_BIG_ROAD);
		Tile ground=ts.getPiece(TileSet.P_GROUND);
		for (int i=0; i<map.getWidth(); i++)
	    {
			for (int j=ycenter-w; j<=ycenter+w; j++)
			{
				if (Math.random()>d) 
					{map.setTile(i, j, road);}
				else 
					{map.setTile(i, j, ground);}	
			}
	    } 
	}
	
	/**
	 * Draws vertical road on map.
	 * @param w w*2+1=(width of road).
	 * @param d - damage (0.0 - 1.0)
	 * @param map map to draw.
	 * @param road road tile.
	 * @param ground ground (damaged road) tile.
	 */	
	private static void drawVertRoad(int w, float d, Map map, TileSet ts)
	{
		Tile road=ts.getPiece(TileSet.P_BIG_ROAD);
		Tile ground=ts.getPiece(TileSet.P_GROUND);
		int xcenter = map.getWidth() / 2;
		for (int i=0; i<map.getHeight(); i++)
	    {
			for (int j=xcenter-w; j<=xcenter+w; j++)
			{
				if (Math.random()>d) 
					{map.setTile(j, i, road);}
				else 
					{map.setTile(j, i, ground);}	
			}
	    } 
				
	}
	
	/**
	 * Checks if there is a wall in rect (x1,y1)-(x2,y2)
	 * @param map map to check.
	 * @returns true if there is a wall.
	 */
	private static boolean thereAreWalls(Map map, int x1, int y1, int x2, int y2)
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

		boolean there_is_a_wall = false;
		for (int i=x1; i<=x2; i++)
		{
			for (int j=y1; j<=y2; j++)
			{
				System.out.println("i="+i+", j="+j);
				if ("wall".equalsIgnoreCase(map.getTile(i,j).getType())) {there_is_a_wall=true;}
				
			}
		}
		return there_is_a_wall;

	}
	
	private static void drawBuildingInRect(Map map, float d, int x1, int y1, int x2, int y2, TileSet ts, boolean random)
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
		
		
		int x1n = x1;
		int x2n = x2;
		int y1n = y1;
		int y2n = y2;
		if (random)
		{
			x1n = x1 + (int)Math.round(Math.random()*((x2-x1)/2-2));
			x2n = x2 - (int)Math.round(Math.random()*((x2-x1)/2-2));
			y1n = y1 + (int)Math.round(Math.random()*((y2-y1)/2-2));
			y2n = y2 - (int)Math.round(Math.random()*((y2-y1)/2-2));
		}

		//door
		int doorX;
		int doorY;

		float doorDir=(float)Math.random();
		if (doorDir<=0.25) //door in N wall
		{
			doorX = x1n+1+(int)Math.round(Math.random()*(x2n-x1n-1));
			doorY = y1n;
		}
		else if (doorDir<=0.5) //door in S wall
		{
			doorX = x1n+1+(int)Math.round(Math.random()*(x2n-x1n-1));
			doorY = y2n;
		}
		else if (doorDir<=0.75) //door in W wall
		{
			doorX = x1n;
			doorY = y1n+1+(int)Math.round(Math.random()*(y2n-y1n-1));
		}
		else //door in E wall
		{
			doorX = x2n;
			doorY = y1n+1+(int)Math.round(Math.random()*(y2n-y1n-1));
		}
		Tile wall = ts.getPiece(TileSet.P_OUTER_WALL);
		Tile ground = ts.getPiece(TileSet.P_GROUND);
		Tile floor = ts.getPiece(TileSet.P_FLOOR_A);
		Tile door = ts.getPiece(TileSet.P_OUTER_DOOR);
		if (door instanceof DoorTile)
		{
			if (Math.random()<1) ((DoorTile)door).close();
		} 
		map.setTile (doorX, doorY, door);	
				
		//if (Math.random()<d) {map.destroyTile(doorX, doorY);}		

		for (int i=x1n; i<=x2n; i++)
		{	
			if ((i!=doorX)||(y1n!=doorY)) map.setTile(i, y1n, wall);
			if ((i!=doorX)||(y2n!=doorY)) map.setTile(i, y2n, wall);
			if (Math.random()<d) {map.destroyTile(i, y1n);}
			if (Math.random()<d) {map.destroyTile(i, y2n);}
		}
		
		for (int i=y1n+1; i<=y2n-1; i++)
		{
			if ((x1n!=doorX)||(i!=doorY)) map.setTile(x1n, i, wall);
			if ((x2n!=doorX)||(i!=doorY)) map.setTile(x2n, i, wall);
			if (Math.random()<d) {map.destroyTile(x1n, i);}
			if (Math.random()<d) {map.destroyTile(x2n, i);}
		}
		
		for (int i=x1n+1; i<x2n; i++)
			for (int j=y1n+1; j<y2n; j++)
		{
			if (Math.random()<(d/4)) map.setTile(i, j, ground);
			else map.setTile(i, j, floor);
		}


	}
	
	private static void drawSmthInRect(Map map, float d, int x1, int y1, int x2, int y2, TileSet ts)
	{
		System.out.println("drawSmthInRect: x1="+x1+", x2="+x2+", y1="+y1+", y2="+y2);
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
		
		if ((x2-x1)>(y2-y1))
		{
			if (((x2-x1)>=18&&Math.random()<=0.9)||(((x2-x1)>=8&&Math.random()<=0.6)&&(x2-x1)<18))
			{
				int x3 = (x2+x1)/2;
				drawSmthInRect(map, d, x1, y1, x3-1, y2, ts);
				drawSmthInRect(map, d, x3+1, y1, x2, y2, ts);
			}
			else
			{
	 			if (!thereAreWalls(map, x1, y1, x2, y2))
 				{
					drawBuildingInRect(map, d, x1, y1, x2, y2, ts, true);
				}
			}
		}
		else
		{
			if (((y2-y1)>=15&&Math.random()<=0.9)||((y2-y1)>=8&&Math.random()<=0.6)&&(y2-y1)<15)
			{
				int y3 = (y2+y1)/2;
				drawSmthInRect(map, d, x1, y1, x2, y3-1, ts);
				drawSmthInRect(map, d, x1, y3+1, x2, y2, ts);
			}
			else
			{
				if (!thereAreWalls(map, x1, y1, x2, y2))
 				{
					drawBuildingInRect(map, d, x1, y1, x2, y2, ts, true);
				}
			}
		}
	}
	
	private static Map simpleVault(int width,
								   	int height,
								   	float d,
								   	Tile floor,
								   	Tile inwall,
								   	Tile outwall,
								   	DoorTile door)
	{
		Map newmap = simpleBordered(width, height, new Tile(), outwall);
		int x=0;
		int y=0;
		//int i=0;
		ArrayList vwalls = new ArrayList();
		ArrayList hwalls = new ArrayList();
		float closeddoorchance=(float)0.5;
//make future rooms grid		
		do
		{
			
			x=x+5+(int)Math.round(Math.random()*15);
			if (x>=width-4) break;
			vwalls.add(new Integer(x));
		}
		while (x<width-4);
		
		int[] vw = new int[vwalls.size()];
		for (int j=0; j<vwalls.size(); j++)
		{
			vw[j]=((Integer)vwalls.get(j)).intValue();
		}

	
		do 
		{
			y=y+5+(int)Math.round(Math.random()*2);
			if (y>=height-4) break;
			hwalls.add(new Integer(y));
		}
		while (y<height-3);
		
		int[] hw = new int[hwalls.size()];
		for (int j=0; j<hwalls.size(); j++)
		{
			hw[j]=((Integer)hwalls.get(j)).intValue();
		}

//make corridors
	
		int x1=0, y1=0, x2=0, y2=0;
		int corrs=150;
		boolean lastvert;
		if (Math.random()<=0.5) lastvert=true;
		else lastvert=false;
		boolean[][] rooms = new boolean[vw.length+1][hw.length+1];
		for (int i=0; i<rooms.length; i++)
		{
			for (int j=0; j<rooms[i].length; j++)
			{
				rooms[i][j]=false;
			}
		}					 
	try 
	{
	
		if (lastvert)
		{
			x1=(int)Math.round(Math.random()*(vw.length/2-1));
			x2=(vw.length-1)/2+(int)Math.round(Math.random()*(vw.length/2));
			lastvert=false;
			y1=(int)Math.round(Math.random()*(hw.length-1));
			drawHorCorridor(newmap, vw[x1], vw[x2], hw[y1], 1, (float)0.0, floor, inwall);	
			for (int i=x1+1; i<=x2; i++)
			{
				if (!rooms[i][y1])
				{
					int dx=vw[i-1]+2+(int)Math.round(Math.random()*(vw[i]-vw[i-1]-4)); //door x
					int dy=hw[y1]-1;
					newmap.setTile(dx, dy, door);
					if (Math.random()<=closeddoorchance) 
						((DoorTile)newmap.getTile(dx,dy)).close();
					rooms[i][y1]=true;
				}
				if (!rooms[i][y1+1])
				{
					int dx=vw[i-1]+2+(int)Math.round(Math.random()*(vw[i]-vw[i-1]-4)); //door x
					int dy=hw[y1]+1;
					newmap.setTile(dx, dy, door);
					if (Math.random()<=closeddoorchance) 
						((DoorTile)newmap.getTile(dx,dy)).close();
					rooms[i][y1+1]=true;					
				}
			}
		} else
		{
			y1=(int)Math.round(Math.random()*(hw.length/2-1));
			y2=(hw.length-1)/2+(int)Math.round(Math.random()*(hw.length/2));
			lastvert=true;
			x1=(int)Math.round(Math.random()*(vw.length-1));
			drawVertCorridor(newmap, vw[x1], hw[y1], hw[y2], 1, (float)0.0, floor, inwall);	
			for (int i=y1+1; i<=y2; i++)
			{
				if (!rooms[x1][i])
				{
					int dy=hw[i-1]+2+(int)Math.round(Math.random()*(hw[i]-hw[i-1]-4)); //door x
					int dx=vw[x1]-1;
					newmap.setTile(dx, dy, door);
					if (Math.random()<=closeddoorchance) 
						((DoorTile)newmap.getTile(dx,dy)).close();
					rooms[x1][i]=true;
				}
				if (!rooms[x1+1][i])
				{
					int dy=hw[i-1]+2+(int)Math.round(Math.random()*(hw[i]-hw[i-1]-4)); //door x
					int dx=vw[x1]+1;
					newmap.setTile(dx, dy, door);
					if (Math.random()<=closeddoorchance) 
						((DoorTile)newmap.getTile(dx,dy)).close();
					rooms[x1+1][i]=true;
				}
			}
		}
	}
	catch (ArrayIndexOutOfBoundsException e)
	{
		e.printStackTrace();
		System.out.println("x1: "+x1);
		System.out.println("y1: "+y1);		
		System.out.println("x2: "+x2);
		System.out.println("y2: "+y2);
		System.out.println("lastx: "+vw[vw.length-1]);
		System.out.println("lasty: "+hw[hw.length-1]);		
		System.out.println("vw: "+vw);		
		System.out.println("hw: "+hw);		
		System.out.println("vert: "+lastvert);
		//Toolkit.readCharacter();				
		
	}

		do 
		{
			if (lastvert)
			{
				x2=(int)Math.round(Math.random()*(vw.length-1));
				y1=y1+(int)Math.round(Math.random()*(y2-y1));
				drawHorCorridor(newmap, vw[x1], vw[x2], hw[y1], 1, (float)0.0, floor, inwall);	
				if (x1>x2)
				{
					int tmpx = x1;
					x1 = x2;
					x2 = tmpx;
				}
				for (int i=x1+1; i<=x2; i++)
				{
					if (!rooms[i][y1])
					{
						int dx=vw[i-1]+2+(int)Math.round(Math.random()*(vw[i]-vw[i-1]-4)); //door x
						int dy=hw[y1]-1;
						newmap.setTile(dx, dy, door);
						if (Math.random()<=closeddoorchance) 
							((DoorTile)newmap.getTile(dx,dy)).close();
						rooms[i][y1]=true;
					}
					if (!rooms[i][y1+1])
					{
						int dx=vw[i-1]+2+(int)Math.round(Math.random()*(vw[i]-vw[i-1]-4)); //door x
						int dy=hw[y1]+1;
						newmap.setTile(dx, dy, door);
						if (Math.random()<=closeddoorchance) 
							((DoorTile)newmap.getTile(dx,dy)).close();
						rooms[i][y1+1]=true;					
					}
				}
			} else
			{
				//y1=hw[(int)Math.round(Math.random()*(hw.length/2-1))];
				y2=(int)Math.round(Math.random()*(hw.length-1));
				x1=x1+(int)Math.round(Math.random()*(x2-x1));
				drawVertCorridor(newmap, vw[x1], hw[y1], hw[y2], 1, (float)0.0, floor, inwall);	
				if (y1>y2)
				{
					int tmpy = y1;
					y1 = y2;
					y2 = tmpy;
				}
				for (int i=y1+1; i<=y2; i++)
				{
					if (!rooms[x1][i])
					{
						int dy=hw[i-1]+2+(int)Math.round(Math.random()*(hw[i]-hw[i-1]-4)); //door x
						int dx=vw[x1]-1;
						newmap.setTile(dx, dy, door);
						if (Math.random()<=closeddoorchance) 
							((DoorTile)newmap.getTile(dx,dy)).close();
						rooms[x1][i]=true;
					}
					if (!rooms[x1+1][i])
					{
						int dy=hw[i-1]+2+(int)Math.round(Math.random()*(hw[i]-hw[i-1]-4)); //door x
						int dx=vw[x1]+1;
						newmap.setTile(dx, dy, door);
						if (Math.random()<=closeddoorchance) 
							((DoorTile)newmap.getTile(dx,dy)).close();
						rooms[x1+1][i]=true;
					}
				}
			}
			lastvert = !lastvert;
		}
		while (corrs--!=0);
		
//make corridor walls
		for (int i=0; i<height; i++)
		{
			for (int j=0; j<width; j++)
			{
				if (newmap.getTile(j,i).getName()=="NULL")
					if (newmap.getTile(j+1,i).getName()==floor.getName()
						||newmap.getTile(j-1,i).getName()==floor.getName()
						||newmap.getTile(j,i+1).getName()==floor.getName()
						||newmap.getTile(j,i-1).getName()==floor.getName()
						||newmap.getTile(j+1,i+1).getName()==floor.getName()
						||newmap.getTile(j+1,i-1).getName()==floor.getName()
						||newmap.getTile(j-1,i+1).getName()==floor.getName()
						||newmap.getTile(j-1,i-1).getName()==floor.getName())
							newmap.setTile(j,i,inwall);
			}
		}		
//make rooms

	for (int i=0; i<vw.length; i++)
	{
		for (int j=1; j<height-1; j++)
		{
			if (newmap.getTile(vw[i], j).getName()=="NULL") newmap.setTile(vw[i], j, inwall);
		}
	}
	for (int j=0; j<hw.length; j++)
	{
		for (int i=1; i<width-1; i++)
		{
			if (newmap.getTile(i, hw[j]).getName()=="NULL") newmap.setTile(i, hw[j], inwall);
		}
	}
	for (int i=1; i<width-1; i++)
	{
		for (int j=1; j<height-1; j++)
		if (newmap.getTile(i, j).getName()=="NULL") newmap.setTile(i, j, floor);
	}

//make other doors 
	boolean allroomsok;
	int dx=0,dy=0;
	do
	{
		allroomsok=true;
		for (int i=0; i<rooms.length; i++)
		{
			for (int j=0; j<rooms[i].length; j++)
			{
				if (!rooms[i][j])
				{
					//float dir=(float)Math.random();
					if (j!=0&&rooms[i][j-1]&&allroomsok)//N
					{
						//System.out.println("N");
						if (i==0) {dx=1+(int)Math.round(Math.random()*(vw[0]-3));}
						else if (i==vw.length) {dx=vw[i-1]+2+(int)Math.round(Math.random()*(width-vw[i-1]-4));}
						else {dx=vw[i-1]+2+(int)Math.round(Math.random()*(vw[i]-vw[i-1]-4));}
						dy=hw[j-1];
						newmap.setTile(dx,dy,door);
						if (Math.random()<=closeddoorchance) 
							((DoorTile)newmap.getTile(dx,dy)).close();
						rooms[i][j]=true;
						allroomsok=false;
					}
					if(j==0&&rooms[i][j+1]&&allroomsok)//S
					{	
						//System.out.println("S");						
						if (i==0) {dx=1+(int)Math.round(Math.random()*(vw[0]-3));}
						else if (i==vw.length) {dx=vw[i-1]+2+(int)Math.round(Math.random()*(width-vw[i-1]-4));}
						else {dx=vw[i-1]+2+(int)Math.round(Math.random()*(vw[i]-vw[i-1]-4));}
						dy=hw[j];
						newmap.setTile(dx,dy,door);
						if (Math.random()<=closeddoorchance) 
							((DoorTile)newmap.getTile(dx,dy)).close();
						rooms[i][j]=true;
						allroomsok=false;
					}
					if (i!=0&&rooms[i-1][j]&&allroomsok)//W
					{
						//System.out.println("W");
						if (j==0) {dy=1+(int)Math.round(Math.random()*(hw[0]-3));}
						else if (j==hw.length) {dy=hw[j-1]+2+(int)Math.round(Math.random()*(height-hw[j-1]-4));}
						else {dy=hw[j-1]+2+(int)Math.round(Math.random()*(hw[j]-hw[j-1]-4));}
						dx=vw[i-1];
						newmap.setTile(dx,dy,door);
						if (Math.random()<=closeddoorchance) 
							((DoorTile)newmap.getTile(dx,dy)).close();
						rooms[i][j]=true;
						allroomsok=false;
					}
					if(i==0&&rooms[i+1][j]&&allroomsok)//E
					{	
						//System.out.println("E");						
						if (j==0) {dy=1+(int)Math.round(Math.random()*(hw[0]-3));}
						else if (j==hw.length) {dy=hw[j-1]+2+(int)Math.round(Math.random()*(height-hw[j-1]-4));}
						else {dy=hw[j-1]+2+(int)Math.round(Math.random()*(hw[j]-hw[j-1]-4));}
						dx=vw[i];
						newmap.setTile(dx,dy,door);
						if (Math.random()<=closeddoorchance) 
							((DoorTile)newmap.getTile(dx,dy)).close();
						rooms[i][j]=true;
						allroomsok=false;
					}
				}
			}
		}
	
	} while (!allroomsok);

		return newmap;
	}
	
	private static void drawHorCorridor(Map map, int x1, int x2, int y, int w, float d, Tile floor, Tile wall)
	{
		if (x1>x2)
		{
			int tmpx = x1;
			x1 = x2;
			x2 = tmpx;
		}
	
		for (int i=x1; i<=x2; i++)
		{
			//System.out.println("i:" + i);
			for (int j=y-(w)/2; j<=y+(w)/2; j++)
			{
				map.setTile(i, j, floor);
			}
			//map.setTile(i, y+w/2, wall);
		}
	}

	private static void drawVertCorridor(Map map, int x, int y1, int y2, int w, float d, Tile floor, Tile wall)
	{
		if (y1>y2)
		{
			int tmpy = y1;
			y1 = y2;
			y2 = tmpy;
		}

		for (int i=y1; i<=y2; i++)
		{
			//System.out.println("i:" + i);
			for (int j=x-(w)/2; j<=x+(w)/2; j++)
			{
				map.setTile(j, i, floor);
			}
			//map.setTile(i, y+w/2, wall);
		}
	}
	
	private static Map caverns(Map m, int w, int h, int startX, int startY, String tileSetName)
	{
		int repeats=70;
		//TileSet ts = new TileSet(tileSetName);
		//Map m = new Map(w,h,ts.getPiece(TileSet.P_INNER_WALL));
		Tile wall=null;
		try
		{wall = new Tile ("Tiles.xml", "stone wall");
		} catch (Exception e){e.printStackTrace(); System.exit(0);}

		//Map m = new Map(w,h,wall);
//		m.setTile(startX, startY, ground);
		boolean[][] matrix1=new boolean[w][h];
		boolean[][] matrix2=new boolean[w][h];
		for (int i=0; i<w; i++) 
			for (int j=0; j<h; j++) 
			{
        m.setTile(i,j,wall);
				matrix1[i][j]=true;
				matrix2[i][j]=true;
			}
					
		matrix1[startX][startY]=false;
		for (int i=0; i<1; i++)
			matrix1[(int)Math.random()*(w-10)+5][(int)Math.random()*(h-10)+5]=false;
			
		int left=1;
		int right=w-2;
		int top=1;
		int bottom=h-2;
		
//		int left=startX-1;
//		int right=startX+1;
//		int top=startY-1;
//		int bottom=startY+1;
		int emptyNeighbors=0;
		for (int k=0; k<repeats; k++)
		{		
			for (int i=0; i<w; i++) 
				for (int j=0; j<h; j++) 
					matrix2[i][j]=matrix1[i][j];
		
			//System.out.println("w="+(right-left)+"; h="+(bottom-top));
			for (int i=left; i<=right; i++)
				for (int j=top; j<=bottom; j++)
				{
					if (!matrix1[i][j]) continue; //skip empty spaces
					emptyNeighbors=0;
					for (int x=i-1; x<=i+1; x++)
						for (int y=j-1; y<=j+1; y++)
							if (x>=0&&y>=0&&x<w&&y<h&&(x!=i||y!=j))
								if (!matrix1[x][y])
									emptyNeighbors++;
					//System.out.println (emptyNeighbors+" empty neighbors");
					if (Math.random()*4<emptyNeighbors)
					{
						matrix2[i][j]=false;
						if (i<=left)
							left=Math.max(i-1, 1);
						else if (i>=right)
							right=Math.min(i+1, w-2);
						if (j<=top)
							top=Math.max(j-1, 1);
						else if (j>=bottom)
							bottom=Math.min(j+1, h-2);
					}
				}
			boolean[][] t=matrix1;
			matrix1=matrix2;
			matrix2=t;
		}
		//Tile ground=ts.getPiece(TileSet.P_GROUND);
		Tile ground=null;
		try
		{
			ground = new Tile ("Tiles.xml", "ground");
		} catch (Exception e){e.printStackTrace(); System.exit(0);}
		for (int i=0; i<w; i++)
			for (int j=0; j<h; j++)
			{
				if (!matrix1[i][j])
					m.setTile(i,j,ground);
			}
		return m;
	}
	
	private void plasma_iter(int[][] arr, int x1, int y1, int x2, int y2, float balance)
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
		int cx=(x1+x2)/2;
		int cy=(y1+y2)/2;
		
		if ((x2-x1)>=1)
		{
			arr[cx][y1]=(arr[x1][y1]+arr[x2][y1])/2;
			arr[cx][y2]=(arr[x1][y2]+arr[x2][y2])/2;
		}
		if ((y2-y1)>=1)
		{
			arr[x1][cy]=(arr[x1][y1]+arr[x1][y2])/2;		
			arr[x2][cy]=(arr[x2][y1]+arr[x2][y2])/2;
		}
		if ((x2-x1)>=2&&(y2-y1)>=2)
		{
			double displace=3*10*((x2-x1)+(y2-y1))/(arr.length+arr[0].length);
			//System.out.println("Displace:"+displace);
			arr[cx][cy]=(int)Math.round((arr[x1][y1]+arr[x1][y2]+arr[x2][y1]+arr[x2][y2])/4+(Math.random()-balance)*displace);
			plasma_iter(arr, x1, y1, cx, cy, 0.5f);
			plasma_iter(arr, cx, y1, x2, cy, 0.5f);
			plasma_iter(arr, x1, cy, cx, y2, 0.5f);
			plasma_iter(arr, cx, cy, x2, y2, 0.5f);
		}
	}

	
	
}