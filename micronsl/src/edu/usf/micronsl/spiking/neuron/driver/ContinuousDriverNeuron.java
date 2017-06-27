package edu.usf.micronsl.spiking.neuron.driver;

import java.util.Random;

public class ContinuousDriverNeuron extends DriverNeuron {

	private float step;
	private float acc;

	public ContinuousDriverNeuron(long time, double s_t, long d, float step, int id) {
		super(time, s_t, d, id);

		this.step = step;
		this.acc = 0f;
	}

	@Override
	public void update(long time) {
		acc += step;
		if (acc > 1){
			acc = 0;
			spike(time);
		}
	}

}
