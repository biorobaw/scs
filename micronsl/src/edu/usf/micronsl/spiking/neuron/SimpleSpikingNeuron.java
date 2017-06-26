package edu.usf.micronsl.spiking.neuron;

import edu.usf.micronsl.spiking.SpikeEventMgr;

/**
 *
 * @author Eduardo Zuloaga
 */
public class SimpleSpikingNeuron extends Neuron {
	
	private double lambda;
	

	public SimpleSpikingNeuron(long time, double s_t, long d, double lambda, int id) {
		super(time, s_t, d, id);

		this.lambda = lambda;
	}

	@Override
	public void update(long time) {
		if (time == lastUpdated) {
			return;
		}
		// if (volts <= 0.001) {
		// volts = 0.0;
		// return;
		// }
		double deltaT = time - lastUpdated;
		double v1 = volts * Math.pow(lambda, deltaT);
		volts = v1;
		lastUpdated = time;
	}

}
