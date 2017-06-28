package edu.usf.micronsl.spiking.module;

import edu.usf.micronsl.NSLSimulation;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.spiking.SpikeEventMgr;

/**
 * This class drives the SpikeEventMgr as a NSL module.
 * @author martin
 *
 */
public class SpikeEventMgrModule extends Module {

	private SpikeEventMgr sEM;

	public SpikeEventMgrModule(String name) {
		super(name);
		
		sEM = SpikeEventMgr.getInstance();
	}

	@Override
	public void run() {
		long time = NSLSimulation.getInstance().getSimTime();
		sEM.process(time);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
