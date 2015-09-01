package edu.usf.ratsim.micronsl;

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

import edu.usf.experiment.utils.Debug;

public class ThreadDependencyExecutor extends ThreadPoolExecutor {

	private Map<DependencyRunnable, Set<DependencyRunnable>> byDependOn;
	private Map<Integer, Set<DependencyRunnable>> byNumDependencies;
	private Map<DependencyRunnable, Integer> numDependencies;
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

	public synchronized void execute(Collection<DependencyRunnable> tasks) {
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
	protected synchronized void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);

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
			notify();
		}

	}

	@Override
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
