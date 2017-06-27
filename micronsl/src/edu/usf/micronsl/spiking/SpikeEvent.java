package edu.usf.micronsl.spiking;

import edu.usf.micronsl.spiking.neuron.InputSpikingNeuron;
import edu.usf.micronsl.spiking.neuron.SpikingNeuron;

public class SpikeEvent {

	public InputSpikingNeuron target;
	public long time;
	public SpikingNeuron source;

	public SpikeEvent(SpikingNeuron src, InputSpikingNeuron t, long time) {
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