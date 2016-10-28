package edu.usf.micronsl.module;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.usf.micronsl.exec.DependencyRunnable;
import edu.usf.micronsl.port.Port;

/**
 * This class represents an executable module that takes its input from a set of
 * ports and outputs values also through ports. The module execution
 * dependencies are automatically computed from the input port's owners, unless
 * specified otherwise.
 * 
 * @author Martin Llofriu
 * 
 */
public abstract class Module extends DependencyRunnable {

	/**
	 * Output ports of the module
	 */
	private Map<String, Port> outPorts;
	/**
	 * Input ports of the module
	 */
	private Map<String, Port> inPorts;
	/**
	 * A list of the input ports
	 */
	private List<Port> inPortsList;
	/**
	 * Module's name
	 */
	private String name;

	/**
	 * Create the module
	 * 
	 * @param name
	 *            A systemwide unique name
	 */
	public Module(String name) {
		super();

		// Linked has maps allow to keep the order of insertion when retrieving
		// values as a collection
		outPorts = new LinkedHashMap<String, Port>();
		inPorts = new LinkedHashMap<String, Port>();
		inPortsList = new LinkedList<Port>();
		this.name = name;
	}

	/**
	 * 
	 * @return The modules name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Add an input port. Adding the port will add a dependency from the port's
	 * owner to this module.
	 * 
	 * @param name
	 *            A name for the module, local to the module
	 * @param port
	 *            A port object
	 */
	public void addInPort(String name, Port port) {
		addInPort(name, port, false);
	}

	/**
	 * Add an input port. Adding the port will add a dependency from the port's
	 * owner to this module or from this module to the port's owner, depending
	 * on the reverseDependency parameter.
	 * 
	 * @param name
	 *            The port's name, local to the module.
	 * @param port
	 *            The port object
	 * @param reverseDependency
	 *            If true, the dependency generated will be reverse, including a
	 *            dependency from this module to the owner of the port.
	 */
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
	 * Returns the set of modules that influences this module's output port's
	 * values, after the run method has finished. By default, the module's ports
	 * will keep a copy of their value, so any change in the input ports will
	 * not affect the output port's values after the run() method has finished.
	 * Thus, by default, the only influencing module is just the module itself.
	 * However, some modules rely on on-the-fly ports (e.g. sum/concat/cartesian
	 * ports), so their value dynamically changes when one of their dependencies
	 * changes.
	 * 
	 * @return The set of modules that influence the value of this modules'
	 *         output ports.
	 */
	public Set<Module> getValueInfluencingModules() {
		Set<Module> res = new LinkedHashSet<Module>();
		res.add(this);
		return res;
	}

	/**
	 * Add a collection of ports. This will call addInPort for each one.
	 * 
	 * @param ports
	 *            A list of port objects
	 */
	public void addInPorts(List<Port> ports) {
		for (Port port : ports)
			addInPort("Nameless Port #" + inPorts.size(), port);
	}

	/**
	 * Add an output port.
	 * 
	 * @param name
	 *            Name of the port, local to the module.
	 * @param port
	 *            The port object.
	 */
	public void addOutPort(String name, Port port) {
		outPorts.put(name, port);
	}

	/**
	 * Returns the output port with a given name
	 * 
	 * @param name
	 *            The required port's name
	 * @return The port object associated to the given name.
	 */
	public Port getOutPort(String name) {
		if (!outPorts.containsKey(name))
			throw new RuntimeException("There is no out-port named " + name);
		return outPorts.get(name);
	}

	/**
	 * Returns the input port with a given name
	 * 
	 * @param name
	 *            The required port's name
	 * @return The port object associated to the given name.
	 */
	public Port getInPort(String name) {
		if (!inPorts.containsKey(name))
			throw new RuntimeException("There is no in-port named " + name);
		return inPorts.get(name);
	}

	/**
	 * In order to keep deterministic execution when the random number
	 * generation seed is fixed, the execution order of independent modules that
	 * use the random number generator should be kept fixed. In order to do
	 * this, each module needs to report whether it uses random or not.
	 * 
	 * @return
	 */
	public abstract boolean usesRandom();

	/**
	 * Returns a list of the module's inports.
	 * @return A list of the module's inports.
	 */
	public List<Port> getInPorts() {
		return inPortsList;
	}

}
