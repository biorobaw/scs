package edu.usf.ratsim.robot.robotito;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.DifferentialRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.PlatformRobot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class Robotito implements DifferentialRobot, SonarRobot, LocalizableRobot, PlatformRobot, Runnable {

    private static final String PORT = "/dev/ttyUSB0";
    private static final int BAUD_RATE = 57600;
    
    // Shared constants with the arduino code
    // 100 is the max absolute value for the command
    private static final int MAX_XVEL = 100;
    private static final int MAX_TVEL = 100;
    // 100 means 5 m/s linear vel
    private static final float XVEL_CONV = MAX_XVEL / 5;
    // 100 means 4 turns/s angular vel
    private static final float TVEL_CONV = (float) (MAX_TVEL / (Math.PI * 8));
    // 128 means zero in the final byte (b = 128 + cmd)
    private static final int ZERO_VEL = 128;
    
	private static final long CONTROL_PERIOD = 50;
	private static final int NUM_SONARS = 12;
	private static final float LINEAR_INERTIA = 0;
	private static final float ANGULAR_INERTIA = 0;
	private static final float ROBOT_RADIUS = 0.075f;

	private XBeeDevice myDevice;
	private RemoteXBeeDevice remoteDevice;
	private float xVel;
	private float tVel;

	private float[] sonarReading;
	private float[] sonarAngles;
	
	private ROSPoseDetector poseDetector;
	private int kP = 20;
	// kI is divided by 10 in the robot
	private int kI = 40;
	private int kD = 0;
    
	public Robotito(ElementWrapper params, Universe u) {
		sonarAngles = new float[NUM_SONARS];
		sonarReading = new float[NUM_SONARS];
		for (int i = 0; i < NUM_SONARS; i++){
			sonarAngles[i] = (float) (2 * Math.PI / NUM_SONARS * i);
			System.out.print(sonarAngles[i] + ",");
			sonarReading[i] = .3f;
		}
		
		try {
			myDevice = new XBeeDevice(PORT, BAUD_RATE);
			myDevice.open();
			
			myDevice.addDataListener(new IDataReceiveListener() {
				

				public void dataReceived(XBeeMessage msg) {
					byte[] data = msg.getData();
					for (int i = 0; i < data.length; i+=2){
						byte hi = data[i];
						byte lo = data[i+1];
						int val =  (hi & 0xff) << 8 | (lo & 0xff);
						sonarReading[i / 2] = convert(val);
//						System.out.print(sonarReading[i / 2] + " ");
					}
//					System.out.println();
				}

				private float convert(int val) {
					float volt = (val/1024.0f) * 5;
					if (volt < .3)
						return .3f; //FLT_MAX;
					else if (volt < 1.8){
						// Inverse of distance using eq
						float distinv = 0.0758f * volt - 0.00265f;
						float dist = 1 / distinv - 0.42f;
						return dist / 100f;
					} else {
						float distinv = 0.1111f * volt - 0.07831f;
						float dist = 1 / distinv - 0.42f;
						return dist / 100f;
					}
				}
			});
			
			remoteDevice = new RemoteXBeeDevice(myDevice, new XBee64BitAddress("0x00002222"));
		} catch (XBeeException e) {
			e.printStackTrace();
		}
		
		xVel = 0;
		tVel = 0;
		
		poseDetector = ROSPoseDetector.getInstance();
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
		
		sendKs();
		
		// Start a thread to send vel packets
		Thread runner = new Thread(this);
		runner.setPriority(Thread.MAX_PRIORITY);
		runner.start();
	}

	private void sendKs() {
		byte[] dataToSend = {(byte)'k', 
				(byte)(kP + 128), (byte)(kI + 128), (byte)(kD + 128),
				(byte)(kP + 128), (byte)(kI + 128), (byte)(kD + 128),
				(byte)(kP + 128), (byte)(kI + 128), (byte)(kD + 128),
				(byte)(kP + 128), (byte)(kI + 128), (byte)(kD + 128)};
		
		try {
			myDevice.sendData(remoteDevice, dataToSend);
		} catch (XBeeException e) {
			System.err.println("XBee error");
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
			System.out.println(xVel + " " + tVel);
			short xVelShort = (short) (xVel * XVEL_CONV + LINEAR_INERTIA * Math.signum(xVel) + ZERO_VEL);
			xVelShort = (short) Math.max(0, Math.min(xVelShort, 255));
			short tVelShort = (short) (tVel * TVEL_CONV + ANGULAR_INERTIA * Math.signum(xVel) + ZERO_VEL);
			tVelShort = (short) Math.max(0, Math.min(tVelShort, 255));
			byte[] dataToSend = {(byte)'v', (byte) Math.abs(xVelShort), (byte) 128, (byte) Math.abs(tVelShort)};
			
			System.out.println(xVelShort + " " + tVelShort);
			try {
				myDevice.sendDataAsync(remoteDevice, dataToSend);
			} catch (XBeeException e) {
				System.err.println("XBee error");
			}
			
			try {
				Thread.sleep(CONTROL_PERIOD);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
				
	}
	
	@Override
	public float[] getSonarReadings() {
		return sonarReading;
	}

	@Override
	public float[] getSonarAngles() {
		return sonarAngles;
	}
	
	@Override
	public float getSonarMaxReading() {
		return .3f;
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
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args){
		Robotito r = new Robotito(null, null);
		r.setLinearVel(.2f);
		r.setAngularVel((float) (-4));
		try {
			Thread.sleep(5000);
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
	public float getRadius() {
		return ROBOT_RADIUS;
	}

}
