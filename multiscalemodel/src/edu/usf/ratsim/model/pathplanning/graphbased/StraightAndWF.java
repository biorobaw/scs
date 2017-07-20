package edu.usf.ratsim.model.pathplanning.graphbased;

import edu.usf.experiment.robot.HolonomicRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.BugUtilities;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Velocities;
import edu.usf.ratsim.support.SonarUtils;

public class StraightAndWF extends Module {

	private enum State {
		STRAIGHT, WF
	};
	private State state;
	private HolonomicRobot robot;
	
	public StraightAndWF(String name, HolonomicRobot robot) {
		super(name);
		
		state = State.WF;
		this.robot = robot;
	}

	@Override
	public void run() {
		Float1dPort readings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort angles = (Float1dPort) getInPort("sonarAngles");
		
		float minFront = SonarUtils.getMinReading(readings, angles, 0f, (float) (Math.PI/6));
		switch(state){
		case STRAIGHT:
			if (minFront < BugUtilities.DEFAULT_OBSTACLE_FOUND_THRS){
				state = State.WF;
			}
			break;
		}
		
//		System.out.println(state);
		
		Velocities v = new Velocities();
		switch(state){
		case STRAIGHT:
			v = new Velocities(0.05f,0, 0);
			break;
		case WF:
			v = BugUtilities.wallFollowRight(readings, angles, robot.getRadius(), BugUtilities.DEFAULT_OBSTACLE_FOUND_THRS);
			break;
		}

		
		v.trim();

		// Execute commands
		robot.setVels(v.x, v.y, v.theta);
	}

	@Override
	public boolean usesRandom() {
		// TODO Auto-generated method stub
		return false;
	}

}
