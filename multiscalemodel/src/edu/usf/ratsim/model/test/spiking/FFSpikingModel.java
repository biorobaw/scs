package edu.usf.ratsim.model.test.spiking;

import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.NSLSimulation;
import edu.usf.micronsl.spiking.SpikingUtils;
import edu.usf.micronsl.spiking.module.NeuronLayer;
import edu.usf.micronsl.spiking.module.NeuronSet;
import edu.usf.micronsl.spiking.module.ProbSpikingModule;
import edu.usf.micronsl.spiking.module.SpikeEventMgrModule;
import edu.usf.micronsl.spiking.neuron.LeakyIntegratorSpikingNeuron;
import edu.usf.micronsl.spiking.plot.NetPlot;

public class FFSpikingModel extends Model {

	public FFSpikingModel(ElementWrapper params, Robot robot) {
		int numLayers = params.getChildInt("numLayers");
		int cellsPerLayer = params.getChildInt("cellsPerLayer");
		float connProb = params.getChildFloat("connProb");
		float weight = params.getChildFloat("weight");
		long spikeDelay = params.getChildLong("spikeDelay");
		float vDecay = params.getChildFloat("vDecay");
		float spikeThreshold = params.getChildFloat("spikeThreshold");

		SpikeEventMgrModule sEMM = new SpikeEventMgrModule("Spike Event Manager");
		addModule(sEMM);

		ProbSpikingModule drivers = new ProbSpikingModule("Drivers", 10, 1, 0.4f, RandomSingleton.getInstance());
		addModule(drivers);

		long time = NSLSimulation.getInstance().getSimTime();
		NeuronSet prevLayer = drivers;
		for (int l = 0; l < numLayers; l++) {
			NeuronLayer nl = new NeuronLayer("Layer " + l);
			for (int n = 0; n < cellsPerLayer; n++) {
				nl.addNeuron(new LeakyIntegratorSpikingNeuron(n, spikeDelay, vDecay, spikeThreshold, time));
			}
			addModule(nl);
			
			DisplaySingleton.getDisplay().addPlot(new NetPlot("Last Plot", "", "", 50, nl.getNeurons()),
					0, l, 1, 1);

			SpikingUtils.connect(prevLayer.getNeurons(), nl.getNeurons(), connProb, weight,
					RandomSingleton.getInstance());

			prevLayer = nl;
		}
		
		

	}
}
