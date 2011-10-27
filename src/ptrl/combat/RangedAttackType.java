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
import ptrl.items.Ammo;
import ptrl.items.RangedWeapon;

public class RangedAttackType implements Serializable
{
	public RangedAttackType(Creature attacker)
	{
		this(attacker, 0, 0, 0, 0, 0, 0, 0, "");
	}

	/**
	 * Creates close AttackType with no effects. Damage of attack =
	 * [X]d[Attribute]+[Y]
	 * 
	 * 
	 * @param aid
	 *            attacker's creature ID.
	 * @param d_t
	 *            damage types (see DT_ constants in Attack class)
	 * @param d_atr
	 *            [Attribute] parameter of damage.
	 * @param d_x
	 *            [X] parameter of damage (no. of dices).
	 * @param d_y
	 *            [Y] part of damage (bonus).
	 * @param s
	 *            corresponding skill no.
	 * @param t
	 *            time.
	 * @param n
	 *            atack type name.
	 * 
	 */
	public RangedAttackType(Creature attacker, int d_t, int d_atr, int d_x,
			int d_y, float th, int s, int t, String n)
	{

		damageAttrib = d_atr;
		damageAttribPart = d_x;
		damageFixedPart = d_y;
		skill_n = s;
		// range=0;
		time = t;
		name = n;
		this.attacker = attacker;
		attacksNumber = 1;
		basicTohit = th;
		projectiles = -1;
		caliber = null;
		range = -1;
		ammoPerShot = 1;
		tileEffect = PtrlConstants.TE_STOP;
		attacksDelay=2;
	}

	public RangedAttackType(Creature attacker, int d_t, int d_atr, int d_x,
			int d_y, float thm, int s, int t, int num, String n)
	{
		this(attacker, d_t, d_atr, d_x, d_y, thm, s, t, n);
		attacksNumber = num;
	}

	public RangedAttackType(Element e, Creature attacker)
	{
		this(attacker);
		name = e.getAttribute("name");
		String d_attr = "";
		String skl = "";
		String dt = "";
		damageAttrib = -1;
		skill_n = -1;
		damageType = -1;
		NodeList nl = e.getChildNodes();
		String value = "";
		for (int i = 0; i < nl.getLength(); i++)
		{
			Node n_child = nl.item(i);
			if (n_child instanceof Element)
			{
				Element e_child = (Element) n_child;
				Text tnode = (Text) e_child.getFirstChild();
				if (tnode != null)
				{
					value = tnode.getData().trim();
				}
				if (e_child.getTagName().equals("d_rand_part"))
					damageRandomPart = Integer.parseInt(value);
				else if (e_child.getTagName().equals("d_fix_part"))
					damageFixedPart = Integer.parseInt(value);
				else if (e_child.getTagName().equals("d_attrib_part"))
					damageAttribPart = Integer.parseInt(value);
				else if (e_child.getTagName().equals("d_factor"))
					damageFactor = Double.parseDouble(value);
				else if (e_child.getTagName().equals("number"))
					attacksNumber = Integer.parseInt(value);
				else if (e_child.getTagName().equals("delay"))
					attacksDelay = Integer.parseInt(value);
				else if (e_child.getTagName().equals("time"))
					time = Integer.parseInt(value);
				else if (e_child.getTagName().equals("projectiles"))
					projectiles = Integer.parseInt(value);
				else if (e_child.getTagName().equals("spreading"))
					spreading = Double.parseDouble(value);
				else if (e_child.getTagName().equals("range"))
					range = Double.parseDouble(value);
				else if (e_child.getTagName().equals("tohit"))
					basicTohit = Double.parseDouble(value);
				else if (e_child.getTagName().equals("tohit_factor"))
					tohitFactor = Double.parseDouble(value);
				else if (e_child.getTagName().equals("minskill"))
					minskill = Integer.parseInt(value);
				else if (e_child.getTagName().equals("caliber"))
					caliber = value;
				else if (e_child.getTagName().equals("ammo_per_shot"))
					ammoPerShot = Integer.parseInt(value);
				else if (e_child.getTagName().equals("damage_type"))
					dt = value;
				else if (e_child.getTagName().equals("attribute"))
					d_attr = value;
				else if (e_child.getTagName().equals("skill"))
					skl = value;
				else if (e_child.getTagName().equals("tile_effect"))
				{
					if (value.equals("bounce"))
						tileEffect = PtrlConstants.TE_BOUNCE;
					else if (value.equals("hit"))
						tileEffect = PtrlConstants.TE_HIT;
					else if (value.equals("stop"))
						tileEffect = PtrlConstants.TE_STOP;
					else if (value.equals("ignore"))
						tileEffect = PtrlConstants.TE_IGNORE;
				}
			}
		}
		for (int i = 0; i < Creature.ATTR_NAMES.length; i++)
		{
			if (d_attr.equalsIgnoreCase(Creature.ATTR_NAMES[i]))
			{
				damageAttrib = i;
				break;
			}
		}
		if (damageAttrib == -1)
		{
			System.out.println("Error parsing AttackType: " + name
					+ "; invalid attribute name");
			System.exit(0);
		}
		for (int i = 0; i < Creature.SKILL_NAMES.length; i++)
		{
			if (skl.equalsIgnoreCase(Creature.SKILL_NAMES[i]))
			{
				skill_n = i;
				break;
			}
		}
		if (skill_n == -1)
		{
			System.out.println("Error parsing AttackType: " + name
					+ "; invalid skill name");
			System.exit(0);
		}
		for (int i = 0; i < PtrlConstants.DAMAGE_TYPE_NAMES.length; i++)
		{
			if (dt.equalsIgnoreCase(PtrlConstants.DAMAGE_TYPE_NAMES[i]))
			{
				damageType = i;
				break;
			}
		}
	}

