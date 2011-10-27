package ptrl.combat;

import java.awt.Color;

import ptrl.creatures.Creature;
import ptrl.items.Item;
import ptrl.map.Actor;
import ptrl.map.Map;
import ptrl.map.Tile;

/**
 * Describes 1 launched projectile/beam
 * 
 * @author Digal
 */
public class RangedAttack
{
	public RangedAttack(Creature attacker, int damage, double damageFactor, int damageType, double tohit, double tohitFactor, double range, int tileEffect)
	{
		this.attacker=attacker;
		this.basicDamage=damage;
		this.damageFactor=damageFactor;
		this.damageType=damageType;
		this.basicTohit=tohit;
		this.tohitFactor=tohitFactor;
		this.range=range;
		this.tileEffect=tileEffect;
		attackCounter=0;
	}
	
	public Creature getAttacker()
	{
		return attacker;
	}

	public double getAzimuth()
	{
		return azimuth;
	}

	public int getBasicDamage()
	{
		return basicDamage;
	}

	public double getBasicTohit()
	{
		return basicTohit;
	}

	public double getDamageFactor()
	{
		return damageFactor;
	}

	public int getDamageType()
	{
		return damageType;
	}

	public double getDistanceTraversed()
	{
		return distanceTraversed;
	}

	public int[] getEffects()
	{
		return effects;
	}

	public float[] getEffectsFactor()
	{
		return effectsFactor;
	}

	public int getOriginX()
	{
		return originX;
	}

	public int getOriginY()
	{
		return originY;
	}

	public Item getProjectile()
	{
		return projectile;
	}

	public double getTohitFactor()
	{
		return tohitFactor;
	}

	public int getTargetX()
	{
		return targetX;
	}

	public int getTargetY()
	{
		return targetY;
	}

	public void setAzimuth(double azimuth)
	{
		this.azimuth = azimuth;
	}

	public void setDistanceTraversed(int distanceTraversed)
	{
		this.distanceTraversed = distanceTraversed;
	}

	public void setOriginX(int originX)
	{
		this.originX = originX;
	}

	public void setOriginY(int originY)
	{
		this.originY = originY;
	}

	public void setTargetX(int x)
	{
		this.targetX = x;
	}

	public void setTargetY(int y)
	{
		this.targetY = y;
	}

	public Attack getAttack()
	{
		int d=(int)Math.ceil(basicDamage-distanceTraversed*damageFactor);
		if (d<0) d=0;
		int th=(int)Math.ceil(basicTohit-distanceTraversed*tohitFactor);
		if (effects==null||effectsFactor==null)
			return new Attack(d, damageType, attacker, th);
		int[] new_effects = new int[effects.length]; 
		for (int i=0; i<effects.length; i++)
		{
			new_effects[i]=(int)Math.ceil(effects[i]-distanceTraversed*effectsFactor[i]);
		}
		return new Attack(d, damageType, attacker, th, new_effects);
	}
	
	/**
	 * Calculates azimuth, using 
	 * shot origin and target coordinates
	 * (must be set before calling this method).
	 * 
	 * @param spread spreading in angles.
	 *
	 */
	private void calcAzimuth()
	{
		double angle=0;
		if (targetX==originX)
		{
			if (targetY<=originY) angle=0;
			else angle=180; 
		}
		else if (targetY==originY)
		{
			if (targetX<=originX) angle=90;
			else angle=270; 
		}
		else 
		{
			double tan=(double)(originX-targetX)/(originY-targetY);
			double atan=Math.toDegrees(Math.atan(tan));
			if (targetY>=originY) atan=atan+180;
			else if (targetX>originX) atan=360+atan;
			angle=atan;
		}

		azimuth+=angle;
		if (azimuth<0) azimuth+=360;
		else if (azimuth>360) azimuth-=360;
	}
	
