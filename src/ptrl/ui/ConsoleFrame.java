package ptrl.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.Timer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ptrl.Game;
import ptrl.creatures.Creature;
import ptrl.creatures.Player;
import ptrl.map.DoorTile;
import ptrl.map.Map;
import ptrl.map.MapGenerator;
import ptrl.map.Tile;
import ptrl.util.GameSettings;
import ptrl.util.MessageStack;

public class ConsoleFrame extends JFrame
{
	public static void main(String[] args) throws SAXException, IOException, Exception
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getScreenDevices()[0];
		
		ConsoleFrame cf = new ConsoleFrame();
		File f = new File("."+File.separator+"config.xml");
		GameSettings gset=GameSettings.getInstance();
		if (gset.isFullScreen())
		{
			cf.setUndecorated(true);
			gd.setFullScreenWindow(cf);
		}
		cf.setVisible(true);
		if (args.length>0)
			if (args[0].equalsIgnoreCase("fonts"))
				cf.panel.printFonts();
	}
	
	/**
	 *	Creates a frame. 
	 * @throws Exception 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public ConsoleFrame() throws SAXException, IOException, Exception
	{
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		panel = new ConsolePanel();
		Container contentPane = getContentPane();
		contentPane.add(panel);
	}
	ConsolePanel panel;
}



class ConsolePanel extends JPanel
{
	/**
	 * Creates ConsolePanel instance with default params.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws Exception 
	 */
	
	public ConsolePanel() throws Exception, SAXException, IOException
	{
		game=null;
		game_started=false;
		gs=new MainMenuDialog();
		MyKeyHandler kh = new MyKeyHandler();
		addKeyListener(kh);
		setFocusable(true);
	}
	

	/**
	 * Creates ConsolePanel instance.
	 * 
	 * @param fs font size.
	 * @param ff font, used for output.
	 */
	public ConsolePanel(int fs, Font ff) throws Exception, SAXException, IOException
	{
		this();
	}	
	
	/*
	 * Initializes new char&color arrays.
	 */
	public void reInit()
	{
		buf_image = createImage(getWidth(), getHeight());
		g=buf_image.getGraphics();
		String ff="monospaced";
		GameSettings gset=GameSettings.getInstance();
		ff=gset.getFontName();
		int fs=gset.getFontSize();
		int fst=gset.getFontStyle();
		animate=false;
		timer = new Timer(gset.getDelay(), new TimeListener());

		c=new Console(g, new Font(ff,fst,fs),getWidth(), getHeight());
		//c=new Console(g, new Font("monospaced",Font.PLAIN,14),getWidth(), getHeight());
	}
		
	public void paintComponent(Graphics g)
	{
		if (printfonts) fontsOut(g);
		super.paintComponent(g);
		if (w!=this.getWidth()||h!=this.getHeight())
		{
			w=this.getWidth();
			h=this.getHeight();
			reInit();
		}
		gs.paint(c);
		g.drawImage(buf_image, 0,0, null);
	}
	
	protected void fontsOut(Graphics g)
	{
			String[] fontnames=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
			try
			{
				PrintWriter writer = new PrintWriter(new File("."+File.separator+"fonts.txt"));
				for (int i=0; i<fontnames.length; i++)
				{
					Font f = new Font(fontnames[i], Font.PLAIN, 12);
					int w1=(int)f.getStringBounds("W", ((Graphics2D)g).getFontRenderContext()).getWidth();
					int w2=(int)f.getStringBounds("i", ((Graphics2D)g).getFontRenderContext()).getWidth();
					if (w1==w2) writer.println(fontnames[i]);
				}
				writer.close();
			}
			catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(this, "File not found!");
				System.exit(0);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, "IOException!");
				System.exit(0);
			}
			System.exit(0);
	}
	
	public void printFonts()
	{
		printfonts=true;
	}
	
	//Strange stuff generated :?
	private static final long serialVersionUID = 1L;
	
	private int w;
	private int h;
	// map to display
	private Map map;
	// number of empty strings on the top
	// and on the bottom of console;
	private int header;
	private int footer;
	// tiles, that have visibility <= fov_threshold shows shaded. 
	private int fov_threshold;
	private Image buf_image;
	private Graphics g;
	private KeyEvent kev;
	private boolean on_map;
	private Console c;
	private IGameScreen gs;
	private Game game;
	private boolean game_started;
	private boolean animate;
	private Timer timer;
	private boolean printfonts=false;
	
	private class MyKeyHandler implements KeyListener
	{
		public void keyPressed(KeyEvent ke)
		{
			if (animate) return;
			if (gs==null) return;
			if (!game_started)
			{
				if (gs.getKeyEvent(ke)) 
				{
					game=((MainMenuDialog)gs).getGame();
					gs=game;
					game_started=true;
					try
					{
						//game.start();
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			else if (gs.getKeyEvent(ke)) 
				System.exit(0);
			repaint();
			if (gs instanceof IAnimated)
			{
				if (((IAnimated)gs).needRefresh())
				{
					animate=true;
					if (!timer.isRunning()) timer.start();
				}
			}
		}
		public void keyReleased(KeyEvent ke){}
		public void keyTyped(KeyEvent ke){}
	}
	
	private class TimeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			if (gs instanceof IAnimated)
			{
				IAnimated ags=(IAnimated)gs;
				ags.refresh();
				repaint();
				if (!ags.needRefresh())
				{
					timer.stop();
					animate=false;
				}
			}
		}
		
	}
}


