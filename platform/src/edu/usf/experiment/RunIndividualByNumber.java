package edu.usf.experiment;



/**
 * This class runs one subject from the experiment given its position. Meant for secuential (or parallel) execution of all individuals.
 * 
 * @author ludo
 *
 */
public class RunIndividualByNumber {

	public static void main(String[] args) {
		if (args.length < 2)
			System.out.println("Usage: java edu.usf.ExperimentRunner " + "logPath individualIndex");
		
		String experimentFile = Experiment.loadGlobalsFromIndividual(args);
		Experiment e = new Experiment(experimentFile);
		e.run();

		System.out.println("[+] Finished running");
		System.exit(0);
		
	}
	
	
}
