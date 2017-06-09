package edu.usf.micronsl;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import edu.usf.micronsl.exec.Debug;
import edu.usf.micronsl.exec.DependencyRunnable;
import edu.usf.micronsl.exec.ThreadDependencyExecutor;
import edu.usf.micronsl.module.Module;

public class Model {

	private Map<String, Module> modules;
	private ThreadDependencyExecutor pool;
	private boolean modulesChanged;
	private List<Module> moduleList;
	private List<Module> runOrder;
	
	
	
	public InitialModule initialModule = new InitialModule();
	public FinalModule finalModule = new FinalModule();

	public Model() {
		modules = new LinkedHashMap<String, Module>();
		pool = new ThreadDependencyExecutor(10, 10, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(20));
		modulesChanged = false;
		moduleList = new LinkedList<Module>();
		
		//add initial and final modules
		Module m = initialModule;
		modules.put(m.getName(), m);
		moduleList.add(m);
		modulesChanged = true;
		
		m=finalModule;
		modules.put(m.getName(), m);
		moduleList.add(m);
		modulesChanged = true;
		
		finalModule.addPreReq(initialModule);
		
		
		
	}

	public void addModule(Module m) {
		if (modules.containsKey(m.getName()))
			throw new RuntimeException("Module " + m.getName() + " already exists");
		modules.put(m.getName(), m);
		moduleList.add(m);
		modulesChanged = true;
		
		//add dependencies with initial and final modules:
		m.addPreReq(initialModule);
		finalModule.addPreReq(m);
	}

	public Module getModule(String name) {
		return modules.get(name);
	}

	public void simRun() {
		if (modulesChanged) {
			if (DependencyRunnable.hasCycles((List<DependencyRunnable>) (List<?>) moduleList))
				throw new RuntimeException("There are cycles in the modules requirements");

			runOrder = addRandomUseDeps(moduleList);

			if (Debug.printSchedulling) {
				System.out.println("Printing run order");
				for (Module m : runOrder)
					System.out.println(m.getName());
			}
			// Module last = null;
			// for( Module m : runOrder){
			// if (last != null){
			// m.addPreReq(last);
			// }
			// last = m;
			// }

			if (DependencyRunnable.hasCycles((Collection<DependencyRunnable>) (Collection<?>) modules.values()))
				throw new RuntimeException("There are cycles in the modules requirements");
			modulesChanged = false;
		}

		// for (Module m : runOrder)
		// m.run();

		// System.out.println("Sending modules to execute");
		pool.execute((List<DependencyRunnable>) (List<?>) moduleList);
		// System.out.println("Sent modules to execute");
		try {
			pool.awaitTermination(1000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println("************ Finished executing");
	}

	/**
	 * Add dependencies based on the use of random
	 * 
	 * @return an order could be reached
	 */
	public static List<Module> addRandomUseDeps(Collection<Module> modules) {
		Set<Module> visited = new HashSet<Module>();
		Set<Module> processed = new HashSet<Module>();
		List<Module> order = new LinkedList<Module>();

		for (Module m : modules)
			addRandomUseDeps(m, visited, processed, order);

		// Add dependencies to serialize the modules using random
		Module lastUsingRandom = null;
		for (Module m : order) {
			if (m.usesRandom()) {
				if (lastUsingRandom != null) {
					m.addPreReq(lastUsingRandom);
					if (Debug.printExecutionOrder)
						System.out
								.println("Adding random dep from " + lastUsingRandom.getName() + " to " + m.getName());
				}
				lastUsingRandom = m;
			}
		}

		return order;
	}

	private static boolean addRandomUseDeps(Module m, Set<Module> visited, Set<Module> processed, List<Module> order) {
		if (processed.contains(m))
			return false;
		if (visited.contains(m)) {
			return true;
		}

		visited.add(m);

		boolean cycles = false;
		for (DependencyRunnable pr : m.getPreReqs())
			cycles = cycles || addRandomUseDeps((Module) pr, visited, processed, order);

		processed.add(m);
		order.add(m);

		return cycles;
	}
	
	public void initialTask(){};
	public void finalTask(){};
	
	private class InitialModule extends Module{
		
		public InitialModule() {
			super("InitialTask");
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			initialTask();
			
		}

		@Override
		public boolean usesRandom() {
			return true;
		}
		
	}
	
	private class FinalModule extends Module{
		
		public FinalModule() {
			super("FinalTask");
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			finalTask();
			
		}

		@Override
		public boolean usesRandom() {
			return true;
		}
		
	}
	
	public void newEpisode(){
		for (Module m : moduleList)
			m.newEpisode();
	}
	
}
