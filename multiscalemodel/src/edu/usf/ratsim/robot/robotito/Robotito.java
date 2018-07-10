package edu.usf.ratsim.robot.robotito;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
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
import edu.usf.experiment.robot.CalibratableRobot;
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
import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.universe.platform.Platform;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.utils.RigidTransformation;


public class Robotito implements DifferentialRobot, HolonomicRobot, SonarRobot, 
								 LocalizableRobot, PlatformRobot, 
								 LocalActionAffordanceRobot, WallRobot, 
								 CalibratableRobot, Runnable  {

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
	private static final float FORWARD_SPEED = 0.03f;
	private static final float ANGULAR_VEL = (float)(15f * Math.PI / 180);
	
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

	//variables pulled from xml file
	private float step;
	private float leftAngle;
	private float rightAngle;
	private float lookaheadSteps;
	private float halfFieldOfView;
	private float visionDist;
	private float closeThrs;
	private ROSUniverse universe;
	
	//error correction values
	private float stepCorrectionRatio = 1.2f; //more accurate value determined by calibrateStep()
	private float rotCorrectionRatio = 2.7f; //learned by experimentation; currently no automatic test
	
	private static final boolean ROBOT_DEBUG = false;
	//TODO: add a MOTION_DEBUG for various motion print statements.
	
	public Robotito(ElementWrapper params, Universe u) {
		
		if(!ROBOT_DEBUG) {
			step = params.getChildFloat("step");
			leftAngle = params.getChildFloat("leftAngle");
			rightAngle = params.getChildFloat("rightAngle");
			lookaheadSteps = params.getChildFloat("lookaheadSteps");
			halfFieldOfView = params.getChildFloat("halfFieldOfView");
			visionDist = params.getChildFloat("visionDist");
			closeThrs = params.getChildFloat("closeThrs");
			universe = (ROSUniverse) u;
		}
		
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
	
	//main method used to test robot and universe functionality.
	//note that you must set "ROBOT_DEBUG" above to true to avoid nullPointerExcetpions
	public static void main(String[] args){
		Robotito r = new Robotito(null, null);
//		r.releaseMotors();

//		r.calibrateSonars();
		
		
		/*//x and y motion tests
		//r.xVel = 0.04f; 
		r.yVel = -0.04f; 
		
		r.setAngularVel((float) (0));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//r.xVel = -0.04f;
		r.yVel = 0.04f;
		
		r.setAngularVel((float) (0));
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//r.xVel = 0.04f; 
		r.yVel = -0.04f; 
		
		r.setAngularVel((float) (0));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} */
		
		
		/*//theta motion test
		r.tVel = 1.28f;
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		r.tVel = -1.28f;
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		
		r.tVel = 0f;
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} */
		
		
		/*
		//TODO: marker for top of the actual code
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		r.step = 0.05f;
		
		r.calibrateStep();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int m = 0; m < 5; m++) {
			r.forward(0.05f);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 
		*/
		
		
		for(int i = 0; i < 25; i++) { //should be a little less than a full rotation.
			r.rotate(-.25f);
			
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		/*
		r.rotate(.195f);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		r.rotate(.195f);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		r.rotate(-.195f);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} */
		
		
//		r.setLinearVel(0);
//		r.setAngularVel(0f);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		System.exit(0);
		
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
	public synchronized void setLinearVel(float linearVel) {
		asyncSetLinearVel(linearVel);
	}

	@Override
	public synchronized void setAngularVel(float angularVel) {
		asyncSetAngularVel(angularVel);
	}
	
	//resolves synchronization errors. Don't make public.
	private void asyncSetLinearVel(float linearVel) {
		xVel = linearVel;
	}
	
	//resolves synchronization errors. Don't make public.
	private void asyncSetAngularVel(float angularVel) {
		tVel = angularVel;
	}

	@Override
	public synchronized void moveContinous(float lVel, float angVel) {
		xVel = lVel;
		tVel = angVel;
	}

	@Override
	public void run() {
		while (true){
			int[] dataToSend;
			
			synchronized(this) {
		//		System.out.println(xVel + " " + tVel);
				short xVelShort = (short) (xVel * XVEL_CONV + LINEAR_INERTIA * Math.signum(xVel) + ZERO_VEL);
				xVelShort = (short) Math.max(0, Math.min(xVelShort, 255));
				short yVelShort = (short) (yVel * XVEL_CONV + LINEAR_INERTIA * Math.signum(yVel) + ZERO_VEL);
				yVelShort = (short) Math.max(0, Math.min(yVelShort, 255));
				short tVelShort = (short) (tVel * TVEL_CONV + ANGULAR_INERTIA * Math.signum(tVel) + ZERO_VEL);
				tVelShort = (short) Math.max(0, Math.min(tVelShort, 255));
				dataToSend = new int[]{(byte)'v', (byte) Math.abs(xVelShort), (byte) Math.abs(yVelShort), (byte) Math.abs(tVelShort)};
				//System.out.println("Sending vels: " + xVelShort + " " + yVelShort + " " + tVelShort);
			}

			try {
				//System.out.println(dataToSend[0]);
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
	public  Coordinate getPosition() {
		return poseDetector.getPosition();
	}
	
	//returns a position filtered to minimize spikes. Not accurate during motion.
	private Coordinate getFilteredPosition() {
		return poseDetector.getFilteredPosition();
	}

	@Override
	public  float getOrientationAngle() {
		return poseDetector.getAngle();
	}

	@Override
	public boolean hasFoundPlatform() {
		String posStr = PropertyHolder.getInstance().getProperty("platformPosition");
		//Note: above line causes Null pointer errors if a platform isn't published
		String[] fields = posStr.split(",");
		float x = Float.parseFloat(fields[0]);
		float y = Float.parseFloat(fields[1]);
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
			System.out.println("Turn affordance: " + ta.getAngle());
		} else if (af instanceof ForwardAffordance) {
			forward(((ForwardAffordance) af).getDistance());
			System.out.println("Forward affordance: " + ((ForwardAffordance) af).getDistance());
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
			// The robot can afford to turn when it cannot move forward or 
			// if it can turn and still move forward.
			realizable =  !canRobotMove(0, getRadius() * lookaheadSteps) 
					      || canRobotMove(ta.getAngle(), getRadius() * lookaheadSteps);
		} else if (af instanceof ForwardAffordance) {
			realizable =  canRobotMove(0, getRadius() * lookaheadSteps);
			//System.out.println("Forward affordance realizable? " + realizable);
		} else if (af instanceof EatAffordance) { //TODO: The code for eating may be totally unnecessary in a platform approach.
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
	
	public float getDistanceToClosestWall(Coordinate pos) {
		float minDist = Float.MAX_VALUE;
		
		LinkedList<Wall> walls = new LinkedList<Wall>(wallDetector.getWalls());
		for(Wall w: walls) {
			if(w.distanceTo(pos) < minDist) {
				minDist = w.distanceTo(pos);
			}
		}
		
		return minDist;
	}

	@Override
	public float getHalfFieldView() { 
		return halfFieldOfView;
	}
	
	private boolean canRobotMove(float angle, float distance) {	
//		RigidTransformation move = new RigidTransformation(step + ROBOT_RADIUS, 0f, angle);
//		RigidTransformation cur = new RigidTransformation((float)getPosition().x, (float)getPosition().y, getOrientationAngle());
//		RigidTransformation to = cur;
//		to.composeBefore(move);
//		LineSegment path = new LineSegment(getPosition(), to.getTranslation());	
//		Coordinate destCenter = to.getTranslation();
//		for (Wall wall : wallDetector.getWalls()) {
//			if(wall.intersects(path)) return false;//(path.distance(wall.s) < MIN_DISTANCE_TO_WALLS);
//		}
		
		//creates forward path from the center of the robot and the forward path from each flank
		Coordinate curPos = getPosition();
		float orientation = getOrientationAngle();
		
		RigidTransformation move = new RigidTransformation(step + ROBOT_RADIUS, 0f, angle);
		
		RigidTransformation[] to = 	   {new RigidTransformation((float)curPos.x, (float)curPos.y, orientation),
									    new RigidTransformation((float)curPos.x, (float)curPos.y + ROBOT_RADIUS, orientation),
									    new RigidTransformation((float)curPos.x, (float)curPos.y - ROBOT_RADIUS, orientation)};
		
		Coordinate[] startCoordinate = {curPos,
										new Coordinate(curPos.x, curPos.y + ROBOT_RADIUS),
										new Coordinate(curPos.x, curPos.y - ROBOT_RADIUS)};
		
		LineSegment[] paths = new LineSegment[3];
		
		for(int i = 0; i < 3; i++) {
			to[i].composeBefore(move);
			paths[i] = new LineSegment(startCoordinate[i], to[i].getTranslation());
		}
		
		//checks that no wall intersects any path
		for (Wall wall : wallDetector.getWalls()) {
			for(LineSegment path : paths) {
				if(wall.intersects(path)) return false;
			}
		}
		
		//confirms that front of robot won't touch any wall
		if(getDistanceToClosestWall(to[0].getTranslation()) <= ROBOT_RADIUS) return false;
		
		return true;
	}
	
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
	
	//Below is an estimate approach. If the motion is noisy, the robot will not have
	//any sort of external checker. As the camera approach is highly inaccurate (as is), 
	//for now I'll use this approach.
	private void forward(float distance) {
		System.out.println("Moving forward");
		setLinearVel(FORWARD_SPEED);
		if(ROBOT_DEBUG) 
			System.out.println("Est. Time: " + (long)((distance/(5*FORWARD_SPEED) * 1000)/stepCorrectionRatio));
		try {
			Thread.sleep((long)((distance/(5*FORWARD_SPEED) * 1000)/stepCorrectionRatio));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setLinearVel(0f);
		System.out.println("Completed forward motion");
	}
	
	//sets the stepCorrectionRatio value, to be used in forward. 
	//Ensures the distance moved is appropriate, based on MOVING_SPEED and stepLength.
	public void calibrateStep() {
		int numMoves = 10;
		System.out.println("Began step calibration: \n\tMoving speed = " + FORWARD_SPEED * 5 + 
							"\n\tStep length: " + step);
		Coordinate start = getFilteredPosition();
		for(int i = 0; i < numMoves; i++) {
			setLinearVel(FORWARD_SPEED);
			try {
				Thread.sleep((long)((step/(5*FORWARD_SPEED) * 1000)));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setLinearVel(0f);
			
			//let wheels and motors settle
			try {
				Thread.sleep(150); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//guarantees that new pose information comes in before camera check
		try {
			Thread.sleep(500); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		stepCorrectionRatio = ((float)start.distance(getFilteredPosition()))/(step * numMoves);
		
		System.out.println("Distance travelled: " + start.distance(getFilteredPosition()) + 
				"\n\tDistance expected: " + step * numMoves + "\n\tError Ratio: " + stepCorrectionRatio);
		
		if(start.distance(getFilteredPosition()) < step) {
			System.out.println("WARNING: Robot didn't travel any signficant distance. Confirm robot and is functional in the frames."
					+ "\n\tSetting step correction ratio to default value.");
			stepCorrectionRatio = 1.2f;
		}
	}

	//helper method to determine direction of intended motion. May be helpful for future
	//work on forward().
	private int signOfDistance(Coordinate c1, Coordinate goal,float orientation) {
		//dv = c1 - origin
		float a = (float)Math.atan2(goal.y - c1.y, goal.x - c1.x);
		float rAngle = GeomUtils.relativeAngle(a, orientation);
		
		if(Math.abs(rAngle) < Math.toRadians(90)) //1 in front of me, -1 behind me	
			return 1;
		else 
			return -1;
	}
	
	//Below uses the camera system to control distance.
	//Though this should be more accurate, since the cameras are noisy and don't
	//provide new poses quickly enough, the distance will often be wildly inaccurate,
	//significantly more so than the estimate approach.
	private void cameraForward(float goalDistance) {
		System.out.println("Moving forward");
		Coordinate start = getPosition();
		float error = goalDistance;
		System.out.println("Start: " +  start.x + "," + start.y);
	
		while(Math.abs(error = goalDistance - (float)start.distance(getPosition())) > .05f) {
			error = boundError(1.5f * error, -.04f, .04f);
			asyncSetLinearVel(error);
			System.out.println("Speed: " + error);
			try {
				Thread.sleep(100); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		asyncSetLinearVel(0f);
		System.out.println("Completed forward motion");
	}
	
	//This is an estimation approach
	private void rotate(float angle) {
		System.out.println("Starting rotation");
		
		//identify the direction to turn
		float goalAngle = GeomUtils.standardAngle(getOrientationAngle() + angle);
		float angleToTurn = GeomUtils.relativeAngle(goalAngle, getOrientationAngle());
		long turningTime;
		
		if(angleToTurn > 0) {
			setAngularVel(ANGULAR_VEL); //turn left
			turningTime = (long)((Math.abs(angleToTurn)/(ANGULAR_VEL * rotCorrectionRatio) * 1000));
		}
		else {
			setAngularVel(-ANGULAR_VEL); //turn right
			turningTime = (long)((Math.abs(angleToTurn)/(ANGULAR_VEL * rotCorrectionRatio * 1.21) * 1000));
		} //TODO above 1.11 constant should be combined with a separate rotCorrectionRatio, and retested.
		
		
		System.out.println(turningTime);
		
		try {
			Thread.sleep(turningTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		setAngularVel(0f);
		System.out.println("Completed rotation");
	}
	
	
	//PID approach
	//current design makes roughly 13 degree step turns.
	//NOTE: currently not used - it causes constant spinning 360deg or more per rotation,
	//and it can get stuck in a loop at corners.
	private void PIDRotate(float angle) {
		System.out.println("Starting rotation");
		float goalAngle = GeomUtils.standardAngle(getOrientationAngle() + angle);
		//System.out.println(goalAngle);
//		float diffAngle, lastDiffAngle = 100f;
		
		System.out.println(GeomUtils.standardAngle(getOrientationAngle()));
		
		while((Math.abs(GeomUtils.relativeAngle(goalAngle, getOrientationAngle()))) > 0.1) { // for 20 degree slices
			float error = GeomUtils.relativeAngle(goalAngle, getOrientationAngle());
			float be = boundError(error, (float)(40f * Math.PI / 180), (float)(40f * Math.PI / 180));
		
			setAngularVel(be * 1f);
			try { //spin 10 degrees
				Thread.sleep(100); //TODO: currently using tested value, not algorithmic one.
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		setAngularVel(0f);
		System.out.println("Completed rotation");
		System.out.println(GeomUtils.standardAngle(getOrientationAngle()));
	}
	
	private float boundError(float error, float lowerBound, float upperBound) {
		if(error > upperBound) 
			return upperBound;
		if(error < lowerBound) 
			return lowerBound;
		return error;
	}

	//TODO: If feeders needed, this will be necessary
	public void eat() {
		System.out.println("\n\n\nRobotito tried to eat!\n\n");
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		this.releaseMotors();
		super.finalize();
		
	}

	@Override
	public void calibrate() {
		calibrateStep();
	}
}
