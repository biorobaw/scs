package edu.usf.micronsl.exec;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.usf.micronsl.module.Module;

/**
 * A thread pool executor that takes into account the runnable's dependencies
 * into account when schedulling.
 * 
 * @author Martin Llofriu
 * 
 */
public class ThreadDependencyExecutor extends ThreadPoolExecutor {

	/**
	 * The map of dependencies
	 */
	private Map<DependencyRunnable, Set<DependencyRunnable>> byDependOn;
	/**
	 * Runnable objects by the number of unmet dependencies
	 */
	private Map<Integer, Set<DependencyRunnable>> byNumDependencies;
	/**
	 * The amount of unmet dependencies by runnable object
	 */
	private Map<DependencyRunnable, Integer> numDependencies;
	/**
	 * The number of tasks to pending to finish executing
	 */
	private int numTasksToExecute;

	public ThreadDependencyExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public ThreadDependencyExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				handler);
	}

	public ThreadDependencyExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
	}

	public ThreadDependencyExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory);
	}

	/**
	 * Setup execution by initializing all data structures and begin execution
	 * of all modules that have no unmet dependencies
	 * 
	 * @param tasks
	 */
	public synchronized void execute(Collection<DependencyRunnable> tasks) {
		// Initialize empty structures
		byDependOn = new HashMap<DependencyRunnable, Set<DependencyRunnable>>();
		byNumDependencies = new HashMap<Integer, Set<DependencyRunnable>>();
		numDependencies = new HashMap<DependencyRunnable, Integer>();
		// Iterate over all to submit tasks
		for (DependencyRunnable dr : tasks) {
			// Add each task to the dependency bins
			for (DependencyRunnable dep : dr.getPreReqs()) {
				if (!byDependOn.containsKey(dep))
					byDependOn
							.put(dep, new LinkedHashSet<DependencyRunnable>());
				byDependOn.get(dep).add(dr);
			}
			// Add each task to the bins by how many it depends on
			int numDeps = dr.getPreReqs().size();
			if (!byNumDependencies.containsKey(numDeps))
				byNumDependencies.put(numDeps,
						new LinkedHashSet<DependencyRunnable>());
			byNumDependencies.get(numDeps).add(dr);
			// Add it to a map to its dependencies
			numDependencies.put(dr, numDeps);
		}

		numTasksToExecute = tasks.size();

		// Submit tasks with no current dependencies
		for (DependencyRunnable dr : byNumDependencies.get(0)) {
			if (Debug.printSchedulling)
				System.out.println("Executing " + ((Module) dr).getName());
			execute(dr);
		}

	}

	@Override
	/**
	 * This method is called after a runnable finishes execution. 
	 * The number of unmet dependencies of all runnables that depend on the finished
	 * one are decreased. Then, all runnables with 0 unmet dependencies are queued to execute. 
	 */
	protected synchronized void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		
		if(t != null)
			{
				t.printStackTrace();
				//new java.util.Scanner(System.in).nextLine();
				System.exit(-1);
			}

		DependencyRunnable completed = (DependencyRunnable) r;

		if (Debug.printSchedulling)
			System.out.println("Completed a task: "
					+ ((Module) completed).getName());

		if (byDependOn.containsKey(completed))
			for (DependencyRunnable antiDep : byDependOn.get(completed)) {
				int numDepsPrev = numDependencies.get(antiDep);
				int numDeps = numDepsPrev - 1;
				// if (DEBUG)
				// System.out.println("Task " + ((Module) antiDep).getName()
				// + " has " + numDeps
				// + " uncompleted dependencies now");

				numDependencies.put(antiDep, numDeps);
				byNumDependencies.get(numDepsPrev).remove(antiDep);
				if (!byNumDependencies.containsKey(numDeps))
					byNumDependencies.put(numDeps,
							new LinkedHashSet<DependencyRunnable>());
				byNumDependencies.get(numDeps).add(antiDep);

				if (numDeps == 0) {
					if (Debug.printSchedulling)
						System.out.println("Executing "
								+ ((Module) antiDep).getName());
					execute(antiDep);
				}
			}

		synchronized (this) {
			numTasksToExecute--;
			if (Debug.printSchedulling) {
				System.out.println("To Execute: " + numTasksToExecute);
				for (Runnable task : getQueue())
					System.out.println("Waiting for: "
							+ ((Module) task).getName());
			}

			notify();
		}

	}

	@Override
	/**
	 * Wait will wait until all runnables are executing,
	 * including those that have yet to be executed due to unmet dependencies.
	 */
	public synchronized boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		while (numTasksToExecute != 0) {
			wait(timeout);
			// if (DEBUG)
			// System.out.println("woke up: " + numTasksToExecute);
		}

		return numTasksToExecute == 0;
	}

}
