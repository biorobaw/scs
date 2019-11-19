package com.github.biorobaw.scs.utils.math;

public class Floats {


	/**
	 * Returns the max value in the array
	 * @param data
	 * @return
	 */
	static public float max(float[] data) {
		float m = Float.NEGATIVE_INFINITY;
		for(var v : data) if(v>m) m=v;
		return m;
	}
	
	/**
	 * Returns the min value in the array
	 * @param values
	 * @return
	 */
	static public float min(float[] values) {
		var m = Float.POSITIVE_INFINITY;
		for(var v : values) if(v<m) m=v;
		return m;
	}
	
	/**
	 * Sum all elements in the array and then returns the result
	 * @param values
	 * @return
	 */
	static public float sum(float[] values) {
		var m = 0f;
		for(var v : values) m+=v;
		return m;
	}
	
	
	/**
	 * Makes a copy of the array.
	 * @param from
	 * @return new array containing the copy
	 */
	static public float[] copy(float[] from) {
		var res = new float[from.length];
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
	static public float[] copy(float[] from, float[] to) {
		for(int i=0; i<from.length; i++)
			to[i] = from[i];
		return to;
	}
	
	
	/**
	 * Applies the exponential function to all elements in the array.
	 * @param data
	 * @return new array containing the results
	 */
	static public float[] exp(float[] data) {
		var res = new float[data.length];
		for(int i=0; i<data.length; i++)
			res[i] = (float)Math.exp(data[i]);
		return res;
	}
	
	/**
	 * Applies the exp function to all elements in the array.
	 * @param data
	 * @param res array to store the results
	 * @result pointer to res
	 */
	static public float[] exp(float[] data, float[] res) {
		for(int i=0; i<data.length; i++)
			res[i] = (float)Math.exp(data[i]);
		return res;
	}
	
	
	/**
	 * Applies the log function to all elements in the array.
	 * @param data
	 * @return new array containing the results
	 */
	static public float[] log(float[] data) {
		var res = new float[data.length];
		for(int i=0; i<data.length; i++)
			res[i] = (float)Math.log(data[i]);
		return res;
	}
	
	/**
	 * Applies the log function to all elements in the array.
	 * @param data
	 * @param res array to store the results
	 * @result pointer to res
	 */
	static public float[] log(float[] data, float[] res) {
		for(int i=0; i<data.length; i++)
			res[i] = (float)Math.log(data[i]);
		return res;
	}
	
	/**
	 * Applies the sin function to all elements in the array.
	 * @param data
	 * @return new array containing the results
	 */
	static public float[] sin(float[] data) {
		var res = new float[data.length];
		for(int i=0; i<data.length; i++)
			res[i] = (float)Math.sin(data[i]);
		return res;
	}
	
	/**
	 * Applies the sin function to all elements in the array.
	 * @param data
	 * @param res array to store the results
	 * @result pointer to res
	 */
	static public float[] sin(float[] data, float[] res) {
		for(int i=0; i<data.length; i++)
			res[i] = (float)Math.sin(data[i]);
		return res;
	}
	
	/**
	 * Applies the cos function to all elements in the array.
	 * @param data
	 * @return new array containing the results
	 */
	static public float[] cos(float[] data) {
		var res = new float[data.length];
		for(int i=0; i<data.length; i++)
			res[i] = (float)Math.cos(data[i]);
		return res;
	}
	
	/**
	 * Applies the cos function to all elements in the array.
	 * @param data
	 * @param res array to store the results
	 * @result pointer to res
	 */
	static public float[] cos(float[] data, float[] res) {
		for(int i=0; i<data.length; i++)
			res[i] = (float)Math.cos(data[i]);
		return res;
	}
	
	/**
	 * Applies the tan function to all elements in the array.
	 * @param data
	 * @return new array containing the results
	 */
	static public float[] tan(float[] data) {
		var res = new float[data.length];
		for(int i=0; i<data.length; i++)
			res[i] = (float)Math.tan(data[i]);
		return res;
	}
	
	/**
	 * Applies the tan function to all elements in the array.
	 * @param data
	 * @param res array to store the results
	 * @result pointer to res
	 */
	static public float[] tan(float[] data, float[] res) {
		for(int i=0; i<data.length; i++)
			res[i] = (float)Math.tan(data[i]);
		return res;
	}
	
	
	/**
	 * Adds two vectors.
	 * @param left
	 * @param right
	 * @return new array containing the results
	 */
	static public float[] add(float[] left, float[] right) {
		var res = new float[left.length];
		for(int i=0; i<left.length; i++)
			res[i] = left[i] + right[i];
		return res;
	}
	
	
	/**
	 * Adds two vectors.
	 * @param left
	 * @param right
	 * @param res array to store the results
	 * @return pointer to res
	 */
	static public float[] add(float[] left, float[] right, float res[]) {
		for(int i=0; i<left.length; i++)
			res[i] = left[i] + right[i];
		return res;
	}
	
	/**
	 * Adds a vector and scalar.
	 * @param left
	 * @param right
	 * @return new array containing the results
	 */
	static public float[] add(float[] left, float right) {
		var res = new float[left.length];
		for(int i=0; i<left.length; i++)
			res[i] = left[i] + right;
		return res;
	}
	
	
	/**
	 * Adds a vector and scalar.
	 * @param left
	 * @param right
	 * @param res array to store the results
	 * @return pointer to res
	 */
	static public float[] add(float[] left, float right, float res[]) {
		for(int i=0; i<left.length; i++)
			res[i] = left[i] + right;
		return res;
	}
	
	/**
	 * Multiply two vectors element by element.
	 * @param left
	 * @param right
	 * @return new array containing the results
	 */
	static public float[] mul(float[] left, float[] right) {
		var res = new float[left.length];
		for(int i=0; i<left.length; i++)
			res[i] = left[i] * right[i];
		return res;
	}
	
	
	/**
	 * Multiply two vectors element by element.
	 * @param left
	 * @param right
	 * @param res array to store the results
	 * @return pointer to res
	 */
	static public float[] mul(float[] left, float[] right, float res[]) {
		for(int i=0; i<left.length; i++)
			res[i] = left[i] * right[i];
		return res;
	}
	
	/**
	 * Multiplies a vector with a scalar
	 * @param left
	 * @param right
	 * @return new array containing the results
	 */
	static public float[] mul(float[] left, float right) {
		var res = new float[left.length];
		for(int i=0; i<left.length; i++)
			res[i] = left[i] * right;
		return res;
	}
	
	
	/**
	 * Multiplies a vector with a scalar
	 * @param left
	 * @param right
	 * @param res array to store the results
	 * @return pointer to res
	 */
	static public float[] mul(float[] left, float right, float res[]) {
		for(int i=0; i<left.length; i++)
			res[i] = left[i] * right;
		return res;
	}
	
	/**
	 * Subtract two vectors element by element.
	 * @param left
	 * @param right
	 * @return new array containing the results
	 */
	static public float[] sub(float[] left, float[] right) {
		var res = new float[left.length];
		for(int i=0; i<left.length; i++)
			res[i] = left[i] - right[i];
		return res;
	}
	
	
	/**
	 * Subtract two vectors element by element.
	 * @param left
	 * @param right
	 * @param res array to store the results
	 * @return pointer to res
	 */
	static public float[] sub(float[] left, float[] right, float res[]) {
		for(int i=0; i<left.length; i++)
			res[i] = left[i] - right[i];
		return res;
	}
	
	/**
	 * Subtract a scalar from a vector
	 * @param left
	 * @param right
	 * @return new array containing the results
	 */
	static public float[] sub(float[] left, float right) {
		var res = new float[left.length];
		for(int i=0; i<left.length; i++)
			res[i] = left[i] - right;
		return res;
	}
	
	
	/**
	 * Subtract a scalar from a vector
	 * @param left
	 * @param right
	 * @param res array to store the results
	 * @return pointer to res
	 */
	static public float[] sub(float[] left, float right, float res[]) {
		for(int i=0; i<left.length; i++)
			res[i] = left[i] - right;
		return res;
	}
	
	
	/**
	 * Subtract a vector from a scalar
	 * @param left
	 * @param right
	 * @return new array containing the results
	 */
	static public float[] sub(float left, float[] right) {
		var res = new float[right.length];
		for(int i=0; i<right.length; i++)
			res[i] = left - right[i];
		return res;
	}
	
	
	/**
	 * Subtract a vector from a scalar
	 * @param left
	 * @param right
	 * @param res array to store the results
	 * @return pointer to res
	 */
	static public float[] sub(float left, float[] right, float res[]) {
		for(int i=0; i<right.length; i++)
			res[i] = left - right[i];
		return res;
	}
	
	/**
	 * Divides two vectors element by element.
	 * @param left
	 * @param right
	 * @return new array containing the results
	 */
	static public float[] div(float[] left, float[] right) {
		var res = new float[left.length];
		for(int i=0; i<left.length; i++)
			res[i] = left[i] / right[i];
		return res;
	}
	
	
	/**
	 * Divides two vectors element by element.
	 * @param left
	 * @param right
	 * @param res array to store the results
	 * @return pointer to res
	 */
	static public float[] div(float[] left, float[] right, float res[]) {
		for(int i=0; i<left.length; i++)
			res[i] = left[i] / right[i];
		return res;
	}
	
	/**
	 * Divides a scalar from a vector
	 * @param left
	 * @param right
	 * @return new array containing the results
	 */
	static public float[] div(float[] left, float right) {
		var res = new float[left.length];
		for(int i=0; i<left.length; i++)
			res[i] = left[i] / right;
		return res;
	}
	
	
	/**
	 * Divides a scalar from a vector
	 * @param left
	 * @param right
	 * @param res array to store the results
	 * @return pointer to res
	 */
	static public float[] div(float[] left, float right, float res[]) {
		for(int i=0; i<left.length; i++)
			res[i] = left[i] / right;
		return res;
	}
	
	
	/**
	 * Divides a vector from a scalar
	 * @param left
	 * @param right
	 * @return new array containing the results
	 */
	static public float[] div(float left, float[] right) {
		var res = new float[right.length];
		for(int i=0; i<right.length; i++)
			res[i] = left / right[i];
		return res;
	}
	
	
	/**
	 * Divides a vector from a scalar
	 * @param left
	 * @param right
	 * @param res array to store the results
	 * @return pointer to res
	 */
	static public float[] div(float left, float[] right, float res[]) {
		for(int i=0; i<right.length; i++)
			res[i] = left / right[i];
		return res;
	}
	
	/**
	 * Gets all elements indicated by the ids
	 * @param from Array to get the elements from
	 * @param ids The indeces to get
	 * @return A new array containing the requested elements
	 */
	static public float[] getElements(float[] from, int[] ids) {
		var res = new float[ids.length];
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
	static public float[] getElements(float[] from, int[] ids, float[] res) {
		for(int i=0; i<ids.length; i++) res[i] = from[ids[i]];
		return res;
	}
	
	/**
	 * Calculates the softmax distribution from the given array
	 * @param values 
	 * @return A new array with the result
	 */
	static public float[] softmax(float[] values) {
		var res = new float[values.length];
		return div(res,sum(exp(sub(values,max(values),res),res)));
	}
	
	/**
	 * Calculates the softmax distribution from the given array
	 * @param values 
	 * @param res Array to store the results
	 * @return A reference to the argument 'res'
	 */
	static public float[] softmax(float[] values, float[] res) {
		return div(res,sum(exp(sub(values,max(values),res),res)));
	}
	
	/**
	 * Round all values in the array
	 * @param values
	 * @return Returns an integer array of rounded values
	 */
	static public int[] round(float[] values) {
		var res = new int[values.length];
		for(int i=0; i< values.length; i++)
			res[i] = Math.round(values[i]);
		return res;
	}
	
	/**
	 * Round all values in the array
	 * @param values
	 * @param res Array to store the results
	 * @return A reference to the argument 'res'
	 */
	static public int[] round(float[] values, int[] res) {
		for(int i=0; i< values.length; i++)
			res[i] = Math.round(values[i]);
		return res;
	}
	

	
	
	
}
