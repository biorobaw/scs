package edu.usf.ratsim.nsl.modules;

import edu.usf.micronsl.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class PlaceIntention extends Module {

	private float[] states;
	private Float1dPort places;
	private Int0dPort goalFeeder;

	public PlaceIntention(String name, Float1dPort places, Int0dPort goalFeeder) {
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
