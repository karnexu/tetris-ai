import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;


public class Tetris
{
	
	// board dimensions
	protected final int ROWS = 20; 								
	protected final int COLS = 10;									
	protected int[][] field = new int[ROWS + 2][COLS + 2];			// -1 for borders, 0 for empty, 1-7 for pieces (by color)
	private int size; 												
	private Point p; 												// coordinates of the top left corner of the playing field

	// pieces 
	protected static final int [][][][] pieces = new int[][][][]	
	{
		{
			// I piece
			{
				{1, 1, 1, 1},
			},
			{
				{1},
				{1},
				{1},
				{1}
			},
			{
				
				{1, 1, 1, 1},
			},
			{
				{1},
				{1},
				{1},
				{1}
			}

		},
		{
			// J piece
			{
				{2, 0, 0},
				{2, 2, 2},
			},
			{
				{2, 2},
				{2, 0},
				{2, 0},
			},
			{
				{2, 2, 2},
				{0, 0, 2},
			},
			{
				{0, 2},
				{0, 2},
				{2, 2},
			}

		},
		{
			// L piece
			{
				{0, 0, 3},
				{3, 3, 3},
				
			},
			{
				{3, 0},
				{3, 0},
				{3, 3},
			},
			{
				{3, 3, 3},
				{3, 0, 0},
			},
			{
				{3, 3},
				{0, 3},
				{0, 3},
			}

		},
		{
			// O piece
			{
				{4, 4},
				{4, 4},

			},
			{
				{4, 4},
				{4, 4},
			},
			{
				{4, 4},
				{4, 4},
			},
			{
				{4, 4},
				{4, 4},
			}

		},
		{
			// S piece
			{
				{0, 5, 5},
				{5, 5, 0},

			},
			{
				{5, 0},
				{5, 5},
				{0, 5},

			},
			{
				{0, 5, 5},
				{5, 5, 0},

			},
			{
				{5, 0},
				{5, 5},
				{0, 5},

			},

		},
		{
			// T piece
			{
				{0, 6, 0},
				{6, 6, 6},
				
			},
			{
				{6, 0},
				{6, 6},
				{6, 0},
			},
			{
				{6, 6, 6},
				{0, 6, 0},
			},
			{
				{0, 6},
				{6, 6},
				{0, 6},
			}

		},
		{
			// Z piece
			{
				{7, 7, 0},
				{0, 7, 7},
		
			},
			{
				{0, 7},
				{7, 7},
				{7, 0},
		
			},
			{
				{7, 7, 0},
				{0, 7, 7},
			},
			{
				{0, 7},
				{7, 7},
				{7, 0},
			}

		}
	};	
			
	private BufferedImage[] blocks = new BufferedImage[7]; 			// stores each of the 7 Tetromino squares as pictures
	public int piece, rotation, posx, posy; 						// stores the piece number and rotation of the piece
																	// posx and posy is coordinate of the top left corner of the piece

	// next piece queue
	public int[] numbers = new int[] { 0, 1, 2, 3, 4, 5, 6 }; 		// shuffles this array and adds it to the queue to ensure all pieces appear an equal number of times
	public Queue<Integer> queue = new LinkedList<Integer>(); 		// stores the next piece, and removes the pieces in a FIFO order

	// hold queue
	boolean hold_used = false; 				// hold can only be used once every time a new piece spawns
	int hold = -1; 							// saves the current piece number that is held
	
	// scoring 
	public int linesCleared = 0; 			// the total number of lines cleared		
	boolean wonGame = false;				// this becomes true when you complete all the levels by clearing lines (currently there are only 10 levels)
	boolean gameOver = false; 				// this becomes true if you lose the game (no more pieces can fall) 
	
	// playMode
	public final int MARATHON = 0; 			// this kind of Tetris speeds up as the level increases. The user aims to reach the highest level. 
	public final int SPRINT = 1; 			// this kind of Tetris is timed. The user aims to clear the most lines in 2 minutes. 
	
	/**
	 * This is the constructor for the class Tetris. 
	 * It calls upon two other methods to import all the Tetrimino squares,
	 * and set up a new piece. It then sets all the borders of the field to -1, 
	 * except the top. 
	 */
	public Tetris() 
	{
		init(); 						
		getPiece(); 				

		for (int i = 0; i < ROWS+2; i++)
			field[i][0] = field[i][COLS+1] = -1; 
		
		for (int j = 0; j < COLS+2; j++)
			field[ROWS+1][j] = -1; 
	}
	
