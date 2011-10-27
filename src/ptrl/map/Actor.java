package ptrl.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import ptrl.combat.Attack;
import ptrl.combat.AttackResult;
import ptrl.combat.Protection;
import ptrl.combat.RangedAttack;
import ptrl.combat.RangedAttackType;
import ptrl.combat.UnarmedAttackType;
import ptrl.creatures.Creature;
import ptrl.util.MapEvent;

public class Actor implements Serializable
{
	public Actor(Creature c, String f, String act)
	{
		fraction = f;
		activity = act;
		creature = c;
		current_path=new int[0][];
	}
	
	/**
	 * Moves actor in direction, given by xy parameter. 
	 * 
	 * @param xy array, that determines direction: xy[0]=dx, xy[1]=dy. 
	 * @return time, taken with movement in milliseconds. Zero if cannot move in this direction.
	 */
	public long move(int[] xy) //returns t in ms
	{
		int x=creature.getX()+xy[0];
		int y=creature.getY()+xy[1];
		if (xy[0]==0&&xy[1]==0) 
		{
			if (time_tail>=1000)
			{
				time_tail-=1000;
				return 1000;
			}
			else 
			{
				long tt=time_tail;
				time_tail=0;
				return tt;
			}
		}
		if (x<0||x>=map.getWidth()||y<0||y>map.getHeight())
		{
			if (time_tail>=1000)
			{
				time_tail-=1000;
				return 1000;
			}
			else 
			{
				long tt=time_tail;
				time_tail=0;
				return tt;
			}
		}
		if (map.getActor(x, y)!=null)
		{
			if (time_tail>=1000)
			{
				time_tail-=1000;
				return 1000;
			}
			else 
			{
				long tt=time_tail;
				time_tail=0;
				return tt;
			}
		}
		int tm=this.getTile(x, y).getTimeMultiplier();
		if (tm>0)
		{
			long t=creature.get1sqTime(xy, tm);
			if (t<=time_tail) 
			{
				time_tail-=t;
				return creature.move1sq(xy, tm);
			}
			else return 0;
		}
		else 
		{
			if (time_tail>=1000)
			{
				time_tail-=1000;
				return 1000;
			}
			else 
			{
				long tt=time_tail;
				time_tail=0;
				return tt;
			}
		}
	}
	
	public long open(int[] xy)
	{
		int x=creature.getX()+xy[0];
		int y=creature.getY()+xy[1];
		if (time_tail<1000)
		{
			return 0;
		}
		if (x<0||x>=map.getWidth()||y<0||y>map.getHeight()) return 0;
		if (map.getActor(x, y)!=null) return 0;
		DoorTile dtl=null;
		Tile tl=this.getTile(x, y);
		if (tl instanceof DoorTile)
		{
			dtl=(DoorTile)tl;
		}
		else return 0;
		if (dtl.isOpen()||(xy[0]==0&&xy[1]==0))
		{
			time_tail-=1000;
			return 1000;
		}
		int t=1000;
		dtl.open();
		time_tail-=t;
		return 1000;
	}
	
	public Creature getCreature()
	{
		return creature;
	}
	
	public String getFraction()
	{
		return fraction;
	}
	
