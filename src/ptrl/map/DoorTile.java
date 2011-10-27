package ptrl.map;

import java.awt.Color;
import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;

import ptrl.combat.PtrlConstants;
import ptrl.items.*;


public class DoorTile extends Tile implements Serializable
{
	//TODO: implement keys
	/**
	 * Method DoorTile
	 *
	 *
	 */

	public DoorTile(String filename, String tilename) throws ParserConfigurationException, SAXException, IOException
 	{
 		this();
 		fillFromFile(filename, tilename);
	}
 	
 	public DoorTile(String tilename, 
 				  String tiletype, 
 				  int mdam, 
 				  char sym, 
 				  char open_sym,
 				  short c, 
 				  int t,
 				  int open_t, 
 				  int h,
 				  Item i)
 	{
		super(tilename, tiletype, mdam, sym, c, t, false, h, i);
		open_time_m=open_t;
		opensymbol=open_sym;
		swim=false;
		open_close_time=3000;
		open();
	}

	public DoorTile() 
	{
 		this("", "door", 0, '+', '/', PtrlConstants.WHITE, 0, 10, 10, null);
 	}

	 public DoorTile(Element e) throws ParserConfigurationException, SAXException, IOException
	 {
	 	super(e);
	 } 
	
 public Tile copy()
 {
 	Item wreck_copy;
 	if (wreck!=null){wreck_copy=wreck.copyAll();}
 	else {wreck_copy = null;}
 	DoorTile dt = new DoorTile(name, 
						type, 
						max_damage, 
						symbol,
						opensymbol, 
						default_color,
						time_m,
						open_time_m, 
						height,
						wreck_copy);
 	if (!opened) dt.close();
 	return dt;
 }



	public char getSymbol()
	{
		if (opened)	{return opensymbol;}
		else {return symbol;}
	}
	
	public int getTimeMultiplier()
 	{
 		if (opened) {return open_time_m;}
	 	else {return time_m;}
 	}

	public int getHeight()
 	{
 		if (opened) {return open_height;}
	 	else {return height;}
 	}

 	
 	/**
  	* @returns time multiplier, used for pathfinding (open door time_m)
  	*/
 	public int getPathTM()
 	{

	 	if (isOpen()) return open_time_m;
	 	else return open_time_m+10;
 	} 	
	
	/**
	 * Opens door
	 */
	public void open()
	{
		opened = true;
	}

	public Item destroyTile()
	{
		Item i = super.destroyTile();
		open();
		opensymbol='.';
		time_m=10;
		return i;
	}
	
	/**
	 * Closes door
	 */
	public void close()
	{
		if (current_damage<max_damage) opened = false;
	}

	/**
	 * Closes door if it is open and vice-versa.
	 */
	public void changeState()
	{
		opened = !opened;
	}
	
	/** 
	 * Return true if the door is open and vice-versa.
	 */
	public boolean isOpen()
	{
		return opened;
	}

	protected void fillFromElement(Element e) throws ParserConfigurationException, SAXException, IOException
 	{
    	name = e.getAttribute("name");
 		//System.out.println(name);
  		String f = e.getAttribute("filename");
 		//System.out.println(f);
 		String opn = e.getAttribute("open");
 		opened = false;
 		if (opn!="") {opened = Boolean.valueOf(opn).booleanValue();}
 		
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
    			else if (e_child.getTagName().equals("opensymbol")) 
    				opensymbol = value.charAt(0);
    			else if (e_child.getTagName().equals("timemult")) 
    				time_m = Integer.parseInt(value);
    			else if (e_child.getTagName().equals("opentimemult")) 
    				open_time_m = Integer.parseInt(value);
    			else if (e_child.getTagName().equals("swim")) 
    				swim = Boolean.valueOf(value).booleanValue();
    			else if (e_child.getTagName().equals("height")) 
    				height = Integer.parseInt(value);
    			else if (e_child.getTagName().equals("openheight")) 
    				open_height = Integer.parseInt(value);
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
	    	
    		else if (s.equalsIgnoreCase("dgray")) default_color=PtrlConstants.LGRAY;
    		else if (s.equalsIgnoreCase("lblue")) default_color=PtrlConstants.LBLUE;
    		else if (s.equalsIgnoreCase("lcyan")) default_color=PtrlConstants.LCYAN;
	    	else if (s.equalsIgnoreCase("lgreen")) default_color=PtrlConstants.LGREEN;
	    	else if (s.equalsIgnoreCase("lmagenta")) default_color=PtrlConstants.LMAGENTA;
    		else if (s.equalsIgnoreCase("lred")) default_color=PtrlConstants.LRED;
    		else if (s.equalsIgnoreCase("lyellow")) default_color=PtrlConstants.LYELLOW;
    		else if (s.equalsIgnoreCase("white")) default_color=PtrlConstants.WHITE;
    	}
    	//Toolkit.readCharacter();
 	} 	
	
	protected void fillFromFile(String filename, String tilename) throws ParserConfigurationException, SAXException, IOException
 	{
	 	File f = new File("."+File.separator+filename);
		//System.out.println("name:" + f);
		//Toolkit.readCharacter();
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
    				if ((ename.equalsIgnoreCase(tilename))&&e.getTagName().equals("door")) fillFromElement(e);
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
	
	/**
	 * 
	 * @return time needed to change door state.
	 */
	
	public int getOpenCloseTime()
	{
		return open_close_time;
	}
	
	private int open_close_time;
	private int open_time_m;
	private int open_height;
	private boolean opened;
	private char opensymbol;
		
}
