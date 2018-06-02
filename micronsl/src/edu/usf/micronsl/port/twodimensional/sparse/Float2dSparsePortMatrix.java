package edu.usf.micronsl.port.twodimensional.sparse;

import java.util.HashMap;
import java.util.Map;

import edu.usf.micronsl.module.Module;

public class Float2dSparsePortMatrix extends Float2dSparsePort {
	
	Map<Entry, Float> nonZero;
	Map<Integer, Map<Entry, Float>> nonZeroPerRow;
	private int ncol;
	private int nrow;

	public Float2dSparsePortMatrix(Module owner, int nrow, int ncol) {
		super(owner);
		
		nonZero = new HashMap<Entry, Float>(nrow * ncol / 1000);
		nonZeroPerRow = new HashMap<Integer, Map<Entry, Float>>(nrow);
		
		this.nrow = nrow;
		this.ncol = ncol;
	}

	@Override
	public Map<Entry, Float> getNonZeroRow(int row) {
		return nonZeroPerRow.get(row);
	}

	@Override
	public Map<Entry, Float> getNonZero() {
		return nonZero;
	}

	@Override
	public int getNRows() {
		return nrow;
	}

	@Override
	public int getNCols() {
		return ncol;
	}

	@Override
	public float get(int i, int j) {
		Entry e = new Entry(i,j);
		if (nonZero.containsKey(e))
			return nonZero.get(e);
		
		return 0;
	}

	@Override
	public void set(int i, int j, float x) {
		if (i < 0 || i >= nrow || j < 0 || j >= ncol) {
			System.out.println("(i,j) = ("+i+","+j+") out of range");
			throw new IllegalArgumentException();
		}
		
		Entry e = new Entry(i,j);
		if (x == 0){
			if (nonZero.containsKey(e)){
				nonZero.remove(e);
				nonZeroPerRow.get(i).remove(e);
			}
		} else {
			nonZero.put(e, x);
			if (!nonZeroPerRow.containsKey(i))
				nonZeroPerRow.put(i, new HashMap<Entry, Float>(ncol));
			nonZeroPerRow.get(i).put(e, x);
		}
	}

	@Override
	public float[][] getData() {
		float[][] data = new float[nrow][ncol];
		getData(data);
		return data;
	}

	@Override
	public void getData(float[][] data) {
		for (Entry e : nonZero.keySet())
			data[e.i][e.j] = nonZero.get(e);
	}

	@Override
	public void clear() {
		nonZero.clear();
		nonZeroPerRow.clear();
	}

	@Override
	public boolean isRowEmpty(int i) {
		return !nonZeroPerRow.containsKey(i) || nonZeroPerRow.get(i).isEmpty();
	}

}
