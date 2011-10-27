package ptrl.combat;

import java.awt.Color;

public class PtrlConstants
{
	//Effects
	public static final int E_STUN=0;		//Stun (time)
	public static final int E_PUSH=1; 		//Push (<0 - throw back) - distance/tiles
	public static final int E_POISON=2;		//Qty.
	public static final int E_DEMORALIZE=3;	//Str of effect	
	public static final int E_BLIND=4;		//Time
	public static final int E_DEAF=5;		//Time
	
	//Tile effects
	public static final int TE_BOUNCE=0;		
	public static final int TE_HIT=1;			//attack tile
	public static final int TE_STOP=2;			//destroy projectile
	public static final int TE_IGNORE=3;		//fly over
	
	//Damage types
	public static final int DT_NORMAL=0;
	public static final int DT_AP=1;
	public static final int DT_BLUNT=2;
	public static final int DT_EXPLOSION=3;
	public static final int DT_THERMAL=4;
	public static final int DT_LASER=5;
	public static final int DT_PLASMA=6;
	public static final int DT_ELECTRIC=7;
	public static final int DT_GAMMA=8;
	public static final int DT_TOXIC=9;
	public static final int DT_MENTAL=10;
	
	public static final String[] DAMAGE_TYPE_NAMES= new String[]{"normal",
												"ap",
												"blunt",
												"explosion",
												"thermal",
												"laser",
												"plasma",
												"electric",
												"gamma",
												"toxic",
												"mental"};
	/**
	  Black color.
	 */
	 public static final short BLACK = 0;//Color.BLACK;
	/**
	  Dark Blue color.
	 */
	 public static final short DBLUE = 1; //new Color(0, 0, 127);
	/**
	  Dark Cyan color.
	 */
	 public static final short DCYAN = 2; //new Color(0, 127, 127);
	/**
	  Dark Green color.
	 */
	 public static final short DGREEN = 3; //new Color(0, 127, 0);
	/**
	  Dark Magenta color.
	 */
	 public static final short DMAGENTA = 4; //new Color(127, 0, 127);
	/**
	  Dark Red color.
	 */
	 public static final short DRED = 5; //new Color(127, 0, 0);
	/**
	  Dark Yellow color.
	 */
	 public static final short DYELLOW = 6; //new Color(127, 64, 0);
	/**
	  Light Gray color.
	 */
	 public static final short LGRAY = 7; //Color.GRAY;
	/**
	  Dark Gray color. (bold black)
	 */
	 public static final short DGRAY = 8; //Color.DARK_GRAY;
	/**
	  Light Blue color.
	 */
	 public static final short LBLUE = 9; //Color.BLUE;
	/**
	  Light Cyan color.
	 */
	 public static final short LCYAN = 10; //Color.CYAN;
	/**
	  Light Green color.
	 */
	 public static final short LGREEN = 11; //Color.GREEN;
	/**
	  Light Magenta color.
	 */
	 public static final short LMAGENTA = 12; //Color.MAGENTA;
	/**
	  Light Red color.
	 */
	 public static final short LRED = 13; //Color.RED;
	/**
	  Light Yellow color.
	 */
	 public static final short LYELLOW = 14; //Color.YELLOW;
	/**
	  White color. (bold white)
	 */
	 public static final short WHITE = 15; //Color.WHITE;

	 public static final short strToColor(String str)
	 {
		    if (str==null) return BLACK;
		    if (str.equalsIgnoreCase("")) return BLACK;

		    if (str.equalsIgnoreCase("black")) return BLACK;
		    else if (str.equalsIgnoreCase("dblue")) return DBLUE;
		    else if (str.equalsIgnoreCase("dcyan")) return DCYAN;
		    else if (str.equalsIgnoreCase("dgreen")) return DGREEN;
		    else if (str.equalsIgnoreCase("dmagenta")) return DMAGENTA;
		    else if (str.equalsIgnoreCase("dred")) return DRED;
		    else if (str.equalsIgnoreCase("dyellow")||str.equalsIgnoreCase("brown")) return DYELLOW;
		    else if (str.equalsIgnoreCase("lgray")) return LGRAY;
		    	
		    else if (str.equalsIgnoreCase("dgray")) return DGRAY;
		    else if (str.equalsIgnoreCase("lblue")) return LBLUE;
		    else if (str.equalsIgnoreCase("lcyan")) return LCYAN;
		    else if (str.equalsIgnoreCase("lgreen")) return LGREEN;
		    else if (str.equalsIgnoreCase("lmagenta")) return LMAGENTA;
		    else if (str.equalsIgnoreCase("lred")) return LRED;
		    else if (str.equalsIgnoreCase("lyellow")) return LYELLOW;
		    else if (str.equalsIgnoreCase("white")) return WHITE;
		    return BLACK;
	 }
	 
	 public static final int strToDamageType(String str)
	 {
		 for (int i = 0; i < PtrlConstants.DAMAGE_TYPE_NAMES.length; i++)
			if (str.equalsIgnoreCase(PtrlConstants.DAMAGE_TYPE_NAMES[i]))
				return i;
	 	return DT_NORMAL;
	 }
}
