package com.github.biorobaw.scs.experiment;

/**
 * Class in charge of running an experiment.
 * @author bucef
 *
 */
public class ExperimentController {

	Experiment e; 
	boolean endTrial = false;
	boolean endEpisode = false;
	long simulation_cycle = 0;
	
	public ExperimentController(Experiment e) {
		this.e = e;
	}
	
	/**
	 * function to run a full experiment
	 */
	public void runExperiment() {
		
		// print starting
		System.out.println("[+] Starting experiment");
		
		// signal new experiment
		signalNewExperiment();	
		
		// Run each trial in order
		for (Trial t : e.trials) runTrial(t);

		// signal end experiment
		signalEndExperiment();
		
		// print finished
		System.out.println("[+] Finished experiment");
		
	}
	
	/**
	 * function to run a trial
	 * @param t Trial to be run
	 */
	public void runTrial(Trial t) {
		// init variables
		System.out.println("[+] Start Trial");
		endTrial = false;
		e.setGlobal("trial", t.trialName);
		e.setGlobal("trial_episodes", t.numEpisodes);
		
		//signal new trial
		signalNewTrial(t);
		
		
		//execute episodes:
		for(int episode = t.startingEpisode; episode < t.numEpisodes && !endTrial; episode++)			
			runEpisode(t,episode);

		
		// signal end trial
		signalEndTrial(t);      
		
		System.out.println("[+] End Trial");


	}
	
	/**
	 * function to run an episode of a trial
	 * @param t The trial
	 * @param episode The episode of the trial
	 */
	private void runEpisode(Trial t, int episode) {
		
		// print episode start
		if(episode % 100 == 0)
		System.out.printf("[+] START (%s - %s) %s %d\n",
							e.getGlobal("group"),
							e.getGlobal("run_id"),
							t.trialName,
							episode);
		
		// set variables:
		e.setGlobal("episode",episode);
		
		// signal new episode
		signalNewEpisode(t,episode);		
		
		// clear scheduler and then schedule this episode's scripts
		e.simulator.clearScripts(); //clear scheduler
		e.simulator.addInitialScripts(t.cycleTasks); // add task scripts
		e.simulator.addInitialScripts(e.subjects.values()); // add model scripts
		for(var s : e.subjects.values()) 
			e.simulator.addInitialScripts(s.getRobot().scripts); // add robot module scripts
		e.simulator.addInitialScript(e.display);
		
		// run simulation
		runSimulation();
		
		//signal end episode
		signalEndEpisode(t,episode);
		
		// print episode start
//		System.out.printf("[+] END (%s - %s) %s %d\n\n",
//							e.getGlobal("group"),
//							e.getGlobal("run_id"),
//							t.trialName,
//							episode);
	}
	
	
	public void runSimulation() {
		// init variables
		var sim = e.simulator;
		endEpisode = false;
		simulation_cycle=0;
		
		while(!endEpisode) {
			e.setGlobal("cycle",simulation_cycle);
			
			sim.advanceTime();
			
			simulation_cycle++;
			if (simulation_cycle % 1000 == 0) System.out.println(".");
			if (simulation_cycle % 5000 == 0) System.out.println("");
		}
	}
	
	/**
	 * Signals all scripts that the experiment is about to start
	 */
	private void signalNewExperiment() {
		for(var s : e.tasks) s.newExperiment();
		for(var t : e.trials) {
			for(var s : t.trialTasks) s.newExperiment();
			for(var s : t.episodeTasks) s.newExperiment();
			for(var s : t.cycleTasks) s.newExperiment();
		}
		for(var m : e.subjects.values()) {
			m.newEpisode();
			for(var s : m.getRobot().scripts) s.newExperiment();
		}
		e.display.newExperiment();
	}
	
	/**
	 * Signals all scripts that the experiment is about to end
	 */
	private void signalEndExperiment() {
		for(var s : e.tasks) s.endExperiment();
		for(var t : e.trials) {
			for(var s : t.trialTasks) s.endExperiment();
			for(var s : t.episodeTasks) s.endExperiment();
			for(var s : t.cycleTasks) s.endExperiment();
		}
		for(var m : e.subjects.values()) {
			m.endExperiment();
			for(var s : m.getRobot().scripts) s.endExperiment();
		}
		e.display.endExperiment();
	}
	
	/**
	 * Signals a new trial
	 */
	private void signalNewTrial(Trial t) {
		for(var s : t.trialTasks) s.newTrial();
		for(var s : t.episodeTasks) s.newTrial();
		for(var s : t.cycleTasks) s.newTrial();
		for(var m : e.subjects.values()) {
			m.newTrial();
			for(var s : m.getRobot().scripts) s.newTrial();
		}
		e.display.newTrial();
	}
	
	/**
	 * Signals the end of a trial to the display, robot and model 
	 */
	private void signalEndTrial(Trial t) {
		for(var s : t.trialTasks) s.endTrial();
		for(var s : t.episodeTasks) s.endTrial();
		for(var s : t.cycleTasks) s.endTrial();
		for(var m : e.subjects.values()) {
			m.endTrial();
			for(var s : m.getRobot().scripts) s.endTrial();
		}
		e.display.endTrial();
	}
	
	/**
	 * Signals the start of an episode to all scripts and display
	 * @param episodeNumber
	 */
	private void signalNewEpisode(Trial t, int episodeNumber) {
		// for(var s : t.trialTasks) s.newEpisode();
		for(var s : t.episodeTasks) s.newEpisode();
		for(var s : t.cycleTasks) s.newEpisode();
		for(var m : e.subjects.values()) {
			m.newEpisode();
			for(var s : m.getRobot().scripts) s.newEpisode();
		}
		e.display.newEpisode();

	}
	
	/**
	 * Signals the end of the episode to all scripts and display
	 * @param episodeNumber
	 */
	private void signalEndEpisode(Trial t, int episodeNumber) {
		// for(var s : t.trialTasks) s.endEpisode();
		for(var s : t.episodeTasks) s.endEpisode();
		for(var s : t.cycleTasks) s.endEpisode();
		for(var m : e.subjects.values()) {
			m.endEpisode();
			for(var s : m.getRobot().scripts) s.endEpisode();
		}
		e.display.endEpisode();
				
	}
	
	public void endEpisode() {
		endEpisode = true;
	}
	
	public void endTrial() {
		endTrial = true;
	}
	
	public long getSimulationCycle() {
		return simulation_cycle;
	}
}
