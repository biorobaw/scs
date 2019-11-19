package com.github.biorobaw.scs.utils.math;

public class Longs {
	
	/**
	 * Create a range from min to max in steps
	 * @param min min value (inclusive)
	 * @param max max value (exclusive)
	 * @param step step between consecutive elements
	 * @return A new array containing the results
	 */
	static public long[] range(long min, long max, long step) {
		int size = (int)(1 + (max-1-min) / step);
		var res = new long[size];
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
	static public long[] range(long min, long max) {
		return range(min,max,1);
	}
	
	/**
	 * Create a range from 0 (inclusive) to max in steps of 1
	 * @param max max value (exclusive)
	 * @return A new array containing the results
	 */
	static public long[] range(long max) {
		return range(0,max,1);
	}
	
	/**
	 * Returns the max value in the array
	 * @param data
	 * @return
	 */
	static public long max(long[] data) {
		var m = Long.MIN_VALUE;
		for(var v : data) if(v>m) m=v;
		return m;
	}
	
	/**
	 * Returns the min value in the array
	 * @param values
	 * @return
	 */
	static public long min(long[] values) {
		var m = Long.MAX_VALUE;
		for(var v : values) if(v<m) m=v;
		return m;
	}
	
	/**
	 * Makes a copy of the array.
	 * @param from
	 * @return new array containing the copy
	 */
	static public long[] copy(long[] from) {
		var res = new long[from.length];
		for (int i=0; i<from.length; i++)
			res[i] = from[i];
		return res;
	}
	
	/**
	 * Makes a copy of the array.
	 * @param from
	 * @param to array to store the results
	 * @result polonger to param 'to'
	 */
	static public long[] copy(long[] from, long[] to) {
		for (int i=0; i<from.length; i++)
			to[i] = from[i];
		return to;
	}
	
	/**
	 * Makes a copy of the array casting to long.
	 * @param from
	 * @return new array containing the copy
	 */
	static public long[] copy(int[] from) {
		var res = new long[from.length];
		for (int i=0; i<from.length; i++)
			res[i] = (long)from[i];
		return res;
	}
	
	/**
	 * Makes a copy of the array casting to long.
	 * @param from
	 * @param to array to store the results
	 * @result polonger to param 'to'
	 */
	static public long[] copy(int[] from, long[] to) {
		for (int i=0; i<from.length; i++)
			to[i] = (long)from[i];
		return to;
	}
	
	/**
	 * Gets all elements indicated by the ids
	 * @param from Array to get the elements from
	 * @param ids The indeces to get
	 * @return A new array containing the requested elements
	 */
	static public long[] getElements(long[] from, int[] ids) {
		var res = new long[ids.length];
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
	static public long[] getElements(long[] from, int[] ids, long[] res) {
		for(int i=0; i<ids.length; i++) res[i] = from[ids[i]];
		return res;
	}
}
