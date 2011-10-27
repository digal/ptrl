package ptrl.creatures;

import java.io.File;
import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import ptrl.combat.PtrlConstants;
import ptrl.combat.RangedAttackType;
import ptrl.combat.UnarmedAttackType;
import ptrl.items.Equipment;

public class Player extends Humanoid implements Serializable
{
	public Player() 
	{
		super("Player", "Generic Player");
		setSymbol('@');
		setColor(PtrlConstants.WHITE);
		skillpoints=0;
		created=false; 
		setCurrentCloseAttackN(0);
		countAll();
		hp=stats[MHP].getBasicValue();
	}	

	public void countAll()
	{
		super.countAll();
	}
	
	public int getSkillpoints()
	{
		return skillpoints;
	}

	public void incSkillPoints()
	{
		if (!created) skillpoints++;
	}
	
	public void decSkillPoints()
	{
		if ((!created)&&skillpoints>0) skillpoints--;
	}
	
	public void setName(String s)
	{
		if (!created) super.setName(s);		
	}
	
	public void setClass(int c)
	{
		if (!created) playerclass=c;
		
	}
	
	public void setSex(int s)
	{
		if (!created) sex=s;
	}
	

	public void refreshAttackTypes()
	{
		super.refreshAttackTypes();
		int u=skills[SKL_UNARMED].getValue();
		//close_attacks=new UnarmedAttackType[0];
		UnarmedAttackType a;
		if (u>=40)
		{
			if (getEquipment().getSlot(Equipment.EQ_RIGHTHAND).getItem()==null&&getEquipment().getSlot(Equipment.EQ_LEFTTHAND).getItem()==null)
			{
				a=new UnarmedAttackType(this, PtrlConstants.DT_BLUNT, STR, 2, 0, (float)0.25, SKL_UNARMED, 2, 2, "double punch");
				addCloseAttackType(a);
			}
			if (getEquipment().getSlot(Equipment.EQ_RIGHTHAND).getItem()==null)
			{
				a=new UnarmedAttackType(this, PtrlConstants.DT_NORMAL, DEX, 2, 6, (float)0.6, SKL_UNARMED, 2, "palm blow");
				addCloseAttackType(a);
				a=new UnarmedAttackType(this, PtrlConstants.DT_BLUNT, STR, 2, 2, (float)0.35, SKL_UNARMED, 1, "power punch");
				addCloseAttackType(a);
			}
			a=new UnarmedAttackType(this, PtrlConstants.DT_BLUNT, STR, 2, 4, (float)0.5, SKL_UNARMED, 2, "power kick");
			addCloseAttackType(a);
		}
		else if (u>=30)
		{
			if (getEquipment().getSlot(Equipment.EQ_RIGHTHAND).getItem()==null&&getEquipment().getSlot(Equipment.EQ_LEFTTHAND).getItem()==null)
			{
				a=new UnarmedAttackType(this, PtrlConstants.DT_BLUNT, STR, 2, 0, (float)0.2, SKL_UNARMED, 2, 2, "double punch");
				addCloseAttackType(a);
			}
			if (getEquipment().getSlot(Equipment.EQ_RIGHTHAND).getItem()==null)
			{
				a=new UnarmedAttackType(this, PtrlConstants.DT_BLUNT, STR, 2, 0, (float)0.35, SKL_UNARMED, 1, "power punch");
				addCloseAttackType(a);
			}
			a=new UnarmedAttackType(this, PtrlConstants.DT_BLUNT, STR, 2, 2, (float)0.5, SKL_UNARMED, 2, "power kick");
			addCloseAttackType(a);
		}
		else if (u>=20)
		{
			if (getEquipment().getSlot(Equipment.EQ_RIGHTHAND).getItem()==null)
			{
				a=new UnarmedAttackType(this, PtrlConstants.DT_BLUNT, STR, 2, 0, (float)0.3, SKL_UNARMED, 1, "power punch");
				addCloseAttackType(a);
			}
			a=new UnarmedAttackType(this, PtrlConstants.DT_BLUNT, STR, 1, 6, (float)0.5, SKL_UNARMED, 2, "kick");
			addCloseAttackType(a);
		}
		else if (u>=10)
		{
			if (getEquipment().getSlot(Equipment.EQ_RIGHTHAND).getItem()==null)
			{
				a=new UnarmedAttackType(this, PtrlConstants.DT_BLUNT, STR, 1, 1, (float)0.2, SKL_UNARMED, 1, "punch");
				addCloseAttackType(a);
			}
			a=new UnarmedAttackType(this, PtrlConstants.DT_BLUNT, STR, 1, 3, (float)0.4, SKL_UNARMED, 2, "kick");
			addCloseAttackType(a);
		}
		else
		{
			if (getEquipment().getSlot(Equipment.EQ_RIGHTHAND).getItem()==null)
			{
				a=new UnarmedAttackType(this, PtrlConstants.DT_BLUNT, STR, 1, 0, 0, SKL_UNARMED, 1, "punch");
				addCloseAttackType(a);
			}
		}
		setCurrentCloseAttackN(close_attacks.length-1);
		readConfig("player.xml");
	}
	 
