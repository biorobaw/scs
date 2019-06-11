package edu.usf.experiment.condition;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.ElementWrapper;

public class DoneCondition implements Condition {

	Globals  g = Globals.getInstance();
	
	public DoneCondition(ElementWrapper params) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean holds(Episode e) {
		// TODO Auto-generated method stub
		boolean retVal = (boolean)g.get("done");
		if (retVal) 
			System.out.println("Exit because done condition: " + retVal);
		return retVal;
	}

}
