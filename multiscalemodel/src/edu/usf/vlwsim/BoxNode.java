package edu.usf.vlwsim;

import java.util.Map;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

import org.w3c.dom.Node;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;

public class BoxNode extends ExpUniverseNode {

	public BoxNode(Node node) {
		Map<String, Float> values = readValues(node);

		Color3f color = new Color3f(values.get("cr"), values.get("cg"),
				values.get("cb"));
		float x = values.get("x");
		float y = values.get("y");
		float z = values.get("y");
		float xp = values.get("xp");
		float yp = values.get("yp");
		float zp = values.get("yp");

		Appearance app = new Appearance();
		TransparencyAttributes ta = new TransparencyAttributes(
				TransparencyAttributes.FASTEST, 0);
		app.setColoringAttributes(new ColoringAttributes(color, 1));
		app.setTransparencyAttributes(ta);
		// x,y,z: length, width, and height.
		Primitive vol = new Box(x, y, z, app);
		// TODO: check name stuff
		addVolume(null, vol, xp, yp, zp);
	}
}
