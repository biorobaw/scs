package edu.usf.experiment.robot.affordance;

public abstract class Affordance implements Comparable<Affordance> {

	float realizable;
	boolean override;
	float value;

	public Affordance() {
		realizable = 0;
		override = false;
		value = 0;
	}

	public boolean isRealizable() {
		return realizable == 1;
	}

	public float getRealizable() {
		return realizable;
	}

	public void setRealizable(float realizable) {
		this.realizable = realizable;
	}
	
	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	
	@Override
	public int compareTo(Affordance a) {
		if (a.override && !override)
			return -1;
		else if (!a.override && override)
			return 1;
		else
			if (realizable == a.realizable)
				if (value < a.value)
					return -1;
				else if (value == a.value)
					return 0;
				else 
					return 1;
			else
				if (realizable < a.realizable)
					return -1;
				else
					return 1;
	}
	
	public abstract String toString();

	public void setRealizable(boolean realizableBoolean) {
		realizable = realizableBoolean ? 1 : 0;
	}


}
