package ptrl.combat;

import java.io.Serializable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import ptrl.creatures.Attribute;
import ptrl.creatures.Creature;
import ptrl.creatures.Skill;
import ptrl.items.Weapon;

public class MeleeAttackType extends UnarmedAttackType implements Serializable
{
	public MeleeAttackType(Element e, Creature attacker)
	{
		super(e, attacker);
	    name = e.getAttribute("name");
	 	NodeList nl = e.getChildNodes();
	 	int d_z=0;
	 	String value = "";
 		for (int i=0; i<nl.getLength(); i++)
 		{
 			Node n_child = nl.item(i);
 			if (n_child instanceof Element)
 			{
 				Element e_child = (Element)n_child;
 				Text tnode = (Text)e_child.getFirstChild();
 				if (tnode!=null) {value = tnode.getData().trim();}
 				if (e_child.getTagName().equals("d_rand_part"))
 					d_z = Integer.parseInt(value);
 				else if (e_child.getTagName().equals("minskill"))
 					minskill = Integer.parseInt(value);
 			}	
 		}
		damageRandomPart=d_z;
	}
 		
	public boolean isAviable (int skillv)
	{
		if (skillv>=minskill) return true;
		return false;    
	}
	
	public Attack getAttack()
	{
		int d=1+(int)Math.ceil(Math.random()*attacker.getAttributeValue(damage_attrib)*damageAttribPart-1)+(int)Math.ceil(Math.random()*damageRandomPart)+damageFixedPart;
		float th=tohit_mod+(float)attacker.getSkills()[skill_n].getValue()/50;
		//System.out.println(th);
		return new Attack(d, damage_type, attacker, th);
	}
	
	public String getShortInfoString()
	{
		return "["+host.getName()+": "+name+"]";
	}
	
	public String getInfoString(Attribute[] a, Skill[] s)
	{
		String str = "["+host.getName()+": "+name+"] damage: ";
		int max_d=a[damage_attrib].getValue()*damageAttribPart+damageFixedPart+damageRandomPart;
		int min_d=1+damageFixedPart;
		float th=tohit_mod+(float)s[skill_n].getValue()/50;
		if (attacks_number==1) str=str+min_d+"-"+max_d+"; tohit: ";
		else str=str+attacks_number+"*("+min_d+"-"+max_d+"); tohit: ";
		str=str+th+"; t: "+time+" s.";
		return str;
	}
	

	public int getDamageRandomPart()
	{
		return damageRandomPart;
	}

	public void setDamageRandomPart(int damageRandomPart)
	{
		this.damageRandomPart = damageRandomPart;
	}	

	public void setHost(Weapon host)
	{
		this.host=host;
	}

	
 	protected int damageRandomPart;
 	protected Weapon host;
	protected int minskill;
	


}
