/********************************************************
	LifeCell class, stores only high-level info, whether the cell is living and flipping its state from
		living to dead and vice-versa
*********************************************************/

import java.awt.Graphics;

public class LifeCell 
{

	private boolean cell;

	public LifeCell()
	{
		cell = false;
	}
	
	public LifeCell(int height, int width)
	{
		cell = false;
	}
		
	public boolean isLiving ()
	{
		return cell;
	}
	
	public void flipState()
	{
		cell = !cell;
	}

}