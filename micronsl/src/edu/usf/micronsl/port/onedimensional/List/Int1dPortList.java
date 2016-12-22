package edu.usf.micronsl.port.onedimensional.List;

import java.util.LinkedList;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Int1dPort;

/**
 * A port that holds a 1 dimensional set of integers using a native array.
 * 
 * @author Martin Llofriu
 *
 */
public class Int1dPortList extends Int1dPort {

	/**
	 * The array to hold the data
	 */
	public LinkedList<Integer> data = new LinkedList<Integer>();

	/**
	 * Constructor that takes the data as an argument. Take into account that
	 * arrays are passed by reference (i.e. it is not copied)
	 * 
	 * @param owner
	 *            The owner module
	 * @param data
	 *            The data array to use as storage.
	 */
	
	
	public Int1dPortList(Module owner, LinkedList<Integer> data) {
		super(owner);

		if(data!=null) this.data = data;
	}
	
	public Int1dPortList(Module owner) {
		this(owner,(LinkedList<Integer>)null);
	}

	@Override
	public int getSize() {
		return data.size();
	}

	@Override
	public int get(int index) {
		return data.get(index);
	}

	@Override
	public void set(int i, int x) {
		data.set(i, x);
	}

	@Override
	public void clear() {
		data.clear();
	}

	/**
	 * Get access to the internal data storage. The array is returned as a
	 * reference, so it is not of exclusive access.
	 * 
	 * @return
	 */
	@Override
	public int[] getData() {
		int[] ans = new int[data.size()];
		for(int i=0;i<data.size();i++) ans[i]=data.get(i);
		return ans;
	}

	@Override
	public void getData(int[] data) {
		for(int i=0;i<this.data.size();i++) data[i]=this.data.get(i);
	}

	public void add(Integer val){
		data.add(val);
	}
	
}
