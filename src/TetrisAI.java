import java.awt.*;

/**
 * TetrisAI finds the best rotation of a piece and the best position of it on the board. 
 * It calculates the best rotation and position by taking into account 3 variables: 
 * the number of full rows that will be cleared after the piece is placed (which should be maximized)
 * the number of holes in the board after the piece is placed (which should be minimized), 
 * and the overall bumpiness of the board after the piece is placed (which should be minimized). 
 * The score will be determined by the equation y = 10*numOfCompleteLines() - 11.4*numOfHoles () -2.4*numOfBumps(). 
 * It will calculate the highest score for the current piece as well as the piece in the hold queue. 
 * Finally, the piece, rotation, and position with the highest score will be where the next piece will be placed
 * on the board.  
 *
 */
public class TetrisAI extends Tetris
{
	
	double bestScore;
	int bestPiece, bestRotation, bestX, bestY; 
	 	
	public TetrisAI() 
	{
		
	}
	
	public void draw (Graphics g, Dimension dim, int playMode, long time)					
	{		
		calculate(); 	//calculates best next piece 
		display(g, dim, playMode, time);
	}

	/**
	 * First calculates the best rotation and position of a piece. It then 
	 * determines whether the current piece yields a highest score or 
	 * the piece in the hold queue. It will take the higher of the two. 
	 */

	public void calculate()
	{
		double [] highest = getBestScore(piece); 
		
		bestScore = highest[0]; 
		bestPiece = (int)highest[1]; 
		bestRotation = (int)highest[2] ;
		bestX = (int)highest[3]; 
		bestY = (int)highest[4]; 

		if (!hold_used)
		{
			if (hold == -1)
				highest = getBestScore(queue.peek());
			else
				highest = getBestScore(hold); 
			
			if (highest[0] > bestScore)
			{
				hold(); 

				bestScore = highest[0]; 
				bestPiece = (int)highest[1]; 
				bestRotation = (int)highest[2] ;
				bestX = (int)highest[3]; 
				bestY = (int)highest[4]; 
			}
		}
	}
	
	/**
	 * Puts the piece in every possible rotation and position on the board,
	 * and calculates its score based on three factors: number of complete
	 * rows, number of holes, and the number of bumps. The last two variables
	 * should be minimized which is why they are multiplied by negative constants. 
	 * 
	 * @param pc the current piece
	 * @return the highest score that can be yielded by this piece
	 */
	
	public double [] getBestScore(int pc)
	{
		double [] highest = new double[5]; //bestScore, bestPiece, bestRotation, bestX, best Y
		highest[0] = -10000; 			//initial score is set to be as low as possible 
		
		for (int i = 0; i < 4; i++)											// check each rotation
			for (int j = 1; j < COLS+1-pieces[pc][i][0].length+1; j++) 		// check each column 
			{			
				int k = 0;		
			
				boolean keepDropping = true;
				while (keepDropping)
				{
					if (k + 1 == ROWS + 1)
						break;
					
					k++;

					boolean isNotClear = false;
					outer:for (int y = k; y < k + pieces[pc][i].length; y++)
						for (int x = j; x < j + pieces[pc][i][0].length; x++)	// check if bottom touches anything
							if (pieces[pc][i][y - k][x - j] > 0 && (field[y][x] > 0 || field[y][x] == -1))
							{
								isNotClear = true;
								keepDropping = false;
								break outer;
							}

					if (isNotClear && k >= 1)
					{
						k--;

						for (int y = k; y < k + pieces[pc][i].length; y++)
							for (int x = j; x < j + pieces[pc][i][y - k].length; x++)
								if (pieces[pc][i][y - k][x - j] != 0)
									field[y][x] = 100;

						keepDropping = false;
					}
				}
				
				double score = 10*numOfCompleteLines()  -11.4*numOfHoles () -2.4*numOfBumps() ;

				if (score > highest[0] && k >= 4)
				{
					highest[0] = score;			// highest score 
					highest[1] = pc;			// rotation
					highest[2] = i;				// x coordinate
					highest[3] = j;				// y coordinate
					highest[4] = k;
				}
				
				for (int y = k; y < k + pieces[pc][i].length; y++)
					for (int x = j; x < j + pieces[pc][i][y - k].length; x++)
						if (pieces[pc][i][y - k][x - j] != 0 && field[y][x] == 100)
							field[y][x] = 0;
			}
		return highest; 
	}
	
	/**
	 * Calculates the number of holes in the board. 
	 * A hole is defined as an empty space such that 
	 * there is at least one tile in the same column above it. 
	 * 
	 * @return the number of holes in the board
	 */
	
	public int numOfHoles ()								//minimize: number of holes
	{		
		int holes = 0; 
		for (int i = 1; i < COLS+1; i++ )		
		{
			int top = ROWS+4; 
			for (int j = 1; j < ROWS + 1; j++) 	
			{
				if (field[j][i] != 0)
					top = j;

				if (field[j][i] == 0 && j > top)
					holes++;
			}
		}
		return holes; 
	}

	/**
	 * Calculates the number of full lines that will be cleared.  
	 * We want to maximize this value. 
	 * 
	 * @return the number of complete rows 
	 */
	
	public int numOfCompleteLines()							
	{
		int completeLines = 0;

		for (int y = 1; y < ROWS+1; y++)
		{
			boolean fullRow = true;
			
			for (int x = 1; x < COLS + 1; x++)
				if (field[y][x] == 0)
				{
					fullRow = false;
					break;
				}

			if (fullRow)
				completeLines++;
		}
		return completeLines;
	}
	
	/**
	 * The bumpiness is the total fluctuation of the top of the grid. 
	 * To ensure that the top of the grid is as monotone as possible, 
	 * the AI will try to minimize this value. This value will be 
	 * calculated by taking the sum of the slope of absolute value of 
	 * differences in all adjacent columns 
	 * 
	 * @return the total number of bumps in the grid 
	 */
	
	public int numOfBumps()									// minimize: roughness
	{
		int total = 0;

		int[] heights = new int[COLS];

		for (int x = 1; x < COLS + 1; x++)
			for (int y = 1; y < ROWS + 1; y++)

				if (field[y][x] != 0)
				{
					heights[x - 1] = ROWS + 1 - y;
					break;
				}

		for (int i = 1; i < heights.length; i++)
			total += Math.abs(heights[i] - heights[i - 1]);

		return total;
	}
} 
