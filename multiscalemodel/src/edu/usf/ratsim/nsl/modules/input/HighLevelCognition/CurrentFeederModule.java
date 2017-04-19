package edu.usf.ratsim.nsl.modules.input.HighLevelCognition;

import edu.usf.experiment.robot.componentInterfaces.FeederVisibilityInterface;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Feeder;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;

/**
 * Provides an output port with the identifier of the feeder currently being visited (-1 otherwise)
 * @author Pablo Scleidorovich
 *
 */
public class CurrentFeederModule extends Module {

	private Subject sub;
	private Int0dPort outPort;

	public CurrentFeederModule(String name, Subject sub) {
		super(name);
		
		this.sub = sub;
		
		outPort = new Int0dPort(this);
		addOutPort("currentFeeder", outPort);
	}

	@Override
	public void run() {
		//System.out.println("Done current feeders");
		
		//check if tried to eat (otherwise don't count as if visited)
		
		if(sub.hasTriedToEat()){
		
			FeederVisibilityInterface fvi = (FeederVisibilityInterface)sub.getRobot();
			Feeder closest = fvi.getClosestFeeder();
			//System.out.println("id closest" + closest.getId());
			if (closest == null || !VirtUniverse.getInstance().isRobotCloseToFeeder(closest.getId()))
				outPort.set(-1);
			else
				outPort.set(closest.getId());
		} else outPort.set(-1);
		//System.out.println("Done current feeders");
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
