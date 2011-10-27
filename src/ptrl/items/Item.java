package ptrl.items;

import ptrl.combat.PtrlConstants;
import ptrl.creatures.AttribModifier;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *Abstract item
 */
public class Item implements Serializable
{
 /**
 *Creates an item
 *
 *@param w - weight of item (kg's).
 *@param n - name of item.
 *@param p - approximate price of item (PNV's).
 *@param l - level of item
 *@param s - symbol used to show item on map
 *@param c - color of symbol
 */
 public Item(float w, String n, String mn,float p, float l, char s, short c)
 {
    this();
 	weight=w;
 	name=n;
 	price=p;
 	level=l;
 	symbol=s;
 	color=c;
 	many_name=mn;
 }
 /**
 *Creates item(s)
 *
 *@param w - weight of item (kg's).
 *@param n - name of item.
 *@param p - approximate price of item (PNV's).
 *@param l - level of item
 *@param s - symbol used to show item on map
 *@param c - color of symbol
 *@param q - quanity 
 */
 public Item(float w, String n, String mn, float p, float l, char s, short c, int q)
 {
	this(w, n, mn, p, l, s, c);
	qty=q;
 }
 public Item()
 {
	attrMods=new ArrayList<AttribModifier>();
 	weight=1;
 	name="Null item";
 	price=0;
 	level=0;
 	symbol='X';
 	color=PtrlConstants.WHITE;
 	many_name="Heap of null items";
 	x=-1;
 	y=-1;
 	qty=1;
 }
 /**
 *Creates an item from xml element
 *
 *@param n XML Element. Attribute "name" must be set like this
 *&lt;item name="stone"&gt;
 *	...
 *&lt/item&gt;
 */
 public Item(Element e) throws ParserConfigurationException, SAXException, IOException
 {
 	this();
 	fillFromElement(e);
 } 
 
 public Item(String filename, String itemname) throws ParserConfigurationException, SAXException, IOException
 {
 	this();
 	fillFromFile(filename, itemname);
 }

 public void fillFromElement(Element e) throws ParserConfigurationException, SAXException, IOException
 {
 	name = e.getAttribute("name");
 	String f = e.getAttribute("file");
 	String q = e.getAttribute("qty");
 	if (q!="") {qty = Integer.parseInt(q);}
	if (qty==0) {qty=1;}
 	if (f!="")
 	{
 		fillFromFile(f, name);
	 	return;
 	}
 	NodeList nl = e.getChildNodes();
    for (int i=0; i<nl.getLength(); i++)
    {
    	Node n_child = nl.item(i);
    	if (n_child instanceof Element)
	    		parseElement((Element)n_child);
    }
 }
  
 public static int strToType(String t)
 {
   	for (int i=0; i<TYPE_NAMES.length; i++)
   		if (t.equalsIgnoreCase(TYPE_NAMES[i]))
   			return i;
   	return IS_MATERIAL;
 }
 
 protected boolean parseElement(Element e)
 {
		String value="";
		Text tnode = (Text)e.getFirstChild();
		if (tnode!=null) 
			value = tnode.getData().trim();
		else 
			return true;
		if (e.getTagName().equals("weight")) weight = Float.parseFloat(value);
		else if (e.getTagName().equals("price")) price = Float.parseFloat(value);
		else if (e.getTagName().equals("manyname")) many_name = value;
		else if (e.getTagName().equals("level")) level = Float.parseFloat(value);
		else if (e.getTagName().equals("symbol")) symbol = value.charAt(0);
		else if (e.getTagName().equals("color")) this.color=PtrlConstants.strToColor(value);
		else if (e.getTagName().equals("type")) inventory_section = strToType(value);
		else if (e.getTagName().equals("attr_mod")) 
			attrMods.add(new AttribModifier(e));
		else 
			return false;
		return true;
 }
 
 protected void fillFromFile(String filename, String itemname) throws ParserConfigurationException, SAXException, IOException
 {
 	File f = new File("."+File.separator+filename);	
	//System.out.println("file name:" + f);
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
    			//System.out.println("tag:"+e.getTagName());
    			//System.out.println("class:"+this.getClass().getSimpleName());
    			if (ename.equalsIgnoreCase(itemname)&&e.getTagName().equalsIgnoreCase(this.getClass().getSimpleName())) fillFromElement(e);
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
	//System.out.println(toString());
 }


 
 public void setXY(int nx, int ny)
 {
 	x=nx;
 	y=ny;
 }

 public int getX()
 {
 	return x;
 } 

 public int getY()
 {
 	return y;
 } 

