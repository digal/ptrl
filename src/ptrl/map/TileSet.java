package ptrl.map;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

import ptrl.items.Item;

public class TileSet
{
	
	public static void main(String[] args)
	{
		TileSet ts=new TileSet("forest");
		System.out.println("Loaded");
		ts.out();
		int trees=0;
		int bushes1=0;
		int bushes2=0;
		
		for (int i=0; i<100000; i++)
		{
			String s=ts.getPiece(TileSet.P_OBSTACLE).getName();
			if (s.equalsIgnoreCase("tree")) trees++;
			else if (s.equalsIgnoreCase("bush1")) bushes1++;
			else if (s.equalsIgnoreCase("bush2")) bushes2++;
		}
		System.out.println("Trees: "+trees);
		System.out.println("Bushes 1: "+bushes1);
		System.out.println("Bushes 2: "+bushes2);
	}
	
	/**
	 * Creates default tileset.
	 *
	 */
	@SuppressWarnings("unchecked")
	public TileSet()
	{
		set = new ArrayList[ROLE_NAMES.length];
		chances=new ArrayList[ROLE_NAMES.length];
		for (int i=0; i<set.length; i++)
		{
			set[i] = new ArrayList<Tile>();
			chances[i]=new ArrayList<Float>();
			
		}
	}
	
	 public TileSet(String filename)
	 {
		 this();
		 try
		 {
			 loadFromFile(filename);
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
			 System.exit(1);
		 }
	 }
	 
	 public void out()
	 {
		for (int i=0; i<set.length; i++)
		{
			if (set[i]==null) System.out.println(ROLE_NAMES[i]+": null.");
			else System.out.println(ROLE_NAMES[i]+": "+set[i].size()+" tile(s).");
		}
	 }
	 
	 public Tile getPiece(int n)
	 {
		 if (set[n].size()==0)
		 {
			 if (n==0)
				 return null;
			 else 
				 return getPiece(0); //return default tile 
		 }
			 
		 else if (set[n].size()==1) 
			 return getSingleSetPiece(n);
		 else 
			 return getMultiSetPiece(n);
	 }
	 
	 private Tile getSingleSetPiece(int n)
	 {
		 return set[n].get(0);
	 }
	 
	 private Tile getMultiSetPiece(int n)
	 {
		 int qty=set[n].size();
		 float chance_sum=0;
		 for (int i=0; i<qty; i++)
		 {
			 chance_sum+=chances[n].get(i);
		 }
		 double dice=Math.random()*chance_sum;
		 double s=0;
		 for (int i=0; i<qty; i++)
		 {
			 s+=chances[n].get(i);
			 if (dice<=s) return set[n].get(i);
		 }
		 System.out.println ("Return last");
		 return set[n].get(qty-1);
		 
	 }
	 
	
	 protected void loadFromFile(String filename) throws Exception
	 {
	 	File f = new File("./tilesets"+File.separator+filename+".xml");

		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);
			//System.out.println(doc);
			Element root = doc.getDocumentElement();
			if (root.getTagName()!="tileset") throw new Exception("It's not a tileset");
			String exts=root.getAttribute("inherits");
			if (!exts.equals(""))
				loadFromFile(exts);
			name=root.getAttribute("name");
			NodeList nl = root.getChildNodes();
		    for (int i=0; i<nl.getLength(); i++)
			{
	    		Node n = nl.item(i);
	    		if (n instanceof Element)
	    		{
	    			Element e = (Element)n;
	    			if (e.getTagName().equals("piece")) addPiece(e);
	    		}
	    	}
		}
		catch (SAXParseException e)
		{
			System.out.println("Tileset parsing error!");
			System.out.println("line: "+e.getLineNumber());
			System.out.println("col: "+e.getColumnNumber());
			e.printStackTrace();
			System.exit(1);
		}
	 }
	
	/**
	 * Adds a tileset piece from the XML element.
	 * 
	 * @param e Element
	 * @throws Exception
	 */
	private void addPiece(Element e) throws Exception
	{
	    String role = e.getAttribute("role");
	    String inh = e.getAttribute("inherit");
	    if (inh.equalsIgnoreCase("no"))
	    {
	    	clearPiece(role);
	    }
	 	NodeList nl = e.getChildNodes();
    	for (int i=0; i<nl.getLength(); i++) 
   		{
    		Node n_child=nl.item(i);
    		if (i>=nl.getLength()) return;
        	if (n_child instanceof Element)
        	{
           		Element e_child = (Element)n_child;
           		float chance=1.0f; 
           		String chStr = e_child.getAttribute("chance");
           		if (!chStr.equals(""))
           			chance=Float.parseFloat(chStr);
           		if (e_child.getTagName()=="tile") 
           			addTile(role, chance, new Tile(e_child));
           		else if (e_child.getTagName()=="door")
           			addTile(role, chance, new DoorTile(e_child));

        	}
   		}
	}
	 
	private void clearPiece(String role)
	{
		int rn=-1;
		for (int i=0; i<ROLE_NAMES.length; i++)
		{
			if (role.equalsIgnoreCase(ROLE_NAMES[i])) 
			{
				rn=i;
				break;
			}
		}
		set[rn]=new ArrayList<Tile>();
		chances[rn]=new ArrayList<Float>();
		
	}

	private void addTile (String role, float chance, Tile t)
	{
		int rn=-1;
		for (int i=0; i<ROLE_NAMES.length; i++)
		{
			if (role.equalsIgnoreCase(ROLE_NAMES[i])) 
			{
				rn=i;
				break;
			}
		}
		if (rn==-1) return;
		set[rn].add(t);
		chances[rn].add(chance);
	}
	
	
	private String name;
	//private Tile[] tiles;
	
	
	
	private ArrayList<Tile>[] set;
	private ArrayList<Float>[] chances;

	
	
	public static final int P_DEFAULT=0;
	public static final int P_GROUND=1;
	public static final int P_FLOOR_A=2;
	public static final int P_FLOOR_B=3;
	public static final int P_BIG_ROAD=4;
	public static final int P_SMALL_ROAD=5;
	public static final int P_OBSTACLE=6;
	public static final int P_FURNITURE=7;
	public static final int P_INNER_DOOR=8;
	public static final int P_OUTER_DOOR=9;
	public static final int P_INNER_WALL=10;
	public static final int P_OUTER_WALL=11;
	public static final int P_BARRICADE=12;


	public static String[] ROLE_NAMES = new String[]{"default_tile",
											   "ground",
											   "floor_a",
											   "floor_b",
											   "big_road",
											   "small_road",
											   "obstacle",
											   "furniture",
											   "inner_door",
											   "outer_door",
											   "inner_wall",
											   "outer_wall",
											   "barricade" };
}
