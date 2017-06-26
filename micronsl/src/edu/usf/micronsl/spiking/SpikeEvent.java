package edu.usf.micronsl.spiking;

import edu.usf.micronsl.spiking.neuron.SpikingNeuron;

public class SpikeEvent {

	public SpikingNeuron target;
	public long time;
	public SpikingNeuron source;

	public SpikeEvent(SpikingNeuron src, SpikingNeuron t, long time) {
		target = t;
		source = src;
		this.time = time;
	}

	public SpikeEvent(SpikingNeuron src, long time) {
		target = null;
		source = src;
		this.time = time;
	}

}