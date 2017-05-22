package edu.usf.vlwsim;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
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
		
		setCapability(BranchGroup.ALLOW_DETACH);
	}
	
	public PlatformNode(float x, float y, float r) {
		normalColor = new Color3f(1f, 0, 0);
		float xp = x;
		float yp = y;
		float zp = 0;
		float h = 0;

		app = new Appearance();
		app.setColoringAttributes(new ColoringAttributes(normalColor, 1));
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		Primitive vol = new Cylinder(r, h, app);
		addVolume(null, vol, xp, yp, zp);

		position = new Point3f(xp, yp, zp);
		
		setCapability(BranchGroup.ALLOW_DETACH);
	}

	public Point3f getPosition() {
		return position;
	}
}
