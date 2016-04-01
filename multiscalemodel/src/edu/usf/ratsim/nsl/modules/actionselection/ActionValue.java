package edu.usf.ratsim.nsl.modules.actionselection;

public final class ActionValue implements Comparable<ActionValue> {

	private int action;
	private float value;

	public ActionValue(int action, float value) {
		super();
		this.action = action;
		this.value = value;
	}

	public int getAction() {
		return action;
	}

	public float getValue() {
		return value;
	}

	@Override
	public int compareTo(ActionValue o) {
		if (value < o.value)
			return -1;
		else if (value == o.value)
			return 0;
		else
			return 1;
	}

}