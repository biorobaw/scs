package edu.usf.ratsim.robot.robotito;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;

public class XbeeTestReceive {

    /* Constants */
    // TODO Replace with the port where your sender module is connected to.
    private static final String PORT = "/dev/ttyUSB0";
    // TODO Replace with the baud rate of your sender module.  
    private static final int BAUD_RATE = 9600;

    private static final String DATA_TO_SEND = "Hello XBee World!";
	private static final String REMOTE_NODE_IDENTIFIER = "LOCAL";

    public static void main(String[] args) {
    	System.out.println(" +-----------------------------------------+");
		System.out.println(" |  XBee Java Library Receive Data Sample  |");
		System.out.println(" +-----------------------------------------+\n");
		
		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		try {
			myDevice.open();
			
			myDevice.addDataListener(new IDataReceiveListener() {
				public void dataReceived(XBeeMessage msg) {
					System.out.println(msg.getDataString());
				}
			});
			
			System.out.println("\n>> Waiting for data...");
			
		} catch (XBeeException e) {
			e.printStackTrace();
			System.exit(1);
		}
    }
}
