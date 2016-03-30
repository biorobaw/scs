package edu.usf.micronsl;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class DependencyRunnable implements Runnable {

	private List<DependencyRunnable> preReqs;

	public DependencyRunnable() {
		preReqs = new LinkedList<DependencyRunnable>();
	}

	public List<DependencyRunnable> getPreReqs() {
		return preReqs;
	}

	public void addPreReq(DependencyRunnable dr1) {
		if (!preReqs.contains(dr1))
			preReqs.add(dr1);
	}

	/**
	 * Set up the run order for the modules by performing a dfs search over the
	 * deps graph
	 * 
	 * @return an order could be reached
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