	public void init(int originX, int originY, int targetX, int targetY)
	{
		int tan_mod=1;
		setOriginXY(originX, originY);
		setCurrentXY(originX, originY);
		setTargetXY(targetX, targetY);
		calcAzimuth();
		if (getAzimuth()>45&&getAzimuth()<=135) //W
		{
			trajectoryOrientation=EW;
			sign=-1;
		}
		else if ((getAzimuth()>135&&getAzimuth()<=225)) //S
		{
			trajectoryOrientation=SN;
			sign=1;
			tan_mod=-1;
		}
		else if ((getAzimuth()>225&&getAzimuth()<=315)) //E
		{
			trajectoryOrientation=EW;
			sign=1;
			tan_mod=-1;
		}
		else //N
		{
			trajectoryOrientation=SN;
			sign=-1;
		}
		
		if (trajectoryOrientation==SN)
		{
			deltaerr=-Math.tan(Math.toRadians(azimuth))*tan_mod;
		}else 
		{
			deltaerr=-1/Math.tan(Math.toRadians(azimuth))*tan_mod;
		}
		err=0;
		double unit=Math.sqrt(1+deltaerr*deltaerr);
		deltaerr=deltaerr/unit;
		distanceTraversed=0;
		active=true;
	}
	
	public void iterate()
	{
		if (getAttackCounter()>0) decAttackCounter();
		else 
		{
			fly1Tile();
			if (getCurrentX()<0||getCurrentY()<0||getCurrentX()>=map.getWidth()||getCurrentY()>=map.getHeight()) 
			{
				setActive(false);
				return;
			}
			
			Actor a=map.getActor(getCurrentX(), getCurrentY());
			if (getCurrentX()<0||getCurrentX()>map.getWidth()-1||getCurrentY()<0||getCurrentY()>map.getHeight()-1)
			{
				setActive(false);
				//hits=true;		
			}
			else if(a!=null)
			{
				a.takeAttack(getAttack()); 
				setActive(false);
			}			
			else if (getDistanceTraversed()>=getRange())
				setActive(false);
			else
			{
				if (tileEffect==PtrlConstants.TE_HIT||tileEffect==PtrlConstants.TE_STOP)
					if ((map.getTile(getCurrentX(), getCurrentY()).getHeight()>Math.random()*10
							&&Math.max(Math.abs(getOriginX()-getCurrentX()), Math.abs(getOriginY()-getCurrentY()))>1)
						||map.getTile(getCurrentX(), getCurrentY()).getHeight()>=10) 
					{
						if (tileEffect==PtrlConstants.TE_HIT) 
						{
							map.getTile(getCurrentX(), getCurrentY()).getAttack(getAttack());
							if (map.getTile(getCurrentX(), getCurrentY()).getCurrentDamage()>=map.getTile(getCurrentX(), getCurrentY()).getMaxDamage())
							{
								Item itm=map.getTile(getCurrentX(), getCurrentY()).destroyTile();
								if (itm!=null) map.addItem(itm, getCurrentX(), getCurrentY());
							}
						}
						setActive(false);
					}
			}

		}

	}
	
	public void fly1Tile()
	{
		double dx=1/Math.sqrt(1+deltaerr*deltaerr);
		if (trajectoryOrientation==EW)
		{
			err+=deltaerr;
			if (err>=0.5)
			{
				currentY++;
				err-=1.0;
			}
			else if (err<=-0.5) 
			{
				currentY--;
				err+=1.0;
			}
			currentX+=sign*dx;
		}
		else
		{
			err+=deltaerr;
			if (err>=0.5)
			{
				currentX++;
				err-=1.0;
			}
			else if (err<=-0.5) 
			{
				currentX--;
				err+=1.0;
			}
			currentY+=sign*dx;
		}
		distanceTraversed+=1;
		//System.out.println("traversed="+distanceTraversed+", max="+range); 
	}
	
	public int getCurrentX()
	{
		return (int)Math.round(currentX);
	}

	public int getCurrentY()
	{
		return (int)Math.round(currentY);
	}

	public void setCurrentX(int currentX)
	{
		this.currentX = currentX;
	}

	public void setCurrentY(int currentY)
	{
		this.currentY = currentY;
	}
	
	public void setCurrentXY(int x, int y)
	{
		setCurrentX(x);
		setCurrentY(y);
	}
	
