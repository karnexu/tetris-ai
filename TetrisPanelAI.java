import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

//restart game

/**
 * The <code> TetrisPanelAI </code> class will create a panel with 
 * 2 Tetris sprint games. One will be played by a user, and the other 
 * will be played by an AI (in other words, a computer. The 2 players 
 * will race to clear the most lines in a 2 minute time period. 
 *
 * USER CONTROLS:
 * Left arrow key to shift left 
 * Right arrow key to shift right
 * Up arrow key to rotate clockwise
 * Down arrow key to soft drop
 * Space bar to hard drop 
 * Shift to hold a piece 
 * 
 * OTHER CONTROLS:
 * Esc key to pause game
 * R key to restart game 
 */

public class TetrisPanelAI extends Panel implements KeyListener
{
	// graphics stuff 
	private Dimension dim = null;
	private Color back_Color = Color.BLACK;
	private Graphics osg1 = null;
	private Graphics osg2 = null; 
	private BufferedImage osi1 = null;
	private BufferedImage osi2 = null;
	
	// time in milliseconds 
	long startTime;
	long currTime;
	long pauseStart; 
	long pauseEnd; 
	
	// constants for the switch statement
	final int PAUSE = KeyEvent.VK_ESCAPE;
	final int RESTART = KeyEvent.VK_R; 
	
	//timer 
	long delay = 0; 
	long interval = 20;
	
	boolean paused = false; 
	
	Timer timer = new Timer();
	TimerTask task = new TimerTask()
	{
		public void run()
		{
			if (currTime -(pauseEnd-pauseStart)- startTime >= 1000*60*2)		//2 minutes
				return; 
			
			if (user.lostGame())
			{
				comp.wonGame = true; 
				return;
			}
			
			else if (comp.lostGame())
			{
				user.wonGame = true; 
				return; 
			}

			if (paused ==  true)
			{
				return; 
			}
	
			repaint();

			//player 1 natural drop
			if (user.count < user.countsPerLevel)
				user.count++;

			else
			{
				user.naturalDrop();
				user.count = 0;
			}
			
			//player 2 natural drop
			if (comp.rotation != comp.bestRotation)
			{
				comp.rotate_cw();
				comp.count++;
			}

			else if (comp.posx < comp.bestX)
			{
				comp.shift_right();
				comp.count++;
			}

			else if (comp.posx > comp.bestX)
			{
				comp.shift_left();
				comp.count++;
			} 
			
			else
			{
				comp.naturalDrop();
				comp.count = 0;
			}
		}
	};
	
	Player1 user = new Player1(); 
	Player2 comp = new Player2(); 
		
	static class Player1 extends Tetris
	{
		Tetris tetris = new Tetris();
		
		int count = 0;
		int countsPerLevel = 20; 
	}
	
	static class Player2 extends TetrisAI
	{
		TetrisAI tetris = new TetrisAI();
		
		int count = 0;
		int countsPerLevel = 1; 
	}
	
	public TetrisPanelAI()
	{
        startTime = System.currentTimeMillis();
		timer.scheduleAtFixedRate(task, delay, interval);
		addKeyListener(this);
	}

	/** 
	 * Finds the size of the panel, and initializes an off-screen image
	 * and off-screen graphics for each player. It then calls update() 
	 * to do all the drawing. This method is only called when 
	 * the dimension of the panels are changed. 
	 */
	public void paint(Graphics g)
	{
		dim = getSize(); 			
		
		osi1 = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
		osi2 = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);

		osg1 = osi1.getGraphics();
		osg2 = osi2.getGraphics();

		update(g);
	}
	/**
	 * Draws the off-screen image for each player. 
	 * Also sends the current time, in seconds, so that 
	 * it can be printed on the Tetris game. 
	 */
	public void update(Graphics g)
	{
		osg1.setColor(back_Color);
		osg2.setColor(back_Color);

		osg1.fillRect(0, 0, dim.width/2, dim.height);
		osg2.fillRect(dim.width/2, 0, dim.width, dim.height);

		currTime = (System.currentTimeMillis() -(pauseEnd-pauseStart)- startTime );
		user.display(osg1, dim, user.SPRINT, currTime/1000);
		comp.draw(osg2, dim, comp.SPRINT, currTime/1000);

		g.drawImage(osi1, 0, 0, dim.width/2, dim.height, osi1.getWidth()*2/10, 0, osi1.getWidth()*8/10, osi1.getHeight(), this); 
		g.drawImage(osi2, dim.width/2, 0, dim.width, dim.height, osi2.getWidth()*2/10, 0, osi2.getWidth()*8/10, osi1.getHeight(), this); 
	}
	
	public void keyPressed(KeyEvent ke)
	{	
		switch (ke.getKeyCode())
		{
			case KeyEvent.VK_RIGHT:
				user.shift_right();
				break;

			case KeyEvent.VK_LEFT:
				user.shift_left();
				break;

			case KeyEvent.VK_DOWN:
				user.naturalDrop();
				break;
				
			case KeyEvent.VK_SPACE:
				user.hard_drop();
				break;
				
			case KeyEvent.VK_UP:
				user.rotate_cw();
				break;
				
			case KeyEvent.VK_SHIFT:
				user.hold();
				break;
			
			case PAUSE:			
			{
				paused = !paused;

				if (paused)
				{
					pauseStart = System.currentTimeMillis(); 
					pauseEnd = pauseStart;
				}
				else 
				{
					pauseEnd = System.currentTimeMillis();
				}

				break;
			}
				
			case RESTART: 					
			{
		        startTime = System.currentTimeMillis();
				user = new Player1();
				comp = new Player2(); 
				user.wonGame = false;
				comp.wonGame = false;
				timer = new Timer();
				timer.scheduleAtFixedRate(task, delay, interval);
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


