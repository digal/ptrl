package ptrl.map;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.sun.jndi.url.corbaname.corbanameURLContextFactory;

import ptrl.combat.PtrlConstants;
import ptrl.util.CityNameGenerator;
import ptrl.util.GameSettings;

public class GlobalMap implements Serializable
{
	
	private MapDescriptor[][][] tilemap;
	int[][] heights;
	private String name;
	private int pcX;
	private int pcY;
	private int pcZ;
	private int waterThreshold;
	private int mountainsThreshhold;
	private static final long serialVersionUID = 5892280250967034975L;
	private static final int LEVELS=255; 
	private static final double RIVERS=0.002;
	private static final double FORESTS=0.001;
	private static final double CITIES=0.0020;

	private static final int FOREST_ITERATIONS=70;
	private int w;
	private int h;
	
	
	//private static final int UNEVEN_FACTOR=30;
	

	public GlobalMap(String name)
	{
		w=GameSettings.getInstance().getGlobalMapSide();
		h=GameSettings.getInstance().getGlobalMapSide();
		this.name=name;
		tilemap=new MapDescriptor[w][h][];
		heights=new int[w][h];
		generate();
		refreshKnown();
	}
	
	private void generate()
	{
		heights[0][0]=(int)(Math.random()*LEVELS);
		heights[heights.length-1][0]=(int)(Math.random()*LEVELS);
		heights[0][heights[0].length-1]=(int)(Math.random()*LEVELS);
		heights[heights.length-1][heights[0].length-1]=(int)(Math.random()*LEVELS);
		float balance=GameSettings.getInstance().getGlobalMapBalance();
		plasma_iter(heights, 0, 0, heights.length-1, heights[0].length-1, balance);
		heightsToTilemap();
		forests();
		for (int i=0; i<RIVERS*w*h; i++)
		{
			int x=2+(int)Math.round(Math.random()*(w-4));
			int y=2+(int)Math.round(Math.random()*(h-4));
//			System.out.println("x="+x+", y="+y+";");
			if (tilemap[x][y][0].getSurface()!="sea"&&
					tilemap[x][y][0].getSurface()!="lake"&&
					tilemap[x][y][0].getSurface()!="river")
				makeRiver(x, y);
		}
		seaSides();
		citiesAndRoads();
		setPCX(1);
		setPCY(1);
		makeAllVisible();
	}

	public MapDescriptor getTile(int x, int y, int z)
	{
		return tilemap[x][y][z];
	}
	
	public int getPCX()
	{
		return pcX;
	}

	public int getPCY()
	{
		return pcY;
	}

	public int getPCZ()
	{
		return pcZ;
	}
		
	public void setPCX(int pcX)
	{
		this.pcX = pcX;
	}

	public void setPCY(int pcY)
	{
		this.pcY = pcY;
	} 

	public void setPCZ(int pcZ)
	{
		this.pcZ = pcZ;
	} 
	
	public int getWidth()
	{
		return tilemap.length;
	}

	public int getHeight()
	{
		return tilemap[0].length;
	}
	
	public int getDepth(int x, int y)
	{
		return tilemap[x][y].length;
	}
	
	public void refreshKnown()
	{
		for (int i=pcX-1; i<=pcX+1; i++)
			for (int j=pcY-1; j<=pcY+1; j++)
				if (i>=0&&j>=0&&i<getWidth()&&j<getHeight())
					getTile(i, j, pcZ).setKnownByPlayer(true);
	}
	
	private void plasma_iter(int[][] arr, int x1, int y1, int x2, int y2, float balance)
	{
		if (x1>x2)
		{
			int tmpx = x1;
			x1 = x2;
			x2 = tmpx;
		}
		if (y1>y2)
		{
			int tmpy = y1;
			y1 = y2;
			y2 = tmpy;
		}
		int cx=(x1+x2)/2;
		int cy=(y1+y2)/2;
		
		if ((x2-x1)>=1)
		{
			arr[cx][y1]=(arr[x1][y1]+arr[x2][y1])/2;
			arr[cx][y2]=(arr[x1][y2]+arr[x2][y2])/2;
		}
		if ((y2-y1)>=1)
		{
			arr[x1][cy]=(arr[x1][y1]+arr[x1][y2])/2;		
			arr[x2][cy]=(arr[x2][y1]+arr[x2][y2])/2;
		}
		if ((x2-x1)>=2&&(y2-y1)>=2)
		{
			double displace=3*LEVELS*((x2-x1)+(y2-y1))/(w+h);
			//System.out.println("Displace:"+displace);
			arr[cx][cy]=(int)Math.round((arr[x1][y1]+arr[x1][y2]+arr[x2][y1]+arr[x2][y2])/4+(Math.random()-balance)*displace);
			plasma_iter(arr, x1, y1, cx, cy, 0.5f);
			plasma_iter(arr, cx, y1, x2, cy, 0.5f);
			plasma_iter(arr, x1, cy, cx, y2, 0.5f);
			plasma_iter(arr, cx, cy, x2, y2, 0.5f);
		}
	}
	