	public void startTurn(long t)
	{
		//setTurnFinished(false);
		old_tt=time_tail;
		time_tail+=t;
		try
		{
			continueTurn();
		}
		catch (Exception e)
		{
			System.out.println("!!!"+getCreature().getName()+", xy=("+getCreature().getX()+", "+getCreature().getY()+")");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public boolean continueTurn()  //t in ms
	{
		boolean havetime=true;
		boolean turndone=false;
		turnInterrupt=false;
		//System.out.println("activity: "+activity);
		while (havetime&&!turnInterrupt)
		{
			System.out.println(activity+", dest x: "+dest_x+", dest y: "+dest_y);
			System.out.println("time_tail="+time_tail);

			if (activity=="aimless")
			{
				havetime=aimless();
			}
			else if (activity=="attack")
			{
				havetime=attackDest();
			}
			else if (activity=="none")
			{
				havetime=none();
			}
			if (havetime) turndone=true;
		}
		if (turndone&&!turnInterrupt) 
		{
			time_tail=Math.min(time_tail, old_tt);
		}
		return turndone;
	}
	
	//move aimlessly
	public boolean aimless() //t in ms
	{
		if (getNearestEnemy())
		{ 
			activity="attack";
			return attackDest();
		}
			
		int[]xy = {0, 0}; 
		int dir=0;
		dir=(int)Math.round(0.5+Math.random()*9);
		if (dir==7||dir==8||dir==9)
		{
			xy[1]=-1;
		}
		else if (dir==1||dir==2||dir==3)
		{
			xy[1]=1;
		}
		
		if (dir==7||dir==4||dir==1)
		{
			xy[0]=-1;
		}
		else if (dir==9||dir==6||dir==3)
		{
			xy[0]=1;
		}
		if (getCreature().getX()+xy[0]<0
				||getCreature().getX()+xy[0]>=map.getWidth()
				||getCreature().getY()+xy[1]<0
				||getCreature().getY()+xy[1]>=map.getHeight())
			return true;
		if (move(xy)!=0) return true;
		else
		{
			return false; 
		}
	}
	
	//wait for events
	public boolean none()
	{
		if (time_tail<1000) return false;
		move(new int[]{0,0});
		return true;
	}

	//
	public boolean attackDest()
	{
		boolean path_is_found=false;

		if (!getNearestEnemy()) //&&
		{
			if (getCreature().getX()==dest_x&&getCreature().getY()==dest_y)
			{
				activity="aimless";
				return none();
			}
			else
			{
				if (current_path.length==0)
				{
					if (findStraightPath(dest_x, dest_y)) path_is_found=true;
					else if (findWavePath(dest_x, dest_y)) path_is_found=true;
					path_counter=0;

					if (path_is_found)
					{
						Tile tl=this.getTile(getCreature().getX()+current_path[0][0], getCreature().getY()+current_path[0][1]);
						if (tl instanceof DoorTile)
						{
							DoorTile dtl=(DoorTile)tl;
							if (!dtl.isOpen()) 
							{
								long t=open(current_path[0]);
								if (t==0) return false;
								else return true;
							}
						}

						if (move(current_path[0])!=0) 
						{
							return true;
						}
						else
						{
							return false; 
						}
					}else 
					{
						activity="aimless";
						return none();
					}
				}
				else
				{
					System.out.println("Path is already found");
					
					if (path_counter>=current_path.length)
					{
						System.out.println("path_counter>=current_path.length");
						activity="aimless";
						return none();
					}
					int[] xy=current_path[path_counter];
					Tile tl=this.getTile(getCreature().getX()+xy[0], getCreature().getY()+xy[1]);
					if (tl instanceof DoorTile)
					{
						DoorTile dtl=(DoorTile)tl;
						if (!dtl.isOpen())
						{
							System.out.println("closed door("+xy[0]+", "+xy[1]+")");
							long t=open(current_path[0]);
							if (t==0) return false;
							else return true;
						}
					}
					if (move(xy)!=0) 
					{
						System.out.println("move("+xy[0]+", "+xy[1]+")");
						path_counter++;
						return true;
					}
					else
					{
						System.out.println("not move("+xy[0]+", "+xy[1]+")");
						return false; 
					}
				}
			}
		}
		int dx=dest_x-getCreature().getX();
		int dy=dest_y-getCreature().getY();
//		System.out.println("x="+getCreature().getX());
//		System.out.println("y="+getCreature().getY());
//		System.out.println("dest_x="+dest_x);
//		System.out.println("dest_y="+dest_y);
		if (setAviableRangedAttack(dest_x, dest_y))
		{
			if (getCreature().getCurrentRangedAttackType().getTime()*1000<=time_tail) 
			{
				rangedAttack(dest_x, dest_y);
				return true;
			}
			else return false;
		}
		else if (!setAviableCloseAttack()) return none();
		else if (dx>=-1&&dx<=1&&dy>=-1&&dy<=1)
		{
			long t=getCreature().getCurrentCloseAttackType().getTime()*1000;
			closeAttack(new int[]{dx, dy});
			time_tail-=t;
			return true;
		}
		
		if (findStraightPath(dest_x, dest_y)) path_is_found=true;
		else if (findWavePath(dest_x, dest_y)) path_is_found=true;
		path_counter=0;

		if (path_is_found)
		{
			//demoPath(1);
			if (move(current_path[0])!=0) return true;
			else
			{
				//path_counter++;
				return false; 
			}
		}else 
		{
			activity="aimless";
			return none();
		}
	}
	
	
	
	/**
	 * Finds a shortest path to the point (dx, dy)
	 * using the wave algorithm,  
	 * and fills the path matrix current_path[].
	 * @param dx x coord of destination 
	 * @param dy y coord of destination 
	 * @returns true if path is found
	 */
	public boolean findWavePath(int dx, int dy)
	{
		System.out.println("findWavePath");
		//wave map
		int sx=getCreature().getX();
		int sy=getCreature().getY();
		boolean path_is_found=false;
		//boolean cannot_found;
		int wave=0;
		int maxwave=1;
		//int path_length=0;
		int[][] wmap=new int[map.getWidth()][map.getHeight()];
		for (int i=0; i<wmap.length; i++) Arrays.fill(wmap[i], -1);
		wmap[sx][sy]=0;
		//System.out.println("dx: "+dx);
		//System.out.println("dy: "+dy);	
		//Toolkit.readCharacter();		
		do 
		{
			search:				
			for (int x=0; x<map.getWidth(); x++)
				for (int y=0; y<map.getHeight(); y++)
				{
					if (x==dx&&y==dy&&wmap[x][y]==wave) 
					{
						path_is_found=true;
						//System.out.println("Path is found." );
						//System.out.println("x: "+x);
						//System.out.println("y: "+y);								
						//Toolkit.readCharacter();	
						break search;
					}
					if (wmap[x][y]==wave)
					{
						if (x<map.getWidth()-1&&this.getTile(x+1,y).getPathTM()!=0&&wmap[x+1][y]==-1)
						{
							wmap[x+1][y]=wave+this.getTile(x+1,y).getPathTM();
							if (wmap[x+1][y]>maxwave) maxwave=wmap[x+1][y];
						}
						if (x>0&&this.getTile(x-1,y).getPathTM()!=0&&wmap[x-1][y]==-1) 
						{
							wmap[x-1][y]=wave+this.getTile(x-1,y).getPathTM();
							if (wmap[x-1][y]>maxwave) maxwave=wmap[x-1][y];
						}
						if (y<map.getHeight()-1&&this.getTile(x,y+1).getPathTM()!=0&&wmap[x][y+1]==-1) 
						{
							wmap[x][y+1]=wave+this.getTile(x,y+1).getPathTM();
							if (wmap[x][y+1]>maxwave) maxwave=wmap[x][y+1];
						}
						if (y>0&&this.getTile(x,y-1).getPathTM()!=0&&wmap[x][y-1]==-1) 
						{
							wmap[x][y-1]=wave+this.getTile(x,y-1).getPathTM();
							if (wmap[x][y-1]>maxwave) maxwave=wmap[x][y-1];
						}
						if (x<map.getWidth()-1&&y<map.getHeight()-1&&this.getTile(x+1,y+1).getPathTM()!=0&&wmap[x+1][y+1]==-1) 
						{
							wmap[x+1][y+1]=wave+(int)(this.getTile(x+1,y+1).getPathTM()/0.7);
							if (wmap[x+1][y+1]>maxwave) maxwave=wmap[x+1][y+1];
						}
						if (x>0&&y<map.getHeight()-1&&this.getTile(x-1,y+1).getPathTM()!=0&&wmap[x-1][y+1]==-1) 
						{
							wmap[x-1][y+1]=wave+(int)(this.getTile(x-1,y+1).getPathTM()/0.7);
							if (wmap[x-1][y+1]>maxwave) maxwave=wmap[x-1][y+1];
						}
						if (x<map.getWidth()-1&&y>0&&this.getTile(x+1,y-1).getPathTM()!=0&&wmap[x+1][y-1]==-1) 
						{
							wmap[x+1][y-1]=wave+(int)(this.getTile(x+1,y-1).getPathTM()/0.7);
							if (wmap[x+1][y-1]>maxwave) maxwave=wmap[x+1][y-1];
						}
						if (x>0&&y>0&&this.getTile(x-1,y-1).getPathTM()!=0&&wmap[x-1][y-1]==-1) 
						{
							wmap[x-1][y-1]=wave+(int)(this.getTile(x-1,y-1).getPathTM()/0.7);	
							if (wmap[x-1][y-1]>maxwave) maxwave=wmap[x-1][y-1];
						}
					}
				}
			//System.out.println("wave: "+wave+"; maxwave: "+maxwave);
			//path_length++;
		} while (wave++<=maxwave&&!path_is_found);
		if (!path_is_found) return false;
		ArrayList path = new ArrayList();
		int x = dx;
		int y = dy;
		int i = 0;
		do
		{
			//find a tile around with smallest wave sign 
			int offs_x=0;
			int offs_y=0;
			int minwave=wmap[x][y];
			//System.out.println("x: "+x+"; y: "+y+"; minwave: "+minwave);
			//Toolkit.readCharacter();
			for (int ox=-1; ox<=1; ox++)
				for (int oy=-1; oy<=1; oy++)
				{	if (x+ox>=0&&y+oy>=0&&x+ox<map.getWidth()&&y+oy<map.getHeight())
						if (wmap[x+ox][y+oy]<minwave&&wmap[x+ox][y+oy]!=-1)
						{	
							try
							{
								minwave=wmap[x+ox][y+oy];
								offs_x=-ox;
								offs_y=-oy;
							}	
							catch (ArrayIndexOutOfBoundsException e)
							{
								System.out.println("FindPath problem");
								System.out.println("i:"+i+"; x: "+x+"; y: "+y);
								//Toolkit.readCharacter();
							}
						}	
				}

			path.add(new int[]{offs_x, offs_y});
			x-=offs_x;		
			y-=offs_y;
			i++;				
		}while(x!=sx||y!=sy);
		current_path = new int [path.size()][2];
		for (int j=path.size(); j>0; j--)
		{
			current_path[path.size()-j]=(int[])path.get(j-1);
		}
		return true;
	}
	
	public boolean findStraightPath(int dx, int dy)
	{
		int x1=getCreature().getX();
		int y1=getCreature().getY();
		int x2=dx;
		int y2=dy;
		int y=y1;
		int x=x1;
		int prev_x;
		int prev_y;
		float ret=0;
		int[][] new_path = new int[Math.max(Math.abs(x2-x1), Math.abs(y2-y1))+1][2];
		int i=0;
		//if (x1==x2&&y1==y2) return getCreature().getFOVRange();
		if (Math.abs(x2-x1)>=Math.abs(y2-y1))
		{
			float deltaerr=(float)(y2-y1)/Math.abs(x2-x1); //(Math.abs(dy-py))/(Math.abs(dx-px));
			float err=0;
			int sgn=1;
			if (x1>x2) sgn=-1;
			for (x=x1; sgn*x<=sgn*x2; x+=sgn)
			{
				prev_x=x;
				prev_y=y;
				err+=deltaerr;
				if (err>=0.5)
				{
					y++;
					err-=1.0;
				}
				else if (err<=-0.5) 
				{
					y--;
					err+=1.0;
				}
				if (x!=x1||y!=y1)
				{
					int tm=this.getTile(x, y).getTimeMultiplier();
					if (tm==0||((x!=x2||y!=y2)&&map.getActor(x, y)!=null)) 
					{
						/*System.out.println("findStraightPath("+x2+", "+y2+") error");
						System.out.println("deltaerr="+deltaerr);
						System.out.println("x="+x+", y="+y);*/
						return false;
					}
				}
				new_path[i][0]=sgn;
				new_path[i][1]=y-prev_y;
				i++;
			}
		}else
		{
			//System.out.println("horiz");
			float deltaerr=(float)(x2-x1)/Math.abs(y2-y1); //(Math.abs(dy-py))/(Math.abs(dx-px));
			float err=0;
			int sgn=1;
			if (y1>y2) sgn=-1;
			for (y=y1; sgn*y<=sgn*y2; y+=sgn)
			{
				prev_x=x;
				prev_y=y;
				err+=deltaerr;
				if (err>=0.5)
				{
					x++;
					err-=1.0;
				}
				else if (err<=-0.5) 
				{
					x--;
					err+=1.0;
				}
				if (x!=x1||y!=y1)
				{
					int tm=this.getTile(x, y).getTimeMultiplier();
					if (tm==0||((x!=x2||y!=y2)&&map.getActor(x, y)!=null))
					{
						/*System.out.println("findStraightPath("+x2+", "+y2+") error");
						System.out.println("deltaerr="+deltaerr);
						System.out.println("x="+x+", y="+y);*/
						return false;
					}
				}
				new_path[i][0]=x-prev_x;
				new_path[i][1]=sgn;
				i++;
			}
		}
		current_path=new_path;
		return true;
	}
	
	/**
	 * Calculates a 360-degrees field of view. 
	 */
	public void calc360Fov()
	{
		int px=creature.getX();
		int py=creature.getY();
		int mw=map.getWidth();
		int mh=map.getHeight();
		int l=getCreature().getFOVRange();
		//System.out.println("px:"+px+"; py:"+py+"; l:"+l);
		int ffov[][] = new int[mw][mh];
		for (int i=0; i<ffov.length; i++)
			for (int j=0; j<ffov[0].length; j++)
				{ffov[i][j]=-1;}
		

		ffov[px][py]=l; //tile where actor stands
		/*
		 4 3
		5   2
		6   1
		 7 8
		 	
		*/
		//-----------Octant 1-------------
		for (int i=0; i<=l; i++)
		{
			int dx=px+l;
			//int dy=py+i;
			float deltaerr=(float)i/l;//(Math.abs(dy-py))/(Math.abs(dx-px));
			float err=0;
			float deltaviz=(float)Math.sqrt(1+deltaerr*deltaerr);
			//System.out.println("de="+deltaerr);
			float viz=l;
			int y=py;
			for (int x=px; x<=dx; x++)
			{
				if (x<0||y<0||x>mw-1||y>mh-1) continue;
				if (ffov[x][y]<viz)ffov[x][y]=(int)Math.floor(viz);//if (ffov[x][y]<viz)ffov[x][y]=viz;
				int h=getTile(x, y).getHeight();
				if (h>=10) viz=0;
				else if (x<=px+1) viz=viz-deltaviz; 
				else viz=viz-deltaviz-h;
				err+=deltaerr;
				if (err>=0.5)
				{
					y++;
					err-=1.0;
				}
			}
		} 

		//-----------Octant 2-------------
		for (int i=0; i<=l; i++)
		{
			int dx=px+l;
			//int dy=py-i;
			float deltaerr=(float)i/l;//(Math.abs(dy-py))/(Math.abs(dx-px));
			float err=0;
			float deltaviz=(float)Math.sqrt(1+deltaerr*deltaerr);
			float viz=l;
			int y=py;
			for (int x=px; x<=dx; x++)
			{
				if (x<0||y<0||x>mw-1||y>mh-1) continue;
				if (ffov[x][y]<viz)ffov[x][y]=(int)Math.floor(viz);//if (ffov[x][y]<viz)ffov[x][y]=viz;
				int h=getTile(x, y).getHeight();
				if (h>=10) viz=0;
				else if (x<=px+1) viz=viz-deltaviz; 
				else viz=viz-deltaviz-h;
				err+=deltaerr;
				if (err>=0.5)
				{
					y--;
					err-=1.0;
				}
			}
		} 


		//-----------Octant 6-------------
		for (int i=0; i<=l; i++)
		{
			int dx=px-l;
			//int dy=py+i;
			float deltaerr=(float)i/l;//(Math.abs(dy-py))/(Math.abs(dx-px));
			float err=0;
			float deltaviz=(float)Math.sqrt(1+deltaerr*deltaerr);
			float viz=l;
			int y=py;
			for (int x=px; x>=dx; x--)
			{
				if (x<0||y<0||x>mw-1||y>mh-1) continue;
				if (ffov[x][y]<viz)ffov[x][y]=(int)Math.floor(viz);//if (ffov[x][y]<viz)ffov[x][y]=viz;
				int h=getTile(x, y).getHeight();
				if (h>=10) viz=0;
				else if (x>=px-1) viz=viz-deltaviz; 
				else viz=viz-deltaviz-h;
				err+=deltaerr;
				if (err>=0.5)
				{
					y++;
					err-=1.0;
				}
			}
		} 

		//-----------Octant 5-------------
		for (int i=0; i<=l; i++)
		{
			int dx=px-l;
			//int dy=py-i;
			float deltaerr=(float)i/l;//(Math.abs(dy-py))/(Math.abs(dx-px));
			float err=0;
			float deltaviz=(float)Math.sqrt(1+deltaerr*deltaerr);
			float viz=l;
			int y=py;
			for (int x=px; x>=dx; x--)
			{
				if (x<0||y<0||x>mw-1||y>mh-1) continue;
				if (ffov[x][y]<viz)ffov[x][y]=(int)Math.floor(viz);//if (ffov[x][y]<viz)ffov[x][y]=viz;
				int h=getTile(x, y).getHeight();
				if (h>=10) viz=0;
				else if (x>=px-1) viz=viz-deltaviz; 
				else viz=viz-deltaviz-h;
				err+=deltaerr;
				if (err>=0.5)
				{
					y--;
					err-=1.0;
				}
			}
		} 

		//-----------Octant 8-------------
		for (int i=0; i<=l; i++)
		{
			//int dx=px+i;
			int dy=py+l;
			float deltaerr=(float)i/l;//(Math.abs(dx-px))/(Math.abs(dy-py));
			float err=0;
			float deltaviz=(float)Math.sqrt(1+deltaerr*deltaerr);
			float viz=l;
			int x=px;
			for (int y=py; y<=dy; y++)
			{
				if (y<0||x<0||x>mw-1||y>mh-1) continue;
				if (ffov[x][y]<viz)ffov[x][y]=(int)Math.floor(viz);//if (ffov[x][y]<viz)ffov[x][y]=viz;
				int h=getTile(x, y).getHeight();
				if (h>=10) viz=0;
				else if (y<=py+1) viz=viz-deltaviz; 
				else viz=viz-deltaviz-h;
				err+=deltaerr;
				if (err>=0.5)
				{
					x++;
					err-=1.0;
				}
			}
		} 

		//-----------Octant 7-------------
		for (int i=0; i<=l; i++)
		{
			//int dx=px-i;
			int dy=py+l;
			float deltaerr=(float)i/l;//(Math.abs(dx-px))/(Math.abs(dy-py));
			float err=0;
			float deltaviz=(float)Math.sqrt(1+deltaerr*deltaerr);
			float viz=l;
			int x=px;
			for (int y=py; y<=dy; y++)
			{
				if (y<0||x<0||x>mw-1||y>mh-1) continue;
				if (ffov[x][y]<viz)ffov[x][y]=(int)Math.floor(viz);//if (ffov[x][y]<viz)ffov[x][y]=viz;
				int h=getTile(x, y).getHeight();
				if (h>=10) viz=0;
				else if (y<=py+1) viz=viz-deltaviz; 
				else viz=viz-deltaviz-h;
				err+=deltaerr;
				if (err>=0.5)
				{
					x--;
					err-=1.0;
				}
			}
		} 
		
		//-----------Octant 3-------------
		for (int i=0; i<=l; i++)
		{
			//int dx=px+i;
			int dy=py-l;
			float deltaerr=(float)i/l;//(Math.abs(dx-px))/(Math.abs(dy-py));
			float err=0;
			float deltaviz=(float)Math.sqrt(1+deltaerr*deltaerr);
			float viz=l;
			int x=px;
			for (int y=py; y>=dy; y--)
			{
				if (y<0||x<0||x>mw-1||y>mh-1) continue;
				if (ffov[x][y]<viz)ffov[x][y]=(int)Math.floor(viz);//if (ffov[x][y]<viz)ffov[x][y]=viz;
				int h=getTile(x, y).getHeight();
				if (h>=10) viz=0;
				else if (y>=py-1) viz=viz-deltaviz; 
				else viz=viz-deltaviz-h;
				err+=deltaerr;
				if (err>=0.5)
				{
					x++;
					err-=1.0;
				}
			}
		} 

		//-----------Octant 4-------------
		for (int i=0; i<=l; i++)
		{
			//int dx=px-i;
			int dy=py-l;
			float deltaerr=(float)i/l;//(Math.abs(dx-px))/(Math.abs(dy-py));
			float err=0;
			float deltaviz=(float)Math.sqrt(1+deltaerr*deltaerr);
			float viz=l;
			int x=px;
			for (int y=py; y>=dy; y--)
			{
				if (y<0||x<0||x>mw-1||y>mh-1) continue;
				if (ffov[x][y]<viz)ffov[x][y]=(int)Math.floor(viz);//if (ffov[x][y]<viz)ffov[x][y]=viz;
				int h=getTile(x, y).getHeight();
				if (h>=10) viz=0;
				else if (y>=py-1) viz=viz-deltaviz; 
				else viz=viz-deltaviz-h;
				err+=deltaerr;
				if (err>=0.5)
				{
					x--;
					err-=1.0;
				}
			}
		} 			
		fov=new int[2*l+1][2*l+1];
		//System.out.println("dim:"+(2*l+1));
		//Toolkit.readCharacter();			
		for (int i=0; i<fov.length; i++)
			for (int j=0; j<fov[0].length; j++)
				{
					if (px-l+i<0||py-l+j<0||px-l+i>mw-1||py-l+j>mh-1) continue;
					int v=ffov[px-l+i][py-l+j];
					if (v>=0) fov[i][j]=v;
					else fov[i][j]=0;
				}

		
	}
	
	public int[][] getFov()
	{
		return fov;
	}
	
	public AttackResult takeAttack(Attack a)
	{
		Protection p = getCreature().getProtection();
		if (!p.isHit(a)) return null;
		else 
		{
			AttackResult ar=new AttackResult(this.getCreature(), p.getAttackDamage(a), getCreature().getX(),  getCreature().getY());
			getCreature().damage(ar.getDamage());
			getCreature().takeEvent(new MapEvent("hit", MapEvent.T_UNDER_ATTACK, ar.getDamage(), getCreature().getX(), getCreature().getY(), a.getAttacker()));
			Creature attacker = a.getAttacker();
			if (attacker!=null&&getCreature().getHP()+ar.getDamage()>0) 
				attacker.takeEvent(new MapEvent("you_hit", MapEvent.T_UNDER_ATTACK, ar.getDamage(), getCreature().getX(), getCreature().getY(), getCreature())); 
			if (getCreature().getHP()<=0) 
			{
				map.getTile(getCreature().getX(), getCreature().getY()).pour(1);
				map.deathEvent();
				getCreature().takeEvent(new MapEvent("death", MapEvent.T_UNDER_ATTACK, 0, getCreature().getX(), getCreature().getY(), a.getAttacker()));
				if (getCreature().getHP()+ar.getDamage()>0) attacker.takeEvent(new MapEvent("you_kill", MapEvent.T_UNDER_ATTACK, ar.getDamage(), getCreature().getX(), getCreature().getY(), getCreature())); 
			}
			return ar; 
		}
	}
	
	public AttackResult[] closeAttack (int[] xy)
	{
		
		Creature c = getCreature();
		Actor target = map.getActor(c.getX()+xy[0], c.getY()+xy[1]);
		if (target==null) return new AttackResult[0];
		AttackResult[] ar = new AttackResult[c.getCurrentCloseAttackType().getAttacksN()];
		for (int i=0; i<ar.length; i++)
		{
			ar[i]=target.takeAttack(c.getCurrentCloseAttackType().getAttack());
			if (ar[i]==null) target.getCreature().takeEvent(new MapEvent("miss", MapEvent.T_UNDER_ATTACK, 0, c.getX(), c.getY(), this.getCreature()));
			//else target.getCreature().takeEvent(new MapEvent("hit", MapEvent.T_UNDER_ATTACK, ar[i].getDamage(), c.getX(), c.getY(), this));
		}
		return ar; 
	}
	
	public void rangedAttack (int x, int y)
	{
		if (getCreature().getCurrentRangedAttackType()==null) return; 
		RangedAttack[] ret = getCreature().getCurrentRangedAttackType().getShotRangedAttacks(getCreature());
		for (int i=0; i<ret.length; i++)
		{
			ret[i].init(getCreature().getX(), getCreature().getY(), x, y);
			map.addProjectile(ret[i]);
		}
		time_tail-=getCreature().getCurrentRangedAttackType().getTime()*1000;
		turnInterrupt=true;
	}
	
	public boolean setAviableCloseAttack()
	{
		UnarmedAttackType[] ars=getCreature().getCloseAttackTypes();
		for (int i=0; i<ars.length; i++)
		{
			if (ars[i].getTime()*1000<=time_tail) 
			{
				getCreature().setCurrentCloseAttackN(i);
				return true;
			}
		}
		return false;
	}
	
	public boolean setAviableRangedAttack(int tx, int ty)
	{
		RangedAttackType[] ras=getCreature().getRangedAttackTypes();	
		if (ras==null) return false;
		else if (ras.length==0) return false;
		for (int i=0; i<ras.length; i++)
		{
			double d=Math.sqrt((getCreature().getX()-tx)*(getCreature().getX()-tx)+(getCreature().getY()-ty)*(getCreature().getY()-ty));
			//System.out.println("d="+d);
			//System.out.println("Range="+ras[i].getRange());
			if (d<=ras[i].getRange()||ras[i].getRange()==-1) 
			{
				getCreature().setCurrentRangedAttackN(i);
				
				System.out.println("ready to fire");
				return true;
			}
		}
		return false;
	}
	
	public int seeActor(Actor a)
	{
		int x1=getCreature().getX();
		int y1=getCreature().getY();
		int x2=a.getCreature().getX();
		int y2=a.getCreature().getY();
		int y=y1;
		int x=x1;
		float viz=getCreature().getFOVRange();
		float ret=0;
		if (x1==x2&&y1==y2) return getCreature().getFOVRange();
		if (Math.abs(x2-x1)>=Math.abs(y2-y1))
		{
			float deltaerr=(float)(y2-y1)/Math.abs(x2-x1); //(Math.abs(dy-py))/(Math.abs(dx-px));
			float err=0;
			float deltaviz=(float)Math.sqrt(1+deltaerr*deltaerr);
			int sgn=1;
			if (x1>x2) sgn=-1;
			for (x=x1; sgn*x<=sgn*x2; x+=sgn)
			{
				ret=viz;
				int h=this.getTile(x, y).getHeight();
				if (h>=10) viz=0;
				else if (sgn*x<=sgn*(x1+1)) viz=viz-deltaviz; 
				else viz=viz-deltaviz-h;
				err+=deltaerr;
				if (err>=0.5)
				{
					y++;
					err-=1.0;
				}
				else if (err<=-0.5) 
				{
					y--;
					err+=1.0;
				}
			}
		}else
		{
			//System.out.println("horiz");
			float deltaerr=(float)(x2-x1)/Math.abs(y2-y1); //(Math.abs(dy-py))/(Math.abs(dx-px));
			float err=0;
			float deltaviz=(float)Math.sqrt(1+deltaerr*deltaerr);
			viz+=deltaviz;
			int sgn=1;
			if (y1>y2) sgn=-1;
			for (y=y1; sgn*y<=sgn*y2; y+=sgn)
			{
				ret=viz;
				int h=this.getTile(x, y).getHeight();
				if (h>=10) viz=0;
				else if (sgn*y<=sgn*(y1+1)) viz=viz-deltaviz; 
				else viz=viz-deltaviz-h;
				err+=deltaerr;
				if (err>=0.5)
				{
					x++;
					err-=1.0;
				}
				else if (err<=-0.5) 
				{
					x--;
					err+=1.0;
				}
			}
		}
		if (ret<=0) return 0;
		else return (int)Math.floor(ret);
	}
	
	public ArrayList<Actor> getVisibleActors()
	{
		int x=getCreature().getX();
		int y=getCreature().getY();
		int l=getCreature().getFOVRange();
		Actor[] a = map.getActorsInRect(x-l, x+l, y-l, y+l);
		ArrayList visible = new ArrayList();
		for (int i=0; i<a.length; i++)
		{
			if (seeActor(a[i])>0&&a[i].getCreature()!=getCreature()) visible.add(a[i]);
		}
		return visible;
	}
	
	public boolean getNearestEnemy()
	{
		ArrayList<Actor> arr=getVisibleActors();
		Iterator<Actor> i = arr.iterator();
		while (i.hasNext())
		{
			Actor a=i.next();
			if (a.getFraction()=="player")
			{
				dest_x=a.getCreature().getX();
				dest_y=a.getCreature().getY();
				//System.out.println("See the player: x="+dest_x+", y="+dest_y+";");
				return true;
			}
		}
		return false;
	}
	
	public Map getMap()
	{
		return map;
	}

	protected void setMap(Map map)
	{
		this.map = map;
	}
	
	protected void setTilemap(Tile[][] tilemap)
	{
		this.tilemap = tilemap;
	}
	
	private Tile getTile(int x, int y)
	{
		if (x<0||x>=map.getWidth()||y<0||y>=map.getHeight()) return null;
		Tile t = this.tilemap[x][y];
		return t;
	}
	
	public boolean turnInterrupted()
	{
		return turnInterrupt;
	}

	public void finishTurn()
	{
		turnInterrupt=false;
	}
	
	private String fraction; //eg. citizens, monsters, raiders, player
	private String activity;
	private Creature creature;
	private long time_tail;
	private long old_tt;
	private int[][] current_path;
	private int path_counter;
	private int dest_x;
	private int dest_y;	
	private int[][] fov;
	private Map map;
	private Tile[][] tilemap;
	private boolean turnInterrupt; //animation is needed, e.g. shot


}//Actor class	

