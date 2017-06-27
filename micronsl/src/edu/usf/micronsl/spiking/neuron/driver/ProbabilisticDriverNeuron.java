package edu.usf.micronsl.spiking.neuron.driver;

import java.util.Random;

public class ProbabilisticDriverNeuron extends DriverNeuron {

	private Random r;
	private float prob;

	public ProbabilisticDriverNeuron(int id, long spikeDelay, float prob, Random r) {
		super(id, spikeDelay);

		this.r = r;
		this.prob = prob;
	}

	@Override
	public void updateStep(long time) {
		// Spike with probability p
		float sample = r.nextFloat();
		if (sample < prob)
			spike(time);
	}

	@Override
	public void updateAfterSpike(long time) {
		// Do nothing - stateless
	}

}
