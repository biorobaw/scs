package edu.usf.ratsim.nsl.modules.input;

import java.util.StringTokenizer;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.PropertyHolder;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.vector.PointPort;

/**
 * Provides an output port with the position of the platform
 * @author Martin Llofriu
 *
 */
public class PlatformPosition extends Module {

	private PointPort outPort;

	public PlatformPosition(String name) {
		super(name);
		
		outPort = new PointPort(this);
		addOutPort("platformPosition", outPort);
	}

	@Override
	public void run() {
		String pos = PropertyHolder.getInstance().getProperty("platformPosition");
		if(pos != null){
			StringTokenizer tok = new StringTokenizer(pos, ",");
			float x = Float.parseFloat(tok.nextToken());
			float y = Float.parseFloat(tok.nextToken());
			outPort.set(new Coordinate(x,y,0));
		}
			
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
