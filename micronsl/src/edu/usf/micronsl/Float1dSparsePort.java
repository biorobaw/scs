package edu.usf.micronsl;

import java.util.Map;

public abstract class Float1dSparsePort extends Float1dPort {

	public Float1dSparsePort(Module owner) {
		super(owner);
	}

	public abstract Map<Integer, Float> getNonZero();

}
