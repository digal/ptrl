package ptrl.combat;

import java.io.Serializable;
import java.text.NumberFormat;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import ptrl.creatures.Attribute;
import ptrl.creatures.Creature;
import ptrl.creatures.Skill;

/**
 * Attack specification. Describes the attack type. 
 * 
 * @author Digal
 */
public class UnarmedAttackType implements Serializable
{
	/**
	 * Creates close AttackType with no effects. 
	 * Damage of attack = [X]d[Attribute]+[Y]
	 * 
	 *
	 * @param aid attacker's creature ID.
	 * @param d_t damage types (see DT_ constants in Attack class)
	 * @param d_atr [Attribute] parameter of damage.
	 * @param d_x [X] parameter of damage (no. of dices).
	 * @param d_y [Y] part of damage (bonus). 
	 * @param s corresponding skill no.
	 * @param t time.
	 * @param n atack type name. 
	 * 
	 */ 
	public UnarmedAttackType(Creature attacker, int d_t, int d_atr, int d_x, int d_y, float thm, int s, int t, String n)
	{
		
		damage_attrib=d_atr;
		damageAttribPart=d_x;
		damageFixedPart=d_y;
		skill_n=s;
		//range=0;
		time=t;
		name=n;
		this.attacker=attacker;
		attacks_number=1;
		tohit_mod=thm;
	}
	
	public UnarmedAttackType(Creature attacker, int d_t, int d_atr, int d_x, int d_y, float thm, int s, int t, int num, String n)
	{
		this(attacker, d_t, d_atr, d_x, d_y, thm, s, t, n);
		attacks_number=num;
	}
	
	public Attack getAttack()
	{
		int d=1+(int)Math.ceil(Math.random()*attacker.getAttributeValue(damage_attrib)*damageAttribPart-1)+damageFixedPart;
		float th=tohit_mod+(float)attacker.getSkills()[skill_n].getValue()/50;
		//System.out.println(th);
		return new Attack(d, damage_type, attacker, th);
	}
	
	public String getShortInfoString()
	{
		return "["+name+"]";
	}
	
	public String getInfoString()
	{
		Attribute a=attacker.getAttribute(damage_attrib);
		Skill s=attacker.getSkills()[skill_n];
		String str = "["+name+"] damage: ";
		int max_d=a.getValue()*damageAttribPart+damageFixedPart;
		int min_d=1+damageFixedPart;
		double th=tohit_mod+(float)s.getValue()/50;
		if (attacks_number==1) str=str+min_d+"-"+max_d+"; tohit: ";
		else str=str+attacks_number+"*("+min_d+"-"+max_d+"); tohit: ";
		NumberFormat nf=NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		str=str+nf.format(th)+"; t: "+time+" s.";
		return str;
	}
	
	public UnarmedAttackType (Element e, Creature attacker)
	{
		
	    name = e.getAttribute("name");
	 	NodeList nl = e.getChildNodes();
	 	int d_type=-1;
	 	int d_atr=-1; 
	 	int d_x=0;
	 	int d_y=0; 
	 	int s=-1;
	 	int t=0;
	 	int rng=0;
	 	int num=1;
	 	String dt="normal";
	 	String value = "";
	 	String attr = "";
	 	String skl = "";
 		for (int i=0; i<nl.getLength(); i++)
 		{
 			Node n_child = nl.item(i);
 			if (n_child instanceof Element)
 			{
 				Element e_child = (Element)n_child;
 				Text tnode = (Text)e_child.getFirstChild();
 				if (tnode!=null) {value = tnode.getData().trim();}
 				if (e_child.getTagName().equals("damage_type")) 
 					dt = value;
 				else if (e_child.getTagName().equals("d_attrib_part"))
 					d_x = Integer.parseInt(value);
 				else if (e_child.getTagName().equals("d_fix_part"))
 					d_y = Integer.parseInt(value);
 				else if (e_child.getTagName().equals("tohit"))
 					tohit_mod = Float.parseFloat(value);
 				else if (e_child.getTagName().equals("range"))
 					rng = Integer.parseInt(value);
 				else if (e_child.getTagName().equals("attribute"))
 					attr = value;
 				else if (e_child.getTagName().equals("skill"))
 					skl = value;
 				else if (e_child.getTagName().equals("time"))
 					t = Integer.parseInt(value);
 				else if (e_child.getTagName().equals("number"))
 					num = Integer.parseInt(value);
 			}	
 		}
 		for (int i=0; i<Creature.ATTR_NAMES.length; i++)
 		{
 			if (attr.equalsIgnoreCase(Creature.ATTR_NAMES[i])) 
 			{
 				d_atr=i;
 				break;
 			}
 		}
 		if (d_atr==-1) 
		{
 			System.out.println("Error parsing AttackType: "+name+"; invalid attribute name");
 			System.exit(0);
		}
 		for (int i=0; i<Creature.SKILL_NAMES.length; i++)
 		{
 			if (skl.equalsIgnoreCase(Creature.SKILL_NAMES[i])) 
 			{
 				s=i;
 				break;
 			}
 		}
 		if (s==-1) 
		{
 			System.out.println("Error parsing AttackType: "+name+"; invalid skill name");
 			System.exit(0);
		}
 		for (int i=0; i<PtrlConstants.DAMAGE_TYPE_NAMES.length; i++)
 		{
 			if (dt.equalsIgnoreCase(PtrlConstants.DAMAGE_TYPE_NAMES[i])) 
 			{
 				d_type=i;
 				break;
 			}
 		}
 		if (d_type==-1) 
		{
 			System.out.println("Error parsing AttackType: "+name+"; invalid damage type");
 			System.exit(0);
		}
		damage_attrib=d_atr;
		damageAttribPart=d_x;
		damageFixedPart=d_y;
		skill_n=s;
		//range=0;
		time=t;
		this.attacker=attacker;
		attacks_number=num;
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * 	@return damage type.
	 */
	public int getDamageType()
	{
		return damage_type;
	}
	
	/**
	 * 
	 * @return time, needed to attack (in seconds). 
	 */
	public int getTime()
	{
		return time;
	}
	
	/**
	 * @return effective range if ranged attack or 0 if close combat.
	 */
	/*public int getRange()
	{
		return range;
	}*/
	
	/**
	 * @return true if attack is ranged.
	 */
	/*public boolean isRanged()
	{
		if (range!=0) return true;
		else return false;
	}*/
	
	public String toString()
	{
		return "[AttackType: "+name+"]";
	}
	
	public int getAttacksN()
	{
		return attacks_number;
	}
	
	protected int attacks_number;
	protected int time;
	protected String name;
	protected Creature attacker;
	//protected int range; 	//0 if close combat
	protected int damage_type;
	protected int damage_attrib;
	protected int damageAttribPart; 
	protected int damageFixedPart; 
	protected int skill_n;
	protected float tohit_mod;
	
	public int getDamageAttribPart()
	{
		return damageAttribPart;
	}

	public int getDamageFixedPart()
	{
		return damageFixedPart;
	}

	public void setDamageAttribPart(int damageAttribPart)
	{
		this.damageAttribPart = damageAttribPart;
	}

	public void setDamageFixedPart(int damageFixedPart)
	{
		this.damageFixedPart = damageFixedPart;
	}
	
	public void setAttacker (Creature attacker)
	{
		this.attacker=attacker;
	}
}
