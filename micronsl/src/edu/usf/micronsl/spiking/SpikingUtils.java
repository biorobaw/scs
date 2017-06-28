package edu.usf.micronsl.spiking;

import java.util.Random;
import java.util.Set;

import edu.usf.micronsl.spiking.neuron.InputSpikingNeuron;
import edu.usf.micronsl.spiking.neuron.SpikingNeuron;

public class SpikingUtils {

	public static void connect(Set<SpikingNeuron> from, Set<SpikingNeuron> to, float connProb, float weight, Random r) {
		for (SpikingNeuron fromSN : from)
			for (SpikingNeuron toSN : to)
				if (r.nextFloat() < connProb){
					connect(fromSN, (InputSpikingNeuron) toSN, weight);
				}
					
	}

	public static void connect(SpikingNeuron fromSN, InputSpikingNeuron toSN, float weight) {
		fromSN.addOutputNeuron(toSN);
		toSN.addInputNeuron(fromSN, weight);
	}

}
