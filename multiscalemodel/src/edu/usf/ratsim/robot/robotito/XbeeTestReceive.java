//package edu.usf.ratsim.robot.robotito;
//
//import com.digi.xbee.api.RemoteXBeeDevice;
//import com.digi.xbee.api.XBeeDevice;
//import com.digi.xbee.api.exceptions.XBeeException;
//import com.digi.xbee.api.listeners.IDataReceiveListener;
//import com.digi.xbee.api.models.XBee64BitAddress;
//import com.digi.xbee.api.models.XBeeMessage;
//
//import jdk.management.resource.internal.UnassignedContext;
//
//public class XbeeTestReceive {
//
//    /* Constants */
//    // TODO Replace with the port where your sender module is connected to.
//    private static final String PORT = "/dev/ttyUSB0";
//    // TODO Replace with the baud rate of your sender module.  
//    private static final int BAUD_RATE = 9600;
//
//    private static final String DATA_TO_SEND = "Hello XBee World!";
//	private static final String REMOTE_NODE_IDENTIFIER = "LOCAL";
//	
//	private static byte[] data = null;
//
//    public static void main(String[] args) {
//    	System.out.println(" +-----------------------------------------+");
//		System.out.println(" |  XBee Java Library Receive Data Sample  |");
//		System.out.println(" +-----------------------------------------+\n");
//		
//		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
//		
//		try {
//			myDevice.open();
//			myDevice.addDataListener(new IDataReceiveListener() {
//				public void dataReceived(XBeeMessage msg) {
//					data = msg.getData();
//				}
//			});
//			
//			System.out.println("\n>> Waiting for data...");
//			
//		} catch (XBeeException e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
//		
//		while (true){
//			if (data != null)
//				for (int i = 0; i < data.length; i+=2){
//					byte hi = data[i];
//					byte lo = data[i+1];
//					int val =  (hi & 0xff) << 8 | (lo & 0xff);
//					System.out.print(val + " ");
//				}
//				System.out.println();
//				
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		}
//    }
//}
