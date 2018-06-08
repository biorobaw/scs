package edu.usf.experiment.task.platform;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.XMLUtils;

public class CreatePlatformROS extends Task {
	private Coordinate pos;
	private float radius;
	
	public CreatePlatformROS(ElementWrapper params) {
		super(params);
		pos = new Coordinate();
		pos.x = params.getChildFloat("x");
		pos.y = params.getChildFloat("y");
		radius = params.getChildFloat("radius");
	}

	@Override
	public void perform(Universe u, Subject s){
		if (!(u instanceof PlatformUniverse))
			throw new IllegalArgumentException("");
		
		PlatformUniverse pu = (PlatformUniverse) u;
		
		pu.addPlatform(pos, radius);
		
		PropertyHolder.getInstance().setProperty("platformPosition", pos.x + "," + pos.y);
	}

	
}
