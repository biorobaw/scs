package edu.usf.experiment;

import java.io.File;
import java.util.List;

import edu.usf.experiment.condition.Condition;
import edu.usf.experiment.display.Display;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.NSLSimulation;

/**
 * This class models a runnable trial of an experiment. It consists of N
 * episodes. When run, the trial runs initial tasks, then all episodes, and then
 * final tasks and plotters.
 * 
 * Each trial contains a set of episodes.
 * 
 * @author gtejera, mllofriu
 * 
 */
public class Trial implements Runnable {
	private Subject subject;
	private String  trialName;
	private String  trialLogPath;

	int numEpisodes = -1;
	int startingEpisode = 0;
	private List<Task> beforeTrialTasks;
	private List<Task> afterTrialTasks;
	private List<Task> beforeEpisodeTasks;
	private List<Task> afterEpisodeTasks;
	private List<Task> beforeCycleTasks;
	private List<Task> afterCycleTasks;
	private List<Condition> stopConds;

	
	private Universe universe;
	Globals g = Globals.getInstance();
	Display display;

	public Trial(ElementWrapper trialNode, String parentLogPath, Subject subject, Universe universe) {
		super();
		this.trialName 	 = trialNode.getChildText("name");
		this.subject = subject;
		this.universe = universe;
		
		//load trial tasks
		beforeTrialTasks = Task.loadTask(trialNode.getChild("beforeTrialTasks"));
		afterTrialTasks = Task.loadTask(trialNode.getChild("afterTrialTasks"));
		
		//load episode tasks
		beforeEpisodeTasks 	= Task.loadTask(trialNode.getChild("beforeEpisodeTasks"));
		afterEpisodeTasks 	= Task.loadTask(trialNode.getChild("afterEpisodeTasks"));
		
		//load cycle tasks
		beforeCycleTasks   	= Task.loadTask(trialNode.getChild("beforeCycleTasks"));
		afterCycleTasks 	= Task.loadTask(trialNode.getChild("afterCycleTasks"));
		
		//load stop condition
		stopConds 			= Condition.load(trialNode.getChild("stopConditions"));
		
		//get number of episodes, if the number is not defined, possibly
		//the old xml format is being used, give error and hint
		if(!trialNode.hasChild("numberOfEpisodes")) {
			System.err.println("ERROR: number of episodes in trial "+ trialName + " was not specified.");
			System.err.println("\t This error may be due to using an old experiment xml format, try adapting to the new format");
			System.exit(-1);
		}
		numEpisodes = trialNode.getChildInt("numberOfEpisodes");
		
		
		//make log folders (deprecated)
		//makeLogFolders(parentLogPath);

	}

	public void run() {

		display = Display.getDisplay();
		// Lock on the subject to ensure mutual exclusion for the same rat
		// Assumes is fifo
		synchronized (getSubject()) { //what else is running that requires mutual exclusion?
			g.put("trial", trialName);
			
			//singla new trial
			signalNewTrial();
			
			// perform before trial tasks
			for (Task task : beforeTrialTasks) task.perform(universe,subject);
			
			//execute episodes:
			for(int episode = startingEpisode; episode<numEpisodes ; episode++)			
				runEpisode(episode);
	
			
			// Perform after trial tasks
			for (Task task : afterTrialTasks) task.perform(universe,subject);
			
			
			signalEndTrial();        			

		}

	}
	
	
	
	
	
	
	
	
	
	public String getName() {
		return trialName;
	}

	public Subject getSubject() {
		return subject;
	}

	public String getSubjectName() {
		return getSubject().getName();
	}

	public String getGroup() {
		return getSubject().getGroup();
	}

	public String toString() {
		return trialName;
	}

	public Universe getUniverse() {
		return universe;
	}
	
	public void makeLogFolders(String logFolder) {
		//generate logging folders:
		trialLogPath = logFolder + File.separator + trialName + File.separator;
		File file = new File(trialLogPath);
		file.mkdirs();
		
		for(int i=0;i<numEpisodes;i++) 
			(new File(trialLogPath 
						+ i + "/"
						+ getSubject().getGroup() + "/"
						+ getSubject().getName() + "/")).mkdirs();
	}
	
