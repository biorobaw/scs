package edu.usf.micronsl.Datatypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SparseArray<T extends Number> {

	Map<Integer, T> data = new HashMap<Integer,T>();
	
	public T get(int i){
		if(data.keySet().contains(i)) return data.get(i);
		return (T) new Integer(0);
	}
	
	public T getNonZero(int i){
		return data.get(i);
	}
	
	public Set<Integer> getNonZero(){
		return data.keySet();
	}
	
	public void set(int i,T value){
		if(value==(T) new Integer(0)) data.remove(i);
		else data.put(i, value);
		
	}
	
	public void setZero(int i){
		data.remove(i);
	}
	
	public void setNonZero(int i,T val){
		data.put(i, val);
	}
	
	public int size(){
		return data.size();
	}
	
	public void clear(){
		data.clear();
	}
	
	
}
