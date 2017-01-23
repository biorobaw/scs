package edu.usf.ratsim.nsl.modules.port;

import edu.usf.experiment.robot.RobotAction;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;

public class ModelActionPort extends Port{

	public RobotAction data;
	
	public ModelActionPort(Module owner,RobotAction data) {
		super(owner);
		this.data = data;
		// TODO Auto-generated constructor stub
	}
	
	public ModelActionPort(Module owner) {
		super(owner);
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		data = null;
		
	}
	
	public void set(RobotAction data){
		this.data = data;
	}
	
	public RobotAction get(){
		return data;
	}

}