	public void create()
	{
		//set skills, using class information
		for (int i=0; i<CLASS_PRIMARY_SKILLS[playerclass].length; i++)
		{
			skills[CLASS_PRIMARY_SKILLS[playerclass][i]].setValue((int)(attribs[skills[i].getAttrib()].getBasicValue()*3+Math.round(Math.random()*attribs[skills[i].getAttrib()].getBasicValue()*2)));
			skills[CLASS_PRIMARY_SKILLS[playerclass][i]].makePrimary();
		}
		for (int i=0; i<CLASS_SECONDARY_SKILLS[playerclass].length; i++)
		{
			skills[CLASS_SECONDARY_SKILLS[playerclass][i]].setValue((int)(attribs[skills[i].getAttrib()].getBasicValue()*2+Math.round(Math.random()*attribs[skills[i].getAttrib()].getBasicValue()*1)));
		}
		for (int i=0; i<skills.length; i++)
		{
			if (skills[i].getValue()==0&&(Math.round(Math.random()*7+3)<attribs[skills[i].getAttrib()].getValue()))
				skills[i].setValue((int)(attribs[skills[i].getAttrib()].getBasicValue()*1+Math.round(Math.random()*attribs[skills[i].getAttrib()].getBasicValue()*1)));
		}
		countAll();
		//lock Player
		created=true;
	}
	
	public void nextCloseAttackType()
	{
		if (getCurrentCloseAttackN()<getCloseAttackTypes().length-1) setCurrentCloseAttackN(getCurrentCloseAttackN()+1);
		else setCurrentCloseAttackN(0);
	}

	public void prevCloseAttackType()
	{
		if (getCurrentCloseAttackN()>0) setCurrentCloseAttackN(getCurrentCloseAttackN()-1);
		else setCurrentCloseAttackN(getCloseAttackTypes().length-1);
	}
	
	public void nextRangedAttackType()
	{
		if (getCurrentRangedAttackN()<getRangedAttackTypes().length-1) setCurrentRangedAttackN(getCurrentRangedAttackN()+1);
		else setCurrentRangedAttackN(0);
	}

	public void prevRangedAttackType()
	{
		if (getCurrentRangedAttackN()>0) setCurrentRangedAttackN(getCurrentRangedAttackN()-1);
		else setCurrentRangedAttackN(getRangedAttackTypes().length-1);
	}
	
	public String toString()
	{
		String s="["+name+", the "+CLASSNAMES[playerclass]+"; hp: "+hp+"/"+stats[MHP].getValue()+"; spd: "+stats[SPD].getValue()+"; dp: "+protection.getDefence()+"]";
		return s;
	}	

	public void turn()
	{
		if (events!=null)
			for (int i=0; i<events.length; i++)
				System.out.println(events[i].toString());
		super.turn();
	}
	
	private void readConfig(String filename)
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
	    			//String ename = e.getAttribute("name");
	    			//System.out.println("tag:"+e.getTagName());
	    			if (e.getTagName().equalsIgnoreCase("ranged_attack_type")) 
	    				addRangedAttackType(new RangedAttackType(e, this));
	    		}
	  		}
		}
		catch (SAXParseException e)
		{
			System.out.println("oops!");
			System.out.println("line: "+e.getLineNumber());
			System.out.println("col: "+e.getColumnNumber());
			e.printStackTrace();
			System.exit(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		//System.out.println(toString());
	
	}
	
	private int playerclass;
	
	public static final String[] CLASSNAMES=new String[]{"Street fighter",
														 "Soldier",
														 "Extrasensor",
														 "Assassin"};

	
													 	
	public static final int[][] CLASS_PRIMARY_SKILLS = new int[][]
	{
		{SKL_UNARMED, SKL_MELEE, SKL_ATHLETICS},	//Street Fighter
		{SKL_SMG, SKL_ASSAULT, SKL_THROWING}, 		//Soldier
		{SKL_EXS, SKL_PSI},							//Exsor
		{SKL_HIDE, SKL_TRAPS, SKL_PISTOLS}			//Assasin
	};

	public static final int[][] CLASS_SECONDARY_SKILLS = new int[][]
	{
		{SKL_PISTOLS, SKL_SMG, SKL_RIFLES, SKL_THROWING, SKL_DODGE, SKL_TRADE, SKL_CHAT},	//Street Fighter
		{SKL_UNARMED, SKL_MELEE, SKL_PISTOLS, SKL_HEAVY, SKL_RIFLES, SKL_DODGE, SKL_SWIM, SKL_SURVIVE}, 		//Soldier
		{SKL_UNARMED, SKL_ANIMALEMP, SKL_PISTOLS, SKL_MEDICINE, SKL_CHAT, SKL_LEADERSHIP},							//Exsor
		{SKL_UNARMED, SKL_MELEE, SKL_RIFLES, SKL_LOCKPICK, SKL_CLIMB, SKL_DODGE}			//Assasin
	};

	private int skillpoints;
	private boolean created;

}
