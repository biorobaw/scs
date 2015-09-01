package edu.usf.ratsim.proofofconcepts;

import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class PiQuat {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Transform3D t = new Transform3D();
		t.setRotation(new AxisAngle4f(new Vector3f(0,0,1), (float) (Math.PI)));
		Quat4f q = new Quat4f();
		t.get(q);
		q.normalize();
		System.out.println(q);
	}

}
