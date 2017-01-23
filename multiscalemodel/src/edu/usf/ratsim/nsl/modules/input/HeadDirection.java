package edu.usf.ratsim.nsl.modules.input;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.componentInterfaces.LocalizationInterface;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Float0dPort;

/**
 * Provides an output port stating whether the subject just ate
 * @author Martin Llofriu
 *
 */
public class HeadDirection extends Module {

	public Float0dPort hd;
	private LocalizableRobot robot;
	private LocalizationInterface localization;
	private Runnable option;

	public HeadDirection(String name, Robot robot) {
		super(name);
		if(robot instanceof LocalizableRobot){
			this.robot = (LocalizableRobot)robot;
			option = new LocalizableRobotOption();
		} else {
			localization = (LocalizationInterface)robot;
			option = new InterfaceOption();
		}
		
		
		
		
		hd = new Float0dPort(this);
		addOutPort("hd", hd);
	}

	@Override
	public void run() {
		//System.out.println("HD");
		option.run();
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
	class LocalizableRobotOption implements Runnable{

		@Override
		public void run() {
			hd.set(robot.getOrientationAngle());
			
		}
		
	}
	
	class InterfaceOption implements Runnable {

		@Override
		public void run() {
			hd.set(localization.getHD());
			
		}
		
	}
	
}
