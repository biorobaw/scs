package edu.usf.experiment;

import java.io.File;
import java.util.List;

import edu.usf.experiment.log.Logger;
import edu.usf.experiment.log.LoggerLoader;
import edu.usf.experiment.plot.Plotter;
import edu.usf.experiment.plot.PlotterLoader;
import edu.usf.experiment.universe.UniverseLoader;
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

	private List<Plotter> beforePlotters;
	private List<Logger> beforeLoggers;

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

		setUniverse(UniverseLoader.getInstance().load(root, logPath));

		// Load tasks and plotters
		beforePlotters = PlotterLoader.getInstance().load(
				root.getChild("beforeExperimentPlotters"), logPath);
		beforeLoggers = LoggerLoader.getInstance().load(
				root.getChild("beforeExperimentLoggers"), logPath);

	}

	/***
	 * Runs the experiment for the especified subject. Just goes over trials and
	 * runs them all. It also executes tasks and plotters.
	 */
	public void run() {
		System.out.println("[+] Executing Loggers and Plotters");
		for (Logger logger : beforeLoggers) {
			logger.log(this);
			logger.finalizeLog();
		}
		
		Plotter.plot(beforePlotters);
	}

	public static void main(String[] args) {
		if (args.length < 2)
			System.out.println("Usage: PreExperiment " + "exprimentLayout");

		PreExperiment e = new PreExperiment(args[0], args[1]);
		e.run();

		System.exit(0);
	}

}
