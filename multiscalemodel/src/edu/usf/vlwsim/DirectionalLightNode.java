package edu.usf.vlwsim;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.DirectionalLight;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public class DirectionalLightNode extends ExpUniverseNode {

	public DirectionalLightNode(Vector3f direction, Color3f color) {
		BoundingSphere bounds = new BoundingSphere();
		bounds.setRadius(1000d);
		BoundingSphere bounds1 = new BoundingSphere();
		bounds1.setRadius(1000d);
		BoundingSphere bounds2 = new BoundingSphere();
		bounds2.setRadius(1000d);

		DirectionalLight lightD = new DirectionalLight(color, direction);
		lightD.setInfluencingBounds(bounds);
		DirectionalLight lightD1 = new DirectionalLight(color, direction);
		lightD1.setInfluencingBounds(bounds1);
		DirectionalLight lightD2 = new DirectionalLight(color, direction);
		lightD2.setInfluencingBounds(bounds2);

		addChild(lightD);
	}
}
