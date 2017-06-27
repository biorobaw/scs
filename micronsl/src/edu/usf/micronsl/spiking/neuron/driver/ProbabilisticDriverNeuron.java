package edu.usf.micronsl.spiking.neuron.driver;

import java.util.Random;

public class ProbabilisticDriverNeuron extends DriverNeuron {

	private Random r;
	private float prob;

	public ProbabilisticDriverNeuron(long time, double s_t, long d, float prob, int id, Random r) {
		super(time, s_t, d, id);

		this.r = r;
		this.prob = prob;
	}

	@Override
	public void update(long time) {
		// Spike with probability p
		float sample = r.nextFloat();
		if (sample < prob)
			spike(time);
	}

}
