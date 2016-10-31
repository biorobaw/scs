package edu.usf.micronsl.Datatypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implements a sparse matrix with a given number of rows
 * @author bucef
 *
 * @param <T1>
 */

public class SparseMatrix<T1 extends Number> {
	private Map<Integer, SparseArray<T1>> data = new HashMap<Integer,SparseArray<T1>>();
	private Set<Integer> nonEmpty = new HashSet<Integer>(); 
	
	public int rows;
	public int columns;
	
	public SparseMatrix(int rows,int columns) {
		// TODO Auto-generated constructor stub
		this.rows= rows;
		this.columns=columns;
		for(int i=0;i<rows;i++)
			data.put(i, new SparseArray<T1>());
	}
	
	public SparseMatrix(T1[][] matrix){
		this(matrix.length,matrix[0].length);
		for (int i=0;i<matrix.length;i++)
			for(int j=0;j<matrix[i].length;j++)
				set(i,j,matrix[i][j]);

	}
	
	public T1 get(int i,int j){
		if(nonEmpty.contains(i) && data.get(i).getNonZero().contains(j)) return data.get(i).get(j);
		return (T1) new Integer(0);
	}
	
	public T1 getNonZero(int i, int j){
		return data.get(i).get(j);
	}
	
	public Set<Integer> getNonZeroRowIndexes(){
		return nonEmpty;
	}
	
	public SparseArray<T1> getRow(int i){
		return data.get(i);
	}
	
	public void set(int i,int j,T1 val){
		if (val == (T1) new Integer(0)) {
			data.get(i).setZero(j);
			if(data.get(i).size()==0)
				nonEmpty.remove(i);
		}else{
			data.get(i).setNonZero(j,val);
			nonEmpty.add(i);
		}
		
	}
	public void clear(){
		for (int k : nonEmpty) data.get(k).clear();
		nonEmpty.clear();
	}
	
	public void getDataAsArray(T1[][] copyData) {
		// TODO Auto-generated method stub
		for (int i=0;i<rows;i++)
			for(int j=0;j< columns;j++)
				copyData[i][j] = get(i, j);
		
	}

}
