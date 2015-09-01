package edu.usf.ratsim.micronsl;

import java.util.List;

public class Float1dPortConcatenate extends Float1dPort {

	private List<Float1dPort> states;
	private int size;
	private float[] tmpData;

	public Float1dPortConcatenate(Module owner, List<Float1dPort> states) {
		super(owner);

		this.states = states;

		size = 0;
		int maxSize = 0;
		for (Float1dPort state : states){
			size += state.getSize();
			if (state.getSize() > maxSize)
				maxSize = state.getSize();
		}
		tmpData = new float[maxSize];
		
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public float get(int index) {
		for (Float1dPort state : states) {
			if (index - state.getSize() < 0)
				return state.get(index);
			else
				index -= state.getSize();
		}

		throw new RuntimeException("Index out of bounds");
	}

	@Override
	public synchronized float[] getData() {
		float [] res = new float[size];
		int i = 0;
		for (Float1dPort state : states){
			int stateSize = state.getSize();
			for (int j = 0; j < stateSize; j++)
				tmpData[j] = 0;
			state.getData(tmpData);
			System.arraycopy(tmpData, 0, res, i, stateSize);
			i += stateSize;
		}
			
		return res;
	}

	@Override
	public synchronized void getData(float[] res) {
		int i = 0;
		for (Float1dPort state : states){
			int stateSize = state.getSize();
			for (int j = 0; j < stateSize; j++)
				tmpData[j] = 0;
			state.getData(tmpData);
			System.arraycopy(tmpData, 0, res, i, stateSize);
			i += stateSize;
		}
	}

}
