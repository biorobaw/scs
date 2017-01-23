package platform.simulatorVirtual.components;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.componentInterfaces.FeederVisibilityInterface;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;

public class FeederVisibility implements FeederVisibilityInterface{

	float halfFieldOfView;
	float visionDist;
	
	public FeederVisibility(ElementWrapper params) {
		// TODO Auto-generated constructor stub
		this.halfFieldOfView = params.getChildFloat("halfFieldOfView");
		this.visionDist = params.getChildFloat("visionDist");
	}
	
	
	@Override
	public List<Feeder> getVisibleFeeders(int[] except) {
		
		List<Feeder> res = new LinkedList<Feeder>();
		VirtUniverse universe = VirtUniverse.getInstance();
		
		Point3f pos = universe.getRobotPosition();
		
		for (Integer i : universe.getFeederNums())
			if (except == null || !in(i, except))
				if (universe.canRobotSeeFeeder(i, halfFieldOfView, visionDist)) {
					// Get relative position
					Point3f fpos = universe.getFoodPosition(i);
					Point3f relFPos = new Point3f(fpos.x-pos.x,fpos.y-pos.y,fpos.z-pos.z);
					// Return the landmark
					Feeder relFeeder = new Feeder(universe.getFeeder(i));
					relFeeder.setPosition(relFPos);
					res.add(relFeeder);
				}

		return res;
	}

	@Override
	public Feeder getClosestFeeder() {
		List<Feeder> feeders = getVisibleFeeders(null);
		float minDist = Float.MAX_VALUE;
		Feeder closest = null;
		for (Feeder f : feeders){
			float dist = f.getPosition().distance(new Point3f());
			if (dist < minDist){
				closest = f;
				minDist = dist;
			}
		}
		return closest;	
	}
	
	private boolean in(int o, int[] except) {
		for (int i = 0; i < except.length; i++)
			if (except[i] == o)
				return true;
		return false;
	}

}
