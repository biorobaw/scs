package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.util.List;
import java.util.Map;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.RobotOld;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import platform.simulatorVirtual.robots.PuckRobot;


public class TSPSubjectFrance extends Subject {

	public float step;
	public float leftAngle;
	public float rightAngle;
	
	private TSPModelFrance model;
	
	public PuckRobot robot;

	public TSPSubjectFrance(String name, String group,
			ElementWrapper params, Robot robot) {
		super(name, group, params, robot);

		this.robot = (PuckRobot)robot;
		
		
		model = new TSPModelFrance(params, this, this.robot);
	}
	
	@Override
	public void stepCycle() {
		model.simRun();
		setHasEaten(false);
	}
	

	@Override
	public void newEpisode() {
		model.newEpisode();
	}

	@Override
	public void endEpisode() {
		// TODO Auto-generated method stub
		super.endEpisode();
		
		model.endEpisode();
		
	}
	
	@Override
	public void newTrial() {
		model.newTrial();
	}



	

	public List<PlaceCell> getPlaceCells() {
		return model.getPlaceCells();
	}


	public Map<Integer, Float> getPCActivity() {
		return model.getCellActivation();
	}

	

}
