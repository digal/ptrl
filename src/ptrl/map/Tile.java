package ptrl.map;

import java.lang.*;
import java.awt.Color;
import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

import ptrl.combat.Attack;
import ptrl.combat.PtrlConstants;
import ptrl.items.*;


/**
Tile is a rectangular piece of map, eg. wall, floor, water, grass, etc. 
It takes 1 char position on screen.
*/
public class Tile implements Serializable
{
 /**
  Creates tile.
  @param tilename name of tile eg. "stone wall" or "metal floor"
  @param tiletype type of tile eg. "wall" or "floor"
  @param mdam max damage tile can take before being destroyed
  @param sym symbol, used to show tile on the screen, eg. "#" for wall
  @param c color of symbol (jcurses.system.CharColor)
  @param t - time to pass this tile; 0 - unpassable; 10 - normal
  @param sw - true if tile is liquid (water, acid, lava, etc.), and player have to swim over it.
  @param h - height of tile (0 for floor, 1,0 for wall)
 */
 public Tile(String tilename, 
 				  String tiletype, 
 				  int mdam, 
 				  char sym, 
 				  short c, 
 				  int t, 
 				  boolean sw, 
 				  int h,
 				  Item i)
 {
 	type = tiletype;
 	name = tilename;
	symbol = sym;
 	
 	default_color = c;	
 	current_color = default_color;
 	
 	max_damage = mdam;
 	current_damage = 0;
 	
 	time_m = t;
 	swim = sw;
 	height = h;
 	if (i!=null) {wreck=i.copyAll();}
 	else {wreck=null;}
 }

 public Tile()
 {
 	this("NULL", "NULL", 0, 'X', PtrlConstants.WHITE, 0, false, 10, null);
 }

 public Tile(Element e) throws ParserConfigurationException, SAXException, IOException
 {
 	this();
 	fillFromElement(e);
 } 

 /**
  * Get specific tile from Tiles File
  * @param filename - name of file
  * @param tilename - name of tile in file
  */
 public Tile(String filename, String tilename) throws ParserConfigurationException, SAXException, IOException
 {
 		this();
 		fillFromFile(filename, tilename);
 }
 
 protected void fillFromFile(String filename, String tilename) throws ParserConfigurationException, SAXException, IOException
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
    			String ename = e.getAttribute("name");
    			if (ename.equalsIgnoreCase(tilename)&&e.getTagName().equals("tile")) fillFromElement(e);
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
 
 protected void fillFromElement(Element e) throws ParserConfigurationException, SAXException, IOException
 {
    name = e.getAttribute("name");
 	//System.out.println(name);
  	String f = e.getAttribute("filename");
 	//System.out.println(f); 	
  	if (f!="")
  	{
  		fillFromFile(f, name);
  		return;
  	}
 	NodeList nl = e.getChildNodes();
  	wreck = null;
 	String s = "";
 	String value = "";
    for (int i=0; i<nl.getLength(); i++)
    {
    	Node n_child = nl.item(i);
    	if (n_child instanceof Element)
    	{
    		Element e_child = (Element)n_child;
    		Text tnode = (Text)e_child.getFirstChild();
    		if (tnode!=null) {value = tnode.getData().trim();}
    		if (e_child.getTagName().equals("type")) 
    			type = value;
    		else if (e_child.getTagName().equals("maxdamage")) 
    			max_damage = Integer.parseInt(value);
    		else if (e_child.getTagName().equals("symbol")) 
    			symbol = value.charAt(0);
    		else if (e_child.getTagName().equals("timemult")) 
    			time_m = Integer.parseInt(value);
    		else if (e_child.getTagName().equals("swim")) 
    			swim = Boolean.valueOf(value).booleanValue();
    		else if (e_child.getTagName().equals("height")) 
    			height = Integer.parseInt(value);
    		else if (e_child.getTagName().equals("color")) 
    			s = value;
    		else if (e_child.getTagName().equals("wreck")) 
    			wreck = new Item(e_child);
    			
    	}
    }
    if (s!="")
    {
    	if (s.equalsIgnoreCase("black")) default_color=PtrlConstants.BLACK;
    	else if (s.equalsIgnoreCase("dblue")) default_color=PtrlConstants.DBLUE;
    	else if (s.equalsIgnoreCase("dcyan")) default_color=PtrlConstants.DCYAN;
    	else if (s.equalsIgnoreCase("dgreen")) default_color=PtrlConstants.DGREEN;
    	else if (s.equalsIgnoreCase("dmagenta")) default_color=PtrlConstants.DMAGENTA;
    	else if (s.equalsIgnoreCase("dred")) default_color=PtrlConstants.DRED;
    	else if (s.equalsIgnoreCase("dyellow")||s.equalsIgnoreCase("brown")) default_color=PtrlConstants.DYELLOW;
    	else if (s.equalsIgnoreCase("lgray")) default_color=PtrlConstants.LGRAY;

    	else if (s.equalsIgnoreCase("dgray")) default_color=PtrlConstants.DGRAY;
    	else if (s.equalsIgnoreCase("lblue")) default_color=PtrlConstants.LBLUE;
    	else if (s.equalsIgnoreCase("lcyan")) default_color=PtrlConstants.LCYAN;
    	else if (s.equalsIgnoreCase("lgreen")) default_color=PtrlConstants.LGREEN;
    	else if (s.equalsIgnoreCase("lmagenta")) default_color=PtrlConstants.LMAGENTA;
    	else if (s.equalsIgnoreCase("lred")) default_color=PtrlConstants.LRED;
    	else if (s.equalsIgnoreCase("lyellow")) default_color=PtrlConstants.LYELLOW;
    	else if (s.equalsIgnoreCase("white")) default_color=PtrlConstants.WHITE;
    }
 } 
 
