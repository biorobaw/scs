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
	 * Create new array concatenating left and right arrays
	 * @param left  array of elements
	 * @param right array of elements
	 * @result pointer to param 'to'
	 */
	static public int[] concat(int[] left, int[] right) {
		var res = new int[left.length + right.length];
		for(int i=0; i<left.length; i++)
			res[i] = left[i];
		for(int i=0; i<right.length; i++)
			res[i+left.length] = right[i];
		return res;
	}

	/**
	 * Create a new array concatenating left array with right element
	 * @param left  array of elements
	 * @param right element
	 * @result pointer to param 'to'
	 */
	static public int[] concat(int[] left, int right) {
		var res = new int[left.length + 1];
		for(int i=0; i<left.length; i++)
			res[i] = left[i];
		res[left.length] = right;
		return res;
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

	public interface GetArray {
		public int[] get();
	}

	/**
	 * Sets all elements of the given array equal to the given constant
	 * @param value
	 * @param output copy of the output array pointer
	 * @return
	 */
	static public int[] constant(int value, int[] output) {
		for(int i=0; i< output.length; i++) output[i] = value;
		return output;
	}

	/**
	 * Creates a an array of the given length, with all its elements equal to the given value
	 * @param value
	 * @param length
	 * @return
	 */
	static public int[] constant(int value, int length) {
		return constant(value, new int[length]);
	}

	static public long sum(int[] data){
		long res = 0;
		for(var d: data) res+=d;
		return res;
	}

	/**
	 * Add all elements in the range (includes start position and excludes end)
	 * @param data   data to be added
	 * @param start  start index (inclusive)
	 * @param end    end index (exclusive)
	 * @return
	 */
	static public long sum(int[] data, int start, int end){
		long res = 0;
		for(int i=start; i<end; i++) res+=data[i];
		return res;
	}

}
