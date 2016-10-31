package edu.usf.micronsl.port.twodimensional.sparse;

import java.util.HashMap;
import java.util.Map;

import edu.usf.micronsl.Datatypes.SparseMatrix;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.twodimensional.Port2d;

/**
 * FloatSparseMatrixPort
 * 
 * @author Martin Llofriu
 *
 */
public class SparseMatrixPort<T extends Number> extends Port2d<T>{

	
	/**
	 * The array to hold the values
	 */
	public SparseMatrix<T> data;
	
	
	public SparseMatrixPort(Module owner, SparseMatrix<T> data) {
		super(owner);

		if (data.rows == 0 || data.columns == 0)
			throw new IllegalArgumentException("Cannot use matrix with 0 rows or columns");

		this.data = data;
	}


	@Override
	public int getNRows() {
		// TODO Auto-generated method stub
		return data.rows;
	}


	@Override
	public int getNCols() {
		// TODO Auto-generated method stub
		return data.columns;
	}


	@Override
	public T get(int i, int j) {
		// TODO Auto-generated method stub
		return data.get(i, j);
	}


	@Override
	public void set(int i, int j, T x) {
		// TODO Auto-generated method stub
		data.set(i, j, x);
		
	}





	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return data;
	}


	@Override
	public void getDataArrayMatrix(T[][] copyData) {
		// TODO Auto-generated method stub
		data.getDataAsArray(copyData);
	}


	@Override
	public void clear() {
		data.clear();
		
	}
	

	

}