	private void normalize(int[][] arr)
	{
		int min=LEVELS;
		int max=0;
		int h;
		for (int i=0; i<arr.length; i++)
			for (int j=0; j<arr[0].length; j++)
			{
				h=arr[i][j];
				if (h>max) max=h;
				if (h<min) min=h;
			}
		for (int i=0; i<arr.length; i++)
			for (int j=0; j<arr[0].length; j++)
			{
				h=arr[i][j];
				arr[i][j]=(h-min)*LEVELS/(max-min);
			}
	}
	
	private void makeAllVisible()
	{
		for (int i=0; i<tilemap.length; i++)
			for (int j=0; j<tilemap[0].length; j++)
				tilemap[i][j][0].setKnownByPlayer(true);
	}
	
	public void makeRiver(int startX, int startY)
	{
		ArrayList<int[]> riverPath = new ArrayList<int[]>();
		int x=startX;
		int y=startY;
		int newX=x;
		int newY=y;
		int oldX=x;
		int oldY=y;
		do 
		{
			int min=LEVELS;
			for (int i=x-1; i<=x+1; i++)
				for (int j=y-1; j<=y+1; j++)
					//if ((i!=x||j!=y)&&(i!=oldX||j!=oldY)&&((x-i)*(y-j)==0))
					if ((i!=x||j!=y)&&(i!=oldX||j!=oldY))
						if (heights[i][j]<min&&!tilemap[i][j][0].getSurface().equalsIgnoreCase("new_river"))
						{
							newX=i;
							newY=j;
							min=heights[i][j];
						}
			if (min==LEVELS)
				break;
			//System.out.println("min="+min);
			oldX=x;
			oldY=y;
			x=newX;
			y=newY;
			tilemap[x][y][0].setFgColor(PtrlConstants.LBLUE);
			tilemap[x][y][0].setSurface("new_river");
			tilemap[x][y][0].setSymbol('~');
			if (tilemap[x][y][0].getSurface().equalsIgnoreCase("river")
					||tilemap[x][y][0].getSurface().equalsIgnoreCase("lake")
					||tilemap[x][y][0].getSurface().equalsIgnoreCase("sea")) 
				break;
			if ((x-oldX)*(y-oldY)!=0)
			{
				int x1=x;
				int y1=y;
				if (Math.random()>=0.5)
					x1=oldX;
				else
					y1=oldY;
				tilemap[x1][y1][0].setFgColor(PtrlConstants.LBLUE);
				tilemap[x1][y1][0].setSurface("new_river");
				tilemap[x1][y1][0].setSymbol('~');
				riverPath.add(new int[]{x1,y1});
			}
			riverPath.add(new int[]{x,y});
		} while (heights[x][y]>waterThreshold&&x>0&&x<getWidth()-1&&y>0&&y<getHeight()-1);
		Iterator<int[]> i = riverPath.iterator();
		while (i.hasNext())
		{
			int[] coords=i.next();
			tilemap[coords[0]][coords[1]][0].setSurface("river");
		}
	}
	
	public void polish()
	{
		for (int x=1; x<tilemap.length-1; x++)
			for (int y=1; y<tilemap[x].length-1; y++)
			{
				if (tilemap[x][y][0].getSurface()!="river"
					&&tilemap[x][y][0].getSurface()!="lake"
					&&tilemap[x][y][0].getSurface()!="sea")
				{
					int seas=0; 
					int waters=0;
					for (int i=x-1; i<=x+1; i++)
						for (int j=y-1; j<=y+1; j++)
							if ((i!=x||j!=y))
								if (tilemap[i][j][0].getSurface()=="river"
									||tilemap[i][j][0].getSurface()=="lake"
									||tilemap[i][j][0].getSurface()=="sea")
								{
									waters++;
									if (tilemap[i][j][0].getSurface()=="sea")
										seas++;
								}
					if (waters>=4)
						if (seas>=3)
						{
							tilemap[x][y][0].setFgColor(PtrlConstants.LCYAN);
							tilemap[x][y][0].setSurface("new_sea");
							tilemap[x][y][0].setSymbol('~');
						}
						else
						{
							tilemap[x][y][0].setFgColor(PtrlConstants.LCYAN);
							tilemap[x][y][0].setSurface("new_lake");
							tilemap[x][y][0].setSymbol('~');
						}
				}
			}
	}
	
	
	public int getTerrainLevel(int x, int y)
	{
		return heights[x][y];
	}
	
