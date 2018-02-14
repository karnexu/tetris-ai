import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

/*
 * The <code> TetrisPanel1p </code> class will create a panel with 
 * a Marathon Tetris for 1 player. The user aims to clear as many 
 * lines as possible to try and reach the highest level. There are 
 * currently 8 levels, and 150 lines are required to complete the
 * highest level. The pieces will start dropping faster and faster 
 * as the user's level increases. 
 * 
 * CONTROLS:
 * left and right arrow keys to shift left and right
 * up arrow key to rotate clockwise
 * down arrow key to soft drop
 * space bar to hard drop 
 * shift to hold a piece 
 * esc key to pause
 * R key to restart game 
 * 
 */

public class TetrisPanel1p extends Panel implements KeyListener
{
	// initializes a new Tetris game
	Tetris tetris = new Tetris();

	// dimensions and stuff 
	private Dimension dim = null;	
	private Color back_Color = Color.BLACK;
	private Graphics osg = null;
	private BufferedImage osi = null;

	// levels 
	public final static int[] linesPerLevel = {0, 5, 10, 15, 30, 60, 90, 120, 150};
	public static int level = 1;
	private final int[] countsPerLevel = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
	public int count = 0;
	
	// pause
	boolean paused = false; 

	// timer initialization
	Timer timer = new Timer();
	TimerTask task = new TimerTask()
	{
		public void run()
		{
			if (paused)
				return; 
			
			repaint();

			if (tetris.linesCleared >= linesPerLevel[level])
			{
				level++;
				if (level == linesPerLevel.length)
					tetris.wonGame = true;
			}

			if (count < countsPerLevel[level])
				count++;

			else
			{
				tetris.naturalDrop();
				count = 0;
			}
			
		}
	};

	/*
	 * Constructor for TetrisPanel class 
	 * Adds KeyListener to the class so it can read user input. 
	 * It also schedules the time task. 
	 */
	public TetrisPanel1p()
	{
		addKeyListener(this);
		timer.scheduleAtFixedRate(task, 0, 20);
	}

	/*
	 * The following 2 methods implement double buffered graphics 
	 * paint() is only called whent he dimensions of the panel change 
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g)
	{
		dim = getSize(); 				// obtain the current dimensions of the Panel
		osi = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
		osg = osi.getGraphics();
		update(g);
	}
	/*
	 * update() calls tetris to draw the board
	 * @see java.awt.Container#update(java.awt.Graphics)
	 */
	public void update(Graphics g)
	{
		osg.setColor(back_Color);
		osg.fillRect(0, 0, dim.width, dim.height);
		tetris.display(osg, dim, tetris.MARATHON, 0);
		g.drawImage(osi, 0, 0, this);
	}	
	
	/*
	 * The following three methods are related to keyboard inputs. 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent ke)
	{
		int keyCode = ke.getKeyCode(); 
		
		if (paused && keyCode != KeyEvent.VK_ESCAPE)
			return; 
		
		switch (keyCode)
		{
			case KeyEvent.VK_RIGHT:
				tetris.shift_right();
				break;

			case KeyEvent.VK_LEFT:
				tetris.shift_left();
				break;

			case KeyEvent.VK_DOWN:
				tetris.naturalDrop();
				break;
				
			case KeyEvent.VK_SPACE:
				tetris.hard_drop();
				break;
				
			case KeyEvent.VK_UP:
				tetris.rotate_cw();
				break;

			case KeyEvent.VK_CONTROL:
				tetris.rotate_ccw();
				break; 
				
			case KeyEvent.VK_SHIFT:
				tetris.hold();
				break;

			case KeyEvent.VK_ESCAPE:		
				paused = !paused;
				break;
			
			case KeyEvent.VK_R: 			
			{
				timer = new Timer();
				tetris = new Tetris(); 
				timer.scheduleAtFixedRate(task, 0, 20);
				count = 0; 
				level = 1; 
				break;
			}

			default:
				break;
		}
	}

	public void keyReleased(KeyEvent ke)
	{
	}

	public void keyTyped(KeyEvent ke)
	{
	}
} 