	/**
	 * Imports all Tetromino pictures. 
	 * They are all initialized at the beginning instead of 
	 * every time a Tetromino is printed because that would cause
	 * the program to lag a lot, as I can attest to from personal 
	 * experience. 
	 */
	public void init () 
	{
		try
		{
			blocks[0] = ImageIO.read(new File(getClass().getClassLoader().getResource("cyan.jpg").getFile()));
			blocks[1] = ImageIO.read(new File(getClass().getClassLoader().getResource("blue.jpg").getFile()));
			blocks[2] = ImageIO.read(new File(getClass().getClassLoader().getResource("orange.jpg").getFile()));
			blocks[3] = ImageIO.read(new File(getClass().getClassLoader().getResource("yellow.jpg").getFile()));
			blocks[4] = ImageIO.read(new File(getClass().getClassLoader().getResource("green.jpg").getFile()));
			blocks[5] = ImageIO.read(new File(getClass().getClassLoader().getResource("magenta.jpg").getFile()));
			blocks[6] = ImageIO.read(new File(getClass().getClassLoader().getResource("pink.jpg").getFile()));

//			blocks[1] = ImageIO.read(new File("blue.jpg"));
//			blocks[2] = ImageIO.read(new File("orange.jpg"));
//			blocks[3] = ImageIO.read(new File("yellow.jpg"));
//			blocks[4] = ImageIO.read(new File("green.jpg"));
//			blocks[5] = ImageIO.read(new File("magenta.jpg"));
//			blocks[6] = ImageIO.read(new File("pink.jpg"));	
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets a new piece by taking the next piece in the queue, and if
	 * the queue is empty, then it will add more elements to the queue first. 
	 * It then sets the new piece in the top middle of the board. 
	 */
	public void getPiece()
	{
		if (queue.isEmpty())				
			addToQueue(); 
		piece = queue.remove();
		hold_used = false; 		

		resetPosition();			
	}
	
	/**
	 * The position and the rotation of the current piece 
	 * are all all set to default values. 
	 * The default position is the top center of the board, 
	 * and the default rotation is 0. 
	 */
	private void resetPosition()
	{
		posx = COLS/2 -1;			
		posy = 0;		
		rotation = 0;	
	}

	/**
	 * This method shuffles the array with the piece numbers 0-6, and
	 * then puts them into the queue in their shuffled order.
	 */
	private void addToQueue ()	
	{
		shuffle(numbers);
		
		for (int i = 0; i < numbers.length; i++)
			queue.add(numbers[i]); 
	}
	
	/**
	 * This method shuffles the array of numbers, which are later to
	 * be added to the queue. The reason that an array with numbers 0-6
	 * is used instead of a Random Number Generator is to ensure that
	 * all 7 pieces appear an equal number of times. 
	 */
	private void shuffle(int[] a)
	{
		for (int i = 0; i < a.length; i++)
			swap(a, i, (int) (Math.random() * (a.length - i) + i));
	}
	
	/**
	 * This method swaps two elements of an array with each other. 
	 * It is used as an auxiliary method needed for the method shuffle()
	 */
	private void swap(int[] arr, int a, int b)
	{
		int tmp = arr[a];
		arr[a] = arr[b];
		arr[b] = tmp;
	}

	/**
	 * This determines the size of each square of the tetris board,
	 * based on the dimension of the panel in relation to the dimensions 
	 * of the board. It determines whether the game is won/lost. It then 
	 * does all the drawing of the board, ghost piece, piece, next piece
	 * queue, hold queue, text, and the score/level, by calling other methods. 
	 * @param dim        the <code>width</code> and <code>height</code> of the panel
	 * @param g          the abstract base class used to draw all graphics  
	 * @param playMode   whether the game is Marathon or Sprint style Tetris 
	 *                   (Marathon = 0, Sprint= 1)
	 * @param time       the number of seconds the game has started for
	 */
	
	public void display(Graphics g, Dimension dim, int playMode, long time)
	{
		size = Math.min(dim.width, dim.height) / ROWS;
		Dimension boardDim = new Dimension(size * (COLS + 2), size * (ROWS + 2));
		p = new Point((dim.width - boardDim.width) / 2, (dim.height - boardDim.height) / 2);

		if (wonGame || lostGame())
		{
			gameOver = true;
			printGameOverInstructions(dim, g);
			return;
		}

		printBoard(g, dim);
		printGhost(g); 					
		printPiece(g);
		printQueue(g);
		if (hold != -1)						
			printHold(g);
		printText(g);
		printScore(g, playMode);
		if (playMode == SPRINT)			
			printTime(g, time);
	}
	
	/**
	 * This method prints the time left in the 2-minute long game. 
	 * It prints the time in the format MM:SS.  
	 * @param g
	 * @param currTime current Time in seconds
	 */
	public void printTime (Graphics g, long currTime)		
	{		
		g.setColor(Color.WHITE);

		g.setFont(new Font("Helvetica", Font.BOLD, size/2));	
		g.drawString("TIME LEFT", p.x-3*size, p.y+12*size); 
		
		g.setFont(new Font("Helvetica", Font.BOLD, size*3/2)); 
		g.drawString(getTime(currTime), p.x-7*size/2, p.y+14*size); 
	}
	
	/**
	 * Calculates the time remaining depending on the current time. 
	 * It assumes that the game is only 2 minutes long. 
	 * @param currTime current time in seconds 
	 * @return <String> the time formatted in a MM:SS format. 

	 */
	public String getTime (long currTime)
	{
		long timeRemaining = 120 - currTime; 
		long minutesRemaining = timeRemaining/60; 
		long secondsRemaining = timeRemaining - minutesRemaining*60; 
		
		String time = minutesRemaining+  ":" + (String.format("%02d", secondsRemaining)); 
		
		return time;	
	}
	
	/**
	 * Determines if there is any room left at the top centre of the
	 * board for any more pieces can fall. If not, then the player has 
	 * lost the game. 
	 * 
	 *  @return <boolean> if user has lost the game or not
	 */
	public boolean lostGame()
	{
		if (field[1][COLS / 2] > 0)
			return true;

		return false;
	}

	/**
	 * When the game is lost, the following text will be printed
	 * on screen for the user: telling them whether they won or lost, 
	 * @param dim the dimension of the panel 
	 * @param g The class for all graphics contexts that allow an application to draw onto components
	 */
	public void printGameOverInstructions(Dimension dim, Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, dim.width, dim.height);
		
		g.setFont(new Font("SansSerif", Font.BOLD, size/2)); 
		g.setColor (Color.ORANGE); 
	
		g.drawString((wonGame)?"YOU WON!" : "YOU LOST!", dim.width*40/100, dim.height*35/100);
		g.drawString("LINES CLEARED: " +linesCleared, dim.width*40/100, dim.height*40/100);
		g.drawString("PRESS R TO PLAY AGAIN", dim.width*40/100, dim.height*55/100);
	}
	
	/**
	 * This will print the 10x20 grid. 
	 * 0  represents an empty cell, which will be painted gray.
	 * 1-7 represent a piece, which will be painted their respective Tetromino. 
	 * @param g   
	 * @param dim the dimensions of the panel
	 */
	
	public void printBoard(Graphics g, Dimension dim)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, dim.width, dim.height);
		//g.fillRoundRect(p.x + size, p.y + size, size * COLS, size * ROWS, 7, 7);
	
