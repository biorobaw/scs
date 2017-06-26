/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usf.micronsl.spiking.neuron;

import java.util.Random;

import edu.usf.micronsl.spiking.SpikeEventMgr;

/**
 * @author Eduardo Zuloaga
 */
public class OscillatorNeuron extends SpikingNeuron {

	// classifications:
	// 0: linear
	// 1: probabilistic
	// 2: poisson

	// parameter used in following fashion per classification:
	// 0: rate of firing (per timestep), between
	// 1: probability of firing (per timestep, between
	// 2: lambda of poisson distribution

	private int nclass;
	private double param;
	private double toyval;
	Random rand;

	public OscillatorNeuron(long time, double s_t, long d, int classification, double parameter, int id) {
		super(time, s_t, d, id);
		nclass = classification;
		param = parameter;
		if (param < 0.0)
			param = 0.0;
		if (param > 1.0)
			param = 1.0;
		toyval = 0.0;
		rand = new Random();
		
	}

	public boolean readyToFire() {
		switch (nclass) {
		case 0:
			toyval += param;
			if (toyval >= 1.0) {
				toyval -= 1.0;
				return true;
			} else {
				return false;
			}
		case 1:
			toyval = rand.nextFloat();
			if (toyval < param) {
				return true;
			} else {
				return false;
			}
		case 2:
			return true;
		default:
			return false;
		}
	}

	@Override
	public void update(long time) {
		// TODO Auto-generated method stub

	}

}