 /**
 *Checks if item is equal to otherObject.
 *
 *@param otherObject - the reference object with which to compare
 *@return true if this object is the same as the obj argument; false otherwise.
 */
 public boolean equals (Object otherObject)
 {
 	if (this == otherObject) return true;
 	if (otherObject == null) return false;
 	if (getClass() != otherObject.getClass()) return false;
 	Item other = (Item)otherObject;
 	return weight==other.weight
 		&&name==other.name
 		&&price==other.price
 		&&level==other.level
 		&&symbol==other.symbol
 		&&color==other.color;	
 }

 /**
  *Gets item weight (kg's).
  */
 public float getWeight()
 {
 	return weight*getQty();
 }

 /**
  *Gets item name.
  */ 
 public String getName()
 {
 	if (qty==1)	return name;
 	else {return many_name + "(" + qty +")";}
 }

 /**
  *Gets single item name.
  */ 
 public String getSingleName()
 {
 	return name;
 }
 
 
 /**
  *Gets item price.
  */ 
 public float getPrice()
 {
 	return price;
 }
 /**
  *Gets item level.
  */ 
 public float getLevel()
 {
 	return level;
 }
 /**
  *Gets item level.
  */ 
 public char getSymbol()
 {
 	return symbol;
 }
 /**
  *Gets item level.
  */ 
 public short getColor()
 {
	return color;
 }
 
 public String toString()
 {
 	return getClass().getName()
 		+"[name="+name
 		+";many_name="+many_name
 		+";weight="+weight
 		+";price="+price
 		+";level="+level
 		+";symbol='"+symbol
 		+"';color="+color
 		+";type="+inventory_section
 		+"]";
 }
 
 public String getInvString()
 {
	 return getName();  
 }
 
 public int getQty()
 {
 	return qty;
 }
 
 public int getType()
 {
	 return inventory_section;
 }
 
 public void setType(int type)
 {
	 inventory_section=type;
 }
 
 public void setQty(int q)
 {
 	qty = q;
 }
 
 
 public void add()
 {
 	qty++;
 }

 public void add(int i)
 {
 	qty+=i;
 }
 
 public Item copySingle()
 {
	Item cloned=new Item();
	cloned.color=color;
	cloned.weight=weight;
	cloned.name=name;
	cloned.many_name=many_name;
	cloned.price=price;
	cloned.level=level;
	cloned.symbol=symbol;
 	cloned.inventory_section=inventory_section;
 	cloned.attrMods=new ArrayList<AttribModifier>();
 	Iterator<AttribModifier>  i= attrMods.iterator();
 	while (i.hasNext())
 		cloned.attrMods.add(i.next());
 	return cloned; 											
 }
 
 public Item copyAll()
 {
 	Item cloned = copySingle();
 	cloned.qty=qty;
	return cloned; 											
 }
 
 public ArrayList<AttribModifier> getAttrMods()
 {
	 return attrMods;
 }
 
 protected float weight;
 protected float price;
 protected float level;
 protected String name;	
 protected String many_name;
 protected char symbol;	
 protected short color;
 protected int qty;	
 protected int x;
 protected int y;
 protected ArrayList<AttribModifier> attrMods;
 
 protected int inventory_section;
 
 public static final int IS_MATERIAL=0;
 public static final int IS_HEAD=1;
 public static final int IS_FACE=2;
 public static final int IS_NECK=3;
 public static final int IS_CLOAK=4;
 public static final int IS_BODYARMOR=5;
 public static final int IS_WRIST=6;
 public static final int IS_GIRDLE=7;
 public static final int IS_PANTS=8;
 public static final int IS_BOOTS=9;
 public static final int IS_MELEE=10;
 public static final int IS_SMALL_FIREARMS=11;
 public static final int IS_MEDIUM_FIREARMS=12;
 public static final int IS_BIG_FIREARMS=13;
  public static final int IS_AMMO=14;
 public static final int IS_MODS=15;
 public static final int IS_TOOLS=16;
 public static final int IS_EXPLOSIVES=17;
 public static final int IS_MEDIA=18;
 public static final int IS_DRUGS=19;
 public static final int IS_FOOD=20;
  
 
 
 public static final String[] TYPE_NAMES = new String[]
                              {"Materials",
	 						   "Head gear",
	 						   "Masks",
	 						   "Neck gear",
	 						   "Cloaks",
	 						   "Body armor",
	 						   "Wrists",
	 						   "Girdes",
	 						   "Pants",
	 						   "Boots",
	 						   "Melee weapons",
	 						   "Small firearms",
	 						   "Medium firearms",
	 						   "Big firearms",
	 						   "Ammo",
	 						   "Mods",
	 						   "Tools",
	 						   "Explosives",
	 						   "Media",
	 						   "Drugs",
	 						   "Food"};
	 
}