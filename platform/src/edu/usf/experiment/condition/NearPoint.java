package edu.usf.experiment.condition;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Episode;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class NearPoint implements Condition {
	
	private float thrs;
	private Coordinate point;

	public NearPoint(ElementWrapper params){
		float x = params.getChildFloat("x");
		float y = params.getChildFloat("y");
		point = new Coordinate(x,y);
		thrs = params.getChildFloat("thrs");
	}

	@Override
	public boolean holds(Episode e) {
		Universe u = e.getUniverse();
		if (!(u instanceof GlobalCameraUniverse))
			throw new IllegalArgumentException("");
		
		GlobalCameraUniverse gcu = (GlobalCameraUniverse) u;
		
		Coordinate p = gcu.getRobotPosition();
		return p.distance(point) < thrs;
	}

}
