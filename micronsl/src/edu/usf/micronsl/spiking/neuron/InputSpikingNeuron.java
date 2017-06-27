package edu.usf.micronsl.spiking.neuron;

import java.util.HashMap;

public abstract class InputSpikingNeuron extends SpikingNeuron {

	/**
	 * The synaptic input weights of the neurons connected to this one
	 */
	HashMap<SpikingNeuron, Float> inputWeights;

	public InputSpikingNeuron(int id, long d) {
		super(id, d);

		inputWeights = new HashMap<SpikingNeuron, Float>();
	}

	/**
	 * Connects a presynaptic neuron to this neuron with a specified connection
	 * weight.
	 * 
	 * @param n
	 *            The presynaptic neuron.
	 * @param weight
	 *            The connection weight between the neurons
	 */
	public void addInputNeuron(SpikingNeuron n, float weight) {
		inputWeights.put(n, weight);
	}

	/**
	 * Inputs voltage into the neuron synapses
	 * 
	 * @param time
	 *            the time of the event
	 * @param src
	 *            the presynaptic spiking neuron
	 */
	public void input(long time, SpikingNeuron src) {
		// Update inner voltage
		updateStep(time);

		updateOnInput(src, time);
	}

	public abstract void updateOnInput(SpikingNeuron src, long time);

	/**
	 * Get the connection weight for the presynaptic neuron n.
	 * 
	 * @param n
	 *            The presynaptic neuron.
	 * @return The connection weight.
	 */
	public float getWeight(SpikingNeuron n) {
		return inputWeights.get(n);
	}

}
