package edu.usf.ratsim.micronsl;

public class Float1dPortArray extends Float1dPort {

	float[] data;

	public Float1dPortArray(Module owner, float[] data) {
		super(owner);
		this.data = data;
	}

	@Override
	public int getSize() {
		return data.length;
	}

	@Override
	public float get(int index) {
		return data[index];
	}

	@Override
	public float[] getData() {
		return data;
	}
	
	public void getData(float[] buf){
		System.arraycopy(data, 0, buf, 0, data.length);
	}

	public void set(float x) {
		for (int i = 0; i < data.length; i++)
			data[i] = x;
	}

}
