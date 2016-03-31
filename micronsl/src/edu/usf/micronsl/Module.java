package edu.usf.micronsl;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.usf.micronsl.port.Port;

public abstract class Module extends DependencyRunnable {

	private Map<String, Port> outPorts;
	private String name;
	private Map<String, Port> inPorts;
	private List<Port> inPortsList;

	public Module(String name) {
		super();

		outPorts = new LinkedHashMap<String, Port>();
		inPorts = new LinkedHashMap<String, Port>();
		inPortsList = new LinkedList<Port>();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addInPort(String name, Port port) {
		addInPort(name, port, false);
	}

	public void addInPort(String name, Port port, boolean reverseDependency) {
		if (!reverseDependency) {
			// If it is an in port, the module producing it is a prereq
			if (port.getOwner() != null)
				addPreReq(port.getOwner());
		} else {
			if (port.getOwner() != null)
				for (Module m : port.getOwner().getValueInfluencingModules())
					m.addPreReq(this);
		}

		inPorts.put(name, port);
		inPortsList.add(port);
	}

	/**
	 * Returns the set of modules that influences this module's value By
	 * default, it's just the module itself. However, some modules rely on
	 * on-the-fly ports, so their value dinamically changes when one of their
	 * dependencies changes.
	 * 
	 * @return
	 */
	public Set<Module> getValueInfluencingModules() {
		Set<Module> res = new LinkedHashSet<Module>();
		res.add(this);
		return res;
	}

	private void addPreReqs(List<Module> modules) {
		for (Module m : modules)
			addPreReq(m);
	}

	public void addInPorts(List<Port> ports) {
		for (Port port : ports)
			addInPort("Nameless Port #" + inPorts.size(), port);
	}

	public void addOutPort(String name, Port port) {
		outPorts.put(name, port);
	}

	public Port getOutPort(String name) {
		if (!outPorts.containsKey(name))
			throw new RuntimeException("There is no out-port named " + name);
		return outPorts.get(name);
	}

	public Port getInPort(String name) {
		if (!inPorts.containsKey(name))
			throw new RuntimeException("There is no in-port named " + name);
		return inPorts.get(name);
	}

	public abstract boolean usesRandom();

	public List<Port> getInPorts() {
		return inPortsList;
	}
}
