package ptrl.items;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import ptrl.combat.PtrlConstants;
import ptrl.creatures.AttribModifier;

public class Ammo extends Item implements Serializable
{
	
	public Ammo()
	{
		projectiles=1;
	}
	
	public Ammo(String filename, String itemname) throws ParserConfigurationException, SAXException, IOException
	{
			this();
			fillFromFile(filename, itemname);
	}
	
	public Ammo(Element e) throws ParserConfigurationException, SAXException, IOException
	{
			this();
			fillFromElement(e);
	}

	protected boolean parseElement(Element e)
	{
		if (super.parseElement(e)) 
			return true;
		String value="";
		Text tnode = (Text)e.getFirstChild();
		if (tnode!=null) 
			value = tnode.getData().trim();
		else 
			return true;
		if (e.getTagName().equals("caliber"))
			caliber = value;
		else if (e.getTagName().equals("projectiles"))
			projectiles = Integer.parseInt(value);
		else if (e.getTagName().equals("damage_type"))
			damageType = PtrlConstants.strToDamageType(value);
		else if (e.getTagName().equals("d_rand_part"))
			damageRandomPart = Integer.parseInt(value);
		else if (e.getTagName().equals("d_fix_part"))
			damageFixedPart = Integer.parseInt(value);
		else if (e.getTagName().equals("spreading"))
			spreading = Double.parseDouble(value);
		else 
			return false;
		return true;
	}
	
	public String getCaliber()
	{
		return caliber;
	}


	public int getDamageFixedPart()
	{
		return damageFixedPart;
	}


	public int getDamageRandomPart()
	{
		return damageRandomPart;
	}


	public int getProjectiles()
	{
		return projectiles;
	}


	public double getSpreading()
	{
		return spreading;
	}
	
	private double spreading; //angle in degrees
	protected int damageRandomPart; 
	protected int damageFixedPart; 
	
	private String caliber; //e.g. ".357", ".12g", "7,62mm NATO"
	private int projectiles; 
	private boolean destroy_on_impact; //false for arrows, etc
	private int damageType;
	
	
	 public Item copySingle()
	 {
		Ammo cloned=new Ammo();
		cloned.color=this.color;
		cloned.weight=this.weight;
		cloned.name=this.name;
		cloned.many_name=this.many_name;
		cloned.price=this.price;
		cloned.level=this.level;
		cloned.symbol=this.symbol;
	 	cloned.inventory_section=this.inventory_section;
	 	cloned.caliber=this.caliber;
	 	cloned.damageFixedPart=this.damageFixedPart;
	 	cloned.damageRandomPart=this.damageRandomPart;
	 	cloned.destroy_on_impact=this.destroy_on_impact;
	 	cloned.projectiles=this.projectiles;
	 	cloned.spreading=this.spreading;
	 	cloned.damageType=this.damageType;
	 	Iterator<AttribModifier>  i= attrMods.iterator();
	 	while (i.hasNext())
	 		cloned.attrMods.add(i.next());
	 	return cloned; 											
	 }

	public int getDamageType()
	{
		return damageType;
	}
	
}