	private void heightsToTilemap()
	{
		int sum=0;
		normalize(heights);
		for (int i=0; i<tilemap.length; i++)
			for (int j=0; j<tilemap[i].length; j++)
			{
				tilemap[i][j]=new MapDescriptor[]{new MapDescriptor(i,j,0)};
				float h=Math.max(Math.min(heights[i][j], LEVELS), 0);
				sum+=h;
				h=h/LEVELS;
				//tilemap[i][j][0].setFgColor(new Color(0,h,0));
				tilemap[i][j][0].setFgColor(PtrlConstants.DGREEN);
				tilemap[i][j][0].setSymbol('.');
				tilemap[i][j][0].setSurface("none");

			}
		waterThreshold=sum/(2*w*h);
		mountainsThreshhold=(int)Math.round(1.7*sum/(w*h));
		System.out.println("avg. h="+sum/(w*h));
		System.out.println("water="+waterThreshold);
		System.out.println("mountains="+mountainsThreshhold);
		for (int i=0; i<tilemap.length; i++)
			for (int j=0; j<tilemap[i].length; j++)
			{
				if (heights[i][j]<=waterThreshold)
				{
					if (heights[i][j]<=waterThreshold/2)
						tilemap[i][j][0].setFgColor(PtrlConstants.DBLUE);
					else 
						tilemap[i][j][0].setFgColor(PtrlConstants.LBLUE);
					
					tilemap[i][j][0].setSurface("sea");
					tilemap[i][j][0].setSymbol('~');
				}
				else if (heights[i][j]>=mountainsThreshhold)
				{
					if (heights[i][j]>=(LEVELS+mountainsThreshhold)/2)
					{
						if (Math.random()<=0.8)
						{
							tilemap[i][j][0].setFgColor(PtrlConstants.WHITE);
							tilemap[i][j][0].setSymbol('^');
							tilemap[i][j][0].setSurface("mountains");
						}
						else
						{
							tilemap[i][j][0].setFgColor(PtrlConstants.LGRAY);
							tilemap[i][j][0].setSymbol('^');
							tilemap[i][j][0].setSurface("mountains");
						}
					}
					else if (Math.random()<=0.8) 
					{
						tilemap[i][j][0].setFgColor(PtrlConstants.LGRAY);
						tilemap[i][j][0].setSymbol('^');
						tilemap[i][j][0].setSurface("mountains");
					}
				}
			}
	}
	
	private void forests()
	{
		boolean[][] forests = new boolean[w][h];
		for (int i=0; i<w; i++)
			for (int j=0; j<h; j++)
				forests[i][j]=false;
		
		for (int i=0; i<w*h*FORESTS; i++)
		{
			int x=2+(int)Math.round(Math.random()*(w-4));
			int y=2+(int)Math.round(Math.random()*(h-4));
			if (tilemap[x][y][0].getSurface().equalsIgnoreCase("none"))
				forests[x][y]=true;
			if (tilemap[x+1][y][0].getSurface().equalsIgnoreCase("none"))
				forests[x+1][y]=true;
			if (tilemap[x-1][y][0].getSurface().equalsIgnoreCase("none"))
				forests[x-1][y]=true;
			if (tilemap[x][y+1][0].getSurface().equalsIgnoreCase("none"))
				forests[x][y+1]=true;
			if (tilemap[x][y-1][0].getSurface().equalsIgnoreCase("none"))
				forests[x][y-1]=true;
		}
		
		for (int i=0; i<FOREST_ITERATIONS; i++)
		{
			for (int x=0; x<w; x++)
				for (int y=0; y<h; y++)
				{
					if (!tilemap[x][y][0].getSurface().equalsIgnoreCase("none"))
						continue;
					int n=0;
					boolean sea=false;
					for (int x1=x-1; x1<=x+1; x1++)
						for (int y1=y-1; y1<=y+1; y1++)
						{
							if (x1>=0&&y1>=0&&x1<w&&y1<h&&(x!=x1||y!=y1))
							{
								if (forests[x1][y1])
									n++;
								else if (tilemap[x1][y1][0].getSurface().equalsIgnoreCase("sea"))
									sea=true;
							}
						}
					if (n>=3&&Math.random()<=0.5&&!sea)
					{
						//System.out.println("n="+n);
						tilemap[x][y][0].setSurface("forest");
						tilemap[x][y][0].setFgColor(PtrlConstants.DGREEN);
						tilemap[x][y][0].setSymbol('&');
						tilemap[x][y][0].setTileSetName("forest");
					}
				}
			for (int x=0; x<w; x++)
				for (int y=0; y<h; y++)
				{
					if (tilemap[x][y][0].getSurface().equalsIgnoreCase("forest"))
						forests[x][y]=true;
				}
		}
	}
	
