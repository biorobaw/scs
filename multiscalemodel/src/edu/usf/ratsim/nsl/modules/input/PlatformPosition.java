package edu.usf.ratsim.nsl.modules.input;

import java.util.StringTokenizer;

import javax.vecmath.Point3f;

import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.universe.Universe;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;

/**
 * Provides an output port with the position of the platform
 * @author Martin Llofriu
 *
 */
public class PlatformPosition extends Module {

	private Point3fPort outPort;

	public PlatformPosition(String name) {
		super(name);
		
		outPort = new Point3fPort(this);
		addOutPort("platformPosition", outPort);
	}

	@Override
	public void run() {
		String pos = PropertyHolder.getInstance().getProperty("platformPosition");
		if(pos != null){
			StringTokenizer tok = new StringTokenizer(pos, ",");
			float x = Float.parseFloat(tok.nextToken());
			float y = Float.parseFloat(tok.nextToken());
			outPort.set(new Point3f(x,y,0));
		}
			
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
