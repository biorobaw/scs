package edu.usf.ratsim.experiment.universe.virtual;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;

import edu.usf.experiment.utils.ElementWrapper;

public class PlatformNode extends ExpUniverseNode {

	private Point3f position;
	private Color3f normalColor;
	private Appearance app;

	public PlatformNode(ElementWrapper params) {

		normalColor = new Color3f(params.getChildFloat("cr"),
				params.getChildFloat("cg"), params.getChildFloat("cb"));
		float xp = params.getChildFloat("x");
		float yp = params.getChildFloat("y");
		float zp = params.getChildFloat("z");
		float r = params.getChildFloat("r");
		app = new Appearance();
		app.setColoringAttributes(new ColoringAttributes(normalColor, 1));
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		Primitive vol = new Cylinder(r, .00f, app);
		addVolume(null, vol, xp, yp, zp);

		position = new Point3f(xp, yp, zp);
	}
	
	public PlatformNode(float x, float y) {
		normalColor = new Color3f(1f, 0, 0);
		float xp = x;
		float yp = y;
		float zp = 0;
		float r = .03f;
		float h = 0;

		app = new Appearance();
		app.setColoringAttributes(new ColoringAttributes(normalColor, 1));
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		Primitive vol = new Cylinder(r, h, app);
		addVolume(null, vol, xp, yp, zp);

		position = new Point3f(xp, yp, zp);
	}

	public Point3f getPosition() {
		return position;
	}
}
