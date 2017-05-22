package edu.usf.ratsim.support.proofofconcepts;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class RotationVectorTest {

	public static void main(String[] args) {
		// Get angle to food
		Point3d rPos = new Point3d(1, 0, 0);
		Point3d fPos = new Point3d(0, 0, 1);

		Vector3f rVect = new Vector3f(rPos);
		Vector3f fVect = new Vector3f(fPos);

		// Build quat4d for angle to food
		Transform3D rotT = new Transform3D();
		Quat4f rot = new Quat4f();
		Vector3f cross = new Vector3f();
		cross.cross(rVect, fVect);
		System.out.println(cross);
		rot.x = (float) (Math.sin(rVect.angle(fVect) / 2) * Math.cos(cross
				.angle(new Vector3f(1, 0, 0))));
		rot.y = (float) (Math.sin(rVect.angle(fVect) / 2) * Math.cos(cross
				.angle(new Vector3f(0, 1, 0))));
		rot.z = (float) (Math.sin(rVect.angle(fVect) / 2) * Math.cos(cross
				.angle(new Vector3f(0, 0, 1))));
		rot.w = (float) Math.cos(rVect.angle(fVect) / 2);
		rotT.set(rot);

		rotT.transform(rPos);
		System.out.println(rPos);

		rot.normalize();
		System.out.println(rot);
		System.out.println(Math.toDegrees(Math.acos(rot.w)) * 2);

	}

}
