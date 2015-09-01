package edu.usf.ratsim.proofofconcepts;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4d;

public class QuatTest {

	public static void main(String[] args) {
		Quat4d q = new Quat4d();

		q.w = .2;
		q.x = .1;
		q.y = .3;
		q.z = .4;

		System.out.println(q);

		// Rotatating point 90 around y axis
		Point3f p = new Point3f(10, 0, 0);
		double angle = Math.PI / 2;
		Quat4d rot = new Quat4d();
		rot.w = Math.cos(angle / 2.0);
		rot.x = Math.cos(Math.PI / 2) * Math.sin(angle / 2.0);
		rot.y = Math.cos(0) * Math.sin(angle / 2.0);
		rot.z = Math.cos(Math.PI / 2) * Math.sin(angle / 2.0);
		Transform3D t = new Transform3D();
		t.set(rot);

		t.transform(p);

		System.out.println(p);

		// Rotate using roty
		p = new Point3f(10, 0, 0);
		t = new Transform3D();
		t.rotY(Math.PI / 2);
		t.transform(p);

		System.out.println(p);
	}

}
