import java.awt.*;
import java.awt.event.*;

/**
 * Creates a new Program Window with dimensions 1400x700. 
 * It places the TetrisPanelAI panel onto the window. 
 * It implements window listener so that the program
 * can directly be stopped from the panel. 
 *
 */
public class ProgramWindowAI extends Frame implements WindowListener
{
	private TetrisPanelAI panel = new TetrisPanelAI();		

	public ProgramWindowAI()
	{
		setTitle("Tetris: User Vs. Computer");
		setSize(1400, 700);						
		setLocation(25, 75); 						
		setResizable(true); 						
		add(panel); 								
		setVisible(true); 							

		addWindowListener(this);
	}

	public void windowClosing(WindowEvent e)
	{
		dispose();
		System.exit(0);
	}

	public void windowOpened(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

} 
