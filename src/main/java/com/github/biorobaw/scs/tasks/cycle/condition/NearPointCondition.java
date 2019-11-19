package com.github.biorobaw.scs.tasks.cycle.condition;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.simulation.object.RobotProxy;
import com.github.biorobaw.scs.utils.files.XML;

public class NearPointCondition extends Condition {
	
	private float r2;
	private Vector3D point;
	private RobotProxy robot;
	
	public NearPointCondition(XML xml){
		super(xml);
		point = new Vector3D(xml.getFloatAttribute("x"),
							 xml.getFloatAttribute("y"),
							 0);
		r2 = xml.getFloatAttribute("radius");
		r2 *= r2; // store squared distance
		robot = Experiment.get()
				  .subjects
				  .get(xml.getAttribute("robot_id"))
				  .getRobot()
				  .getRobotProxy();
	}

	@Override
	public boolean condition() {
		return point.distanceSq(robot.getPosition()) < r2;
	}

}
