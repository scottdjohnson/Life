/********************************************************
	LifeStats.java 
	
	Displays the current stats of the board--how many archives of old boards can
		we revert to
*********************************************************/

import java.awt.*;
import javax.swing.*;

public class LifeStats extends JPanel
{
	private JLabel archive;
	private JLabel archs;
	
	private int numArchs;
	private int archiveSize;

	public LifeStats ()
	{
		this.archiveSize = 0;
		
		numArchs = 0;
		
		archive = new JLabel("Archives: " + numArchs);
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(archive);
	}
	
	public void setArchiveSize (int archiveSize)
	{
		this.archiveSize = archiveSize;
	}
	
	public void addGeneration()
	{		
		if (numArchs < archiveSize)
			numArchs++;
			
		archive.setText("Archives: " + numArchs);
	}
	
	public void deleteArchive()
	{
		if (numArchs > 0)
			numArchs--;

		archive.setText("Archives: " + numArchs);
	}
}