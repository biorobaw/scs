package edu.usf.experiment;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.usf.experiment.Deprecated.plot.Plotter;
import edu.usf.experiment.Deprecated.plot.PlotterLoader;
import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.log.Logger;
import edu.usf.experiment.log.LoggerLoader;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.task.TaskLoader;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;

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
	private String name;
	private Subject subject;

	private List<Task> beforeTasks;
	public List<Episode> episodes;
	private List<Task> afterTasks;
	private Universe universe;
	private List<Logger> beforeLoggers;
	private String logPath;
	private List<Logger> afterLoggers;
	
	Globals g = Globals.getInstance();

	public Trial(ElementWrapper trialNode, String parentLogPath, Subject subject, Universe universe) {
		super();
		this.name = trialNode.getChildText("name");
		this.subject = subject;
		this.universe = universe;
		
		
		logPath = parentLogPath + File.separator + name + File.separator;
		
		File file = new File(logPath);
		file.mkdirs();

		beforeTasks = TaskLoader.getInstance().load(trialNode.getChild("beforeTrialTasks"));
		afterTasks = TaskLoader.getInstance().load(trialNode.getChild("afterTrialTasks"));
		
		beforeLoggers = LoggerLoader.getInstance().load(trialNode.getChild("beforeTrialLoggers"), logPath);
		afterLoggers = LoggerLoader.getInstance().load(trialNode.getChild("afterTrialLoggers"), logPath);

		episodes = new LinkedList<Episode>();
		int numEpisodes = trialNode.getChild("episodes").getChildInt("number");
		for (int i = 0; i < numEpisodes; i++)
			episodes.add(new Episode(trialNode.getChild("episodes"), logPath, this, i));
	}

	public void run() {

		// Lock on the subject to ensure mutual exclusion for the same rat
		// Assumes is fifo
		synchronized (getSubject()) {
			g.put("trial", name);
			
			
			// Do all before trial tasks
			for (Task task : beforeTasks) task.perform(UniverseLoader.getUniverse(),this.getSubject());
			for (Logger logger : beforeLoggers) logger.log(UniverseLoader.getUniverse(),this.getSubject());
			
			getSubject().getModel().newTrial();
			
			DisplaySingleton.getDisplay().newTrial();
			
			for (Logger logger : beforeLoggers) logger.finalizeLog();
			
			// Run each episode
			for (Episode episode : episodes) {
				episode.run();
			}
			
			getSubject().getModel().endTrial();
			
			// Do all after trial tasks
			for (Task task : afterTasks) task.perform(UniverseLoader.getUniverse(),this.getSubject());
			// Log and finalize
			for (Logger logger : afterLoggers) logger.log(UniverseLoader.getUniverse(),this.getSubject());
			for (Logger logger : afterLoggers) logger.finalizeLog(); 
			
			DisplaySingleton.getDisplay().endTrial();
		
		    // Plot
		    // Wait until all plots are done before contuining
		    // because some plots create files used by trial plots
			Plotter.join();  
        			

		}

	}

	public String getName() {
		return name;
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
		return name;
	}

	public Universe getUniverse() {
		return universe;
	}
}
