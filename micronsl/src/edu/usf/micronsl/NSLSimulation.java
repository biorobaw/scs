package edu.usf.micronsl;

public class NSLSimulation {

	private static NSLSimulation instance = null;
	
	public static NSLSimulation getInstance(){
		if (instance == null)
			instance = new NSLSimulation();
		return instance;
	}

	private long simTime;
	
	private NSLSimulation(){
		simTime = 0;
	}
	
	public long getSimTime(){
		return simTime;
	}
	
	public void incSimTime(){
		simTime++;
	}
}
