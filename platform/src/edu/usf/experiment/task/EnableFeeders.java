package edu.usf.experiment.task;

import java.util.LinkedList;
import java.util.StringTokenizer;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.FeederUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to enable a list of  feeders
 * @author ludo
 *
 */
public class EnableFeeders extends Task {

	private LinkedList<Integer> indexList;

	public EnableFeeders(ElementWrapper params) {
		super(params);
		String feeders = params.getChildText("feeders");
		StringTokenizer tok = new StringTokenizer(feeders,",");
		indexList = new LinkedList<Integer>();
		while (tok.hasMoreTokens())
			indexList.add(Integer.parseInt(tok.nextToken()));
	}

	public void perform(Universe u, Subject s){
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		for (Integer f : indexList)
			fu.setEnableFeeder(f, true);
	}

}
