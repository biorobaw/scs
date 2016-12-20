package edu.usf.micronsl.port.onedimensional.vector;

import javax.vecmath.Point3f;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;

/**
 * A port that holds a one dimensional set of floats.
 * 
 * @author Pablo Scleidoroich
 *
 */
public class Point3fPort extends Port {

	private Point3f data;
	
	public Point3fPort(Module owner) {
		super(owner);
	}

	public  Point3f get(){
		return data;
	}

	public void set(Point3f data){
		this.data = data;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public void clear() {
		data.set(0, 0, 0);
	}


}
