package edu.usf.experiment.subject.affordance;

public abstract class Affordance implements Comparable<Affordance> {

	boolean realizable;
	float value;

	public Affordance() {
		realizable = false;
		value = 0;
	}

	public boolean isRealizable() {
		return realizable;
	}

	public void setRealizable(boolean realizable) {
		this.realizable = realizable;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	
	@Override
	public int compareTo(Affordance a) {
		if (realizable == a.realizable)
			if (value < a.value)
				return -1;
			else if (value == a.value)
				return 0;
			else 
				return 1;
		else
			if (!realizable)
				return -1;
			else
				return 1;
	}
	
	public abstract String toString();

	public abstract int getIndex();

}
