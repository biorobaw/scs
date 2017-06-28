package edu.usf.micronsl.spiking.module;

import java.util.Set;

import edu.usf.micronsl.spiking.neuron.SpikingNeuron;

public interface NeuronSet {

	public Set<SpikingNeuron> getNeurons();
	
}
