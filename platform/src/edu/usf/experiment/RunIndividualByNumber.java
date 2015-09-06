package edu.usf.experiment;

import java.io.File;
import java.util.List;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.XMLExperimentParser;
import edu.usf.experiment.utils.IOUtils;


/**
 * This class runs one subject from the experiment given its position. Meant for secuential (or parallel) execution of all individuals.
 * 
 * @author ludo
 *
 */
public class RunIndividualByNumber {

	public static void main(String[] args) {
		if (args.length < 2)
			System.out.println("Usage: java edu.usf.ExperimentRunner "
					+ "logPath individualIndex");
		
		String logPath = args[0];
		int individual = Integer.parseInt(args[1]);

		// Assumes pre-experiment put it in the right place
		ElementWrapper root = XMLExperimentParser.loadRoot(logPath + File.separator + "experiment.xml");
		
		runIndividualByNumber(root, logPath, individual);
		
	}

	public static void runIndividualByNumber(ElementWrapper root,
			String logPath, int individual) {
		List<ElementWrapper> groupNodes = root.getChildren("group");
		// Look for the group of the individual to execute
		for (ElementWrapper gNode : groupNodes) {
			String groupName = gNode.getChildText("name");
			int numMembers = gNode.getChildInt("numMembers");
			
			for (int i = 0; i < numMembers; i++){
				// Decrease individual until we get to the one we want
				individual--;
				if (individual < 0){
					// Get the name and create the experiment and run
					String subName = new Integer(i).toString();
					Experiment e = new Experiment(root, logPath, groupName, subName);
					e.run();
					System.exit(0);
				}
			}
		}

	}
}
