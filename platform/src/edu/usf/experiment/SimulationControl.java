package edu.usf.experiment;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

/**
 * This class provides controls for the simulation
 * It allows to set delays in the simulation as well as to execute on a step by step basis
 * 
 * Step by step execution is implemented in a producer consumer scheme
 * When running, consumers can always consume 
 * When pausing, all available steps are consumed
 * Steps are produced by the user by means of the GUI or other input
 */
public class SimulationControl {

	
	//execution contriol variables
	//implement modified consumer producer for doing step by step execution
	static private Semaphore waitSemaphore = new Semaphore(0);
	static private int stepsAvailable = 0;
	static private boolean paused = false;
	
	private static int[] sleepValues = new int[] {5000,3000,2000,1000,500,400,300,100,30,0};
	private static int simulationSpeed = 9;	
	
	static LinkedList<Function<Boolean,Void>> pauseStateListeners = new LinkedList<>();
	static LinkedList<Function<Integer,Void>> simulationSpeedListeners = new LinkedList<>();	
	
	/**
	 * Function to be called by the user interface to execute an extra step.
	 */
	static synchronized public void produceStep() {
		if(paused) {
			stepsAvailable++;
			if(stepsAvailable<=0) waitSemaphore.release();
		}
	}
	
	/**
	 * Function that implements the consumer
	 * returns wheter it was able to consume 
	 */
	static synchronized private boolean consumeStep() {
		//if not paused, there are infinite steps,
		if(paused){   
        	stepsAvailable--;
        	if(stepsAvailable<0) {
        		return false;
        	}

        } 
		return true;
	}
	
	
	static public void waitIfPaused() {
		
		try {
			if(!consumeStep()) waitSemaphore.acquire(); //if cant consume wait until step is produced
			else {
				int sleep = sleepValues[getSimulationSpeed()];
				if(sleep>0) Thread.sleep(sleep);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	static synchronized public boolean togglePause() {
		//toggle
		paused=!paused;
		
		//if resuming, check weather a consumer is waiting, then remove any remaining steps
		if(!paused){
			if(stepsAvailable<0) {
				waitSemaphore.release();
			}
			stepsAvailable=0;
		}

		//return the new value of pause
		
		for(var l : pauseStateListeners) l.apply(paused);
		return paused; 
	}
	
	
	synchronized public static void setSimulationSpeed(int simulationSpeed) {
		SimulationControl.simulationSpeed = simulationSpeed;
		for(var l : simulationSpeedListeners) l.apply(simulationSpeed);
	}
	
	synchronized public static int getSimulationSpeed() {
		return simulationSpeed;
	}

	
	synchronized public static void addSimulationSpeedListener(Function<Integer,Void> r) {
		simulationSpeedListeners.add(r);
	}
	
	synchronized public static void addPauseStateListener(Function<Boolean,Void> r) {
		pauseStateListeners.add(r);
	}

}