	private void setEpisodeGlobals(int episode) {
		//set globals:
		String episodelogPath = trialLogPath 
								+ episode + "/"
								+ getSubject().getGroup() + "/"
								+ getSubject().getName() + "/";
		g.put("episodeLogPath", episodelogPath);
		g.put("episode",episode);
		g.put("cycle",0);
		g.put("saveSnapshotPath", "" + g.get("savePath") +"g" + g.get("group")+"-s"+g.get("subName")+"-t"+g.get("trial") + "-e" + g.get("episode") + "-");
		
	}
	
	private void signalNewTrial() {
		//signal new trial:
		for(Task t : beforeTrialTasks) t.newTrial();
		for(Task t : afterTrialTasks) t.newTrial();
		getSubject().getModel().newTrial();
		Display.getDisplay().newTrial();
	}
	
	private void signalEndTrial() {
		// signal end of trial
		for(Task t : beforeTrialTasks) t.endTrial();
		for(Task t : afterTrialTasks) t.endTrial();
		getSubject().getModel().endTrial();
		Display.getDisplay().endTrial();
	}
	
	private void signalEndEpisode(int episodeNumber) {
		subject.getModel().endEpisode();
		display.endEpisode();
		for( Task t : beforeCycleTasks) t.endEpisode();
		for( Task t : beforeEpisodeTasks) t.endEpisode();
		for( Task t : afterCycleTasks) t.endEpisode();
		for( Task t : afterEpisodeTasks) t.endEpisode();
		
		System.out.println("[+] Episode " + trialName + " "
				+ subject.getGroup() + " " + subject.getName()+ " "
				+ episodeNumber + " finished.");
	}
	
	private void signalNewEpisode(int episodeNumber) {
		subject.getModel().newEpisode();
		for(Task t : beforeEpisodeTasks) t.newEpisode();
		for(Task t : afterEpisodeTasks)  t.newEpisode();
		for(Task t : beforeCycleTasks)   t.newEpisode();
		for(Task t : afterCycleTasks)    t.newEpisode();
		for(var c : stopConds) c.newEpisode();
		
		display.newEpisode();
		
		System.out.println("[+] Episode " + trialName + " "
				+ subject.getGroup() + " " + subject.getName()+ " "
				+ episodeNumber + " started.");
		
	}
	
	
	private void runEpisode(int episode) {
		//set episode globals:
		setEpisodeGlobals(episode);
		
		//Clear state of last episode:
		universe.clearState();		
		subject.robot.clearState();
		
		//signal new episode
		signalNewEpisode(episode);
		
		// Do all before episode tasks
		for (Task task : beforeEpisodeTasks) task.perform(universe,subject);		
		
		//execute cycles until a stop condition is true
		runCycles();
		
		// After episode tasks
		for (Task task : afterEpisodeTasks) task.perform(universe,subject);
		
		//signal end episode
		signalEndEpisode(episode);
	}
	
	
	
	private void runCycles() {
		display.repaint();
		for(int cycle=0;;) {
			//set global
			g.put("cycle",cycle);
			
			//execute before cycle tasks
			for (Task t : beforeCycleTasks) t.perform(Universe.getUniverse(),this.getSubject());

			//execute the model
			long stamp = Debug.tic();
			subject.getModel().run();
			if(Debug.profiling) System.out.println("Model time: " + Debug.toc(stamp));
			
			
			//update display
			//update done only if display is synced or if last draw cycle has completed
			display.updateData();

			//wait according to simulation pause
			SimulationControl.waitIfPaused(); //the pause is here so that the model state can be observed before performing the actions
			
			// Evaluate stop conditions
			for (Condition sc : stopConds) if(sc.holds()) {
				System.out.println();
				return;
			}
			
			//advance simulation time
			universe.step();			
			NSLSimulation.getInstance().incSimTime();

			//perform after cycle tasks
			for (Task t : afterCycleTasks) t.perform(Universe.getUniverse(),this.getSubject());

			
			if (Debug.printEndCycle) System.out.println("End cycle");

			cycle++;
			if (cycle % 1000 == 0) System.out.print(".");
			if (cycle % 5000 == 0) System.out.println("");

			
			

		}
	}
	
	
}
