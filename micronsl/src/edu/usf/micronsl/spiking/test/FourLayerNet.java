package edu.usf.micronsl.spiking.test;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import edu.usf.micronsl.spiking.SpikeEventMgr;
import edu.usf.micronsl.spiking.module.NeuronLayer;
import edu.usf.micronsl.spiking.neuron.LeakyIntegratorSpikingNeuron;
import edu.usf.micronsl.spiking.neuron.SpikingNeuron;
import edu.usf.micronsl.spiking.neuron.driver.ContinuousDriverNeuron;
import edu.usf.micronsl.spiking.neuron.driver.DriverNeuron;
import edu.usf.micronsl.spiking.neuron.driver.ProbabilisticDriverNeuron;
import edu.usf.micronsl.spiking.plot.ControlPanel;
import edu.usf.micronsl.spiking.plot.NetPlot;

/**
 * @author Eduardo Zuloaga
 */
public class FourLayerNet {

	static List<NeuronLayer> layerList;
	static NeuronLayer inputLayer;
	static NeuronLayer outputLayer;
	static List<DriverNeuron> drivers;
	static List<NetPlot> nplots;
	static SpikeEventMgr sEM;
	static long global_time;
	static double global_spike_threshold;
	static long global_delay;
	static double[] global_weight_range;
	static long oscillator_wavelength;

	public enum OscillatorType {
		CONSTANT, POISSON, PROBABILISTIC
	}

	public FourLayerNet(int numLayers, int[] layerNeuronCount, double[] connectivity, double spikeThreshold, long delay,
			double[] weightRange) {
		// netplot stuff
		layerList = new ArrayList<NeuronLayer>();
		drivers = new ArrayList<DriverNeuron>();
		global_time = 0;
		global_spike_threshold = spikeThreshold;
		global_delay = delay;
		global_weight_range = weightRange;

		// generate all layers, pop onto layer list
		for (int i = 0; i < numLayers; i++) {
			NeuronLayer layer = new NeuronLayer(layerNeuronCount[i]);
			// generate neurons
			for (int j = 0; j < layerNeuronCount[i]; j++) {
				layer.addNeuron(
						new LeakyIntegratorSpikingNeuron(global_time, global_spike_threshold, global_delay, 0.8, j));
			}
			layerList.add(layer);

			// make connections to previous layer
			if (i != 0) {
				Random rand = new Random();
				// fetch previous layer
				NeuronLayer prevLayer = layerList.get(i - 1);
				// for every neuron in the previous layer...
				for (SpikingNeuron prevN : prevLayer.getNeurons()) {
					// for every neuron in current layer...
					for (SpikingNeuron nextN : layer.getNeurons()) {
						// generate a random number from 0 to 1.0
						double randFloat = rand.nextFloat();
						// if number is > connectivity, calculate weight,
						// connect
						if (randFloat < connectivity[i - 1]) {
							float total_weight = (float) (global_weight_range[0]
									+ ((Math.abs(randFloat - 1)) * (global_weight_range[1] - global_weight_range[0])));
							prevN.addOutputNeuron(nextN);
							nextN.addInputNeuron(prevN, total_weight);
						}
					}
				}
			}
		}

		sEM = SpikeEventMgr.getInstance();

		// create plots
		JFrame plots = new JFrame("Spike Plot");
		Container panel = plots.getContentPane();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		nplots = new ArrayList<NetPlot>();
		NetPlot nplot;
		int x_size, y_size;
		x_size = 50;
		for (int i = 0; i < layerList.size(); i++) {
			NeuronLayer layer = layerList.get(i);
			y_size = layer.getNumNeurons();
			nplot = new NetPlot("Layer " + i, "Time", "Neuron", x_size, y_size + 1);
			layer.addSpikeListener(nplot);
			// sEM.registerPlot(nplot);

			nplots.add(nplot);
			panel.add(nplot.getPanel());
		}
		plots.pack();
		plots.setVisible(true);

		inputLayer = layerList.get(0);
		outputLayer = layerList.get(layerList.size() - 1);

		launchConsole(this);

	}

	static void launchConsole(FourLayerNet self) {
		new ControlPanel(self);
	}

	static void printState(long currentTime, NeuronLayer layer) {
		int i = 0;
		System.out.println("Time step: " + currentTime);
		for (SpikingNeuron n : layer.getNeurons()) {
			// feed 0 V to the neuron to update its voltage
			n.update(currentTime);
			System.out.println(i + " - V: " + n.voltage);
		}
		System.out.println("");
	}

	static void plotLayers(long time) {
		for (int layerNum = 0; layerNum < layerList.size(); layerNum++) {
			NetPlot nplot = nplots.get(layerNum);
			nplot.plotSpikes(time);
		}
	}

	public int[] getLayerMetadata() {
		// returns: array of layer sizes
		int[] d = new int[layerList.size()];
		for (int i = 0; i < d.length; i++) {
			d[i] = layerList.get(i).getNumNeurons();
		}
		return d;
	}

	public void addProbabilisticDriver(int l, int n, float v, float prob) {
		DriverNeuron neuron = new ProbabilisticDriverNeuron(global_time, 0.99, 1, prob, drivers.size(), new Random());
		SpikingNeuron post = getNeuron(l, n);
		neuron.addOutputNeuron(post);
		post.addInputNeuron(neuron, v);

		drivers.add(neuron);
	}

	public void addContinuousDriver(int l, int n, float v, float step) {
		DriverNeuron neuron = new ContinuousDriverNeuron(global_time, 0.99, 1, step, drivers.size());
		SpikingNeuron post = getNeuron(l, n);
		neuron.addOutputNeuron(post);
		post.addInputNeuron(neuron, v);

		drivers.add(neuron);
	}

	public void clearOscillators() {
		drivers.clear();
	}

	private SpikingNeuron getNeuron(int l, int n) {
		// returns: nth neuron at layer l
		return layerList.get(l).getNeuron(n);
	}

	public void incrementTime() {
		// factor out auto-spiking
		// keep neuron exciting outside of the
		for (DriverNeuron dn : drivers)
			dn.update(global_time);
		sEM.process(global_time);
		printState(global_time, outputLayer);
		plotLayers(global_time);
		global_time += 1;
	}

	public static void main(String[] args) {
		
	    int[] layers = {10,100,100,20};
	    double[] connectivity = {0.1, 1, 0.1};
	    double global_spike_threshold = 0.6;
	    long global_delay = 1;
	    double[] global_weight_range = {0, 0.2};

		FourLayerNet network = new FourLayerNet(layers.length, layers, connectivity, global_spike_threshold,
				global_delay, global_weight_range);

		for (int i = 0; i < 10; i++)
			// network.addContinuousDriver(0, i, 1.0f, .2f);
			network.addProbabilisticDriver(0, i, 1.0f, .2f);

		TimerTask task = new TimerTask() {
			public void run() {
				network.incrementTime();
			}
		};
		Timer timer = new Timer();

		timer.schedule(task, 1000, 5);

	}

}