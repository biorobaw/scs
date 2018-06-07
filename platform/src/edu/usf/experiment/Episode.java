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
import edu.usf.experiment.log.LoggerLoader;
import edu.usf.experiment.plot.Plotter;
import edu.usf.experiment.plot.PlotterLoader;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.task.TaskLoader;
import edu.usf.experiment.universe.Universe;
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
	private List<Plotter> beforeEpisodePlotters;
	private List<Plotter> afterEpisodePlotters;
	private List<Logger> beforeEpisodeLoggers;
	private List<Logger> beforeCycleLoggers;
	private List<Logger> afterCycleLoggers;
	private List<Logger> afterEpisodeLoggers;
	private boolean makePlots;
	
	//execution contriol variables
	//implement modified consumer producer for doing step by step execution
	static private Semaphore pauseSemaphore = new Semaphore(0);
	static private Semaphore accessMutex = new Semaphore(1);
	static private int stepsAvailable = 0;
	static private boolean paused = false;
	
	public float timeStep;
	private static int[] sleepValues = new int[] {5000,3000,2000,1000,500,400,300,100,30,0};
	private NSLSimulation nslSim;

	public Episode(ElementWrapper episodeNode, String parentLogPath, Trial trial, int episodeNumber, boolean makePlots) {
		this.trial = trial;
		this.episodeNumber = episodeNumber;
		this.makePlots = makePlots;
		
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
		
		if (makePlots){
			beforeEpisodePlotters = PlotterLoader.getInstance().load(episodeNode.getChild("beforeEpisodePlotters"), logPath);
			afterEpisodePlotters  = PlotterLoader.getInstance().load(episodeNode.getChild("afterEpisodePlotters"), logPath);
		} else {
			beforeEpisodePlotters = new LinkedList<Plotter>();
			afterEpisodePlotters  = new LinkedList<Plotter>();
		}
		beforeEpisodeLoggers 	= LoggerLoader.getInstance().load(episodeNode.getChild("beforeEpisodeLoggers"), logPath);
		beforeCycleLoggers 		= LoggerLoader.getInstance().load(episodeNode.getChild("beforeCycleLoggers"), logPath);
		afterCycleLoggers 		= LoggerLoader.getInstance().load(episodeNode.getChild("afterCycleLoggers"), logPath);
		afterEpisodeLoggers 	= LoggerLoader.getInstance().load(episodeNode.getChild("afterEpisodeLoggers"), logPath);
		
		nslSim = NSLSimulation.getInstance();
	}

	public void run() {
		Globals g = Globals.getInstance();
		g.put("episodeLogPath", logPath);
		g.put("episode",episodeNumber);
		g.put("cycle",-1);
		

		System.out.println("[+] Episode " + trial.getName() + " "
				+ trial.getGroup() + " " + trial.getSubjectName() + " "
				+ episodeNumber + " started.");
		
		// Do all before trial tasks
		for (Task task : beforeEpisodeTasks) task.perform(this);
		
		// New episode is called after tasks are executed (e.g. reposition the robot)
		getSubject().getModel().newEpisode();
		
		for (Logger logger : beforeEpisodeLoggers) logger.log(this);
		
		Plotter.plot(beforeEpisodePlotters);

		// Execute cycles until stop condition holds
		Display display = DisplaySingleton.getDisplay();
		display.newEpisode();
		boolean finished = false;
		int cycle = 0;
		while (!finished) {
			g.put("cycle",cycle);
			for (Logger l : beforeCycleLoggers) l.log(this);
			for (Task t : beforeCycleTasks) 	t.perform(this);

			getSubject().getModel().run();
			
			
			long stamp = Debug.tic();
			display.updateData();
			display.repaint();
			display.waitUntilDoneRendering();
			//System.out.println("Time rendering: " + Debug.toc(stamp));
			
			if(!finished) waitNextFrame(); //the pause is here so that the model state can be observed before performing the actions
			
			// Evaluate stop conditions
			for (Condition sc : stopConds) if(finished = finished || sc.holds(this)) break;
			
			getUniverse().step();
			
//			getSubject().getModel().runPost();
			
			
//			System.out.println("cycle");
			// TODO: universe step cycle
			
			nslSim.incSimTime();

			for (Logger l : afterCycleLoggers)
				l.log(this);
			for (Task t : afterCycleTasks)
				t.perform(this);

			
			if (Debug.printEndCycle)
				System.out.println("End cycle");

			cycle++;
			if (cycle % 1000 == 0)
				System.out.print(".");
			if (cycle % 5000 == 0)
				System.out.println("");

			
			

		}

		System.out.println();
		
		getSubject().getModel().endEpisode();

		
		// Finalize loggers
		for (Logger l : afterCycleLoggers)
			l.finalizeLog();
		for (Logger l : beforeCycleLoggers)
			l.finalizeLog();
		for (Logger l : beforeEpisodeLoggers) 
			l.finalizeLog();
		for (Logger l : afterEpisodeLoggers) {
			l.log(this);
			l.finalizeLog();
		}

		// After trial tasks
		for (Task task : afterEpisodeTasks)
			task.perform(this);

		// Plotters
		Plotter.plot(afterEpisodePlotters);
		
		// reset All conditions:
//		for (Condition sc : stopConds)
//			sc.reset();

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
	
	static public void waitNextFrame() {
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
