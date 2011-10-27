package ptrl.creatures;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import ptrl.combat.*;
import ptrl.util.MapEvent;
/**
 *Describes an abstract creature
 */
public class Creature implements Serializable
{
	public Creature(String n, String t) 
	{
		name = n;
		type = t;
		attribs=new Attribute[8];
		stats=new Attribute[5];
		for (int i=0; i<attribs.length; i++)
		{
			attribs[i]=new Attribute(5);
		}
		for (int i=0; i<stats.length; i++)
		{
			stats[i]=new Attribute(1);
		}
		setBaseSpeed(1);
		defence=new Attribute(1);
		armor=new Attribute(1);
		close_attacks=new UnarmedAttackType[0];
		ranged_attacks=new RangedAttackType[0];
		skills = new Skill[29];
		skills[0] = new Skill (0, STR);
		skills[1] = new Skill (0, DEX);		
		skills[2] = new Skill (0, PER);
		skills[3] = new Skill (0, DEX);
		skills[4] = new Skill (0, PER);
		skills[5] = new Skill (0, DEX);
		skills[6] = new Skill (0, STR);				
		skills[7] = new Skill (0, PER);								
		skills[8] = new Skill (0, PER);								
		skills[9] = new Skill (0, INT);
		skills[10] = new Skill (0, INT);
		skills[11] = new Skill (0, INT);						
		skills[12] = new Skill (0, INT);								
		skills[13] = new Skill (0, INT);								
		skills[14] = new Skill (0, INT);								
		skills[15] = new Skill (0, CON);								
		skills[16] = new Skill (0, DEX);								
		skills[17] = new Skill (0, DEX);								
		skills[18] = new Skill (0, INT);								
		skills[19] = new Skill (0, DEX);								
		skills[20] = new Skill (0, CHA);								
		skills[21] = new Skill (0, CHA);								
		skills[22] = new Skill (0, CHA);								
		skills[23] = new Skill (0, CHA);								
		skills[24] = new Skill (0, STR);								
		skills[25] = new Skill (0, DEX);								
		skills[26] = new Skill (0, DEX);								
		skills[27] = new Skill (0, EXS);								
		skills[28] = new Skill (0, EXS);
		protection = new Protection(new int[9], (float)0);
	}	
	
	 public Creature(String filename, String t, int l, String n)
	 {
	 		this(n, "");
	 		fillFromFile(filename, t, l);
	 		countAll();
			hp=stats[MHP].getBasicValue();
	 }



