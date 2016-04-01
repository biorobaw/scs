package edu.usf.micronsl.test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import edu.usf.micronsl.exec.DependencyRunnable;
import edu.usf.micronsl.exec.ThreadDependencyExecutor;

public class ThreadDependencyExecutorTest {

	public static void main(String[] args) {
		ThreadDependencyExecutor executor = new ThreadDependencyExecutor(10, 10, 0, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(3));

		DependencyRunnable dr1 = new DummyModule("dr1");
		DependencyRunnable dr2 = new DummyModule("dr2");
		DependencyRunnable dr3 = new DummyModule("dr3");

		dr2.addPreReq(dr1);
		dr3.addPreReq(dr1);

		Collection<DependencyRunnable> tasks = new LinkedList<DependencyRunnable>();
		tasks.add(dr1);
		tasks.add(dr2);
		tasks.add(dr3);

		System.out.println("Sending tasks dr1, dr2 and dr3");
		executor.execute(tasks);

		try {
			executor.awaitTermination(1000, TimeUnit.DAYS);
			System.out.println("Executor terminated");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Shutting down executor");
		executor.shutdown();

	}

}
