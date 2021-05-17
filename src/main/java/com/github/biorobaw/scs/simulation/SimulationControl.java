package com.github.biorobaw.scs.simulation;

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
	static private boolean exited = false;
	
	// mutex to lock / unlock simulation from gui, gui sometimes needs to lock simulation to get data.
	static private Semaphore control = new Semaphore(0);
	
	public static int[] sleepValues = new int[] {5000,3000,2000,1000,500,400,300,100,30,15,8,5,3,2,1,0};
	private static int simulationSpeed = sleepValues.length-1;	
	
	static LinkedList<Function<Boolean,Void>> pauseStateListeners = new LinkedList<>();
	static LinkedList<Function<Integer,Void>> simulationSpeedListeners = new LinkedList<>();	
	
	/**
	 * Function to be called by the user interface to execute an extra step when paused.
	 */
	static synchronized public void produceStep() {
		if(paused) {
			stepsAvailable++;
			if(stepsAvailable<=0) waitSemaphore.release();
		}
	}
	
	/**
	 * Function that implements a consumer of "steps" to simulate.
	 * @return Returns whether it was able to consume. 
	 * If not paused, the function always returns true.
	 * Otherwise it returns true if a step has been produced by the user. 
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
	
	/**
	 * A function that waits until the simulation can proceed with the next cycle.
	 */
	static long previousTime =0;
	static public void waitIfPaused() {
		
		try {
			if(!consumeStep()) {
				control.release();
				waitSemaphore.acquire(); //if cant consume wait until step is produced
				control.acquire();
			}
			else {
				int sleep = sleepValues[getSimulationSpeed()];
				long now = System.currentTimeMillis();
				long remaining = previousTime + sleep - now; 
				if(remaining>0) Thread.sleep(remaining);
				previousTime = now;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * A function to toggle the pause on or off.
	 * @return	Returns true if the simulation is paused and false otherwise.
	 */
	static synchronized public boolean togglePause() {
		//toggle
		asyncSetPause(!paused);
		return paused; 
	}
	
	/**
	 * A function to pause the simulation.
	 * @param new_value
	 * @return Returns the old value of pause
	 */
	static synchronized public boolean setPause(boolean new_value) {
		boolean old_value = paused;
		asyncSetPause(new_value);
		return old_value;
	}
	
		
	static public void pauseAndGetControl() {
		if(exited) return;
		setPause(true);
		control.acquireUninterruptibly();
	}
	
	static public void releaseControl() {
		control.release();
		setPause(false);
	}
	
	static private void asyncSetPause(boolean new_value) {
		// if new value == old value, dont do anything
		if(new_value == paused) return;
		
		paused=new_value;
		
		//if resuming, check weather a consumer is waiting, then remove any remaining steps
		if(!paused){
			if(stepsAvailable<0) {
				waitSemaphore.release();
			}
			stepsAvailable=0;
		}

		//return the new value of pause
		
		for(var l : pauseStateListeners) l.apply(paused);
	}
	
	static synchronized public boolean getPause() {
		return paused;
	}
	
	public static void exit() {
		
		exited = true;
		control.release();
	}
	
	/**
	 * Sets the simulation speed
	 * @param simulationSpeed 0 is the slowest and 15 is the maximum
	 */
	synchronized public static void setSimulationSpeed(int simulationSpeed) {
		SimulationControl.simulationSpeed = simulationSpeed;
		for(var l : simulationSpeedListeners) l.apply(simulationSpeed);
	}
	
	/**
	 * @return Returns the simulation speed, where 0 is the slowest and 15 is the maximum
	 */
	synchronized public static int getSimulationSpeed() {
		return simulationSpeed;
	}

	/**
	 * Adds a listener that waits for changes in simulation speed.
	 * @param r The listener
	 */
	synchronized public static void addSimulationSpeedListener(Function<Integer,Void> r) {
		simulationSpeedListeners.add(r);
	}
	
	/**
	 * Adds a listener that waits for changes to the simulation state "pause".
	 * @param r The listener
	 */
	synchronized public static void addPauseStateListener(Function<Boolean,Void> r) {
		pauseStateListeners.add(r);
	}

}
