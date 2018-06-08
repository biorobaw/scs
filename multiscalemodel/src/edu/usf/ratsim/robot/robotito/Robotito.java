package edu.usf.ratsim.robot.robotito;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress16;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.wpan.TxRequest16;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.robot.DifferentialRobot;
import edu.usf.experiment.robot.HolonomicRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.robot.affordance.ForwardAffordance;
import edu.usf.experiment.robot.affordance.TurnAffordance;
import edu.usf.experiment.robot.affordance.LocalActionAffordanceRobot;
import edu.usf.experiment.robot.WallRobot;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUniverseUtilities;
import edu.usf.experiment.universe.feeder.FeederUtils;
import edu.usf.experiment.robot.PlatformRobot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.experiment.utils.RigidTransformation;
import edu.usf.vlwsim.universe.VirtUniverse;
import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.universe.platform.Platform;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.utils.RigidTransformation;


public class Robotito implements DifferentialRobot, HolonomicRobot, SonarRobot, LocalizableRobot, PlatformRobot, LocalActionAffordanceRobot, WallRobot, Runnable  {

    private static final String PORT = "/dev/ttyUSB0";
    private static final int BAUD_RATE = 57600;
    
    // Shared constants with the arduino code
    // 100 is the max absolute value for the command
    private static final int MAX_XVEL = 100;
    private static final int MAX_TVEL = 100;
    // 100 means 5 m/s linear vel
    private static final float XVEL_CONV = MAX_XVEL / 1;
    // 100 means 4 turns/s angular vel
    private static final float TVEL_CONV = (float) (MAX_TVEL / (Math.PI * 2));
    // 128 means zero in the final byte (b = 128 + cmd)
    private static final int ZERO_VEL = 128;
    
	private static final long CONTROL_PERIOD = 50;
	private static final int NUM_SONARS = 12;
	private static final float LINEAR_INERTIA = 0;
	private static final float ANGULAR_INERTIA = 0;
	private static final float ROBOT_RADIUS = 0.075f;
	private static final boolean WAIT_FOR_LOCATION = false;
	private static final double FOUND_PLAT_THRS = .1f;
	private static final float MIN_DISTANCE_TO_WALLS = 0.15f;

	private XBee xbee;
	private XBeeAddress16 remoteAddress;
    
	
	private float xVel;
	private float yVel;
	private float tVel;

	private ROSPoseDetector poseDetector;
	private ROSWallDetector wallDetector;
	private int kP = 40;
	private int kI = 10;
	private int kD = 0;
	private SonarReceiver sonarReceiver;

	//affordance hack
	private float step;
	private float leftAngle;
	private float rightAngle;
	private float lookaheadSteps;
	private float halfFieldOfView;
	private float visionDist;
	private float closeThrs;
	private ROSUniverse universe;
	