	public void fillFromFile(String filename, String t, int l)
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
	    			int elev = Integer.parseInt(e.getAttribute("level"));
	    			if (ename.equalsIgnoreCase(t)&&e.getTagName().equals("creature")&&elev==l) fillFromElement(e);
	    		}
	  		}
		}
		catch (SAXParseException ex)
		{
			System.out.println("oops!");
			System.out.println("line: "+ex.getLineNumber());
			System.out.println("col: "+ex.getColumnNumber());
			ex.printStackTrace();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	 }
	
	 public void fillFromElement(Element e) throws ParserConfigurationException, SAXException, IOException
	 {
	    type = e.getAttribute("name");
	    int l=Integer.parseInt(e.getAttribute("level"));
	 	//System.out.println(name);
	  	String f = e.getAttribute("file");
	 	//System.out.println(f); 	
	  	if (f!="")
	  	{
	  		fillFromFile(f, type, l);
	  		return;
	  	}
	 	NodeList nl = e.getChildNodes();
	 	String s = "";
	 	String value = "";
	    for (int i=0; i<nl.getLength(); i++)
	    {
	    	Node n_child = nl.item(i);
	    	if (n_child instanceof Element)
	    		parseElement((Element)n_child);
	    }
	}	 
	 
	protected boolean parseElement(Element e)
	{
		String value="";
		Text tnode = (Text)e.getFirstChild();
		if (tnode!=null) 
			value = tnode.getData().trim();
		else 
			return true;
		if (e.getTagName().equals("symbol")) 
			symbol = value.charAt(0);
		else if (e.getTagName().equals("color")) 
			setColor(PtrlConstants.strToColor(value));
		else if (e.getTagName().equals("con")) 
			getAttribute(CON).setBasicValue(Integer.parseInt(value));
		else if (e.getTagName().equals("str")) 
			getAttribute(STR).setBasicValue(Integer.parseInt(value));
		else if (e.getTagName().equals("dex")) 
			getAttribute(DEX).setBasicValue(Integer.parseInt(value));
		else if (e.getTagName().equals("per")) 
			getAttribute(PER).setBasicValue(Integer.parseInt(value));
		else if (e.getTagName().equals("int")) 
			getAttribute(INT).setBasicValue(Integer.parseInt(value));
		else if (e.getTagName().equals("cha")) 
			getAttribute(CHA).setBasicValue(Integer.parseInt(value));
		else if (e.getTagName().equals("exs")) 
			getAttribute(EXS).setBasicValue(Integer.parseInt(value));
		else if (e.getTagName().equals("luc")) 
			getAttribute(LUC).setBasicValue(Integer.parseInt(value));
		else if (e.getTagName().equals("luc")) 
			getAttribute(LUC).setBasicValue(Integer.parseInt(value));
		else if (e.getTagName().equals("close_attack_type")) 
			addCloseAttackType(new UnarmedAttackType(e, this));
		else if (e.getTagName().equals("ranged_attack_type")) 
			addRangedAttackType(new RangedAttackType(e, this));
		else if (e.getTagName().equals("skill"))
		{
		  	String sk = e.getAttribute("name");
	 		for (int j=0; j<Creature.SKILL_NAMES.length; j++)
	 		{
	 			if (sk.equalsIgnoreCase(Creature.SKILL_NAMES[j])) 
	 			{
	 				skills[j].setValue(Integer.parseInt(value));
	 			}
	 		}
		}else 
			return false;
		return true;
	}
	 
	public void countAll()
	{
		setBaseSpeed((float)getAttributeValue(DEX)/5+((float)getAttributeValue(DEX)-(float)getAttributeValue(CON))/10);
		stats[VIS].setBasicValue(getAttributeValue(PER)*5);
		stats[MHP].setBasicValue(getAttributeValue(CON)*2);
		int[] a=new int[11];
		a[PtrlConstants.DT_NORMAL]=0;
		a[PtrlConstants.DT_AP]=0;
		a[PtrlConstants.DT_BLUNT]=0;
		a[PtrlConstants.DT_EXPLOSION]=0;
		a[PtrlConstants.DT_THERMAL]=0;
		a[PtrlConstants.DT_LASER]=0;
		a[PtrlConstants.DT_PLASMA]=0;
		a[PtrlConstants.DT_ELECTRIC]=0;
		a[PtrlConstants.DT_GAMMA]=(int)Math.floor(getAttributeValue(CON)/4);
		a[PtrlConstants.DT_TOXIC]=0;
		float c=(float)getAttributeValue(DEX)/5;
		float b=(float)skills[SKL_DODGE].getValue()/20;
		//System.out.println("c:"+c);
		protection=new Protection(a, b+c);
	}

	public Protection getProtection()
	{
		return protection;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}		
	
	public int getOldX()
	{
		return oldx;
	}

	public int getOldY()
	{
		return oldy;
	}

	
	public String getName()
	{
		return name;
	}
	
	public String getType()
	{
		return type;
	}
