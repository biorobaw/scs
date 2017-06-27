package edu.usf.micronsl.spiking.neuron.driver;

import edu.usf.micronsl.spiking.neuron.SpikingNeuron;

/**
 * This class of neurons are use to inject energy into otherwise passive neuron layers.
 * 
 * The update method will be called on each iteration.
 * @author Eduardo Zuloaga
 */
public abstract class DriverNeuron extends SpikingNeuron {

	public DriverNeuron(int id, long spikeDelay) {
		super(id, spikeDelay);
	}

}
