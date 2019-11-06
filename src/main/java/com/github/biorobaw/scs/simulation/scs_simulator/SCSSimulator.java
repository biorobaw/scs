package com.github.biorobaw.scs.simulation.scs_simulator;

import java.util.HashMap;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.github.biorobaw.scs.simulation.AbstractSimulator;
import com.github.biorobaw.scs.simulation.object.SimulatedObject;
import com.github.biorobaw.scs.utils.XML;

/**
 * This class implements a very simple 2d simulator
 * It assumes the only moving objects are the robots
 * and supports (v,w) speeds or (vx,vy,w) speeds for differential and holonomic drives
 * @author bucef
 *
 */
public class SCSSimulator extends AbstractSimulator {
	
	long next_object_id = 0;
		
	long time = 0;
	
	HashMap<Long,SCSRobot> robots = new HashMap<>();

	public SCSSimulator(XML xml) {
		super(xml);
	}
	
	@Override
	public void addObject(SimulatedObject o, Vector3D position, Rotation orientation) {
		o.set_guid(next_object_id++);
		if( o instanceof SCSRobotProxy ) {
			SCSRobotProxy proxy = (SCSRobotProxy)o;
			proxy.robot = new SCSRobot((float)position.getX(), 
									   (float)position.getY(), 
									   (float)orientation.getAngles(RotationOrder.XYZ)[2]);
			robots.put(proxy.get_guid(), proxy.robot);
		}
	}

	@Override
	public void removeObject(SimulatedObject o) {
		if( o instanceof SCSRobotProxy ) robots.remove(((SCSRobotProxy)o).get_guid());
	}

	@Override
	public Vector3D getObjectPosition(long guid) {
		var r = robots.get(guid);
		return new Vector3D(r.x, r.y, 0);
	}

	@Override
	public void setObjectPosition(long guid, Vector3D pos) {
		var r = robots.get(guid);
		r.x = (float)pos.getX();
		r.y = (float)pos.getY();
		
	}

	@Override
	public void simulate(long time_ms) {
		for(var r : robots.values()) {			
			r.simulate(time_ms);
		}
		time += time_ms;
	}


	@Override
	public long getTime() {
		return time;
	}

	@Override
	public Rotation getObjectOrientation(long guid) {
		var r = robots.get(guid);
		return new Rotation(new Vector3D(0,0,1), r.tita);
	}

	@Override
	public void setObjectOrientation(long guid, Rotation orientation) {
		var r = robots.get(guid);
		r.tita = (float)orientation.getAngles(RotationOrder.XYZ)[2];
	}

}
