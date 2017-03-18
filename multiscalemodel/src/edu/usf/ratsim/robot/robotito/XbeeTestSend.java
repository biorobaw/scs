//package edu.usf.ratsim.robot.robotito;
//
//import com.digi.xbee.api.RemoteXBeeDevice;
//import com.digi.xbee.api.XBeeDevice;
//import com.digi.xbee.api.exceptions.XBeeException;
//import com.digi.xbee.api.models.XBee64BitAddress;
//
//public class XbeeTestSend {
//
//    /* Constants */
//    // TODO Replace with the port where your sender module is connected to.
//    private static final String PORT = "/dev/ttyUSB1";
//    // TODO Replace with the baud rate of your sender module.  
//    private static final int BAUD_RATE = 9600;
//
//    private static final String DATA_TO_SEND = "Hello XBee World!";
//	private static final String REMOTE_NODE_IDENTIFIER = "LOCAL";
//
//    public static void main(String[] args) {
//        XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
//        byte[] dataToSend = DATA_TO_SEND.getBytes();
//
//        try {
//            myDevice.open();
//			
//            byte remoteAddr[] = {0x11,0x11,0x11,0x11};
//            RemoteXBeeDevice remoteDevice = new RemoteXBeeDevice(myDevice, new XBee64BitAddress("0x00001111"));
////            XBeeNetwork xbeeNetwork = myDevice.getNetwork();
////			RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice(REMOTE_NODE_IDENTIFIER);
////			if (remoteDevice == null) {
////				System.out.println("Couldn't find the remote XBee device with '" + REMOTE_NODE_IDENTIFIER + "' Node Identifier.");
////				System.exit(1);
////			}
//            myDevice.sendData(remoteDevice, dataToSend);
////            myDevice.sendBroadcastData(dataToSend);
//            System.out.println(" >> Success");
//
//        } catch (XBeeException e) {
//            System.out.println(" >> Error");
//            e.printStackTrace();
//            System.exit(1);
//        } finally {
//            myDevice.close();
//        }
//    }
//}
