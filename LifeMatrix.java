/********************************************************
	LifeMatrix.java 

	This is the "game board" for the Game of Life.
	This class contains the actual x by y matrix of cells, as well as
		methods for manipulating and painting the cells from a board-wide perspective.
*********************************************************/

import java.awt.*;
import javax.swing.*;
import java.awt.Graphics;

public class LifeMatrix extends JPanel implements Runnable
{
	// Constants
	private static final int CELL_WIDTH		= 5;
	private static final int CELL_HEIGHT		= 5;
	private static final int MATRIX_WIDTH		= 500;
	private static final int MATRIX_HEIGHT		= 500;
	private static final int TOP_MATRIX		= 40;
	private static final int BUFFER_SIZE 		= 100;
	private static final Color LIVING_COLOR		= Color.black;
	private static final Color DEAD_COLOR		= Color.white;
	private static final Color HOVER_COLOR		= Color.gray;

	// External classes
	private CircularBuffer archive;
	private LifeStats stats;
	
	// Class members
	private LifeCell matrixCurrent[][];
	private int cellHeight;
	private int cellWidth;
	private Point hoverPoint;
	private Thread runner;


	public LifeMatrix( int height, int width, int cellHeight, int cellWidth)
	/*
		Constructor for LifeMatrxix
		Create the board with some number of cells of some size, plus create the archive for
		storing the board history
	*/
	{
		archive = new CircularBuffer(BUFFER_SIZE);
		
		matrixCurrent = new LifeCell[height][width];
		this.cellHeight	= cellHeight;
		this.cellWidth	= cellWidth;
		
		int lenX = matrixCurrent.length;
		int lenY = matrixCurrent[0].length;
		
		for (int x = 0; x < lenX; x++)
		{
			for (int y = 0; y < lenY; y++)
			{
				matrixCurrent[x][y] = new LifeCell();
			}
		}
		
	}
	
	public void hitCell ( Point p )
	// Click on a cell at point P, turn it "on" or "off"
	{
		Point cell = roundPoint(p);
		matrixCurrent[cell.x][cell.y].flipState();
		repaint();
	}
	
	public boolean getState ( Point p )
	// Get the state of the cell at point p
	{
		return matrixCurrent[p.x][p.y].isLiving();
	}	
	
	public void paintComponent(Graphics g)
	// Paint the board at it's current state
	{
		super.paintComponent(g);
		
		int lenX = matrixCurrent.length;
		int lenY = matrixCurrent[0].length;

		g.setColor(Color.gray);

		for (int i = 0; i <= MATRIX_WIDTH; i += CELL_WIDTH)
			g.drawLine(i, 0, i, MATRIX_WIDTH);

		for (int i = 0; i <= MATRIX_HEIGHT; i += CELL_HEIGHT)
			g.drawLine( 0, i, MATRIX_HEIGHT, i);

		try {
			if (hoverPoint.getX() >= 0 &&
				hoverPoint.getX() < MATRIX_WIDTH/CELL_WIDTH &&
				hoverPoint.getY() >= 0 && //TOP_MATRIX/CELL_HEIGHT && // Adjust for paintCell position adjustment
				hoverPoint.getY() < MATRIX_HEIGHT/CELL_HEIGHT)
				paintCell(g, hoverPoint, HOVER_COLOR);
		}
		catch (NullPointerException e)
		{
			/*
				No need to do anything, but if hoverPoint is null we want to not fail
			*/
		}

		for (int x = 0; x < lenX; x++)
		{
			for (int y = 0; y < lenY; y++)
			{
				if (matrixCurrent[x][y].isLiving())
					paintCell(g, new Point (x,y), LIVING_COLOR);
			}
		}
	}
	
	public void stepForward()
	// Create a new matrix, move one step forward in the Game of Lige, replace the current matrix with the new one
	{
		int lenX = matrixCurrent.length;
		int lenY = matrixCurrent[0].length;
	
		LifeCell matrixTemp[][] = new LifeCell[lenX][lenY];
	
		// Create a new matrix based on the old matrix, but moving one step forward
		for (int x = 0; x < lenX; x++)
		{
			for (int y = 0; y < lenY; y++)
			{
				matrixTemp[x][y] = new LifeCell();
				if (doesCellLive(x,y))
					matrixTemp[x][y].flipState();
			}
		}
		
		archive.push(matrixCurrent);
		matrixCurrent	= matrixTemp;
		
		stats.addGeneration();
		
		repaint();
	}

