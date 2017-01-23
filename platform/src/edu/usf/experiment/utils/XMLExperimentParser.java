package edu.usf.experiment.utils;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;

import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.universe.Universe;

public class XMLExperimentParser {

	/***
	 * Load the trials for the especified subject form the xml file
	 * 
	 * @param root
	 * @param subject
	 * @param universe
	 * @param makePlots 
	 * @return
	 */
	public static List<Trial> loadTrials(ElementWrapper root, String logPath, Subject subject,
			Universe universe, boolean makePlots) {
		List<Trial> res = new LinkedList<Trial>();

		List<ElementWrapper> trialNodes = root.getChildren("trial");
		// For each trial
		
		System.out.println("tnodes: " + trialNodes.size());
		for (ElementWrapper trialNode : trialNodes) {
			// For each group
			List<ElementWrapper> trialGroups = trialNode.getChild("groups")
					.getChildren("group");
			System.out.println("tgroups: " + trialGroups.size());
			for (ElementWrapper groupNode : trialGroups) {
				String groupName = groupNode.getText();
				// For each subject in the group
				System.out.println("equals: " + groupName + " " + subject.getGroup());
				if (groupName.equals(subject.getGroup())) {
					res.add(new Trial(trialNode, logPath, subject, universe, makePlots));
				}
			}
		}

		return res;
	}

	public static ElementWrapper loadRoot(String experimentFile) {
		// Read experiment file
		Document doc = XMLDocReader.readDocument(experimentFile);
		return new ElementWrapper(doc.getDocumentElement());
	}
}
