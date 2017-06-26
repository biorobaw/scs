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
public abstract class Neuron {
	List<Neuron> outputNeurons;
	HashMap<Neuron, Float> weights;
	public double volts;
	double spike_threshold;
	long lastUpdated;
	long delay;
	long last_spike;
	int[] place;
	public int id;

	public String metadata[] = new String[] {};

	private SpikeEventMgr sEM;
	private LinkedList<SpikeListener> spikeListeners;

	/**
	 * 
	 * @param time
	 * @param s_t
	 * @param d
	 */
	public Neuron(long time, double s_t, long d, int id) {
		outputNeurons = new ArrayList<Neuron>();
		weights = new HashMap<Neuron, Float>();
		volts = 0;
		spike_threshold = s_t;
		delay = d;
		lastUpdated = time;

//		place = new int[2];
//		place[0] = layer_num;
//		place[1] = neu_num;

		this.sEM = SpikeEventMgr.getInstance();
		this.id = id;
		
		this.spikeListeners = new LinkedList<SpikeListener>();
	}

	/**
	 * Connects this neuron to a postsynaptic neuron
	 * @param n The postsynaptic neuron
	 */
	public void addOutputNeuron(Neuron n) {
		outputNeurons.add(n);
	}
	
	/**
	 * Connects a presynaptic neuron to this neuron with a specified connection weight.
	 * @param n The presynaptic neuron.
	 * @param weight The connection weight between the neurons
	 */
	public void addInputNeuron(Neuron n, float weight){
		weights.put(n, weight);
	}
	
	/**
	 * Inputs voltage into the neuron synapses
	 * 
	 * @param time
	 *            the time of the event
	 * @param src
	 *            the presynaptic spiking neuron
	 */
	public void input(long time, Neuron src) {
		// Update inner voltage
		update(time);

		lastUpdated = time;
		// Just add the weight to the inner voltage
		volts += getWeight(src);

		// Check for spiking behavior
		// If currently spiking, do nothing to it
		if (last_spike < time) {
			if (volts >= spike_threshold) {
				spike(time);
			}
		} else {
			volts = 0.0;
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
		sEM.addToLedger(this);
		volts = 0.0;
		// queue voltage signals to all outputs
		for (int i = 0; i < outputNeurons.size(); i++) {
			sEM.queueSpike(this, outputNeurons.get(i), time + outputNeurons.get(i).delay);
		}
		
		for (SpikeListener sl : spikeListeners)
			sl.spikeEvent(new SpikeEvent(this, time));
	}
	
	/**
	 * Get the connection weight for the presynaptic neuron n.
	 * @param n The presynaptic neuron.
	 * @return The connection weight.
	 */
	public float getWeight(Neuron n){
		return weights.get(n);
	}

	/**
	 * Updates the neuron inner state .Should be called when pertinent. e.g.,
	 * neuron receiving voltage
	 * 
	 * @param time
	 *            the time at which the update is called
	 */
	public abstract void update(long time);

	public int[] getLocation() {
		return this.place;
	}

	public void addSpikeListener(SpikeListener listener) {
		spikeListeners.add(listener);
	}

}
