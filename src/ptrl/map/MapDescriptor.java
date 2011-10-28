package ptrl.map;

import java.io.*;

import ptrl.Game;
import ptrl.combat.PtrlConstants;

public class MapDescriptor implements Serializable
{
	private String surface;
	private String generator;
	private String tileSetName;
	private String primaryFraction;
	private float damage;
	private short bgColor;
	private short fgColor;
	private char symbol;
	private boolean knownByPlayer;
	private int x;
	private int y;
	private int z;
	//TODO: implement terrain properties 
	
	public MapDescriptor(int x, int y, int z)
	{
		this.x=x;
		this.y=y;
		this.z=z;
		setSymbol('x');
		setBgColor(PtrlConstants.BLACK);
		setFgColor(PtrlConstants.WHITE);
		knownByPlayer=false;
		surface="";
		generator="";
		tileSetName="default";
	}
	
	public void SaveMap() {
		
	}
	
	public Map getMap()
	{
    Map map;
		File f = new File("."+File.separator+Game.getInstance().getPlayer().getName()+File.separator+"m"+x+"_"+y+"_"+z);
		if (f.exists()) {
			try
			{
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
				map = (Map)in.readObject();

			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
        throw(new IllegalStateException("There's no life after death"));
			}
		} else {
      map = MapGenerator.generate(this);
    }
    return map;
	}

	public short getBgColor()
	{
		return bgColor;
	}

	public float getDamage()
	{
		return damage;
	}

	public short getFgColor()
	{
		return fgColor;
	}

	public String getGenerator()
	{
		return generator;
	}

	public String getPrimaryFraction()
	{
		return primaryFraction;
	}

	public String getSurface()
	{
		return surface;
	}

	public char getSymbol()
	{
		return symbol;
	}

	public String getTileSetName()
	{
		return tileSetName;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getZ()
	{
		return z;
	}

	public void setBgColor(short bgColor)
	{
		this.bgColor = bgColor;
	}

	public void setDamage(float damage)
	{
		this.damage = damage;
	}

	public void setFgColor(short fgColor)
	{
		this.fgColor = fgColor;
	}

	public void setGenerator(String generator)
	{
		this.generator = generator;
	}

	public void setPrimaryFraction(String primaryFraction)
	{
		this.primaryFraction = primaryFraction;
	}

	public void setSurface(String surface)
	{
		this.surface = surface;
	}

	public void setSymbol(char symbol)
	{
		this.symbol = symbol;
	}

	public void setTileSetName(String tileset)
	{
		this.tileSetName = tileset;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public void setZ(int z)
	{
		this.z = z;
	}
	
	public boolean isKnownByPlayer()
	{
		return knownByPlayer;
	}
	
	public void setKnownByPlayer(boolean known)
	{
		this.knownByPlayer=known;
	}
	
	public String toString()
	{
		String s=surface.toString();
		if (generator!=null
				&&!generator.equals(""))
			s+=", "+generator;
		return s;
	}
}
