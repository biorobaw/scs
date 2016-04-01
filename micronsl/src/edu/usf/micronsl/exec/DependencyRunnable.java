package edu.usf.micronsl.exec;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.usf.micronsl.module.Module;

/**
 * A runnable object that has a set of dependencies, which should run before it runs.
 * @author Martin Llofriu
 *
 */
public abstract class DependencyRunnable implements Runnable {

	/**
	 * The list of objects that should run before this one
	 */
	private List<DependencyRunnable> preReqs;

	/**
	 * Create the runnable with an empty list of dependencies.
	 */
	public DependencyRunnable() {
		preReqs = new LinkedList<DependencyRunnable>();
	}

	/**
	 * Returns the list of objects that should run before this one
	 * @return The list of objects that should run before this one
	 */
	public List<DependencyRunnable> getPreReqs() {
		return preReqs;
	}

	/**
	 * Add a given runnable as a dependency to this one.
	 * @param dr1 The runnable this module depends on
	 */
	public void addPreReq(DependencyRunnable dr1) {
		if (!preReqs.contains(dr1))
			preReqs.add(dr1);
	}

	/**
	 * Check the dependency graph for cycles by running a dfs over the dependency graph.
	 * 
	 * @return Whether an order could be reached
	 */
	public static boolean hasCycles(Collection<DependencyRunnable> modules) {
		Set<DependencyRunnable> visited = new HashSet<DependencyRunnable>();
		Set<DependencyRunnable> processed = new HashSet<DependencyRunnable>();

		boolean cycles = false;
		for (DependencyRunnable m : modules) {
			cycles = cycles || hasCycles(m, visited, processed);
			if (cycles)
				break;
		}

		if (cycles)
			System.err.println("Could not find a suitable run order");

		return cycles;
	}

	/**
	 * Helper method to check for cycles recursively, implements the DFS search.
	 * @param dr The current runnable being analyzed
	 * @param visited The list of visited nodes
	 * @param processed The list of processed nodes
	 * @return Whether an order could be reached 
	 */
	private static boolean hasCycles(DependencyRunnable dr, Set<DependencyRunnable> visited,
			Set<DependencyRunnable> processed) {
		if (processed.contains(dr)) {
			return false;
		}

		if (visited.contains(dr)) {
			if (Debug.printSchedulling)
				System.out.println("Module " + ((Module) dr).getName() + " is in a cycle");
			return true;
		}

		visited.add(dr);

		boolean cycles = false;
		for (DependencyRunnable pr : dr.getPreReqs()) {
			cycles = cycles || hasCycles(pr, visited, processed);
			if (cycles)
				break;
		}

		if (Debug.printSchedulling)
			if (cycles)
				System.out.println("Module " + ((Module) dr).getName() + " is in a cycle");
		processed.add(dr);

		return cycles;
	}

}
