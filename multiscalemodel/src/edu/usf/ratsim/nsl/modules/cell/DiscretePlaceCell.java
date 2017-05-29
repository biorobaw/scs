package edu.usf.ratsim.nsl.modules.cell;

public class DiscretePlaceCell {

	private int x;
	private int y;

	public DiscretePlaceCell(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public float getActivation(int x, int y) {
		if (this.x == x && this.y == y)
			return 1;
		else 
			return 0;
			
	}

}
