package edu.usf.vlwsim;

import javax.media.j3d.Transform3D;

public class Robot {

	Transform3D t;

	public Robot(Transform3D t) {
		super();
		this.t = t;
	}

	public Robot() {
		this(new Transform3D());
	}

	public Transform3D getT() {
		return t;
	}

	public void setT(Transform3D t) {
		this.t = t;
	}
	
	
}
