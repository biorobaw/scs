package edu.usf.experiment.condition;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.utils.ElementWrapper;

public class NearPoint implements Condition {
	
	private float thrs;
	private Point2f point;

	public NearPoint(ElementWrapper params){
		float x = params.getChildFloat("x");
		float y = params.getChildFloat("y");
		point = new Point2f(x,y);
		thrs = params.getChildFloat("thrs");
	}

	@Override
	public boolean holds(Episode e) {
		Point3f p = e.getUniverse().getRobotPosition();
		Point2f p2 = new Point2f(p.x, p.y);
		return p2.distance(point) < thrs;
	}

}
