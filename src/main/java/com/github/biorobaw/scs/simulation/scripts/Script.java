package com.github.biorobaw.scs.simulation.scripts;

public interface Script extends Runnable {

	/**
	 * Function to signal the script that an episode is about to start
	 */
	default public void newEpisode() {}
	
	/**
	 * Function to signal the script that an episode has finished
	 */
	default public void endEpisode() {}
	
	
	/**
	 * Function to signal the script that a trial is about to start
	 */
	default public void newTrial() {}
	
	/**
	 * Function to signal the script that the trial has finished
	 */
	default public void endTrial() {}
	
	/**
	 * Function to signal the experiment is about to start
	 */
	default public void newExperiment() {}
	
	/**
	 * Function to signal the experiment is about to end
	 */
	default public void endExperiment() {}
	
	/**
	 * Default run function
	 */
	default public void run() {}
	
	/**
	 * Function that returns the priority of the script.
	 * Lower values have higher priorities.
	 * The priorities define the order in which scripts execute if 
	 * they are scheduled to run at the same time.
	 * Tasks  have a priority of 10
	 * Models have a priority of 20
	 * Robots have a priority of 30
	 * Conditions have a priority of 1000
	 * By default a script has a priority of 100
	 * Displays have the least priority so they always run last (Integer.MAX_VALUE)
	 */
	default public int getPriority() {
		return 100;
	}
	
	/**
	 * Returns after how much time should the script be executed 
	 * when it is first scheduled. 
	 * @return Amoung of simulated time the script should wait 
	 * before it executes for the first time in each episode.
	 */
	default public long getInitialSchedule() {
		return 0;
	}
}
