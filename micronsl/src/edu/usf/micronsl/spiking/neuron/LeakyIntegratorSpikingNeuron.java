package edu.usf.micronsl.spiking.neuron;

/**
 *
 * @author Eduardo Zuloaga
 */
public class LeakyIntegratorSpikingNeuron extends InputSpikingNeuron {

	/**
	 * The decay rate
	 */
	private double lambda;
	/**
	 * The membrane voltage
	 */
	public double voltage;
	/**
	 * Last timestamp of update - used to compute decay analytically
	 */
	long lastUpdated;
	/**
	 * The threshold voltage to trigger a spike
	 */
	private float spikingThreshold;

	public LeakyIntegratorSpikingNeuron(int id, long spikeDelay,  float lambda, float spikingThreshold, long time) {
		super(id, spikeDelay);

		this.lambda = lambda;
		this.spikingThreshold = spikingThreshold;
		this.lastUpdated = time;
	}

	@Override
	public void updateStep(long time) {
		// The decay is computed analytically from the state and time of the
		// last update
		double deltaT = time - lastUpdated;
		double v1 = voltage * Math.pow(lambda, deltaT);
		voltage = v1;
		lastUpdated = time;
	}

	@Override
	public void updateOnInput(SpikingNeuron src, long time) {
		// Just add the weight to the inner voltage
		voltage += getWeight(src);
		
		if (voltage > spikingThreshold)
			spike(time);
	}

	@Override
	public void updateAfterSpike(long time) {
		// Just reset the voltage to the resting state
		voltage = 0;
	}

}
