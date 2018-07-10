package edu.usf.experiment.task.robot;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.robot.CalibratableRobot;

public class CalibrateRobot extends Task {

	public CalibrateRobot(ElementWrapper params) {
		super(params);
		
	}

	public void perform(Universe u, Subject s){
		CalibratableRobot c = (CalibratableRobot) s.getRobot();
		c.calibrate();
	}

}
