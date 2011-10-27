package ptrl.util;

import java.awt.Font;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXParseException;

//Singleton pattern used
public class GameSettings
{

	public static GameSettings getInstance()
	{
		if (gs==null) gs=new GameSettings("config.xml");
		return gs;
	}

	private GameSettings()
	{
		messages=100;
		mapwidth=100;
		mapheight=100;
		maptype="ruins";
		fullscreen=false;
		fontname="Courier New";
		fontsize=12;
		fontstyle=Font.PLAIN;
		globalMapBalance=0.5f;
		globalMapSide=80;
	}
	
	private GameSettings(String filename)
	{
		this();
		File f = new File("."+File.separator+filename);
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);
			//System.out.println(doc);
			Element root = doc.getDocumentElement();
			NodeList nl = root.getChildNodes();
			String value="";
		    for (int i=0; i<nl.getLength(); i++)
	    	{
	    		Node n = nl.item(i);
	    		if (n instanceof Element)
	    		{
	    			Element el=(Element)n;
	    			Text tnode = (Text)el.getFirstChild();
	    			if (tnode!=null) value = tnode.getData().trim();
		    		if (el.getTagName().equals("fullscreen"))
		    		{
		    			if (value.equalsIgnoreCase("yes")) fullscreen=true;
		    			else fullscreen=false;
		    		}
		    		else if (el.getTagName().equals("messages"))
		    		{
		    			messages=Integer.parseInt(value);
		    		}
		    		else if (el.getTagName().equals("mapwidth"))
		    		{
		    			mapwidth=Integer.parseInt(value);
		    		}
		    		else if (el.getTagName().equals("mapheight"))
		    		{
		    			mapheight=Integer.parseInt(value);
		    		}
		    		else if (el.getTagName().equals("maptype"))
		    		{
		    			maptype=value;
		    		}
		    		else if (el.getTagName().equals("font"))
		    		{
		    			fontname=value;
		    		}
		    		else if (el.getTagName().equals("fontsize"))
		    		{
		    			fontsize=Integer.parseInt(value);
		    		}
		    		else if (el.getTagName().equals("delay"))
		    		{
		    			delay=Integer.parseInt(value);
		    		}
		    		else if (el.getTagName().equals("fontstyle"))
		    		{
		    			if (value.equalsIgnoreCase("plain"))
		    				fontstyle=Font.PLAIN;
		    			else if (value.equalsIgnoreCase("bold"))
		    				fontstyle=Font.BOLD;
		    		}
		    		else if (el.getTagName().equals("gm_balance"))
		    		{
		    			globalMapBalance=Float.parseFloat(value);
		    		}
		    		else if (el.getTagName().equals("gm_side"))
		    		{
		    			globalMapSide=Integer.parseInt(value);
		    		}

		    			
	    		}
	    	}
		}
		catch (SAXParseException ex)
		{
			System.out.println("oops!");
			System.out.println("line: "+ex.getLineNumber());
			System.out.println("col: "+ex.getColumnNumber());
			ex.printStackTrace();
			System.exit(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
			
		}
	}
	
	public String getFontName()
	{
		return fontname;
	}

	public int getFontSize()
	{
		return fontsize;
	}

	public boolean isFullScreen()
	{
		return fullscreen;
	}

	public int getMapHeight()
	{
		return mapheight;
	}

	public String getMapType()
	{
		return maptype;
	}

	public int getMapWidth()
	{
		return mapwidth;
	}

	public int getMessages()
	{
		return messages;
	}	
	
	public int getDelay()
	{
		return delay;
	}

	public void setDelay(int delay)
	{
		this.delay = delay;
	}
	
	public int getFontStyle()
	{
		return fontstyle;
	}
	
	public float getGlobalMapBalance() 
	{
		return globalMapBalance;
	}

	public int getGlobalMapSide() 
	{
		return globalMapSide;
	}
	
	private int messages;
	private int mapwidth;
	private int mapheight;
	private String maptype;
	private boolean fullscreen;
	private String fontname;
	private int fontsize;
	private int delay;
	private int fontstyle;
	private float globalMapBalance;
	private int globalMapSide;
	private static GameSettings gs;


}
