package edu.usf.experiment;

import java.util.List;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.XMLExperimentParser;

public class RunAllIndividuals {

	public static void main(String[] args) {
		if (args.length < 1)
			System.out.println("Usage: java edu.usf.ExperimentRunner "
					+ "exprimentLayout logPath");

		String experimentFile = args[0];
		String logPath = args[1];

		ElementWrapper root = XMLExperimentParser.loadRoot(args[0]);

		List<ElementWrapper> groupNodes = root.getChildren("group");
		// Look for the group of the individual to execute
		for (ElementWrapper gNode : groupNodes) {
			String groupName = gNode.getChildText("name");
			int numMembers = gNode.getChildInt("numMembers");

			for (int i = 0; i < numMembers; i++) {
				// Get the name and create the experiment and run
				String subName = new Integer(i).toString();
				Experiment e = new Experiment(experimentFile, logPath,
						groupName, subName);
				e.run();
			}
		}

	}
}
