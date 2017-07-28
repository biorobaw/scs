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

import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.robot.DifferentialRobot;
import edu.usf.experiment.robot.HolonomicRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.PlatformRobot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class Robotito implements DifferentialRobot, HolonomicRobot, SonarRobot, LocalizableRobot, PlatformRobot, Runnable {

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

	private XBee xbee;
	private XBeeAddress16 remoteAddress;
    
	
	private float xVel;
	private float yVel;
	private float tVel;

	private ROSPoseDetector poseDetector;
	private int kP = 100;
	private int kI = 5;
	private int kD = 0;
	private SonarReceiver sonarReceiver;

	public Robotito(ElementWrapper params, Universe u) {
		
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
			short tVelShort = (short) (tVel * TVEL_CONV + ANGULAR_INERTIA * Math.signum(xVel) + ZERO_VEL);
			tVelShort = (short) Math.max(0, Math.min(tVelShort, 255));
			int[] dataToSend = {(byte)'v', (byte) Math.abs(xVelShort), (byte) Math.abs(yVelShort), (byte) Math.abs(tVelShort)};
			
			System.out.println("Sending vels: " + xVelShort + " " + tVelShort);
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
		String posStr = PropertyHolder.getInstance().getProperty("platformPosition");
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
	
	public static void main(String[] args){
		Robotito r = new Robotito(null, null);
//		r.releaseMotors();

//		r.calibrateSonars();
		
		r.setLinearVel(.0f);
//		r.yVel = .1f;
		r.setAngularVel((float) (3));
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

}
