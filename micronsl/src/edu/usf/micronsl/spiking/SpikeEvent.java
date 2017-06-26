package edu.usf.micronsl.spiking;

import edu.usf.micronsl.spiking.neuron.Neuron;

public class SpikeEvent {

	public Neuron target;
	public long timeStamp;
	public Neuron source;

	public SpikeEvent(Neuron src, Neuron t, long time) {
		target = t;
		source = src;
		timeStamp = time;
	}
	
	public SpikeEvent(Neuron src, long time) {
		target = null;
		source = src;
		timeStamp = time;
	}
}