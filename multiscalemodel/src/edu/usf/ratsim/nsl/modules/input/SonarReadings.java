package edu.usf.ratsim.nsl.modules.input;

import edu.usf.experiment.robot.SonarRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;

/**
 * Provides an output port with the robot current sonar readings
 * 
 * @author Martin Llofriu
 *
 */
public class SonarReadings extends Module {

	private SonarRobot robot;
	private float[] readingsData;
	private Float1dPortArray readingsPort;

	public SonarReadings(String name, SonarRobot robot) {
		super(name);

		this.robot = robot;
		// The readings port
		readingsData = new float[robot.getSonarAngles().length];
		readingsPort = new Float1dPortArray(this, readingsData);
		addOutPort("sonarReadings", readingsPort);
		
		// The angles port - this info is fixed
		float[] anglesData = new float[robot.getSonarAngles().length];
		int i = 0;
		for (Float angle : robot.getSonarAngles())
			anglesData[i++] = angle;
		Float1dPortArray anglesPort = new Float1dPortArray(this, anglesData);
		addOutPort("sonarAngles", anglesPort);
	}

	@Override
	public void run() {
		int i = 0;
		for (Float reading : robot.getSonarReadings()){
			readingsData[i++] = reading;
		}
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
