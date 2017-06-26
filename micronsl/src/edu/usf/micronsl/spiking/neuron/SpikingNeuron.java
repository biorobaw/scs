package edu.usf.micronsl.spiking.neuron;

import java.util.ArrayList;
import java.util.HashMap;
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
	List<SpikingNeuron> outputNeurons;
	/**
	 * The synaptic input weights of the neurons connected to this one
	 */
	HashMap<SpikingNeuron, Float> inputWeights;
	/**
	 * The membrane voltage
	 */
	public double voltage;
	/**
	 * The threshold upon which the neuron fires
	 */
	double spike_threshold;
	/**
	 * Last timestamp of update - used to compute decay analytically
	 */
	long lastUpdated;
	/**
	 * The delay applied to spikes before they reach their destination in the following neurons
	 */
	long delay;
	/**
	 * The timestamp of the last spike
	 */
	long last_spike;
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
	 * @param time the current time
	 * @param s_t the spike threshold
	 * @param d the delay
	 */
	public SpikingNeuron(long time, double s_t, long d, int id) {
		outputNeurons = new ArrayList<SpikingNeuron>();
		inputWeights = new HashMap<SpikingNeuron, Float>();
		voltage = 0;
		spike_threshold = s_t;
		delay = d;
		lastUpdated = time;

//		place = new int[2];
//		place[0] = layer_num;
//		place[1] = neu_num;

		this.id = id;
		
		this.spikeListeners = new LinkedList<SpikeListener>();
		addSpikeListener(SpikeEventMgr.getInstance());
	}

	/**
	 * Connects this neuron to a postsynaptic neuron
	 * @param n The postsynaptic neuron
	 */
	public void addOutputNeuron(SpikingNeuron n) {
		outputNeurons.add(n);
	}
	
	/**
	 * Connects a presynaptic neuron to this neuron with a specified connection weight.
	 * @param n The presynaptic neuron.
	 * @param weight The connection weight between the neurons
	 */
	public void addInputNeuron(SpikingNeuron n, float weight){
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
		update(time);

		lastUpdated = time;
		// Just add the weight to the inner voltage
		voltage += getWeight(src);

		// Check for spiking behavior
		// If currently spiking, do nothing to it
		if (last_spike < time) {
			if (voltage >= spike_threshold) {
				spike(time);
			}
		} else {
			voltage = 0.0;
		}
		return;
	}
	
	/**
	 * Produce a spiking event that will notify all following neurons. The spike
	 * will only reach the postsynaptic neurons after a specified delay
	 * 
	 * @param time
	 *            the time of the spike event
	 */
	public void spike(long time) {
		last_spike = time;
		voltage = 0.0;

		SpikeEvent evt = new SpikeEvent(this, time);
		for (SpikeListener sl : spikeListeners)
			sl.spikeEvent(evt);
	}
	
	/**
	 * Get the connection weight for the presynaptic neuron n.
	 * @param n The presynaptic neuron.
	 * @return The connection weight.
	 */
	public float getWeight(SpikingNeuron n){
		return inputWeights.get(n);
	}

	/**
	 * Updates the neuron inner state .Should be called when pertinent. e.g.,
	 * neuron receiving voltage
	 * 
	 * @param time
	 *            the time at which the update is called
	 */
	public abstract void update(long time);

	public void addSpikeListener(SpikeListener listener) {
		spikeListeners.add(listener);
	}

	public List<SpikingNeuron> getOuputNeurons() {
		return outputNeurons;
	}

}
