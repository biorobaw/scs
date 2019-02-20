package edu.usf.experiment;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import edu.usf.experiment.condition.Condition;
import edu.usf.experiment.condition.ConditionLoader;
import edu.usf.experiment.display.Display;
import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.log.Logger;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.task.TaskLoader;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.NSLSimulation;

/**
 * This class represents an actual run. For learning experiments, for example,
 * it might model the period of time between the start and the reaching of a
 * certain goal. An episode consists of cycles. In each cycle, the stepCycle of
 * the subject and universe are called, in that order. Then, stop conditions are
 * evaluated to determine if the episode should end. Tasks and plotters are
 * executed at the end.
 * 
 * @author mllofriu
 * 
 */
public class Episode {

	private Trial trial;
	private int episodeNumber;
	private List<Condition> stopConds;
	private List<Task> beforeCycleTasks;
	private List<Task> afterCycleTasks;
	private String logPath;
	private List<Task> beforeEpisodeTasks;
	private List<Task> afterEpisodeTasks;
	
	//execution contriol variables
	//implement modified consumer producer for doing step by step execution
	static private Semaphore pauseSemaphore = new Semaphore(0);
	static private Semaphore accessMutex = new Semaphore(1);
	static private int stepsAvailable = 0;
	static private boolean paused = false;
	
	public float timeStep;
	private static int[] sleepValues = new int[] {5000,3000,2000,1000,500,400,300,100,30,0};
	private NSLSimulation nslSim;

	public Episode(ElementWrapper episodeNode, String parentLogPath, Trial trial, int episodeNumber) {
		this.trial = trial;
		this.episodeNumber = episodeNumber;
		
		String timeStepString = episodeNode.getChildText("timeStep");
		if(timeStepString!=null) this.timeStep = Float.parseFloat(timeStepString);
		else this.timeStep = 1;

	
		Globals.getInstance().put("sleepValues", sleepValues);
		
//		this.sleepValues[sleepValues.length-1] = episodeNode.getChildInt("sleep");
		
		


		logPath = parentLogPath
				+ episodeNumber + "/"
				+ getSubject().getGroup() + "/"
				+ getSubject().getName() + "/";
		

		File file = new File(logPath);
		file.mkdirs();

		beforeEpisodeTasks 	= TaskLoader.getInstance().load(episodeNode.getChild("beforeEpisodeTasks"));
		afterEpisodeTasks 	= TaskLoader.getInstance().load(episodeNode.getChild("afterEpisodeTasks"));
		beforeCycleTasks   	= TaskLoader.getInstance().load(episodeNode.getChild("beforeCycleTasks"));
		afterCycleTasks 	= TaskLoader.getInstance().load(episodeNode.getChild("afterCycleTasks"));
		stopConds 			= ConditionLoader.getInstance().load(episodeNode.getChild("stopConditions"));
		
		
		nslSim = NSLSimulation.getInstance();
	}

	public void run() {
		Globals g = Globals.getInstance();
		g.put("episodeLogPath", logPath);
		g.put("episode",episodeNumber);
		g.put("cycle",-1);
		g.put("saveSnapshotPath", "" + g.get("savePath") +"g" + g.get("group")+"-s"+g.get("subName")+"-t"+g.get("trial") + "-e" + g.get("episode") + "-");
		
		
		

		System.out.println("[+] Episode " + trial.getName() + " "
				+ trial.getGroup() + " " + trial.getSubjectName() + " "
				+ episodeNumber + " started.");
				
		//Clear state of last episode
		UniverseLoader.getUniverse().clearState();		
		getSubject().robot.clearState();
		getSubject().getModel().newEpisode();
		
		//singla new episode to all tasks:
		for(Task t : beforeCycleTasks) t.newEpisode();
		for(Task t : beforeEpisodeTasks) t.newEpisode();
		for(Task t : afterCycleTasks) t.newEpisode();
		for(Task t : afterEpisodeTasks) t.newEpisode();
		
		// Do all before episode tasks
		for (Task task : beforeEpisodeTasks) task.perform(UniverseLoader.getUniverse(),this.getSubject());

		// Execute cycles until stop condition holds
		boolean finished = false;
		int cycle = 0;
		g.put("cycle",cycle);
		Display display = DisplaySingleton.getDisplay();
		display.newEpisode();
		while (!finished) {
			g.put("cycle",cycle);
			for (Task t : beforeCycleTasks) t.perform(UniverseLoader.getUniverse(),this.getSubject());

			long stamp = Debug.tic();
			getSubject().getModel().run();
			if(Debug.profiling) System.out.println("Model time: " + Debug.toc(stamp));
			
			
			//update data being displayed (update is done if display is being synced or if last frame was already drawn, otherwise update is skipped)
			display.updateData();

			if(!finished) waitNextStep(); //the pause is here so that the model state can be observed before performing the actions
			
			// Evaluate stop conditions
			for (Condition sc : stopConds) {
				if(finished = finished || sc.holds(this)) 
					break;
			}
			
			//advance simulation time
			getUniverse().step();			
			nslSim.incSimTime();

			//perform after cycle tasks
			for (Task t : afterCycleTasks) t.perform(UniverseLoader.getUniverse(),this.getSubject());

			
			if (Debug.printEndCycle) System.out.println("End cycle");

			cycle++;
			if (cycle % 1000 == 0)
				System.out.print(".");
			if (cycle % 5000 == 0)
				System.out.println("");

			
			

		}

		System.out.println();
		
		// After episode tasks
		for (Task task : afterEpisodeTasks) task.perform(UniverseLoader.getUniverse(),this.getSubject());
		
		
		//signal end episode
		getSubject().getModel().endEpisode();
		display.endEpisode();
		for( Task t : beforeCycleTasks) t.endEpisode();
		for( Task t : beforeEpisodeTasks) t.endEpisode();
		for( Task t : afterCycleTasks) t.endEpisode();
		for( Task t : afterEpisodeTasks) t.endEpisode();
		
		

		System.out.println("[+] Episode " + trial.getName() + " "
				+ trial.getGroup() + " " + trial.getSubjectName() + " "
				+ episodeNumber + " finished.");
	}

	public Subject getSubject() {
		return trial.getSubject();
	}

	public Universe getUniverse() {
		return trial.getUniverse();
	}
	
	static public void waitNextStep() {
		try {
			accessMutex.acquire();
            if(paused){   
            	stepsAvailable--;
            	if(stepsAvailable<0) {
            		accessMutex.release();
            		pauseSemaphore.acquire();
            	}else accessMutex.release();

            } else {
            	accessMutex.release();
            	int sleep = sleepValues[(int)Globals.getInstance().get("simulationSpeed")];
        		if (sleep != 0  ) Thread.sleep(sleep);
            	
            }
        } catch (InterruptedException ex) {
        	ex.printStackTrace();
        }
		
	}
	
	static public boolean togglePause() {
		try {
			accessMutex.acquire();
			paused=!paused;
			if(!paused){
				if(stepsAvailable<0) {
					stepsAvailable=0;
					pauseSemaphore.release();
				}
				
			}
            accessMutex.release();
        } catch (InterruptedException ex) {
            
        }
		return paused; //Only the gui calls this function, and only when pressing pause button, not necessary to make copy inside mutex
	}
	
	
	static public void step() {
		try {
			accessMutex.acquire();
			if(paused) {
				stepsAvailable++;
				if(stepsAvailable<=0) pauseSemaphore.release();
			}
            accessMutex.release();
        } catch (InterruptedException ex) {
        }
	}
	

	

}
