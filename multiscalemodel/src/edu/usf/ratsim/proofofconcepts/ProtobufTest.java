package edu.usf.ratsim.proofofconcepts;

import java.net.Socket;

import edu.usf.ratsim.robot.naorobot.protobuf.Connector.Command;
import edu.usf.ratsim.robot.naorobot.protobuf.Connector.Command.Builder;
import edu.usf.ratsim.robot.naorobot.protobuf.Connector.Command.CommandType;
import edu.usf.ratsim.robot.naorobot.protobuf.Connector.Response;

public class ProtobufTest {

	public static void main(String[] args)
	{
		try {
			    Socket protoSocket = new Socket("cerebro", 12345);
			    
//			    Builder b = Command.newBuilder();
//				b.setType(CommandType.startRobot);
//				Command c = b.build();
//				c.writeTo(protoSocket.getOutputStream());
//			    
			    //System.out.println("Press something to rest");
//			    System.in.read();
//			    
//			    b = Command.newBuilder();
//				b.setType(CommandType.stopRobot);
//				c = b.build();
			    
			    while (true) {
			    		Builder b = Command.newBuilder();
					b.setType(CommandType.getInfo);
					Command c = b.build();
					c.writeTo(protoSocket.getOutputStream());
				    
				    Response r = Response.parseDelimitedFrom(protoSocket.getInputStream());
				    System.out.println(r);
				    
				    System.in.read();
			    }
			    
			    
			    
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
	}
}
