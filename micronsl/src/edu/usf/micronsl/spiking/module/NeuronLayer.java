package edu.usf.micronsl.spiking.module;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.usf.micronsl.spiking.SpikeEvent;
import edu.usf.micronsl.spiking.SpikeListener;
import edu.usf.micronsl.spiking.neuron.Neuron;

public class NeuronLayer implements SpikeListener {
	
	private ArrayList<Neuron> neurons;
	private LinkedList<SpikeListener> listeners;

	public NeuronLayer(int numNeurons){
		neurons = new ArrayList<Neuron>(numNeurons);
		listeners = new LinkedList<SpikeListener>();
	}

	public void addNeuron(Neuron n){
		neurons.add(n);
		
		n.addSpikeListener(this);
	}
	
	public List<Neuron> getNeurons(){
		return neurons;
	}
	
	public Neuron getNeuron(int i){
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
