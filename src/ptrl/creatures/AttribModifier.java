package ptrl.creatures;

import java.io.Serializable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class AttribModifier implements Cloneable, Serializable
{
	private AttribModifier(int attrib, int mod, boolean permanent)
	{
		this.attrib=attrib;
		this.mod=mod;
	}
	
 	public AttribModifier(Element e)
 	{
 		this.permanent=false;
 		NodeList nl = e.getChildNodes();
	    for (int i=0; i<nl.getLength(); i++)
	    {
	    	Node n_child = nl.item(i);
	    	if (n_child instanceof Element)
	    	{
	    		Element e_child = (Element)n_child;
	    		Text tnode = (Text)e_child.getFirstChild();
	    		String value = tnode.getData().trim();
	    		if (e_child.getTagName().equals("attr")) 
	    			setAttr(value);
	    		else if (e_child.getTagName().equals("mod")) 
	    			mod=Integer.parseInt(value);
	    		else if (e_child.getTagName().equals("permanent")) 
	    			permanent=true;
	    	}
	    }
 	}
 			
 	private void setAttr(String attrString)
 	{
 		for (int i=0; i<Creature.ATTR_NAMES.length; i++)
 			if (attrString.equalsIgnoreCase(Creature.ATTR_NAMES[i]))
 			{
 				this.attrib=i;
 				return;
 			}
 		for (int i=0; i<Creature.STAT_NAMES.length; i++)
 			if (attrString.equalsIgnoreCase(Creature.STAT_NAMES[i]))
 			{
 				this.attrib=i+(Creature.ATTR_NAMES.length);
 				return;
 			}
 		
 	}
	
	public int getAttrib()
	{
		return attrib;
	}
	public int getMod()
	{
		return mod;
	}

	public Object clone()
	{
		return new AttribModifier(this.attrib, this.mod, this.permanent);
	}
	
	public boolean isPermanent()
	{
		return permanent;
	}
	
	private int attrib;
	private int mod;
	private boolean permanent;

}
