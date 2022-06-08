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
	 * Create new array concatenating left and right arrays
	 * @param left  array of elements
	 * @param right array of elements
	 * @result pointer to param 'to'
	 */
	static public float[] concat(float[] left, float[] right) {
		var res = new float[left.length + right.length];
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
	static public float[] concat(float[] left, float right) {
		var res = new float[left.length + 1];
		for(int i=0; i<left.length; i++)
			res[i] = left[i];
		res[left.length] = right;
		return res;
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
	 * Calculates the softmax distribution from the given array.
	 * Only elements where mask[i]!=0 are considered
	 * @param values 
	 * @param weights array of possitive weights
	 * @return A new array with the result
	 */
	static public float[] softmaxWithWeights(float[] values, float[] weights) {
		var res = new float[values.length];
		return softmaxWithWeights(values,weights,res);
	}
	
	/**
	 * Calculates the softmax distribution from the given array
	 * @param values 
	 * @param weights array of positive weigths 
	 * @param res Array to store the results
	 * @return A reference to the argument 'res'
	 */
	static public float[] softmaxWithWeights(float[] values, float[] weights, float[] res) {
		
		// we first find the maximum to avoid numerical instability: e^large_value = infinity
		float max_exponent = Float.NEGATIVE_INFINITY;
		for(int i=0; i<values.length; i++){
			if(weights[i]!=0 && max_exponent < values[i])
				max_exponent = values[i];
		}
		
		float sum = 0;
		for(int i=0; i <values.length; i++) {
			if(weights[i]!=0) {
				res[i] = weights[i]*(float)Math.exp(values[i]-max_exponent);
				sum+=res[i];
			} else res[i] = 0;
		}
		if(sum==0) {
			System.err.println("ERROR (Floats.java): weightd softmax is not a distribution");
			System.exit(-1);
		}
		for(int i=0; i <values.length; i++) res[i]/=sum;
		return res;
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
	
	/**
	 * Calculates absolute values for all values in the array
	 * @param values
	 * @return Returns an array with the absolute values
	 */
	static public float[] abs(float[] values) {
		var res = new float[values.length];
		for(int i=0; i< values.length; i++)
			res[i] = Math.abs(values[i]);
		return res;
	}
	
	/**
	 * Calculates absolute values for all values in the array
	 * @param values
	 * @param res Array to store the results
	 * @return A reference to the argument 'res'
	 */
	static public float[] abs(float[] values, float[] res) {
		for(int i=0; i< values.length; i++)
			res[i] = Math.abs(values[i]);
		return res;
	}
	
	/**
	 * Converts the array to a comma separated string
	 * @param data
	 * @return
	 */
	static public String toString(float[] data) {
		var res = "";
		if(data.length > 0) res += data[0];
		for(int i=1; i<data.length; i++) res+= ", " + data[i];
		return res;
	}
	
	/**
	 * Sets all elements of the given array equal to the given constant
	 * @param value
	 * @param output copy of the output array pointer
	 * @return
	 */
	static public float[] constant(float value, float[] output) {
		for(int i=0; i< output.length; i++) output[i] = value;
		return output;
	}
	
	/**
	 * Creates a an array of the given length, with all its elements equal to the given value
	 * @param value
	 * @param length
	 * @return
	 */
	static public float[] constant(float value, int length) {
		return constant(value, new float[length]);
	}
	

	/**
	 * Creates a discrete uniform distribution of the given length
	 * @param length
	 * @return
	 */
	static public float[] uniform(int length) {
		return constant(1f/length, length);
	}
	
	/**
	 * Sets all elements of the output array to 1/output.length
	 * @param output a copy of the the output array pointer
	 * @return
	 */
	static public float[] uniform(float[] output) {
		return constant(1f/output.length, output);
	}

	/**
	 * Calculates the entropy of the input normalized by log(base);
	 * @param distribution A probability distribution (we assume it is already normalized).
	 * @param base
	 * @return
	 */
	static public float entropy(float[] distribution, float base) {
		double sum = 0;
		for(var p : distribution) if(p != 0) sum+= p*Math.log(p);
		return -(float)(sum / Math.log(base));
	}


	/**
	 * Negate the values in the array.
	 * @param input
	 * @return new array containing the negated copy
	 */
	static public float[] negate(float[] input) {
		var output = new float[input.length];
		for(int i=0; i<input.length; i++)
			output[i] = -input[i];
		return output;
	}

	/**
	 * Negates the values in the array in place.
	 * @param input
	 * @return array containing the negated copy
	 */
	static public float[] negate(float[] input, float[] output) {
		for(int i=0; i<input.length; i++)
			output[i] = -input[i];
		return output;
	}

	public interface GetArray {
		public float[] get();
	}
}
