package edu.usf.micronsl.spiking.neuron;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.usf.micronsl.spiking.SpikeEvent;
import edu.usf.micronsl.spiking.SpikeEventMgr;
import edu.usf.micronsl.spiking.SpikeListener;

/**
 *
 * @author Eduardo Zuloaga
 */
public abstract class SpikingNeuron {

	/**
	 * The list of neurons this one is connected to
	 */
	List<InputSpikingNeuron> outputNeurons;

	/**
	 * The delay applied to spikes before they reach their destination in the
	 * following neurons
	 */
	public long delay;
	/**
	 * The timestamp of the last spike
	 */
	long lastSpike;
	/**
	 * An id to identify the neuron within its layer
	 */
	public int id;
	/**
	 * A list of spike listeners
	 */
	private LinkedList<SpikeListener> spikeListeners;

	/**
	 * Constructs this neuron
	 * 
	 * @param time
	 *            the current time
	 * @param s_t
	 *            the spike threshold
	 * @param d
	 *            the delay
	 */
	public SpikingNeuron(int id, long d) {
		outputNeurons = new ArrayList<InputSpikingNeuron>();
		delay = d;

		this.id = id;
		this.spikeListeners = new LinkedList<SpikeListener>();
		addSpikeListener(SpikeEventMgr.getInstance());
	}

	/**
	 * Connects this neuron to a postsynaptic neuron
	 * 
	 * @param n
	 *            The postsynaptic neuron
	 */
	public void addOutputNeuron(InputSpikingNeuron n) {
		outputNeurons.add(n);
	}

	/**
	 * Produce a spiking event that will notify all following neurons. The spike
	 * will only reach the postsynaptic neurons after a specified delay
	 * 
	 * @param time
	 *            the time of the spike event
	 */
	public void spike(long time) {
		// Avoid spiking more than once per simulation step
		if (lastSpike != time){
			SpikeEvent evt = new SpikeEvent(this, time);
			for (SpikeListener sl : spikeListeners)
				sl.spikeEvent(evt);
		}
		// Reset is enforce even if it didn't actually spike to avoid carrying input from a spike step
		updateAfterSpike(time);
		// Record the last spike time
		lastSpike = time;
	}

	/**
	 * Updates the neuron inner state due to the pass of time (simulation step).
	 * 
	 * @param time
	 *            the time at which the update is called
	 */
	public abstract void updateStep(long time);

	/**
	 * Updates the neuron's inner state after a spike event.
	 * 
	 * @param time
	 *            the time at which the update is called
	 */
	public abstract void updateAfterSpike(long time);

	public void addSpikeListener(SpikeListener listener) {
		spikeListeners.add(listener);
	}

	public List<InputSpikingNeuron> getOuputNeurons() {
		return outputNeurons;
	}

}
