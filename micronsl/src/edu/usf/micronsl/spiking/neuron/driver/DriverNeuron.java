/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usf.micronsl.spiking.neuron.driver;

import java.util.Random;

import edu.usf.micronsl.spiking.SpikeEventMgr;
import edu.usf.micronsl.spiking.neuron.SpikingNeuron;

/**
 * This class of neurons are use to inject energy into otherwise passive neuron layers.
 * 
 * The update method will be called on each iteration.
 * @author Eduardo Zuloaga
 */
public abstract class DriverNeuron extends SpikingNeuron {

	// classifications:
	// 0: linear
	// 1: probabilistic
	// 2: poisson

	// parameter used in following fashion per classification:
	// 0: rate of firing (per timestep), between
	// 1: probability of firing (per timestep, between
	// 2: lambda of poisson distribution

	public DriverNeuron(long time, double s_t, long d, int id) {
		super(time, s_t, d, id);
	}

}
