package edu.usf.micronsl.spiking;

import edu.usf.micronsl.spiking.neuron.SpikingNeuron;

public class SpikeEvent {

	public SpikingNeuron target;
	public long timeStamp;
	public SpikingNeuron source;

	public SpikeEvent(SpikingNeuron src, SpikingNeuron t, long time) {
		target = t;
		source = src;
		timeStamp = time;
	}
	
	public SpikeEvent(SpikingNeuron src, long time) {
		target = null;
		source = src;
		timeStamp = time;
	}
}