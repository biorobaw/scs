package edu.usf.micronsl.port.onedimensional.cartesian;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import edu.usf.micronsl.exec.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Bool1dPort;

/**
 * A port that computes the Cartesian product of a set of source ports. The
 * values of the source ports are and-ed together using all possible combinations to
 * obtain the values of this port. It is optimized to break combination upon
 * reaching false, and remembering the minimum and maximum index in which the
 * value will return zero as well.
 * 
 * @author Martin Llofriu
 *
 */
public class Bool1dPortCartesian extends Bool1dPort {

	/**
	 * The total size of the port
	 */
	private int size;
	/**
	 * The set of source ports
	 */
	private List<Bool1dPort> sources;
	/**
	 * The minimum index that will return 0, remembered from the last
	 * calculation
	 */
	private int minZeroVal;
	/**
	 * The maximum index that will return 0, remembered from the last
	 * calculation
	 */
	private int maxZeroVal;
	/**
	 * Flag specifying whether minZeroVal and maxZeroVal can be used
	 */
	private boolean validOptimization;
	/**
	 * Whether to apply 0 optimizations or not
	 */
	private boolean optimize;

	/**
	 * 
	 * @param owner
	 *            The owner module
	 * @param sources
	 *            The set of source ports. The sources that are more likely to
	 *            be 0 should be placed at the end of the list to improve
	 *            running time.
	 * @param eps
	 *            The minimum value upon which the number is considered to be
	 *            zero
	 * @param optimize Whether to apply optimizations or not
	 */
	public Bool1dPortCartesian(Module owner, List<Bool1dPort> sources, boolean optimize) {
		super(owner);

		// Invert the list to process it backwards in the get method
		this.sources = new LinkedList<Bool1dPort>();
		for (int i = sources.size() - 1; i >= 0; i--)
			this.sources.add(sources.get(i));

		// The size is the product of the source ports sizes
		if (sources.isEmpty()) {
			size = 0;
		} else {
			size = 1;
			for (Bool1dPort source : sources) {
				size *= source.getSize();
			}
		}

		if (size == 0)
			throw new RuntimeException("Empty input to Cartesian port");

		// No initial optimization
		validOptimization = false;
		this.optimize = optimize;
	}
	
	/**
	 * Constructor that sets optimizations to true by default
	 * @param owner
	 *            The owner module
	 * @param sources
	 *            The set of source ports. The sources that are more likely to
	 *            be 0 should be placed at the end of the list to improve
	 *            running time.
	 * @param eps
	 *            The minimum value upon which the number is considered to be
	 *            zero
	 */
	public Bool1dPortCartesian(Module owner, List<Bool1dPort> sources){
		this(owner, sources, true);
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	/**
	 * Get the value at position index. The i-th value is the product of the
	 * j-th values of each set s. For each set s, the index j is (i /
	 * prod_t=0..s-1(t.getSize()) % s.getSize().
	 * 
	 * The value at 0 is the and of all values at 0 in the sources.
	 * The value at 1 is the value at 1 in the first source times the value at 0
	 * in the others. The value at firstSource.getSize() is the value at 0 in
	 * the first source and-ed with the value at 1 in the second abd-ed with the value at 0
	 * in the rest.
	 */
	public boolean get(int index) {
		if (optimize && validOptimization && index >= minZeroVal && index <= maxZeroVal)
			return false;

		int remainingSize = size;
		boolean jointActivation = true;
		// Track the current scope of the source statetimize
		int currentsource = 0;

		for (Bool1dPort source : sources) {
			remainingSize /= source.getSize();
			int sourceIndex = (index / remainingSize) % source.getSize();
			currentsource += remainingSize * sourceIndex;
			jointActivation &= source.get(sourceIndex);
			if (jointActivation == false) {
				minZeroVal = currentsource;
				maxZeroVal = currentsource + remainingSize - 1;
				validOptimization = true;
				break;
			}
		}

		return jointActivation;
	}

	/**
	 * Invalidate the optimization
	 */
	public void clearOptimizationCache() {
		validOptimization = false;
	}

	@Override
	public boolean[] getData() {
		boolean[] data = new boolean[size];
		getData(data);
		return data;
	}

	@Override
	/**
	 * Returns the data from the port. This method is also optimized to skip
	 * large 0 areas.
	 */
	public void getData(boolean[] data) {
		Stack<Integer> listPointer = new Stack<Integer>();
		Stack<Integer> indexStack = new Stack<Integer>();
		Stack<Boolean> currValStack = new Stack<Boolean>();

		for (int i = sources.get(0).getSize() - 1; i >= 0; i--) {
			listPointer.push(0);
			indexStack.push(i);
			currValStack.push(true);
		}

		int element = 0;
		do {
			int listNum = listPointer.pop();
			int index = indexStack.pop();
			boolean currVal = currValStack.pop();

			currVal &= sources.get(listNum).get(index);
			if (optimize && currVal == false) {
				int elemsToSkip = 1;
				for (int s = listNum + 1; s < sources.size(); s++)
					elemsToSkip *= sources.get(s).getSize();
				element += elemsToSkip;
			} else {
				if (listNum < sources.size() - 1) {
					for (int i = sources.get(listNum + 1).getSize() - 1; i >= 0; i--) {
						listPointer.push(listNum + 1);
						indexStack.push(i);
						currValStack.push(currVal);
					}
				} else {
					data[element] = currVal;

					element++;
				}
			}
		} while (!listPointer.isEmpty());

		if (Debug.printConjCells)
			if (sources.get(0).get(0) == true) {
				System.out.println("Conjuntive cells output " + data.length);
				for (int i = 0; i < data.length; i++)
					if (data[i] != false)
						System.out.print(data[i] + " ");
				System.out.println();
			}

	}

	@Override
	public void set(int i, boolean x) {
		throw new RuntimeException("Cannot set the value of a cartesian port");
	}

	@Override
	public void clear() {
		for (Bool1dPort source : sources)
			source.clear();
	}

}