	public void stepBackward()
	// Move one step backward in time, if there is a history in the archive
	{
		Object tmp = archive.pop();

		if (tmp != null)
			matrixCurrent = (LifeCell[][])tmp;

		stats.deleteArchive();
		
		repaint();
	}
	
	private boolean doesCellLive(int x, int y)
	/*
		Determine whether a cell lives or dies, based on the its current environment
		We do this the easy way--we consider the outer boundaries of the board to always be dead cells
	*/
	{		
		int alive	= 0;
		int dead	= 0;


		/*
			We look at the "previous" x and y locations and the "next" x and y locations, where 
				"previous" is one less and "next" is one more than the current location
			We use these values to look at the cells at the neighboring cells to determine how many 					neighboring cells are dead and how many are alive.
			We do NOT need to know how many dead cells there are, but we may want that info in the future so 				we go ahead and count them anyway
		*/
		int prev_x  	= x - 1;
		int prev_y	= y - 1;
		int next_x  	= x + 1;
		int next_y	= y + 1;
		
		// prev x prev y
		if(prev_x < 0 || prev_y < 0)
			dead++;
		else if (matrixCurrent[prev_x][prev_y].isLiving())
			alive++;
		else 
			dead++;

		// next x next y
		if(next_x == matrixCurrent.length || next_y == matrixCurrent[0].length)
			dead++;
		else if (matrixCurrent[next_x][next_y].isLiving())
			alive++;
		else 
			dead++;

		// prev x next y
		if(prev_x < 0 || next_y == matrixCurrent[0].length)
			dead++;
		else if (matrixCurrent[prev_x][next_y].isLiving())
			alive++;
		else 
			dead++;
		
		// next x prev y
		if(next_x == matrixCurrent.length  || prev_y < 0)
			dead++;
		else if (matrixCurrent[next_x][prev_y].isLiving())
			alive++;
		else 
			dead++;

		// x prev y
		if(prev_y < 0)
			dead++;
		else if (matrixCurrent[x][prev_y].isLiving())
			alive++;
		else 
			dead++;

		// x next y
		if(next_y == matrixCurrent[0].length)
			dead++;
		else if (matrixCurrent[x][next_y].isLiving())
			alive++;
		else 
			dead++;

		// prev x y
		if(prev_x < 0)
			dead++;
		else if (matrixCurrent[prev_x][y].isLiving())
			alive++;
		else 
			dead++;
		
		// next x y
		if(next_x == matrixCurrent.length)
			dead++;
		else if (matrixCurrent[next_x][y].isLiving())
			alive++;
		else 
			dead++;
												
		if (alive == 3 || (alive == 2 && matrixCurrent[x][y].isLiving()))
			return true;
		else
			return false;

	}

	public void hover (Point p)
	// The mouseover point is always "on"
	{
		hoverPoint = roundPoint(p);
		repaint();
	}
	
	public void noHover()
	// Turn off the last mouseover point
	{
		hoverPoint = null;
		repaint();
	}
	
	public Point roundPoint ( Point p )
	// Turn the screen point into a matrix point
	{
		p.x = (p.x - (p.x % cellWidth)) / cellWidth;
		p.y = (p.y - (p.y % cellHeight)) / cellHeight;
		
		return p;
	}

	private void paintCell(Graphics g, Point p, Color c)
	{
		g.setColor (c);
		g.fillRect ((int)p.getX() * cellWidth, (int)p.getY() *  cellHeight, cellHeight, cellWidth );
	}
			
	public void go()
	{
		runner = new Thread(this);
		runner.start();
	}
	
	public void stop()
	{	
		runner = null;
	}
	
	public synchronized void run()
	{
		Thread thisThread = Thread.currentThread();
		while (thisThread == runner) {
			stepForward();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { }
		}
		
	}

	public void addStats(LifeStats ls)
	// Add the stats to the matrix for displaying
	{
		stats = ls;
		stats.setArchiveSize(BUFFER_SIZE);
	}
}