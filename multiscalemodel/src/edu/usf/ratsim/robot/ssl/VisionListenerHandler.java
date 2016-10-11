package edu.usf.ratsim.robot.ssl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import javax.vecmath.Point3f;

import edu.usf.ratsim.robot.naorobot.protobuf.MessagesRobocupSslDetection.SSL_DetectionRobot;
import edu.usf.ratsim.robot.naorobot.protobuf.MessagesRobocupSslWrapper.SSL_WrapperPacket;
import edu.usf.ratsim.support.Position;

public class VisionListenerHandler extends Thread {

	private PrintWriter outStream;
	
	private InetAddress group;
	private MulticastSocket s;
	private Position lastPosition;
	private long lastPosTime;

	public VisionListenerHandler(PrintWriter outStream) {
		this.outStream = outStream;
		
		try {
			s = new MulticastSocket(10002);
			s.joinGroup(new InetSocketAddress("224.5.23.2",10002), NetworkInterface.getByName("lo"));
			s.setReceiveBufferSize(4096);
			System.out.println(s.getReceiveBufferSize());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lastPosTime = 0;
	}
	
	public void run(){
		getRobotPosition();
		outStream.println(positionToString());
		System.out.println(positionToString());
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private synchronized String positionToString() {
		return lastPosition.getX() + " " + lastPosition.getY() + " " + lastPosition.getOrient();
	}

	private void getRobotPosition() {
		// if (Math.abs(System.currentTimeMillis() - lastPosTime) < 200)
		// return lastPosition;

		SSL_WrapperPacket f = null;
		try {
			byte[] buf = new byte[4096];
			DatagramPacket recv = new DatagramPacket(buf, buf.length);
			s.receive(recv);
			byte[] data = new byte[recv.getLength()];
			System.arraycopy(buf, 0, data, 0, recv.getLength());
			f = SSL_WrapperPacket.parseFrom(data);
			
			// Only accept readings from cam 0
			if (f.getDetection().getCameraId() == 0){
				SSL_DetectionRobot robot1 = null;
				for(SSL_DetectionRobot r : f.getDetection().getRobotsBlueList()){
					if (r.getRobotId() == 1)
						robot1 = r;
				}
				
				if (robot1 != null){
					SSL_DetectionRobot r = f.getDetection().getRobotsBlue(0);
					// System.out.println(r.getX());
					// System.out.println(r.getY());
					setPosition(new Position(r.getX(), r.getY(), r.getOrientation()));
					
				} else {
					if (System.currentTimeMillis() - lastPosTime > 500){
	//					System.err.println("Havent detected robot for more than half a second");
					}
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	private synchronized void setPosition(Position position) {
		lastPosition = position;
		lastPosTime = System.currentTimeMillis();
	}

	private synchronized Position getLastPosition(){
		return lastPosition;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		s.leaveGroup(group);
		s.close();
	}
	
	public synchronized boolean hasPosition(){
		return lastPosTime > System.currentTimeMillis() - 300;
	}

	public synchronized Point3f getRobotPoint() {		
		Position p = lastPosition;
		return new Point3f(p.getX(), p.getY(), 0);
	}

	public synchronized float getRobotOrientation() {
		Position p = lastPosition;
		// System.out.println(p.getOrient());
		return p.getOrient();
	}

}
