package common;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Event;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameOfLife extends JFrame implements Runnable
{
	private CellSpace cellSpace;
	private Thread gameThread = null;
	private int genTime = 500;
	private final String clear = "Clear";
	private final String slow = "Slow";
	private final String fast = "Fast";
	private final String hyper = "Hyper";
	private final String nextLabel = "Next";
	private final String startLabel = "Start";
	private final String stopLabel = "Stop";
	private JLabel genLabel;
	private Button startstopButton;

	public GameOfLife()
	{
		int cellSize = 15;
		int cellCols = 10;
		int cellRows = 10;
		String param = null;

		setTitle("Game of Life");
		// set background
		setBackground( new Color( 0x999999 ) );

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// create components and add them to container
		cellSpace = new CellSpace( cellSize, cellCols, cellRows );

		Choice speed = new Choice();
		speed.addItem( slow );
		speed.addItem( fast );
		speed.addItem( hyper );

		genLabel = new JLabel( "Generations: 0             " );

		startstopButton = new Button( startLabel );

		JPanel controls = new JPanel();
		controls.add( new Button( nextLabel ));
		controls.add( startstopButton );
		controls.add( speed );
		controls.add( genLabel );

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add( controls, BorderLayout.SOUTH );
		getContentPane().add( cellSpace, BorderLayout.NORTH );
		pack(); 
		setVisible(true);
	}

	// no start() to prevent starting immediately
	public void start2() {
		if(gameThread == null) {
			gameThread = new Thread(this);
			gameThread.start();
		}
	}

	public void stop() {
		if(gameThread != null) {
			gameThread.stop();
			gameThread = null;
		}
	}

	public void run() {
		while (gameThread != null) {
			cellSpace.next();
			cellSpace.repaint();
			showGenerations();
			try {
				gameThread.sleep( genTime );
			} catch (InterruptedException e){}
		}
	}

	public boolean action(Event evt, Object arg) {
		if( clear.equals( arg ) ) // clear
		{
			cellSpace.clear();
			cellSpace.repaint();
			showGenerations();
			return true;
		}
		else if(nextLabel.equals(arg)) // next
		{
			cellSpace.next();
			cellSpace.repaint();
			showGenerations();
			return true;
		}
		else if(startLabel.equals(arg)) // start
		{
			start2();
			startstopButton.setLabel( stopLabel );
			return true;
		}
		else if(stopLabel.equals(arg)) // stop
		{
			stop();
			startstopButton.setLabel( startLabel );
			return true;
		}
		else if(slow.equals(arg)) // slow
		{
			genTime = 500;
			return true;
		}
		else if(fast.equals(arg)) // fast
		{
			genTime = 100;
			return true;
		}
		else if(hyper.equals(arg)) // hyperspeed
		{
			genTime = 0;
			return true;
		}
		return false;
	}

	// show number of generations
	public void showGenerations() {
		genLabel.setText( "Generations: "+cellSpace.generations );
	}

	public static void main(String[] args) {
		GameOfLife gol = new GameOfLife();
	}
}