 public Tile copy()
 {
 	Item wreck_copy;
 	if (wreck!=null){wreck_copy=wreck.copyAll();}
 	else {wreck_copy = null;}
 	return new Tile(name, 
					type, 
					max_damage, 
					symbol, 
					default_color,
					time_m, 
					swim, 
					height,
					wreck_copy);
 }
 
 /**
 Pours smth. on this tile
 @param substance  0-none (wash tile); 1-blood (red?); 2-slime (green?); 3-water (blue?); 4-toxic waste (yellow?)
 */
 public void pour(int substance)
 {
  switch (substance)
  {
   case 1:
    current_color = blood_color;
    break;
   case 2:
    current_color = slime_color;
    break;
   case 3:
    current_color = water_color;
    break;
   case 4:
    current_color = waste_color;
    break;
   default:
    current_color = default_color;
    break;
  }
 }

 /**
  *Gets current color of the tile (including blood, etc.).
  *@returns current color.
  */
 public Short getCurrentColor()
 {
	return current_color;
 }

 /**
  *Gets current damage level of the tile.
  *@returns current damage.
  */
 public int getCurrentDamage()
 {
 	return current_damage;
 }
 
  /**
  *Gets symbol, that represents the tile.
  *@returns symbol.
  */
 public char getSymbol()
 {
 	return symbol;
 }

 public int getTimeMultiplier()
 {
 	return time_m;
 }
 
 /*
  * Destroys tile
  * @returns an item = wreck,
  */
 public Item destroyTile()
 {
	current_damage=max_damage; 
 	symbol = '^';
 	if (time_m==0||time_m>30)
 	{
 		time_m = 10 + (int)(Math.random()*20);
 	} 
 	height=(int)Math.round(Math.random()*5);
 	name="destroyed " + name;
 	Item wr;
 	//System.out.println(wreck);
 	if (wreck!=null)
 	{
	 	wr = wreck.copyAll();
	 	wreck = null;
	 	wr.setQty((int)Math.round(Math.random()*(wr.getQty()+0.5)));
	 	if (wr.getQty()==0) {wr=null;}
 	}
 	else {wr = null;}
 	return wr;
 }
 
 public void getAttack(Attack a)
 {
	current_damage+=a.getDamage();
 }
 
 public String getType()
 {
 	return type;
 }
 
 public String getName()
 {
 	return name;
 }
 
 /**
  * @returns time multiplier, used for pathfinding
  */
 public int getPathTM()
 {
 	return this.getTimeMultiplier();
 }
 
 public int getHeight()
 {
 	return height;
 }

 public int getMaxDamage()
 {
 	return max_damage;
 }

 
 /**
   Current color of the tile
 */
 protected short current_color; 			

 /**
 *Current damage level of the tile
 *0 - undamaged
 */
 protected int current_damage;

 /**
 *Maximum damage tile can take before being destroyed.
 *0 - Tile is undestroyable
 */
 protected int max_damage;

 //consts
 /**
  Type of tile, eg "wall" or "floor" or liquid.
 */
 protected String type;  

 /** 
  Name of tile, eg "stone wall" or "metal floor".
 */ 		
 protected String name;
 /**
  Symbol, used to show tile on the screen, eg. "#" for wall.
 */  			
 protected char symbol;
 /**
  Default color of tile.
 */   		
 protected short default_color;
 /**
  Color of tile if stained with blood.
 */ 	
 protected short blood_color = PtrlConstants.LRED; 	
 /**
  Color of tile if stained with slime.
 */ 			
 protected short slime_color = PtrlConstants.LGREEN; 	
 /**
  Color of tile if wet.
 */ 			
 protected short water_color = PtrlConstants.DBLUE; 
 /**
  Color of tile if stained with toxic waste.
 */			
 protected short waste_color = PtrlConstants.LYELLOW;	
 /**
 Multiplies time, required to pass this tile, 0 if unpassable (eg. wall), 15 means TIMEx1,5 and so on
 */ 			
 protected int time_m;      		
 /**
  True if tile is liquid (water, acid, lava, etc.), and player have to swim over it.
 */
 protected boolean swim;
 protected int height;     	
 protected Item wreck;

}









