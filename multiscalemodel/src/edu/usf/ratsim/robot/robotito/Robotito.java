package edu.usf.ratsim.robot.robotito;

import java.util.List;

import javax.vecmath.Point3f;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.ElementWrapper;

public class Robotito extends LocalizableRobot {

	// TODO Replace with the port where your sender module is connected to.
    private static final String PORT = "/dev/ttyUSB0";
    // TODO Replace with the baud rate of your sender module.  
    private static final int BAUD_RATE = 9600;
    
	public Robotito(ElementWrapper params) {
		super(params);
		
		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		try {
			myDevice.open();
			
			myDevice.addDataListener(new IDataReceiveListener() {
				public void dataReceived(XBeeMessage msg) {
					System.out.println(msg.getDataString());
				}
			});
			
		} catch (XBeeException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public Point3f getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getOrientationAngle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean seesFeeder() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Feeder> getAllFeeders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getDistanceToClosestWall() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getHalfFieldView() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int closeToNoseWalls(float distToConsider) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Feeder getFeederInFront() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eat() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasFoundFood() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startRobot() {
		// TODO Auto-generated method stub

	}

	@Override
	public void forward(float distance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rotate(float degrees) {
		// TODO Auto-generated method stub

	}

	@Override
	public Feeder getFlashingFeeder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean seesFlashingFeeder() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFeederClose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Affordance> checkAffordances(List<Affordance> possibleAffordances) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkAffordance(Affordance af) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void executeAffordance(Affordance selectedAction, Subject sub) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Feeder> getVisibleFeeders(int[] is) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Point3f> getVisibleWallEnds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLastAteFeeder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLastTriedToEatFeeder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasFoundPlatform() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void moveContinous(float lVel, float angVel) {
		// TODO Auto-generated method stub

	}

}
