package edu.usf.micronsl.spiking.module;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import edu.usf.micronsl.NSLSimulation;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.spiking.neuron.SpikingNeuron;
import edu.usf.micronsl.spiking.neuron.driver.ProbabilisticDriverNeuron;

public class ProbSpikingModule extends Module implements NeuronSet {

	private Set<SpikingNeuron> cells;

	public ProbSpikingModule(String name, int numCells, long spikeDelay, float prob, Random r) {
		super(name);
		
		cells = new LinkedHashSet<SpikingNeuron>();
		for (int i = 0; i < numCells; i++)
			cells.add(new ProbabilisticDriverNeuron(i, spikeDelay, prob, r));
	}

	@Override
	public void run() {
		long time = NSLSimulation.getInstance().getSimTime();
		for (SpikingNeuron c : cells)
			c.updateStep(time);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	@Override
	public Set<SpikingNeuron> getNeurons() {
		return cells;
	}

}
