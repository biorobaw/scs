package edu.usf.micronsl.spiking.neuron;

/**
 *
 * @author Eduardo Zuloaga
 */
public class LeakyIntegratorSpikingNeuron extends SpikingNeuron {
	
	private double lambda;
	

	public LeakyIntegratorSpikingNeuron(long time, double s_t, long d, double lambda, int id) {
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
		double v1 = voltage * Math.pow(lambda, deltaT);
		voltage = v1;
		lastUpdated = time;
	}

}