	public String getShortInfoString()
	{
		String weapon = "";
		if (host != null)
		{
			weapon = host.getName();
			if (caliber != null)
			{
				Ammo ammo = host.getAmmo(caliber);
				String am = "(";
				if (ammo != null)
					am += "(" + ammo.getQty();
				else
					am += "0";
				am += "/" + host.getMaxAmmo(caliber) + ")";
				weapon += am + ":";
			}
		}
		String str = "[" + weapon + name + "]";
		return str;
	}
	
	public String getInfoString()
	{
		Attribute a=attacker.getAttribute(damageAttrib);
		Skill s=attacker.getSkills()[skill_n];
		String weapon = "";
		if (host != null)
		{
			weapon = host.getName();
			if (caliber != null)
			{
				Ammo ammo = host.getAmmo(caliber);
				String am = "(";
				if (ammo != null)
					am += "(" + ammo.getQty();
				else
					am += "0";
				am += "/" + host.getMaxAmmo(caliber) + ")";
				weapon += am + ":";
			}
		}
		String str = "[" + weapon + name + "] damage: ";
		int max_d = a.getValue() * damageAttribPart
				+ getDamageFixedPart() + getDamageRandomPart();
		int min_d = 1 + getDamageFixedPart();
		double th = basicTohit + (float) s.getValue() / 50;
		String d=min_d + "-" + max_d;
		if (attacksNumber != 1||getProjectiles()!=1)
		{
			d="("+d+")";
			if (getProjectiles()!=1)
				d=getProjectiles()+d;
			if (attacksNumber!=1)
				d=attacksNumber+"x"+d;
		}
		str = str + d;
		str+="; tohit: ";
		NumberFormat nf=NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		str = str + nf.format(th) + "; t: " + time + " s.";
		return str;
	}

