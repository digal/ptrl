package ptrl.util;

import ptrl.creatures.Creature;
import ptrl.map.Actor;

public class MapEvent
{
	public MapEvent (String n, int tp, int val, int ex, int ey, Creature source)
	{
		name=n;
		type=tp;
		value=val;
		x=ex;
		y=ey;
		this.source=source;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getType()
	{
		return type;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public String toString()
	{
		return "[MapEvent: type="+type+", name="+name+", x="+x+", y="+y+", value="+value+" ]";
	}
	
	public Creature getSource()
	{
		return source;
	}
	
	private int type;
	
	public static final int T_UNDER_ATTACK=0;
	public static final int T_SOUND=1;
	public static final int T_MENTAL=2;
	
	private int x;
	private int y;
	private int value;
	private String name;
	private Creature source;


}
