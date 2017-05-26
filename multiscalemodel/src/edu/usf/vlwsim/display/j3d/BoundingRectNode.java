package edu.usf.vlwsim.display.j3d;

import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.w3c.dom.Node;

public class BoundingRectNode extends ExpUniverseNode {

	private Rectangle2D.Float rect;

	public BoundingRectNode(Node node) {
		Map<String, Float> values = readValues(node);
		
		rect = new Rectangle2D.Float(values.get("x"), values.get("y"), values.get("w"), values.get("h"));
	}
	
	public Rectangle2D.Float getRect(){
		return rect;
	}
}