	public void setTargetXY(int x, int y)
	{
		setTargetX(x);
		setTargetY(y);
	}
	
	public void setOriginXY(int x, int y)
	{
		setOriginX(x);
		setOriginY(y);
	}
	
	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
		if (active==false)
		{
			if (damageType==PtrlConstants.DT_TOXIC)
			{
				Tile t=map.getTile(getCurrentX(), getCurrentY());
				if (t!=null) 
					t.pour(2);
			}
		}
	}

	public double getRange()
	{
		return range;
	}
	
	public int getAttackCounter()
	{
		return attackCounter;
	}

	public void setAttackCounter(int attackCounter)
	{
		this.attackCounter = attackCounter;
	}
	
	public void decAttackCounter()
	{
		this.attackCounter--;
	}
	
	public char getSymbol()
	{
		if (this.damageType==PtrlConstants.DT_ELECTRIC)
			return '~';
		else if (getCurrentX()<0||getCurrentX()>map.getWidth()-1||getCurrentY()<0||getCurrentY()>map.getHeight()-1)
			return '.';
		else if (this.damageType==PtrlConstants.DT_GAMMA)
			return (map.getTile(getCurrentX(), getCurrentY()).getSymbol());
		else if (this.damageType==PtrlConstants.DT_MENTAL
				 ||this.damageType==PtrlConstants.DT_THERMAL
				 ||this.damageType==PtrlConstants.DT_TOXIC
				 ||this.damageType==PtrlConstants.DT_EXPLOSION
				 ||this.damageType==PtrlConstants.DT_BLUNT)
			return '*';
		else
		{
			int angle = (int)Math.round(this.azimuth/45);
			if (angle==0||angle==4||angle==8) return '|';
			else if (angle==1||angle==5) return '\\';
			else if (angle==2||angle==6) return '-';
			else return '/';
		}
	}
	
	public short getColor()
	{
		if (this.damageType==PtrlConstants.DT_ELECTRIC)
			return PtrlConstants.LCYAN;
		else if (this.damageType==PtrlConstants.DT_LASER)
			return PtrlConstants.LRED;
		else if (this.damageType==PtrlConstants.DT_THERMAL)
			return PtrlConstants.DRED;
		else if (this.damageType==PtrlConstants.DT_EXPLOSION)
			return PtrlConstants.LYELLOW;
		else if (this.damageType==PtrlConstants.DT_TOXIC)
			return PtrlConstants.LGREEN;
		else if (this.damageType==PtrlConstants.DT_GAMMA)
			return PtrlConstants.WHITE;
		else if (this.damageType==PtrlConstants.DT_PLASMA)
			return PtrlConstants.DGREEN;
		else if (getCurrentX()<0||getCurrentX()>map.getWidth()-1||getCurrentY()<0||getCurrentY()>map.getHeight()-1)
			return PtrlConstants.LGRAY;
		else if (this.damageType==PtrlConstants.DT_MENTAL)
			return map.getTile(getCurrentX(), getCurrentY()).getCurrentColor();
		else 
			return PtrlConstants.LGRAY;
		
	}
	
	private int basicDamage;
	private double damageFactor; //damage penalty per 1 range unit (tile)
	private int damageType;
	private double basicTohit; //assume that we already at tile, where target stands
	private double tohitFactor;
	private int[] effects;
	private float[] effectsFactor;
	
	private Item projectile;
	private Creature attacker;
	private Map map;

	//Trajectory data
	private double distanceTraversed;
	private double range;
	
	private int originX;
	private int originY;
	private int targetX;
	private int targetY;
	private double currentX;
	private double currentY;
	
	private boolean trajectoryOrientation;

	private int sign;
	
	public static final boolean SN=false;	//South-north
	public static final boolean EW=true;	//East-west
	
	private double deltaerr;
	private double err;
	
	private double azimuth;
	
	private boolean active;

	private int attackCounter;

	private int tileEffect;
	
	public Map getMap()
	{
		return map;
	}

	public void setMap(Map map)
	{
		this.map = map;
	}


}