	private void seaSides()
	{
		for (int x=0; x<w; x++)
			for (int y=0; y<h; y++)
			{
				if (!tilemap[x][y][0].getSurface().equalsIgnoreCase("none"))
					continue;
				boolean sea=false;
				for (int x1=x-1; x1<=x+1; x1++)
					for (int y1=y-1; y1<=y+1; y1++)
					{
						if (x1>=0&&y1>=0&&x1<w&&y1<h)
							if (tilemap[x1][y1][0].getSurface().equalsIgnoreCase("sea"))
								sea=true;
					}
				if (sea)
				{
					//System.out.println("sea");
					tilemap[x][y][0].setSurface("sea shore");
					tilemap[x][y][0].setFgColor(PtrlConstants.DYELLOW);
					tilemap[x][y][0].setSymbol('.');
				}
			}
	}
	
	public void citiesAndRoads()
	{
		int cities_n=(int)Math.round(CITIES*w*h);
		CityNameGenerator cng = new CityNameGenerator();
		ArrayList<int[]> cities = new ArrayList<int[]>();
		boolean[][] roadsMatrix=new boolean[cities_n][cities_n];
		int[][][] pathfindArray=new int[w][h][];
		for(int i=0; i<w; i++)
			for (int j=0; j<h; j++)
			{
				pathfindArray[i][j]=new int[]{-1, -1}; //path_length, nearest settlement
			}
		int cs=0;
		while (cs<cities_n) //generate cities
		{
			int x=2+(int)Math.round(Math.random()*(w-4));
			int y=2+(int)Math.round(Math.random()*(h-4));
			if (tilemap[x][y][0].getSurface().equalsIgnoreCase("river")
				||tilemap[x][y][0].getSurface().equalsIgnoreCase("lake")
				||(Math.random()<0.9&&tilemap[x][y][0].getSurface().equalsIgnoreCase("sea")))
			continue;	
			cities.add(new int[]{x,y});
			String name=cng.getName();
			SettlementMapDescriptor desc = new SettlementMapDescriptor(x,y,0,name);
			desc.setSurface(tilemap[x][y][0].getSurface());
			desc.setSymbol('*');
			desc.setFgColor(PtrlConstants.LGRAY);
			desc.setTileSetName("town");
			tilemap[x][y][0]=desc;
			pathfindArray[x][y]=new int[]{0, cs};
			for (int j=0; j<cities_n; j++)
				if (cs==j) roadsMatrix[cs][j]=true;
				else roadsMatrix[cs][j]=false;
			cs++;
		}
		
		int roads=0; //generated roads
		int step=0;
		boolean all_complete=false;
		while (roads<cities_n+2&&!all_complete)
		{
			if (!all_complete&&allPathsFound(roadsMatrix))
				all_complete=true;
			for (int x=0; x<w; x++)
				for (int y=0; y<h; y++)
				{
					if (pathfindArray[x][y][0]!=step) 
						continue;
					this_tile:
					for (int i=x-1; i<=x+1; i++)
						for (int j=y-1; j<=y+1; j++)
						{
							if (i<0||j<0||i>=w||j>=h||(i==x&&j==y))
								continue;
							if (pathfindArray[x][y][1]!=pathfindArray[i][j][1]
							    &&pathfindArray[i][j][1]>=0)
							{
								//make road;
								int city1=pathfindArray[x][y][1];
								int city2=pathfindArray[i][j][1];
//								System.out.println("city1="+city1);
//								System.out.println("city2="+city2);
								if (roadsMatrix[city1][city2]&&!all_complete)
									continue;
								roadsMatrix[city1][city2]=true;
								roadsMatrix[city2][city1]=true;
								//System.out.println("Road from "+pathfindArray[x][y][1]+" to "+pathfindArray[i][j][1]);
								makeRoad(pathfindArray, x, y, cities.get(city1)[0], cities.get(city1)[1], PtrlConstants.DYELLOW);
								//System.out.println("Road from "+pathfindArray[i][j][1]+" to "+pathfindArray[x][y][1]);
								makeRoad(pathfindArray, i, j, cities.get(city2)[0], cities.get(city2)[1], PtrlConstants.DYELLOW);
								roads++;
								processRoadMatrix(roadsMatrix);
								break this_tile;
							}
							if (pathfindArray[i][j][0]!=-1)
								continue;
							boolean unpassable=false;
							int add=10;
							if ((i-x)*(j-y)!=0)	add=14;
							if (tilemap[i][j][0].getSurface().equalsIgnoreCase("mountains"))
								add=add*4;
							add=add+(int)Math.ceil(Math.abs(heights[x][y]-heights[i][j]));
							if (tilemap[i][j][0].getSurface().equalsIgnoreCase("river")
								//&&(x-i)*(y-j)==0								
								&&i+(i-x)>0
								&&i+(i-x)<w
								&&j+(j-y)>0
								&&j+(j-y)<h
								&&!tilemap[i+(i-x)][j+(j-y)][0].getSurface().equalsIgnoreCase("river")
								&&!tilemap[i+(i-x)][j+(j-y)][0].getSurface().equalsIgnoreCase("sea")
								&&!tilemap[i+(i-x)][j+(j-y)][0].getSurface().equalsIgnoreCase("lake"))
							{
//								System.out.println("bridge: x1="+x+", y1="+y);
//								System.out.println("        x2="+i+", y2="+j);
//								System.out.println("        x3="+(i+(i-x))+", y3="+(j+(j-y)));
								add+=20;
								pathfindArray[i+(i-x)][j+(j-y)][0]=step+add+1;
								pathfindArray[i+(i-x)][j+(j-y)][1]=pathfindArray[x][y][1];
							}
							else if (tilemap[i][j][0].getSurface().equalsIgnoreCase("sea")
									 ||tilemap[i][j][0].getSurface().equalsIgnoreCase("lake"))
							{
								unpassable=true;
								//add=add*10000;
							}
							else if (tilemap[i][j][0].getSurface().equalsIgnoreCase("river"))
							{
								unpassable=true;
								//add=add*100000;
							}
							if (unpassable)
							{
								pathfindArray[i][j][0]=-1;
								continue;
							}
							pathfindArray[i][j][0]=step+add;
							pathfindArray[i][j][1]=pathfindArray[x][y][1];
						}
				}
			if (++step>100000)
			{
				all_complete=true;
				System.out.println("Interrupting map generation, step "+step+".");
			}
		}
		
		
		
		
	}
	
