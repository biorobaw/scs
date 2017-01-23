package edu.usf.ratsim.nsl.modules.input.Vision;

import java.util.List;

import edu.usf.experiment.robot.componentInterfaces.FeederVisibilityInterface;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Feeder;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.List.Int1dPortList;

/**
 * Provides an output port with the identifier of the closes feeder
 * @author Martin Llofriu
 *
 */
public class VisibleFeedersModule extends Module {

	private Subject sub;
	
	private Int1dPortList outPort = new Int1dPortList(this);

	public VisibleFeedersModule(String name, Subject sub) {
		super(name);
		
		this.sub = sub;
		addOutPort("visibleFeeders", outPort);
		
	}

	@Override
	public void run() {
		
		FeederVisibilityInterface fvi = (FeederVisibilityInterface)sub.getRobot(); 
		
		outPort.clear();		
		List<Feeder> feeders = fvi.getVisibleFeeders(new int[] {});
		for(Feeder f : feeders) outPort.add(f.getId());
		
		//System.out.println("Done visible feeders");
		

	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
