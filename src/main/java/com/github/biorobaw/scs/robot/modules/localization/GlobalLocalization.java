package com.github.biorobaw.scs.robot.modules.localization;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.github.biorobaw.scs.robot.RobotModule;
import com.github.biorobaw.scs.utils.XML;

public class GlobalLocalization extends RobotModule implements SlamModule {
		
	public GlobalLocalization(XML xml) {
		super(xml);
	}
	
	@Override
	public Vector3D getPosition() {
		return proxy.getPosition();
	}

	@Override
	public Rotation getOrientation() {		
		return proxy.getOrientation();
	}
	
	@Override
	public float getOrientation2D() {
		return proxy.getOrientation2D();
	}
	
	
}
