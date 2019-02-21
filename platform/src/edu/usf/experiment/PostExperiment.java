package edu.usf.experiment;

import java.io.File;
import java.util.List;

import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.display.NoDisplay;
import edu.usf.experiment.log.Logger;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.XMLExperimentParser;

/**
 * This holds a set of trials over a group of individuals. Group parameters, and
 * trial parameters are loaded from an xml file. See helloworld.xml for details.
 * 
 * When executed, this class only executes one subject's trials, loading from
 * the xml only the information needed for this subject.
 * 
 * @author gtejera,mllofriu
 * 
 */
public class PostExperiment extends Experiment implements Runnable {
//	private List<Plotter> afterPlotters;
	private List<Task> afterTasks;

	/**
	 * Build an object only for experiment purposes
	 * 
	 * @param experimentFile
	 * @param logPath
	 */
	public PostExperiment(String logPath) {
		logPath += File.separator;
		System.out.println("[+] Wrapping up experiment at " + logPath);

		ElementWrapper root = XMLExperimentParser.loadRoot(logPath + "experiment.xml");

		DisplaySingleton.setDisplay(new NoDisplay());
		setUniverse(Universe.load(root, logPath));

		afterTasks = Task.loadTask(root.getChild("afterExperimentLoggers"));

	}

	/***
	 * Runs the experiment for the especified subject. Just goes over trials and
	 * runs them all. It also executes tasks and plotters.
	 */
	public void run() {
		System.out.println("[+] Running plotters and loggers");
		
		// Log and finalize
		for (Task t : afterTasks) {
			if(t instanceof Logger) {
				Logger logger = (Logger)t;
				logger.perform(Universe.getUniverse(),this.getSubject());
				logger.finalizeLog();
			}
		}
		// Plot
//		Plotter.plot(afterPlotters);
//		
//		Plotter.join();
//
//		Plotter.join();
	}

	public static void main(String[] args) {
		if (args.length < 1)
			System.out.println("Usage: java edu.usf.experiment.PostExperiment "
					+ "exprimentLayout");

		PostExperiment e = new PostExperiment(args[0]);
		e.run();

		System.exit(0);
	}

}