	public Robotito(ElementWrapper params, Universe u) {
		
		//affordance hack
		step = params.getChildFloat("step");
		leftAngle = params.getChildFloat("leftAngle");
		rightAngle = params.getChildFloat("rightAngle");
		lookaheadSteps = params.getChildFloat("lookaheadSteps");
		halfFieldOfView = params.getChildFloat("halfFieldOfView");
		visionDist = params.getChildFloat("visionDist");
		closeThrs = params.getChildFloat("closeThrs");
		universe = (ROSUniverse) u;
		
		xbee = new XBee();
		try {
			xbee.open(PORT, BAUD_RATE);
		} catch (XBeeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		remoteAddress = new XBeeAddress16(0x22, 0x22);
		
		xVel = 0;
		yVel = 0;
		tVel = 0;
		
		poseDetector = ROSPoseDetector.getInstance();
		wallDetector = ROSWallDetector.getInstance();
		
		if (WAIT_FOR_LOCATION){
			System.out.println("[+] Waiting for updated localization information");
			long now = System.currentTimeMillis();
			while (poseDetector.lastPoseReceived < now)
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			System.out.println("[+] Got new info");
		}
		
		sonarReceiver = new SonarReceiver(xbee, NUM_SONARS);
		sonarReceiver.setPriority(Thread.MAX_PRIORITY);
		sonarReceiver.start();
		
		sendKs();
		engageMotors();
//		releaseMotors();
		
		// Start a thread to send vel packets
		Thread runner = new Thread(this);
		runner.setPriority(Thread.MAX_PRIORITY);
		runner.start();
		Logger xbeeLogger = Logger.getLogger("com.digi.xbee.api.connection.DataReader");
		xbeeLogger.setLevel(Level.OFF);
	}

	private void sendKs() {
		int[] dataToSend = {(byte)'k', 
				(byte)(kP + 128), (byte)(kI + 128), (byte)(kD + 128),
				(byte)(kP + 128), (byte)(kI + 128), (byte)(kD + 128),
				(byte)(kP + 128), (byte)(kI + 128), (byte)(kD + 128),
				(byte)(kP + 128), (byte)(kI + 128), (byte)(kD + 128)};
		
		try {
			TxRequest16 rq = new TxRequest16(remoteAddress, dataToSend);
			// Disable ACKs
			rq.setFrameId(0);
			xbee.sendRequest(rq);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
	}

	@Override
	public void startRobot() {
	}

	@Override
	public void setLinearVel(float linearVel) {
		xVel = linearVel;
	}

	@Override
	public void setAngularVel(float angularVel) {
		tVel = angularVel;
	}

	@Override
	public void moveContinous(float lVel, float angVel) {
		xVel = lVel;
		tVel = angVel;
	}

	@Override
	public void run() {
		while (true){
//			System.out.println(xVel + " " + tVel);
			short xVelShort = (short) (xVel * XVEL_CONV + LINEAR_INERTIA * Math.signum(xVel) + ZERO_VEL);
			xVelShort = (short) Math.max(0, Math.min(xVelShort, 255));
			short yVelShort = (short) (yVel * XVEL_CONV + LINEAR_INERTIA * Math.signum(yVel) + ZERO_VEL);
			yVelShort = (short) Math.max(0, Math.min(yVelShort, 255));
			short tVelShort = (short) (tVel * TVEL_CONV + ANGULAR_INERTIA * Math.signum(tVel) + ZERO_VEL);
			tVelShort = (short) Math.max(0, Math.min(tVelShort, 255));
			int[] dataToSend = {(byte)'v', (byte) Math.abs(xVelShort), (byte) Math.abs(yVelShort), (byte) Math.abs(tVelShort)};
			
			System.out.println("Sending vels: " + xVelShort + " " + yVelShort + " " + tVelShort);
			try {
				TxRequest16 rq = new TxRequest16(remoteAddress, dataToSend);
				// Disable ACKs
				rq.setFrameId(0);
				xbee.sendAsynchronous(rq);
			} catch (XBeeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			System.out.println("Vels sent");
			try {
				Thread.sleep(CONTROL_PERIOD);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
				
	}
	
	@Override
	public float[] getSonarReadings() {
		return sonarReceiver.sonarReading;
	}

	@Override
	public float[] getSonarAngles() {
		return sonarReceiver.sonarAngles;
	}
	
	@Override
	public float getSonarMaxReading() {
		return SonarReceiver.MAX_READ;
	}

	@Override
	public float getSonarAperture() {
		return 0;
	}

	@Override
	public Coordinate getPosition() {
		return poseDetector.getPosition();
	}

	@Override
	public float getOrientationAngle() {
		return poseDetector.getAngle();
	}

	@Override
	public boolean hasFoundPlatform() {
		System.out.println(1);
		String posStr = PropertyHolder.getInstance().getProperty("platformPosition");
		//the above line sets posStr to be null - presumably the position isn't
		//getting published
		String[] fields = posStr.split(",");
		float x = Float.parseFloat(fields[0]);
		float y = Float.parseFloat(fields[1]);
		System.out.println(3);
		return new Coordinate(x,y).distance(getPosition()) < FOUND_PLAT_THRS;
	}

	

	private void releaseMotors() {
		int[] dataToSend = {(byte)'r'};		
		try {
			TxRequest16 rq = new TxRequest16(remoteAddress, dataToSend);
			// Disable ACKs
			rq.setFrameId(0);
			xbee.sendRequest(rq);
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}

	private void engageMotors() {
		int[] dataToSend = {(byte)'e'};		
		try {
			TxRequest16 rq = new TxRequest16(remoteAddress, dataToSend);
			// Disable ACKs
			rq.setFrameId(0);
			xbee.sendRequest(rq);
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
	private void calibrateSonars() {
		List<Float> volts = new LinkedList<Float>();
		List<Float> dists = new LinkedList<Float>();
		for (float dist = 5f; dist <= 40; dist += 5f){
			System.out.println("Place the robot at dist " + dist + " and press enter");
			try {
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			float raw = sonarReceiver.rawReadings[0];
			volts.add(raw);
			dists.add(dist);
		}
		
		sonarReceiver.calibrate(volts, dists);
	}

	@Override
	public float getRadius() {
		return ROBOT_RADIUS;
	}
	
	public static void main(String[] args){
		Robotito r = new Robotito(null, null);
//		r.releaseMotors();

//		r.calibrateSonars();
		
		r.setLinearVel(.0f);
		r.yVel = -3f;
		r.setAngularVel((float) (0));
		try {
			Thread.sleep(500000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		r.setLinearVel(0);
//		r.setAngularVel(0f);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		System.exit(0);
		
	}

	@Override
	public void setVels(float x, float y, float t) {
		xVel = x;
		yVel = y;
		tVel = t;
	}
	
	
	
	
	
	
	//Affordance hack
	//Below methods were added by David Ehrenhaft
	//Note that these are not yet tested and almost certainly have errors to work
	//out. 
	
	@Override
	public void executeAffordance(Affordance af) {
		// WORKAROUND Dont execute the first cycle
		// TODO: fix this with better model-universe interaction
		if (PropertyHolder.getInstance().getProperty("cycle").equals("0"))
			return;

		if (af instanceof TurnAffordance) {
			TurnAffordance ta = (TurnAffordance) af;
			List<Affordance> forward = new LinkedList<Affordance>();
			forward.add(new ForwardAffordance(ta.getDistance()));
			rotate(ta.getAngle());
		} else if (af instanceof ForwardAffordance) {
			forward(((ForwardAffordance) af).getDistance());
		} else if (af instanceof EatAffordance) {
			// Updates food in universe
				eat(); // TODO: should the robot check for feeder close or just
						// execute action
			// rotate((float) Math.PI);
		} else
			throw new RuntimeException("Affordance " + af.getClass().getName() + " not supported by robot");
	}
	
	@Override
	public List<Affordance> checkAffordances(List<Affordance> affs) {
		for (Affordance af : affs) {
			af.setRealizable(checkAffordance(af));
		}

		return affs;
	}
	
	@Override
	public float checkAffordance(Affordance af) {
		boolean realizable;
		if (af instanceof TurnAffordance) {
			TurnAffordance ta = (TurnAffordance) af;
			// Either it can move there, or it cannot move forward and the
			// other angle is not an option
			realizable =  !canRobotMove(0, getRadius() * lookaheadSteps) 
					      || canRobotMove(ta.getAngle(), getRadius() * lookaheadSteps);
		} else if (af instanceof ForwardAffordance)
			realizable =  canRobotMove(0, getRadius() * lookaheadSteps);
		else if (af instanceof EatAffordance) {
			if (getClosestPlatform(getVisiblePlatforms()) != null)
				realizable = getClosestPlatform(getVisiblePlatforms()).getPosition()
						.distance(new Coordinate()) < closeThrs;
			// TODO: this is not good for MultiFeeders, where the robot
			// needs to eat on empty feeders to be disapointed- Fix
			else
				realizable = false;
		} else
			throw new RuntimeException("Affordance " + af.getClass().getName() + " not supported by robot");
	
		return realizable ? 1 : 0;
	}
	
	@Override
	public List<Affordance> getPossibleAffordances() {
		List<Affordance> res = new LinkedList<Affordance>();

		res.add(getLeftAffordance());
		res.add(getForwardAffordance());
		res.add(getRightAffordance());
		res.add(new EatAffordance());

		return res;
	}
	
	@Override
	public float getMinAngle() {
		return Math.min(leftAngle, rightAngle);
	}

	@Override
	public float getStepLength() {
		return step;
	}

	@Override
	public Affordance getForwardAffordance() {
		return new ForwardAffordance(step);
	}

	@Override
	public Affordance getLeftAffordance() {
		return new TurnAffordance(leftAngle, step);
	}

	@Override
	public Affordance getRightAffordance() {
		return new TurnAffordance(rightAngle, step);
	}

	@Override
	public List<Coordinate> getVisibleWallEnds() {
		LinkedList<Wall> walls = new LinkedList<Wall>(wallDetector.getWalls());
		LinkedList<Coordinate> wallEnds = new LinkedList<Coordinate>();
		
		//Converts each wall to a pair of coordinates
		for(Wall w: walls) {
			wallEnds.add(new Coordinate(w.getX1(), w.getY1()));
			wallEnds.add(new Coordinate(w.getX2(), w.getY2()));
		}
		return wallEnds;
	}

	@Override
	public float getDistanceToClosestWall() {
		float minDist = Float.MAX_VALUE;
		Coordinate curPos = getPosition();
		
		LinkedList<Wall> walls = new LinkedList<Wall>(wallDetector.getWalls());
		for(Wall w: walls) {
			if(w.distanceTo(curPos) < minDist) {
				minDist = w.distanceTo(curPos);
			}
		}
		
		return minDist;
	}

	@Override
	public float getHalfFieldView() { 
		return halfFieldOfView;
	}
	
	//TODO: confirm code is meaningful
	private boolean canRobotMove(float angle, float distance) {
		//chunck below probably useless
		RigidTransformation move = new RigidTransformation(step, 0f, angle);
		RigidTransformation to = new RigidTransformation((float)getPosition().x, (float)getPosition().y, getOrientationAngle());
		to.composeBefore(move);
		// Check if crosses any wall
		boolean intesectsWall = false;
		LineSegment path = new LineSegment(getPosition(), 
				                           new Coordinate(getPosition().x + distance * Math.sin(angle), 
				        		   		                  getPosition().y + distance * Math.cos(angle)));
		for (Wall wall : wallDetector.getWalls()) {
			intesectsWall = intesectsWall || (path.distance(wall.s) < MIN_DISTANCE_TO_WALLS);
		}

		return !intesectsWall;
	}
	
	
	//Even more hacky than the above
	public List<Platform> getVisiblePlatforms() {
		List<Platform> res = new LinkedList<Platform>();
		for (Platform p : universe.getPlatforms()) {
			if (canRobotSeePlatform(p)) {
				// Get relative position
				Coordinate relFPos = GeomUtils.relativeCoords(p.getPosition(),
						getPosition(), getOrientationAngle());
				// Return the landmark
				Platform relPlatform = new Platform(p);
				relPlatform.setPosition(relFPos);
				res.add(relPlatform);
			}
		}
		return res;
	}
	
	public boolean canRobotSeePlatform(Platform p) {
		float angleToFeeder = angleToPlatform(p);
		boolean inField = angleToFeeder <= halfFieldOfView;

		boolean intersects = false;
		Coordinate rPos = getPosition();
		Coordinate pPos = p.getPosition();
		LineSegment lineOfSight = new LineSegment(rPos, pPos);
		for (Wall w : wallDetector.getWalls())
			intersects = intersects || w.intersects(lineOfSight);

		boolean closeEnough = p.getPosition().distance(new Coordinate(getPosition())) < visionDist;

		return inField && !intersects && closeEnough;
	}
	
	
	private float angleToPlatform(Platform p) {
			return Math.abs(GeomUtils.relativeAngleToPoint(getPosition(), getOrientationAngle(),
					p.getPosition()));
	}
	
	/** Returns closest platform from a list of platforms, or returns null if 
	 *  the list is empty.
	 *  
	 *  @param List<Platform> platforms
	 */
	private Platform getClosestPlatform(List<Platform> platforms) {
		Platform closest = null;
		if(!platforms.isEmpty()) {
			double minDist = platforms.get(0).getPosition().distance(getPosition());
			for(Platform p : platforms) {
				if(p.getPosition().distance(getPosition()) < minDist) {
					closest = p;
					minDist = p.getPosition().distance(getPosition());
				}
			}
		}
		return closest;
	}
	
	//TODO: possible that this may go in a random direction.
	private void forward(float distance) {
		Coordinate start = getPosition();
		while(start.distance(getPosition()) < distance) {
			setLinearVel(.4f); //TODO: temporarily using random speed
		}
		setLinearVel(.0f);
		yVel = -3f;
	}
	
	private void rotate(float angle) {
		while(getOrientationAngle()  - 0.1f < angle &&
			  getOrientationAngle()  + 0.05f > angle) { //TODO: 0.05 angular allowance
			setAngularVel(0.1f); //TODO: temporarily using random speed
		}
		setAngularVel((float) (0));
	}
	
	
	
	//TODO: If feeders needed, this will be necessary
	public void eat() {
	}
}
