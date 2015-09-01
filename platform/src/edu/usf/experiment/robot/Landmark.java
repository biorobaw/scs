package edu.usf.experiment.robot;

import javax.vecmath.Point3f;

public class Landmark {

	public int id;
	public Point3f location;
	
	public Landmark(int id, Point3f location) {
		super();
		this.id = id;
		this.location = location;
	}

//	public Landmark(edu.usf.ratsim.robot.naorobot.protobuf.Connector.Landmark lm) {
//		this(lm.getId(), new Point3f(lm.getX(), lm.getY(), lm.getZ()));
//	}
//	
//	public Landmark(edu.usf.ratsim.robot.romina.protobuf.Connector.Landmark lm) {
//		this(lm.getId(), new Point3f(lm.getX(), lm.getY(), lm.getZ()));
//	}
	
}
