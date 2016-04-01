package edu.usf.ratsim.nsl.modules.qlearning;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;

public interface QLAlgorithm {

	void setUpdatesEnabled(boolean b);

	void savePolicy();

	void dumpPolicy(String trial, String groupName, String subName, String rep,
			int numIntentions, Universe universe, Subject subject);

}
