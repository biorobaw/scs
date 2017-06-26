package edu.usf.micronsl.spiking.module;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.usf.micronsl.spiking.SpikeEvent;
import edu.usf.micronsl.spiking.SpikeEventMgr;
import edu.usf.micronsl.spiking.SpikeListener;
import edu.usf.micronsl.spiking.neuron.SpikingNeuron;

public class NeuronLayer implements SpikeListener {
	
	private ArrayList<SpikingNeuron> neurons;
	private LinkedList<SpikeListener> listeners;

	public NeuronLayer(int numNeurons){
		neurons = new ArrayList<SpikingNeuron>(numNeurons);
		listeners = new LinkedList<SpikeListener>();
	}

	public void addNeuron(SpikingNeuron n){
		neurons.add(n);
		
		n.addSpikeListener(this);
	}
	
	public List<SpikingNeuron> getNeurons(){
		return neurons;
	}
	
	public SpikingNeuron getNeuron(int i){
		return neurons.get(i);
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
}
