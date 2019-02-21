package edu.usf.experiment;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
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
	private List<Task> afterTasks;
	public List<Episode> episodes;
	private Universe universe;
	private String logPath;
	
	Globals g = Globals.getInstance();

	public Trial(ElementWrapper trialNode, String parentLogPath, Subject subject, Universe universe) {
		super();
		this.name 	 = trialNode.getChildText("name");
		this.subject = subject;
		this.universe = universe;
		
		
		logPath = parentLogPath + File.separator + name + File.separator;
		
		File file = new File(logPath);
		file.mkdirs();

		beforeTasks = Task.loadTask(trialNode.getChild("beforeTrialTasks"));
		afterTasks = Task.loadTask(trialNode.getChild("afterTrialTasks"));
		

		episodes = new LinkedList<Episode>();
		int numEpisodes = trialNode.getChild("episodes").getChildInt("number");
		for (int i = 0; i < numEpisodes; i++)
			episodes.add(new Episode(trialNode.getChild("episodes"), logPath, this, i));
	}

	public void run() {

		// Lock on the subject to ensure mutual exclusion for the same rat
		// Assumes is fifo
		synchronized (getSubject()) { //what else is running that requires mutual exclusion?
			g.put("trial", name);
			
			
			//singla new trial:
			for(Task t : beforeTasks) t.newTrial();
			for(Task t : afterTasks) t.newTrial();
			getSubject().getModel().newTrial();
			
			
			// Do all before trial tasks
			for (Task task : beforeTasks) task.perform(Universe.getUniverse(),this.getSubject());

			
			//singal new trial to display (must occur after tasks)
			DisplaySingleton.getDisplay().newTrial();
			
			
			// Run each episode
			for (Episode episode : episodes) {
				episode.run();
			}
			
			
			
			// Do all after trial tasks
			for (Task task : afterTasks) task.perform(Universe.getUniverse(),this.getSubject());
			
			//Signal end of trial
			for(Task t : beforeTasks) t.endTrial();
			for(Task t : afterTasks) t.endTrial();
			getSubject().getModel().endTrial();
			DisplaySingleton.getDisplay().endTrial();
			        			

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
