package edu.usf.micronsl.port.onedimensional.vector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;

/**
 * A port that holds a one dimensional set of floats.
 * 
 * @author Pablo Scleidoroich
 *
 */
public class PointPort extends Port {

	private Coordinate data;
	
	public PointPort(Module owner) {
		super(owner);
	}

	public  Coordinate get(){
		return data;
	}

	public void set(Coordinate data){
		this.data = data;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public void clear() {
		data = new Coordinate();
	}


}
