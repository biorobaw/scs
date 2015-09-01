package edu.usf.ratsim.nsl.modules;

import edu.usf.ratsim.micronsl.Float1dPortArray;
import edu.usf.ratsim.micronsl.Float1dPort;
import edu.usf.ratsim.micronsl.IntPort;
import edu.usf.ratsim.micronsl.Module;

public class PlaceIntention extends Module {

	private float[] states;
	private Float1dPort places;
	private IntPort goalFeeder;

	public PlaceIntention(String name, Float1dPort places, IntPort goalFeeder) {
		super(name);
		this.goalFeeder = goalFeeder;
		this.places = places;
		states = new float[goalFeeder.getSize() * places.getSize()];
		addOutPort("states", new Float1dPortArray(this, states));
	}

	public void run() {
		for (int i = 0; i < states.length; i++)
			states[i] = 0;

		for (int i = 0; i < places.getSize(); i++) {
			goalFeeder.get();
			places.get(i);
			states[goalFeeder.get() * places.getSize() + i] = places.get(i);
		}
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
}
