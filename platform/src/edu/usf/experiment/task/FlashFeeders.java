package edu.usf.experiment.task;

import java.util.LinkedList;
import java.util.StringTokenizer;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to enable a list of  feeders
 * @author ludo
 *
 */
public class FlashFeeders extends Task {

	private LinkedList<Integer> indexList;

	public FlashFeeders(ElementWrapper params) {
		super(params);
		String feeders = params.getChildText("feeders");
		StringTokenizer tok = new StringTokenizer(feeders,",");
		indexList = new LinkedList<Integer>();
		while (tok.hasMoreTokens())
			indexList.add(Integer.parseInt(tok.nextToken()));
	}

	public void perform(Universe u, Subject s){
		for (Integer f : indexList)
			u.setFlashingFeeder(f, true);
	}

}