/*	public float getEnergy()
	{
		return energy;
	}
	
	public float getMaxEnergy()
	{
		return max_energy;
	}
*/	
	public float getSpeed()
	{
		double val = stats[SPD].getValue()*0.01;
		return (float)val;
	}
	
	public void setBaseSpeed(float newSpeed)
	{
		stats[SPD].setBasicValue((int)(newSpeed*100));
	}
	
	public void setXY(int nx, int ny)
	{
		x = nx;
		y = ny;
		oldx=x;
		oldy=y;
	}
	/**
	 * Step 1 sq - use for player movement
	 * @param xy - direction 
	 * @param time_m - tile time modifier
	 * @returns - time of movement
	 */
	
	public long move1sq(int[] xy, int time_m)  //t in ms
	{
		long r=0;
		if (time_m!=0)//time_m=0 ==> no moving
		{
			oldx=x;
			oldy=y;
 			x+=xy[X];
			y+=xy[Y];
			r=get1sqTime(xy, time_m);
		}
		return r;
	}
	
	public long get1sqTime(int[] xy, int time_m)  
	{
		float r=0;
		if (time_m!=0)//time_m=0 ==> no moving
		{
			if (xy[X]*xy[Y]==0) 
				r=time_m/(getSpeed());
			else
			{
				float sp=getSpeed()*SIN45;
				r=time_m/sp;
				
			}
				
		}
		return Math.round(r*100);
	}

	public Attribute getAttribute(int i)
	{
		return attribs[i];
	}
	
	public Attribute getStat(int i)
	{
		return stats[i];
	}
	
	public int getAttributeValue (int i)
	{
		return getAttribute(i).getValue();
	}
	
	public char getSymbol()
	{
		return symbol;
	}
	
	public short getColor()
	{
		return color;
	}
	
	public void setSymbol(char c)
	{
		symbol=c;
	}

	public void setColor(short c)
	{
		color=c;
	}
	
	public void setName(String s)
	{
		name=s;
	}
	
	public void setSex(int s)
	{
		sex=s;
	}
	
	public Skill[] getSkills()
	{
		return skills;
	}
	
	public String toString()
	{
		String s="["+getTheName(true)+"; hp: "+hp+"/"+max_hp.getValue()+"; spd: "+getSpeed()+"; dp: "+protection.getDefence()+"]";
		return s;
	}
	
	public String getTheName(boolean capitalise)
	{
		if (name==""&&capitalise) return "The "+type;
		else if (name==""&&!capitalise) return "the "+type;
		else return name+" the "+type;
	}

	public void addCloseAttackType(UnarmedAttackType at)
	{
		at.setAttacker(this);
		UnarmedAttackType[] newcat = new UnarmedAttackType[close_attacks.length+1];
		for (int i=0; i<close_attacks.length; i++)
		{
			newcat[i]=close_attacks[i];
		}
		newcat[close_attacks.length]=at;
		close_attacks=newcat;
	}
	
	public void addRangedAttackType(RangedAttackType at)
	{
		if (at.getMinSkill()>getSkills()[at.getSkillN()].getValue()) return;
		at.setAttacker(this);
		RangedAttackType[] newcat = new RangedAttackType[ranged_attacks.length+1];
		for (int i=0; i<ranged_attacks.length; i++)
		{
			newcat[i]=ranged_attacks[i];
		}
		newcat[ranged_attacks.length]=at;
		ranged_attacks=newcat;
	}
	
	public UnarmedAttackType[] getCloseAttackTypes()
	{
		return close_attacks;
	}

	public RangedAttackType[] getRangedAttackTypes()
	{
		return ranged_attacks;
	}
	
	public int getHP()
	{
		return hp;
	}
	
	public Attribute[] getAttributes()
	{
		return attribs;
	}
	
	public Attribute[] getStats()
	{
		return stats;
	}
	
	public void damage(int d)
	{
	    //System.out.println("old hp: "+hp+", damage: "+d);
		hp-=d;
	    //System.out.println("new hp: "+hp);

	}
	
	public int getFOVRange()
	{
		return getStat(VIS).getValue();
	}
	
	public int getCurrentCloseAttackN()
	{
		return close_at_number;
	}
	
	public void setCurrentCloseAttackN(int n)
	{
		close_at_number=n;
	}
	
	public UnarmedAttackType getCurrentCloseAttackType()
	{
		return getCloseAttackTypes()[close_at_number];
	}

	public int getCurrentRangedAttackN()
	{
		return ranged_at_number;
	}
	
	public void setCurrentRangedAttackN(int n)
	{
		ranged_at_number=n;
	}
	

	public RangedAttackType getCurrentRangedAttackType()
	{
		if (ranged_attacks==null) return null;
		if (ranged_attacks.length==0) return null;
		if (ranged_at_number>=ranged_attacks.length) ranged_at_number=ranged_attacks.length-1;
		return getRangedAttackTypes()[ranged_at_number];
	}

	
	public void turn()
	{
		events=null;
	}
	
	public void takeEvent (MapEvent me)
	{
		if (events==null) events=new MapEvent[]{me};
		else 
		{
			MapEvent[] new_events=new MapEvent[events.length+1];
			for (int i=0; i<events.length; i++)
			{
				new_events[i]=events[i];
			}
			new_events[events.length]=me;
			events=new_events;
		}
	}
	
	public MapEvent[] getEvents()
	{
		return events;
	}
	
	public int getSex()
	{
		return sex;
	}
	
	protected String name;
	protected String type;
	protected char symbol;
	protected short color;
