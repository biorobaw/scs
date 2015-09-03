package edu.usf.ratsim.micronsl;

import java.util.Map;

public abstract class Float1dSparsePort extends Float1dPort {

	public Float1dSparsePort(Module owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	public abstract Map<Integer,Float> getNonZero();

	
}
