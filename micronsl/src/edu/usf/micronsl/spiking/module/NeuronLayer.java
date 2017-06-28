package edu.usf.micronsl.spiking.module;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.map.HashedMap;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.spiking.SpikeEvent;
import edu.usf.micronsl.spiking.SpikeListener;
import edu.usf.micronsl.spiking.neuron.LeakyIntegratorSpikingNeuron;
import edu.usf.micronsl.spiking.neuron.SpikingNeuron;

public class NeuronLayer extends Module implements SpikeListener, NeuronSet {
	
	private Set<SpikingNeuron> neurons;
	private Map<Integer, SpikingNeuron> neuronsById;
	private LinkedList<SpikeListener> listeners;
	
	public NeuronLayer(){
		this("Anon Layer");
	}

	public NeuronLayer(String name){
		super(name);
		neurons = new LinkedHashSet<SpikingNeuron>();
		neuronsById = new HashedMap<Integer, SpikingNeuron>();
		listeners = new LinkedList<SpikeListener>();
	}

	public void addNeuron(SpikingNeuron n){
		neurons.add(n);
		neuronsById.put(n.id, n);
		
		n.addSpikeListener(this);
	}
	
	public Set<SpikingNeuron> getNeurons(){
		return neurons;
	}
	
	public SpikingNeuron getNeuron(int id){
		return neuronsById.get(id);
	}
	
	public int getNumNeurons() {
		return neurons.size();
	}
	
	public void addSpikeListener(SpikeListener sl){
		listeners.add(sl);
	}

	@Override
	public void spikeEvent(SpikeEvent e) {
		for (SpikeListener sl : listeners)
			sl.spikeEvent(e);
	}

	@Override
	public void run() {
		// Do nothing on run - Spiking neurons do not need update on every step
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
}
