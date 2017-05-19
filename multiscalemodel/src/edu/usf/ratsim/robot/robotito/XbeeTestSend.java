package edu.usf.ratsim.robot.robotito;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;

public class XbeeTestSend {

    /* Constants */
    // TODO Replace with the port where your sender module is connected to.
    private static final String PORT = "/dev/ttyUSB0";
    // TODO Replace with the baud rate of your sender module.  
    private static final int BAUD_RATE = 57600;

    public static void main(String[] args) {
        XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
        
        // (x, y, theta) velocities. 128 means 0, >128 is positive, <128 is negative
        byte[] dataToSend = {(byte) 150, (byte) 128, (byte) 128};

        try {
            myDevice.open();
			
            RemoteXBeeDevice remoteDevice = new RemoteXBeeDevice(myDevice, new XBee64BitAddress("0x00002222"));
            myDevice.sendData(remoteDevice, dataToSend);
            System.out.println(" >> Success");

        } catch (XBeeException e) {
            System.out.println(" >> Error");
            e.printStackTrace();
            System.exit(1);
        } finally {
            myDevice.close();
        }
    }
}
