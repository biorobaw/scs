package com.github.biorobaw.scs.utils.math;

public class Doubles {


	/**
	 * Returns the max value in the array
	 * @param data
	 * @return
	 */
	static public double max(double[] data) {
		double m = Double.NEGATIVE_INFINITY;
		for(var v : data) if(v>m) m=v;
		return m;
	}
	
	/**
	 * Returns the min value in the array
	 * @param values
	 * @return
	 */
	static public double min(double[] values) {
		var m = Double.POSITIVE_INFINITY;
		for(var v : values) if(v<m) m=v;
		return m;
	}
	
	/**
	 * Sum all elements in the array and then returns the result
	 * @param values
	 * @return
	 */
	static public double sum(double[] values) {
		var m = 0;
		for(var v : values) m+=v;
		return m;
	}
	
	/**
	 * Makes a copy of the array.
	 * @param from
	 * @return new array containing the copy
	 */
	static public double[] copy(double[] from) {
		var res = new double[from.length];
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
	static public double[] copy(double[] from, double[] to) {
		for(int i=0; i<from.length; i++)
			to[i] = from[i];
		return to;
	}
	
	
	/**
	 * Applies the exponential function to all elements in the array.
	 * @param data
	 * @return new array containing the results
	 */
	static public double[] exp(double[] data) {
		var res = new double[data.length];
		for(int i=0; i<data.length; i++)
			res[i] = (double)Math.exp(data[i]);
		return res;
	}
	
	/**
	 * Applies the exp function to all elements in the array.
	 * @param data
	 * @param res array to store the results
	 * @result pointer to res
	 */
	static public double[] exp(double[] data, double[] res) {
		for(int i=0; i<data.length; i++)
			res[i] = (double)Math.exp(data[i]);
		return res;
	}
	
	
	/**
	 * Applies the log function to all elements in the array.
	 * @param data
	 * @return new array containing the results
	 */
	static public double[] log(double[] data) {
		var res = new double[data.length];
		for(int i=0; i<data.length; i++)
			res[i] = (double)Math.log(data[i]);
		return res;
	}
	
	/**
	 * Applies the log function to all elements in the array.
	 * @param data
	 * @param res array to store the results
	 * @result pointer to res
	 */
	static public double[] log(double[] data, double[] res) {
		for(int i=0; i<data.length; i++)
			res[i] = (double)Math.log(data[i]);
		return res;
	}
	
	/**
	 * Applies the sin function to all elements in the array.
	 * @param data
	 * @return new array containing the results
	 */
	static public double[] sin(double[] data) {
		var res = new double[data.length];
		for(int i=0; i<data.length; i++)
			res[i] = (double)Math.sin(data[i]);
		return res;
	}
	
	/**
	 * Applies the sin function to all elements in the array.
	 * @param data
	 * @param res array to store the results
	 * @result pointer to res
	 */
	static public double[] sin(double[] data, double[] res) {
		for(int i=0; i<data.length; i++)
			res[i] = (double)Math.sin(data[i]);
		return res;
	}
	
	/**
	 * Applies the cos function to all elements in the array.
	 * @param data
	 * @return new array containing the results
	 */
	static public double[] cos(double[] data) {
		var res = new double[data.length];
		for(int i=0; i<data.length; i++)
			res[i] = (double)Math.cos(data[i]);
		return res;
	}
	
	/**
	 * Applies the cos function to all elements in the array.
	 * @param data
	 * @param res array to store the results
	 * @result pointer to res
	 */
	static public double[] cos(double[] data, double[] res) {
		for(int i=0; i<data.length; i++)
			res[i] = (double)Math.cos(data[i]);
		return res;
	}
	
	/**
	 * Applies the tan function to all elements in the array.
	 * @param data
	 * @return new array containing the results
	 */
	static public double[] tan(double[] data) {
		var res = new double[data.length];
		for(int i=0; i<data.length; i++)
			res[i] = (double)Math.tan(data[i]);
		return res;
	}
	
	/**
	 * Applies the tan function to all elements in the array.
	 * @param data
	 * @param res array to store the results
	 * @result pointer to res
	 */
	static public double[] tan(double[] data, double[] res) {
		for(int i=0; i<data.length; i++)
			res[i] = (double)Math.tan(data[i]);
		return res;
	}
	
	
	/**
	 * Adds two vectors.
	 * @param left
	 * @param right
	 * @return new array containing the results
	 */
	static public double[] add(double[] left, double[] right) {
		var res = new double[left.length];
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
	static public double[] add(double[] left, double[] right, double res[]) {
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
	static public double[] add(double[] left, double right) {
		var res = new double[left.length];
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
	static public double[] add(double[] left, double right, double res[]) {
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
	static public double[] mul(double[] left, double[] right) {
		var res = new double[left.length];
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
	static public double[] mul(double[] left, double[] right, double res[]) {
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
	static public double[] mul(double[] left, double right) {
		var res = new double[left.length];
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
	static public double[] mul(double[] left, double right, double res[]) {
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
	static public double[] sub(double[] left, double[] right) {
		var res = new double[left.length];
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
	static public double[] sub(double[] left, double[] right, double res[]) {
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
	static public double[] sub(double[] left, double right) {
		var res = new double[left.length];
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
	static public double[] sub(double[] left, double right, double res[]) {
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
	static public double[] sub(double left, double[] right) {
		var res = new double[right.length];
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
	static public double[] sub(double left, double[] right, double res[]) {
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
	static public double[] div(double[] left, double[] right) {
		var res = new double[left.length];
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
	static public double[] div(double[] left, double[] right, double res[]) {
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
	static public double[] div(double[] left, double right) {
		var res = new double[left.length];
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
	static public double[] div(double[] left, double right, double res[]) {
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
	static public double[] div(double left, double[] right) {
		var res = new double[right.length];
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
	static public double[] div(double left, double[] right, double res[]) {
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
	static public double[] getElements(double[] from, int[] ids) {
		var res = new double[ids.length];
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
	static public double[] getElements(double[] from, int[] ids, double[] res) {
		for(int i=0; i<ids.length; i++) res[i] = from[ids[i]];
		return res;
	}
	
	
	/**
	 * Calculates the softmax distribution from the given array
	 * @param values 
	 * @return A new array with the result
	 */
	static public double[] softmax(double[] values) {
		var res = new double[values.length];
		return div(res,sum(exp(sub(values,max(values),res),res)));
	}
	
	/**
	 * Calculates the softmax distribution from the given array
	 * @param values 
	 * @param res Array to store the results
	 * @return A reference to the argument 'res'
	 */
	static public double[] softmax(double[] values, double[] res) {
		return div(res,sum(exp(sub(values,max(values),res),res)));
	}
	
	/**
	 * Round all values in the array
	 * @param values
	 * @return Returns an integer array of rounded values
	 */
	static public int[] round(double[] values) {
		var res = new int[values.length];
		for(int i=0; i< values.length; i++)
			res[i] = (int)Math.round(values[i]);
		return res;
	}
	
	/**
	 * Round all values in the array
	 * @param values
	 * @param res Array to store the results
	 * @return A reference to the argument 'res'
	 */
	static public int[] round(double[] values, int[] res) {
		for(int i=0; i< values.length; i++)
			res[i] = (int)Math.round(values[i]);
		return res;
	}
	
	/**
	 * Round all values in the array
	 * @param values
	 * @return Returns an integer array of rounded values
	 */
	static public long[] roundL(double[] values) {
		var res = new long[values.length];
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
	static public long[] roundL(double[] values, long[] res) {
		for(int i=0; i< values.length; i++)
			res[i] = Math.round(values[i]);
		return res;
	}
	
	
}
