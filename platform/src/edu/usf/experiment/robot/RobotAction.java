package edu.usf.experiment.robot;

import java.util.LinkedList;

public class RobotAction {

	public String actionId;
	public LinkedList<Object> params = new LinkedList<Object>();
	
	public RobotAction(String id,Object... varargs){
		actionId = id;
		for (Object o : varargs) params.addLast(o);		

	}
		


}