//	private float energy;
//	private float max_energy;
//	protected float speed; //1.0 is normal; <1 - slow; >1 - faster

	protected Attribute[] attribs;
	protected Attribute[] stats;
	protected Skill[] skills;
	
	protected int hp;
	protected Attribute max_hp;
	
	protected Attribute defence;
	protected Attribute armor;
	
	protected int x;
	protected int y;
	protected int oldx;
	protected int oldy;

	protected final static int X=0;
	protected final static int Y=1;
	
	protected Protection protection;
	protected UnarmedAttackType[] close_attacks;
	protected RangedAttackType[] ranged_attacks;
	private int close_at_number;
	private int ranged_at_number;

	
	private static long id_counter=0;
	
	private static float SIN45=(float)0.7071;
	
	protected MapEvent[] events;
	
	protected int sex;
	
	//creature attribs:
	public static final int STR=0; //strenght carrying+melee+hth
	public static final int CON=1; //constitution - hp+some kinds of protection
	public static final int DEX=2; //dexterity - speed 
	public static final int PER=3; //perception
	public static final int INT=4; //intellect - tech, medic skills and so on. must affect AI for non-pc
	public static final int CHA=5; //charisma	
	public static final int EXS=6; //extrasensorix
	public static final int LUC=7; //luck

	//STATS
	public static final int VIS=0; //vision  
	public static final int HEA=1; //hearing
	public static final int MNL=2; //mental
	public static final int MHP=3; //max hp
	public static final int SPD=4;
	
	//----------SKILLS-----------
	public static final int SKL_UNARMED=0; 
	public static final int SKL_MELEE=1;
	public static final int SKL_PISTOLS=2; 
	public static final int SKL_SMG=3; 
	public static final int SKL_RIFLES=4; 
	public static final int SKL_ASSAULT=5; 
	public static final int SKL_HEAVY=6; 
	public static final int SKL_ENERGY=7; 
	public static final int SKL_THROWING=8; 
	public static final int SKL_TRAPS=9; 
	public static final int SKL_MEDICINE=10; 
	public static final int SKL_TECH=11; 
	public static final int SKL_ELECTRONICS=12; 
	public static final int SKL_COMPUTERS=13; 
	public static final int SKL_COOKING=14; 
	public static final int SKL_SURVIVE=15;
	public static final int SKL_HIDE=16;
	public static final int SKL_DODGE=17;
	public static final int SKL_LOCKPICK=18;
	public static final int SKL_STEAL=19;
	public static final int SKL_TRADE=20;
	public static final int SKL_CHAT=21;
	public static final int SKL_LEADERSHIP=22;
	public static final int SKL_ANIMALEMP=23;
	public static final int SKL_ATHLETICS=24;
	public static final int SKL_CLIMB=25;
	public static final int SKL_SWIM=26;
	public static final int SKL_EXS=27;
	public static final int SKL_PSI=28;

	  
	public static final String[] ATTR_NAMES = new String[]{ "Str", 
															"Con", 
															"Dex", 
															"Per", 
															"Int", 
															"Cha", 
															"Exs", 
															"Luc" };
	
	public static final String[] STAT_NAMES = new String[]{ "Vision", 
															"Hearing", 
															"Mental", 
															"Max HP", 
															"Speed"}; 
	
	public static final String[] SKILL_NAMES = new String[]{"Unarmed combat", 
															"Melee weapons", 
															"Pistols",
															"Smg's", 
															"Rifles", 
															"Assault Rifles",
															"Heavy weapons", 
															"Energy weapons",  
															"Throwing", 
															"Traps & Explosives", 
															"Medicine", 
															"Techics", 
															"Electronics",
															"Computers", 
															"Cooking", 
															"Surviving", 
															"Hide",
															"Dodge", 
															"Lockpicking", 
															"Steal",
															"Trade", 
															"Chat", 
															"Leadership",
															"Animal Empathy", 
															"Athletics", 
															"Climbing", 
															"Swimming", 
															"Extrasensorics", 
															"Combat Psionics", 
															};
	
	public static final int SEX_MALE = 0;
	public static final int SEX_FEMALE = 1;
	public static final int SEX_MIDDLE = 2;


	
	

}	