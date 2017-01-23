package edu.usf.ratsim.nsl.modules.input.HighLevelCognition;

import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.universe.Feeder;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.ratsim.robot.virtual.VirtualRobot;

/**
 * Provides an output port with the identifier of the feeder currently being visited (-1 otherwise)
 * @author Pablo Scleidorovich
 *
 */
public class CurrentFeederModule extends Module {

	private SubjectOld sub;
	private Int0dPort outPort;

	public CurrentFeederModule(String name, SubjectOld sub) {
		super(name);
		
		this.sub = sub;
		
		outPort = new Int0dPort(this);
		addOutPort("currentFeeder", outPort);
	}

	@Override
	public void run() {
		//System.out.println("Done current feeders");
		Feeder closest = sub.getRobot().getClosestFeeder();
		//System.out.println("id closest" + closest.getId());
		if (closest == null || !((VirtualRobot)sub.getRobot()).withinEatingDistanceFromFeeder(closest.getId()))
			outPort.set(-1);
		else
			outPort.set(closest.getId());
		//System.out.println("Done current feeders");
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