	public RangedAttack[] getShotRangedAttacks(Creature attacker)
	{
		int n = attacksNumber;
		int m = getProjectiles();
		if (caliber != null)
		{
			Ammo ammo = host.getAmmo(caliber);
			if (ammo != null)
			{
				if (ammo.getQty() < n * ammoPerShot)
					n = (int) Math.floor(ammo.getQty() / ammoPerShot);
				ammo.setQty(ammo.getQty() - n * ammoPerShot);
			} else
				n = 0;
		}
		RangedAttack[] ret = new RangedAttack[n * m];
		int actor_spread = 0; 
		for (int i = 0; i < n; i++)
		{
			actor_spread = 0; // TODO: calculate creature-dependent spreading
			for (int j = 0; j < m; j++)
			{
				int d = 1
						+ (int) Math.ceil(Math.random()
								* attacker.getAttributeValue(damageAttrib)
								* getDamageAttribPart() - 1)
						+ (int) Math.ceil(Math.random() * getDamageRandomPart())
						+ getDamageFixedPart();
				double th = basicTohit
						+ (double) attacker.getSkills()[skill_n].getValue()
						/ 50;
				ret[m * i + j] = new RangedAttack(attacker, d, damageFactor,
						getDamageType(), th, tohitFactor, this.range,
						this.tileEffect);
				ret[m * i + j].setAzimuth(Math.random() * getSpreading() - getSpreading()
						/ 2 + actor_spread);
				ret[m * i + j].setAttackCounter(i * attacksDelay);
			}
		}
		return ret;
	}

	public int getProjectiles()
	{
		if (caliber!=null)
			if (projectiles==-1)
				if (getHost()!=null)
					if (getHost().getAmmo(caliber)!=null)
						return getHost().getAmmo(caliber).getProjectiles();
		return projectiles;
	}

	public double getSpeading()
	{
		if (caliber!=null)
			if (getHost()!=null)
				if (getHost().getAmmo(caliber)!=null)
					return spreading+getHost().getAmmo(caliber).getSpreading();
		return spreading;
	}

	public void setProjectiles(int projectiles)
	{
		this.projectiles = projectiles;
	}

	public void setSpeading(double speading)
	{
		this.spreading = speading;
	}

	public void setHost(RangedWeapon host)
	{
		this.host = host;
	}
	
	public RangedWeapon getHost()
	{
		return this.host;
	}

	public int getTime()
	{
		return time;
	}

	public double getRange()
	{
		return range;
	}

	public int getAmmoPerShot()
	{
		return ammoPerShot;
	}
	
	protected int attacksNumber;
	
	protected int attacksDelay;

	protected int time;

	protected String name;

	protected int damageType; // -1 if ammo-dependent

	protected int damageAttribPart;

	protected int damageFixedPart;

	protected int damageRandomPart;

	protected double damageFactor;

	// assume that the missile already at tile, where target stands,
	// e.g. describes chances to dodge
	private double basicTohit;

	private double tohitFactor;

	protected int damageAttrib;

	protected int skill_n;

	protected int minskill;

	protected double spreading; // in angles

	protected double range; // -1 if infinite

	private int projectiles; // in 1 shot, -1 if ammo-dependent

	private String caliber; // null if not needed

	private int ammoPerShot;

	private Creature attacker;

	protected RangedWeapon host;

	private int tileEffect; // see PtrlConstants.TE_* constants

	public String getName()
	{
		return name;
	}

	public void setAttacker(Creature attacker)
	{
		this.attacker = attacker;
	}

	public String getCaliber()
	{
		return caliber;
	}

	public int getDamageAttribPart()
	{
		return damageAttribPart;
	}

	public int getDamageFixedPart()
	{
		if (caliber!=null)
			if (getHost()!=null)
				if (getHost().getAmmo(caliber)!=null)
					return damageFixedPart+getHost().getAmmo(caliber).getDamageFixedPart();
		return damageFixedPart;
	}

	public int getDamageRandomPart()
	{
		if (caliber!=null)
			if (getHost()!=null)
				if (getHost().getAmmo(caliber)!=null)
					return damageRandomPart+getHost().getAmmo(caliber).getDamageRandomPart();
		return damageRandomPart;
	}

	public int getDamageType()
	{
		if (caliber!=null)
			if (damageType==-1)
				if (getHost()!=null)
					if (getHost().getAmmo(caliber)!=null)
						return getHost().getAmmo(caliber).getDamageType();
		return damageType;
	}

	public double getSpreading()
	{
		if (caliber!=null)
			if (getHost()!=null)
				if (getHost().getAmmo(caliber)!=null)
					return spreading+getHost().getAmmo(caliber).getSpreading();
		return spreading;
	}

	public int getAttacksDelay()
	{
		return attacksDelay;
	}

	public int getMinSkill()
	{
		return minskill;
	}
	
	public int getSkillN()
	{
		return skill_n;
	}
	
}