		for (int x = 1; x < COLS + 1; x++)
			for (int y = 1; y < ROWS + 1; y++)
			{
				if (field[y][x] == 0)
				{
					g.setColor(new Color(26, 26, 26));
					g.fillRoundRect(p.x + x * size + 2, p.y + y * size + 2, size - 4, size - 4, 7, 7);
				}
				else  					
				{
					BufferedImage img = blocks[field[y][x] - 1];
					g.drawImage(img, p.x + x * size, p.y + y * size, p.x + x * size + size, p.y + y * size + size, 0, 0, img.getWidth(), img.getHeight(), null);
				}
			}
	}
	
	/**
	 * This will printed the ghost piece, which will show 
	 * where the current piece will land if it is dropped.
	 * The print instructions make an illusion of a grey 
	 * square with a white outline, although in reality 
	 * it is a smaller gray rectangle covering a bigger 
	 * white rectangle. 
	 */
	
	public void printGhost(Graphics g)
	{
		int drop = calculateDrop();

		for (int x = posx; x < posx + pieces[piece][rotation][0].length; x++)
			for (int y = posy; y < posy + pieces[piece][rotation].length; y++)
				if (pieces[piece][rotation][y - posy][x - posx] != 0)
				{
					g.setColor(new Color(110, 110, 110));
					g.fillRect(p.x + x * size + 2, p.y + (y + drop - 2) * size + 2, size - 4, size - 4);

					g.setColor(new Color(26, 26, 26));
					g.fillRect(p.x + x * size + 4, p.y + (y + drop - 2) * size + 4, size - 8, size - 8);
				}
	}

	/**
	 * This calculates how far the ghost piece will be printed below the
	 * current piece, so that it doesn't hit a border or another piece. 
	 *
	 * @return <Integer> the number of squares below that current piece
	 *					the ghost block will be printed
	 */
	public int calculateDrop()
	{
		int drop = 1;

		outer:
		for (drop = 1; drop < ROWS + 2; drop++)
			for (int y = posy; y < posy + pieces[piece][rotation].length; y++)
				for (int x = posx; x < posx + pieces[piece][rotation][0].length; x++)
					if (pieces[piece][rotation][y - posy][x - posx] != 0)
						if (field[y + drop][x] == -1 || field[y + drop][x] > 0)
						{
							drop++;
							break outer;
						}
		return drop;
	}

	/**
	 * Prints the current falling piece in its respective position 
	 * on the board. 
	 */
	public void printPiece(Graphics g)
	{
		for (int x = posx; x < posx + pieces[piece][rotation][0].length; x++)
			for (int y = posy; y < posy + pieces[piece][rotation].length; y++)
				if (pieces[piece][rotation][y - posy][x - posx] > 0 && y >= 1)
				{
					BufferedImage img = blocks[piece];
					g.drawImage(img, p.x + x * size, p.y + y * size, p.x + x * size + size, p.y + y * size + size, 0, 0, img.getWidth(), img.getHeight(), null);
				}
	}

	/**
	 * Prints the Hold, Level, Goal, Next Piece, and 
	 * tells user how to pause/resume the game. 
	 */

	public void printText(Graphics g)
	{
		g.setFont(new Font("Helvetica", Font.BOLD, size/2));
		
		g.setColor(Color.WHITE);
		g.drawString("HOLD:", p.x - 3*size, p.y + 2 * size);
		g.drawString("NEXT PIECE:", p.x+(2+COLS)*size, p.y + 2*size);		

		g.setColor(Color.YELLOW);
		g.drawString("PRESS ESC TO", p.x - 4 * size, p.y + 17 * size);
		g.drawString("PAUSE/RESUME", p.x - 4 * size, p.y + 18 * size);
	}

	/**
	 * Prints the next 5 pieces to the right of the board
	 * It first has to make sure there are at least 5 elements 
	 * in the queue. If not, it adds elements. Then it goes 
	 * ahead to print all of them. 
	 */
	public void printQueue(Graphics g)
	{
		int space = 7*size/2; 
		if (queue.size() < 5)
			addToQueue();

		Queue<Integer> copy = new LinkedList<Integer>();
		copy.addAll(queue);

		for (int i = 0; i < 5; i++)
		{
			int nextPiece = copy.remove();
			for (int x = 0; x < pieces[nextPiece][0][0].length; x++)
				for (int y = 0; y < pieces[nextPiece][0].length; y++)
					if (pieces[nextPiece][0][y][x] > 0)
					{
						BufferedImage img = blocks[nextPiece];
						g.drawImage(img, p.x + (x+COLS+2) * size, p.y + (y+3) * size + i * space, 
										 p.x + (x+COLS+2) * size + size, p.y + (y+3)* size + i * space + size, 
										 0, 0, img.getWidth(), img.getHeight(), null);
					}
		}
	}
	
	/** 
	 * If the hold queue is currently unoccupied, 
	 * the current piece will go to the hold queue. 
	 * If the hold queue is currently occupied, 
	 * the current piece and the hold will swap.
	 * The new piece will appear in the place of the 
	 * old piece after holding, and all positioning 
	 * will be reset (to top centre of board, with rotation 0) 
	 */
	public void hold()
	{
		if (hold_used)
			return;

		if (hold == -1)
		{
			hold = piece;
			piece = queue.remove();
		}

		else
		{
			int tmp = piece;
			piece = hold;
			hold = tmp;
		}

		hold_used = true;
		resetPosition();
	}

	/**
	 * Prints the piece that is currently being held in the hold queue. 
	 * @param g
	 * 
	 */
	public void printHold (Graphics g)
	{
		for (int x = posx; x < posx + pieces[hold][0][0].length; x++)
			for (int y = posy; y < posy + pieces[hold][0].length; y++)
				if (pieces[hold][0][y - posy][x - posx] > 0)
				{
					BufferedImage img = blocks[hold];
					g.drawImage(img, p.x+(x-posx-3)*size, p.y+(y-posy+3)*size, 
									 p.x+(x-posx-3)*size+size, p.y+(y-posy+3)*size+size, 
								     0, 0, img.getWidth(), img.getHeight(), null);
				}
	}

	/**
	 * Prints the current level, and the number of lines required to 
	 * reach the next level. The level and number of lines required 
	 * to reach each level is set in TetrisPanel, thus it will have 
	 * to call upon it to find this information. These variables were 
	 * initialized as static variables in TetrisPanel so that they
	 * could be called upon here.
	 * @param g
	 * @param playMode 0 for Marthon, and 1 for Sprint
	 */

	public void printScore(Graphics g, int playMode)
	{
		g.setColor(Color.WHITE);

		if (playMode == MARATHON)
		{
			g.drawString("LEVEL:", p.x - 3*size, p.y + 7 * size);
			g.drawString("" + TetrisPanel1p.level, p.x - 3*size, p.y+9*size);
			
			g.drawString("GOAL:", p.x - 3*size, p.y + 12 * size);
			int goal = TetrisPanel1p.linesPerLevel[TetrisPanel1p.level];
			g.drawString("" + (goal-linesCleared), p.x - 3*size, p.y+14*size); 
		}
		
		else if (playMode == SPRINT)
		{
			g.drawString("LINES:", p.x - 3*size, p.y + 7 * size);
			g.drawString("" + linesCleared, p.x - 3*size, p.y+9*size);
		}
	}

	/**
	 * The following 2 methods are responsible for moving the 
	 * piece left and right on the board. These methods are called
	 * upon through TetrisPanel, depending on the user input. 
	 * If the piece touches a border, then it can not longer move 
	 * any further. 
	 */
	
	public void shift_right()
	{
		posx++;

		if (isNotClear())
			posx--;
	}

	public void shift_left()
	{
		posx--;

		if (isNotClear())
			posx++;
	}

	/**
	 * This following 2 methods rotate the piece clockwise
	 * and counter-clockwise. They are triggered by
	 * user commands to the keyboard as well. 
	 */
	
	public void rotate_cw()
	{
		rotation = (rotation + 1) % 4;

		if (isNotClear())
			rotation = (rotation - 1 < 0) ? 3 : (rotation - 1);
	}

	public void rotate_ccw()
	{
		rotation = (rotation - 1 < 0) ? (3) : (rotation - 1);

		if (isNotClear())
			rotation = (rotation + 1) % 4;
	}
	
	/**
	 * The following two methods respond to the user's drop 
	 * commands for  the piece. The first method immediately 
	 * brings the piece to the bottom, while the second method
	 * is a slow square-by-square drop. 
	 */
	public void hard_drop()
	{
		while (naturalDrop());
	}
	
	public boolean naturalDrop()
	{
		if (posy+1 == ROWS+4)
			return true;

		posy++;

		if (isNotClear())
		{
			posy--;

			addPiece();
			getPiece();

			return false;
		}
		return true;
	}

	/**
	 * This determines whether the piece has hit either
	 * another piece or a border of the field. If it has, it 
	 * will return true.  
	 * @return whether the piece is has hit another piece or a border
	 *         that prohibits it from continuing to move
	 */
	private boolean isNotClear()
	{
		for (int x = posx; x < posx + pieces[piece][rotation][0].length; x++)
			for (int y = posy; y < posy + pieces[piece][rotation].length; y++)
				if (pieces[piece][rotation][y - posy][x - posx] > 0 && (field[y][x] > 0 || field[y][x] == -1))
					return true;

		return false;
	}

	/**
	 * When a piece is added, it will become part of the field. 
	 * If the row becomes full after a piece is added, it will
	 * remove, or "clear", that row. 
	 */
	private void addPiece()
	{
		for (int y = posy; y < posy + pieces[piece][rotation].length; y++)
		{
			boolean emptyRow = true;
			for (int x = posx; x < posx + pieces[piece][rotation][0].length; x++)
				if (pieces[piece][rotation][y - posy][x - posx] > 0)
				{
					field[y][x] = pieces[piece][rotation][y - posy][x - posx];
					emptyRow = false;
				}

			if (!emptyRow)
			{
				boolean full = true;
				for (int x = 1; x < COLS + 1 && full; x++)
					if (field[y][x] == 0)
						full = false;

				if (full)
					removeRow(y);
			}
		}
	}
	
	/**
	 * If a row is full, then this method will remove that row, 
	 * and move all the rows above it down. The number of 
	 * lines cleared will also increment, which will contribute 
	 * to the level. 
	 */
	public void removeRow(int row)
	{
		boolean clear = false;
		for (int y = row; y >= 1 && !clear; y--)
		{
			clear = true;
			for (int x = 1; x < COLS + 1; x++)
			{
				field[y][x] = field[y - 1][x];

				if (field[y][x] > 0 && clear)
					clear = false;
			}
		}
		linesCleared++;
	}
	
} // end of the class Tetris
