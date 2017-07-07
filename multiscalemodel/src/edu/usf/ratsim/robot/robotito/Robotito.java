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
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class Robotito implements DifferentialRobot, SonarRobot, LocalizableRobot, Runnable {

    private static final String PORT = "/dev/ttyUSB0";
    private static final int BAUD_RATE = 57600;
    
    private static final int MAX_XVEL = 127;
    private static final int MAX_TVEL = 127;
	private static final int ZERO_VEL = 128;
    private static final float XVEL_CONV = MAX_XVEL / .5f;
    private static final float TVEL_CONV = (float) (MAX_TVEL / (Math.PI * 2 * 1));
    
	private static final long CONTROL_PERIOD = 100;
	private static final int NUM_SONARS = 12;

    
	private XBeeDevice myDevice;
	private RemoteXBeeDevice remoteDevice;
	private float xVel;
	private float tVel;

	private float[] sonarReading;
	private float[] sonarAngles;
    
	public Robotito(ElementWrapper params, Universe u) {
		sonarAngles = new float[NUM_SONARS];
		sonarReading = new float[NUM_SONARS];
		for (int i = 0; i < NUM_SONARS; i++){
			sonarAngles[i] = (float) (2 * Math.PI / NUM_SONARS * i);
			System.out.print(sonarAngles[i] + ",");
			sonarReading[i] = 0f;
		}
		
		myDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		try {
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
			System.exit(1);
		}
		
		xVel = 0;
		tVel = 0;
		
		// Start a thread to send vel packets
		new Thread(this).start();
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
			short xVelShort = (short) (xVel * XVEL_CONV + ZERO_VEL);
			xVelShort = (short) Math.max(0, Math.min(xVelShort, 255));
			short tVelShort = (short) (tVel * TVEL_CONV + ZERO_VEL);
			tVelShort = (short) Math.max(0, Math.min(tVelShort, 255));
			byte[] dataToSend = {(byte) xVelShort, (byte) 128, (byte) tVelShort};
			
//			System.out.println(xVelShort + " " + tVelShort);
			try {
				myDevice.sendData(remoteDevice, dataToSend);
			} catch (XBeeException e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(CONTROL_PERIOD);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
				
	}
	
	public static void main(String[] args){
		Robotito r = new Robotito(null, null);
		r.setLinearVel(0f);
		r.setAngularVel(0f);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		r.setLinearVel(0);
		r.setAngularVel(0f);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
		
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
	public float getSonarAperture() {
		return 0;
	}

	@Override
	public Coordinate getPosition() {
		return new Coordinate();
	}

	@Override
	public float getOrientationAngle() {
		return 0f;
	}

}
