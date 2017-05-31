package edu.usf.ratsim.nsl.modules.celllayer;

public class DiscreteCoord {
	public int x, y;
	
	public DiscreteCoord(int x, int y){
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DiscreteCoord))
			return false;
		
		return ((DiscreteCoord)obj).x == x && ((DiscreteCoord)obj).y == y;
	}

	@Override
	public int hashCode() {
		return 31 * x + y;
	}

	
}
