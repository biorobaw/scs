package edu.usf.vlwsim.display;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;

import edu.usf.experiment.utils.ElementWrapper;

public class CylinderNode extends ExpUniverseNode {

	public CylinderNode(ElementWrapper params) {
		Color3f color = new Color3f(params.getChildFloat("cr"),
				params.getChildFloat("cg"), params.getChildFloat("cb"));
		float xp = params.getChildFloat("x");
		float yp = params.getChildFloat("y");
		float zp = params.getChildFloat("z");
		float r = params.getChildFloat("r");
		float h = params.getChildFloat("h");

		Appearance app = new Appearance();
		app.setColoringAttributes(new ColoringAttributes(color, 1));
		Primitive vol = new Cylinder(r, h, app);
		addVolume(null, vol, xp, yp, zp);
	}

	/**
	 * @param r
	 *            Cylinder radius
	 * @param h
	 *            Cylinder height
	 * @param color
	 *            Cylinder color
	 * @param xp
	 *            Cylinder center x coordinate
	 * @param yp
	 *            Cylinder center y coordinate
	 * @param zp
	 *            Cylinder center z coordinate
	 */
	public CylinderNode(float r, float h, Color3f color, float xp, float yp,
			float zp) {
		Appearance app = new Appearance();
		app.setColoringAttributes(new ColoringAttributes(color, 1));
		Primitive vol = new Cylinder(r, h, app);
		addVolume(null, vol, xp, yp, zp);
	}
}
