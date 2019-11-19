package com.github.biorobaw.scs.utils.math;

public class Integers {
	
	/**
	 * Create a range from min to max in steps
	 * @param min min value (inclusive)
	 * @param max max value (exclusive)
	 * @param step step between consecutive elements
	 * @return A new array containing the results
	 */
	static public int[] range(int min, int max, int step) {
		int size = 1 + (max-1-min) / step;
		var res = new int[size];
		for (int i = 0; i < size; i++) {
			res[i] = min + i*step;
		}
		return res;
	}
	
	/**
	 * Create a range from min to max in steps of 1
	 * @param min min value (inclusive)
	 * @param max max value (exclusive)
	 * @return A new array containing the results
	 */
	static public int[] range(int min, int max) {
		return range(min,max,1);
	}
	
	/**
	 * Create a range from 0 (inclusive) to max in steps of 1
	 * @param max max value (exclusive)
	 * @return A new array containing the results
	 */
	static public int[] range(int max) {
		return range(0,max,1);
	}
	
	/**
	 * Returns the max value in the array
	 * @param data
	 * @return
	 */
	static public int max(int[] data) {
		var m = Integer.MIN_VALUE;
		for(var v : data) if(v>m) m=v;
		return m;
	}
	
	/**
	 * Returns the min value in the array
	 * @param values
	 * @return
	 */
	static public int min(int[] values) {
		var m = Integer.MAX_VALUE;
		for(var v : values) if(v<m) m=v;
		return m;
	}
	
	/**
	 * Makes a copy of the array.
	 * @param from
	 * @return new array containing the copy
	 */
	static public int[] copy(int[] from) {
		var res = new int[from.length];
		for(int i=0; i<from.length; i++)
			res[i] = from[i];
		return res;
	}
	
	/**
	 * Makes a copy of the array.
	 * @param from
	 * @param to array to store the results
	 * @result pointer to param 'to'
	 */
	static public int[] copy(int[] from, int[] to) {
		for(int i=0; i<from.length; i++)
			to[i] = from[i];
		return to;
	}
	
	/**
	 * Makes a copy of the array casting to int.
	 * @param from
	 * @return new array containing the copy
	 */
	static public int[] copy(long[] from) {
		var res = new int[from.length];
		for(int i=0; i<from.length; i++)
			res[i] = (int)from[i];
		return res;
	}
	
	/**
	 * Makes a copy of the array casting to int.
	 * @param from
	 * @param to array to store the results
	 * @result pointer to param 'to'
	 */
	static public int[] copy(long[] from, int[] to) {
		for(int i=0; i<from.length; i++)
			to[i] = (int)from[i];
		return to;
	}
	
	/**
	 * Gets all elements indicated by the ids
	 * @param from Array to get the elements from
	 * @param ids The indeces to get
	 * @return A new array containing the requested elements
	 */
	static public int[] getElements(int[] from, int[] ids) {
		var res = new int[ids.length];
		for(int i=0; i<ids.length; i++) res[i] = from[ids[i]];
		return res;
	}
	
	/**
	 * Gets all elements indicated by the ids
	 * @param from Array to get the elements from
	 * @param ids The indeces to get
	 * @param res Array to store the results
	 * @return A reference to the argument 'res'
	 */
	static public int[] getElements(int[] from, int[] ids, int[] res) {
		for(int i=0; i<ids.length; i++) res[i] = from[ids[i]];
		return res;
	}
}