	private void processRoadMatrix(boolean[][] matrix)
	{
		boolean changes=true;
		while (changes)
		{
			changes=false;
			for (int i=0; i<matrix.length; i++)
			{
				for (int j=0; j<matrix[i].length; j++)
					if (i==j) 
						continue;
					else if (matrix[i][j]&&!matrix[j][i])
					{
						matrix[j][i]=true;
						changes=true;
					}
				for (int j=0; j<matrix.length; j++)
					if (i==j||!matrix[i][j])
						continue;
					else 
						for (int k=0; k<matrix[i].length; k++)
							if (matrix[j][k]&&!matrix[i][k])
							{
								matrix[i][k]=true;
								changes=true;
							}
			}
		}
	}
	
	private boolean allPathsFound(boolean[][] matrix)
	{
		boolean[][] matrix2=new boolean[matrix.length][matrix[0].length];
		for (int i=0; i<matrix.length; i++)
			for (int j=0; j<matrix.length; j++)
				matrix2[i][j]=matrix[i][j];
		//processRoadMatrix(matrix2);
		for (int i=0; i<matrix2.length; i++)
			for (int j=0; j<matrix2[i].length; j++)
				if (!matrix2[i][j])
					return false;
		return true;
	}

	private void makeRoad(int[][][] arr, int startx, int starty, int endx, int endy, short c)
	{
		int city=arr[startx][starty][1];
		int x=startx;
		int y=starty;
		int oldx=x;
		int oldy=y;
		while (x!=endx||y!=endy)
		{
			int[] forcedXY=null;
			//System.out.println("x="+x+", y="+y);
			if (tilemap[x][y][0].getSurface().equalsIgnoreCase("river")
					&&!tilemap[oldx][oldy][0].getSurface().equalsIgnoreCase("river")
					&&!tilemap[x+x-oldx][y+y-oldy][0].getSurface().equalsIgnoreCase("river")
					&&(oldx-x)==0)
			{
				tilemap[x][y][0].setGenerator("bridge");
				tilemap[x][y][0].setFgColor(c);
				tilemap[x][y][0].setSymbol('|');
				forcedXY=new int[]{x+x-oldx, y+y-oldy};
			}
			else if (tilemap[x][y][0].getSurface().equalsIgnoreCase("river")
					&&!tilemap[oldx][oldy][0].getSurface().equalsIgnoreCase("river")
					&&!tilemap[x+x-oldx][y+y-oldy][0].getSurface().equalsIgnoreCase("river")
					&&(oldy-y)==0)
			{
				tilemap[x][y][0].setGenerator("bridge");
				tilemap[x][y][0].setFgColor(c);
				tilemap[x][y][0].setSymbol('-');
				forcedXY=new int[]{x+x-oldx, y+y-oldy};
			}
			else if (tilemap[x][y][0].getSurface().equalsIgnoreCase("river")
					&&!tilemap[oldx][oldy][0].getSurface().equalsIgnoreCase("river")
					&&!tilemap[x+x-oldx][y+y-oldy][0].getSurface().equalsIgnoreCase("river")
					&&(oldx-x)*(oldy-y)>0)
			{
				tilemap[x][y][0].setGenerator("bridge");
				tilemap[x][y][0].setFgColor(c);
				tilemap[x][y][0].setSymbol('\\');
				forcedXY=new int[]{x+x-oldx, y+y-oldy};
			}
			else if (tilemap[x][y][0].getSurface().equalsIgnoreCase("river")
					&&!tilemap[oldx][oldy][0].getSurface().equalsIgnoreCase("river")
					&&!tilemap[x+x-oldx][y+y-oldy][0].getSurface().equalsIgnoreCase("river")
					&&(oldx-x)*(oldy-y)<0)
			{
				tilemap[x][y][0].setGenerator("bridge");
				tilemap[x][y][0].setFgColor(c);
				tilemap[x][y][0].setSymbol('/');
				forcedXY=new int[]{x+x-oldx, y+y-oldy};
			}
			else if (tilemap[x][y][0].getSurface().equalsIgnoreCase("sea")
					||tilemap[x][y][0].getSurface().equalsIgnoreCase("river")
					||tilemap[x][y][0].getSurface().equalsIgnoreCase("lake"))
			{
				tilemap[x][y][0].setGenerator("road");
				tilemap[x][y][0].setFgColor(PtrlConstants.DCYAN);
				tilemap[x][y][0].setSymbol('.');
			}
			else 
			{
				tilemap[x][y][0].setGenerator("road");
				tilemap[x][y][0].setFgColor(c);
				tilemap[x][y][0].setSymbol(':');
			}
			
			int[] min_delta=new int[]{0,0};
			int min=arr[x][y][0];
			if (forcedXY==null)
			{
				for (int i=x-1; i<=x+1; i++)
					for (int j=y-1; j<=y+1; j++)
					{
						if (i<0||j<0||i>=w||j>=h||(i==x&&j==y)||(i==oldx&&j==oldy))
							continue;
						//System.out.println("path="+arr[i][j][0]);
						if (arr[i][j][1]==city&&arr[i][j][0]<min&&arr[i][j][0]>=0)
						{
							//System.out.println("min="+min);
							min=arr[i][j][0];
							min_delta[0]=i-x;
							min_delta[1]=j-y;
						}
					}
			//System.out.println("min="+min);
				oldx=x;
				oldy=y;
				if (min_delta[0]==0&&min_delta[1]==0)
				{
					System.out.println("zero deltas, x="+x+", y="+y);
					return;
				}
				x+=min_delta[0];
				y+=min_delta[1];
			}
			else 
			{
				oldx=x;
				oldy=y;
				x=forcedXY[0];
				y=forcedXY[1];
			}
		}
	}
	
	public MapDescriptor getCurrentTile()
	{
		return getTile(getPCX(), getPCY(), getPCZ());
	}
	
	public Map getMap()
	{
		return getCurrentTile().getMap();
	}
}

