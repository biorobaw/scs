package edu.usf.vlwsim;

import java.util.HashMap;
import java.util.Map;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sun.j3d.utils.geometry.Primitive;

public abstract class ExpUniverseNode extends BranchGroup {

	protected Map<String, Float> readValues(Node node) {
		Map<String, Float> values = new HashMap<String, Float>();
		NamedNodeMap attrs = node.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Node attr = attrs.item(i);
			values.put(attr.getNodeName(),
					Float.parseFloat(attr.getNodeValue()));
		}
		return values;
	}

	protected void addVolume(String name, Primitive vol, float x, float y,
			float z) {
		Transform3D translate = new Transform3D();
		Vector3f position = new Vector3f(x, y, z);
		translate.setTranslation(position);
		Transform3D rot = new Transform3D();
		rot.rotX(Math.PI/2);
		translate.mul(rot);
		TransformGroup tg = new TransformGroup(translate);
		tg.addChild(vol);
		this.addChild(tg);

		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	}

	protected Material createMaterial(Color3f color) {
		Material mat = new Material();
		mat.setDiffuseColor(color);
		mat.setSpecularColor(color);
		mat.setShininess(0f);
		// // by gonzalo: apago reflejos ambiente y especular en los cuerpos
		// pues molesta bastante para el conteo de pixeles.
		mat.setEmissiveColor(color);
		return mat;
	}

}
