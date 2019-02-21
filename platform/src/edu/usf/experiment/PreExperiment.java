package edu.usf.experiment;

import java.io.File;
import java.util.List;

import edu.usf.experiment.log.Logger;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;
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
public class PreExperiment extends Experiment implements Runnable {

	private List<Task> beforeTasks;

	/**
	 * Build an object only for pre experiment purposes
	 * 
	 * @param experimentFile
	 * @param logPath
	 */
	public PreExperiment(String experimentFile, String logPath) {
		logPath = logPath + "/";
		System.out.println("[+] Setting up experiment at " + logPath);
		
		ElementWrapper root = XMLExperimentParser.loadRoot(experimentFile);

		System.out.println("[+] Creating directories");
		File file = new File(logPath);
		file.mkdirs();

		System.out.println("[+] Copying files");
		IOUtils.copyFile(experimentFile, logPath + "/experiment.xml");
		String mazeFile = root.getChild("universe").getChild("params")
				.getChildText("maze");
		IOUtils.copyFile(mazeFile, logPath + "/maze.xml");
		
		System.out.println("[+] Saving git status");
		IOUtils.exec("git status", ".");
		System.out.println("[+] Saving current commit");
		IOUtils.exec("git log --pretty=format:'%h' -n 1", ".");
		System.out.println("[+] Saving date");
		IOUtils.exec("date", ".");

		setUniverse(Universe.load(root, logPath));

		// Load tasks and plotters
		beforeTasks = Task.loadTask(root.getChild("beforeExperimentTasks"));

	}

	/***
	 * Runs the experiment for the especified subject. Just goes over trials and
	 * runs them all. It also executes tasks and plotters.
	 */
	public void run() {
		System.out.println("[+] Executing Loggers and Plotters");
		for (Task t : beforeTasks) {
			if(t instanceof Logger) {
				Logger logger = (Logger)t;
				logger.perform(Universe.getUniverse(),this.getSubject());
				logger.finalizeLog();
			};
			
		}
	}

	public static void main(String[] args) {
		if (args.length < 2)
			System.out.println("Usage: PreExperiment " + "exprimentLayout");

		PreExperiment e = new PreExperiment(args[0], args[1]);
		e.run();

		System.exit(0);
	}

}
