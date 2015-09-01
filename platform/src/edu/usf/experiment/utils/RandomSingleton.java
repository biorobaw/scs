package edu.usf.experiment.utils;

import java.util.Random;

public class RandomSingleton {

	private static Random instance = null;

	public static Random getInstance() {
		if (instance == null)
			instance = new Random();
		
		return instance;
	}
}
