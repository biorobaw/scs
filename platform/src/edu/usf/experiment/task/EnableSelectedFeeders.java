package edu.usf.experiment.task;

import java.util.LinkedList;
import java.util.StringTokenizer;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to enable a list of  feeders
 * @author ludo
 *
 */
public class EnableSelectedFeeders extends Task {

	private LinkedList<Integer> indexList;

	public EnableSelectedFeeders(ElementWrapper params) {
		super(params);
	}

	public void perform(Universe u, Subject s){
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		String feeders = PropertyHolder.getInstance().getProperty("SelectedFeeders");
		StringTokenizer tok = new StringTokenizer(feeders,",");
		indexList = new LinkedList<Integer>();
		while (tok.hasMoreTokens())
			indexList.add(Integer.parseInt(tok.nextToken()));
		
		for (Integer f : indexList)
			fu.setEnableFeeder(f, true);
	}

}
