package platform.simulatorVirtual.components;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.componentInterfaces.LocalizationInterface;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;

public class GlobalLocalization implements LocalizationInterface {

	VirtUniverse universe = VirtUniverse.getInstance();
	@Override
	public Point3f getPosition() {
		

		return universe.getRobotPosition();
	}

	@Override
	public float getHD() {
		return universe.getRobotOrientationAngle();
	}

}
