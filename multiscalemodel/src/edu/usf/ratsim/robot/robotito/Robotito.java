package edu.usf.ratsim.robot.robotito;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;

import edu.usf.experiment.robot.DifferentialRobot;
import edu.usf.experiment.utils.ElementWrapper;

public class Robotito implements DifferentialRobot, Runnable {

    private static final String PORT = "/dev/ttyUSB0";
    private static final int BAUD_RATE = 57600;
    
    private static final int MAX_XVEL = 127;
    private static final int MAX_TVEL = 127;
	private static final int ZERO_VEL = 128;
    private static final float XVEL_CONV = MAX_XVEL / 3f;
    private static final float TVEL_CONV = (float) (MAX_TVEL / (Math.PI * 2 * 2));
    
	private static final long CONTROL_PERIOD = 100;

    
	private XBeeDevice myDevice;
	private RemoteXBeeDevice remoteDevice;
	private float xVel;
	private float tVel;

    
	public Robotito(ElementWrapper params) {
		myDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		try {
			myDevice.open();
			
			myDevice.addDataListener(new IDataReceiveListener() {
				public void dataReceived(XBeeMessage msg) {
//					System.out.println(msg.getDataString());
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
			short xVelShort = (short) (xVel * XVEL_CONV + ZERO_VEL);
			xVelShort = (short) Math.max(0, Math.min(xVelShort, 255));
			short tVelShort = (short) (tVel * TVEL_CONV + ZERO_VEL);
			tVelShort = (short) Math.max(0, Math.min(tVelShort, 255));
			byte[] dataToSend = {(byte) xVelShort, (byte) 128, (byte) tVelShort};
			
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
		Robotito r = new Robotito(null);
		r.setLinearVel(1f);
		r.setAngularVel(2f);
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

}
