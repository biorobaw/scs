package edu.usf.experiment.condition;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.ElementWrapper;

public class DoneCondition implements Condition {

	Globals  g = Globals.getInstace();
	
	public DoneCondition(ElementWrapper params) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean holds(Episode e) {
		// TODO Auto-generated method stub
		boolean retVal = g.get("done")!=null;
		if (retVal) System.out.println("Exit because done condition");
		return retVal;
	}

}
