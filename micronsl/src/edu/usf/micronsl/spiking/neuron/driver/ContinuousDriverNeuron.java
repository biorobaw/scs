package edu.usf.micronsl.spiking.neuron.driver;

import java.util.Random;

public class ContinuousDriverNeuron extends DriverNeuron {

	private float step;
	private float acc;

	public ContinuousDriverNeuron(int id,  long spikeDelay, float step) {
		super(id, spikeDelay);

		this.step = step;
		this.acc = 0f;
	}

	@Override
	public void updateStep(long time) {
		acc += step;
		if (acc > 1){
			spike(time);
		}
	}

	@Override
	public void updateAfterSpike(long time) {
		acc = 0;		
	}

}
