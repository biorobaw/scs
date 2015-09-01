package edu.usf.experiment;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.log.Logger;
import edu.usf.experiment.log.LoggerLoader;
import edu.usf.experiment.plot.Plotter;
import edu.usf.experiment.plot.PlotterLoader;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.task.TaskLoader;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

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
	private List<Episode> episodes;
	private List<Task> afterTasks;
	private Universe universe;
	private List<Logger> beforeLoggers;
	private String logPath;
	private List<Plotter> beforePlotters;
	private List<Plotter> afterPlotters;
	private List<Logger> afterLoggers;

	public Trial(ElementWrapper trialNode, String parentLogPath, Subject subject, Universe universe) {
		super();
		this.name = trialNode.getChildText("name");
		this.subject = subject;
		this.universe = universe;
		
		logPath = parentLogPath + File.separator + name + File.separator;
		
		File file = new File(logPath);
		file.mkdirs();

		beforeTasks = TaskLoader.getInstance().load(
				trialNode.getChild("beforeTrialTasks"));
		afterTasks = TaskLoader.getInstance().load(
				trialNode.getChild("afterTrialTasks"));
		beforePlotters = PlotterLoader.getInstance().load(
				trialNode.getChild("beforeTrialPlotters"), logPath);
		afterPlotters = PlotterLoader.getInstance().load(
				trialNode.getChild("afterTrialPlotters"), logPath);
		beforeLoggers = LoggerLoader.getInstance().load(
				trialNode.getChild("beforeTrialLoggers"), logPath);
		afterLoggers = LoggerLoader.getInstance().load(
				trialNode.getChild("afterTrialLoggers"), logPath);

		subject.newTrial();

		episodes = new LinkedList<Episode>();
		int numEpisodes = trialNode.getChild("episodes").getChildInt("number");
		for (int i = 0; i < numEpisodes; i++)
			episodes.add(new Episode(trialNode.getChild("episodes"), logPath, this, i));
	}

	public void run() {

		// Lock on the subject to ensure mutual exclusion for the same rat
		// Assumes is fifo
		synchronized (getSubject()) {
			PropertyHolder props = PropertyHolder.getInstance();
			props.setProperty("trial", name);
			props.setProperty("log.directory", logPath);
			
			getSubject().newTrial();
			
			// Do all before trial tasks
			for (Task task : beforeTasks)
				task.perform(this);
			for (Logger logger : beforeLoggers)
				logger.log(this);
			for (Plotter plotter : beforePlotters)
				plotter.plot();

			// Run each episode
			for (Episode episode : episodes) {
				episode.run();
			}
			
			

			// Do all after trial tasks
			for (Task task : afterTasks)
				task.perform(this);
			// Log and finalize
			for (Logger logger : afterLoggers)
				logger.log(this);
			
			for (Logger logger : beforeLoggers)
				logger.finalizeLog();
			for (Logger logger : afterLoggers)
				logger.finalizeLog(); 
			// Plot
			for (Plotter plotter : afterPlotters)
				plotter.plot();
			
			

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
