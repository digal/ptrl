package ptrl.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import ptrl.Game;
import ptrl.combat.PtrlConstants;

public class MainMenuDialog implements IGameScreen
{
	public MainMenuDialog ()
	{
		dialogs=null;
		//cur_dlg=0;
		menu=new String[]{"Start a new game", "Load a saved game", "Read manual", "Quit"};
	}
	
	public void paint(Console c)
	{
		if (dialogs!=null) 
		{
			dialogs[cur_dlg].paint(c);
			return;
		}
		c.clear();
		String head="Welcome to Plutonium Roguelike";
		int w=head.length();
		int h=menu.length+2;
		int y = (int)c.getSymHeight()/2 - (int)h/2;			
		int x = (int)c.getSymWidth()/2 - (int)w/2;
		c.printString(head, x, y, PtrlConstants.LCYAN);
		short col;
		for (int i=0;i<menu.length;i++)
		{
			if (i==cur) col=PtrlConstants.WHITE;
			else col=PtrlConstants.LGRAY;
			c.printString(menu[i], x, y+i+2, col); 
		}
	}

	public boolean getKeyEvent(KeyEvent ke)
	{
		char ch=ke.getKeyChar();
		if (dialogs!=null)
		{
			if (dialogs[cur_dlg].getKeyEvent(ke))
			{
				if (cur==0)
				{
					if (cur_dlg<dialogs.length-1) cur_dlg++;
					else 
					{
						game.getPlayer().create();
						try
						{
							game.start();
						} catch (ParserConfigurationException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SAXException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return true;
					}
				}
				//TODO: load game and manual branches
				//else if (cur==1)
			}
		}
		else
		if ((ch=='8'||ke.getKeyCode()==KeyEvent.VK_UP)&&cur>0) cur--;
		else if ((ch=='2'||ke.getKeyCode()==KeyEvent.VK_DOWN)&&cur<menu.length-1) cur++;
		else if (ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			if (cur==0) //new game
			{
				try
				{
					game=new Game();
				} catch (ParserConfigurationException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				dialogs=new IGameScreen[]{new NameDialog(game.getPlayer()), new GenderDialog(game.getPlayer()), new ClassDialog(game.getPlayer()), new AttribsDialog(game.getPlayer(), 7)};
				cur_dlg=0;
				System.out.println("New game");
			}
			else if (cur==1)
			{
				try
				{
					ObjectInputStream in = new ObjectInputStream(new FileInputStream("save.sav"));
					game=(Game)in.readObject();
					System.out.println("Game loaded");
				} catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
			else if (cur==3) System.exit(0);
		}
		return false;
	}

	public Game getGame()
	{
		return game;
	}
	
	private int cur;
	private int cur_dlg;
	private Game game;
	private String[] menu;
	private IGameScreen[] dialogs;
}
