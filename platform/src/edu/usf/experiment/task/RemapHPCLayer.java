package edu.usf.experiment.task;

import java.util.LinkedList;
import java.util.StringTokenizer;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to deactivate a random feeder from the set of enabled ones
 * 
 * @author ludo
 *
 */
public class RemapHPCLayer extends Task {

	private LinkedList<Integer> indexList;
	private String group;

	public RemapHPCLayer(ElementWrapper params) {
		super(params);

		group = params.getChildText("group");
		String layers = params.getChildText("layers");
		StringTokenizer tok = new StringTokenizer(layers,",");
		indexList = new LinkedList<Integer>();
		while (tok.hasMoreTokens())
			indexList.add(Integer.parseInt(tok.nextToken()));
	}

	@Override
	public void perform(Experiment experiment) {
		perform(experiment.getSubject());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getSubject());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getSubject());
	}
	
	private void perform(Subject u) {
		if (u.getGroup().equals(group))
			u.remapLayers(indexList);
	}


}
