package edu.usf.ratsim.robot.ssl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.vecmath.Point3f;

import edu.usf.ratsim.robot.naorobot.protobuf.MessagesRobocupSslDetection.SSL_DetectionRobot;
import edu.usf.ratsim.robot.naorobot.protobuf.MessagesRobocupSslWrapper.SSL_WrapperPacket;
import edu.usf.ratsim.support.Position;

//North east
//(414, 1270)
//
//South east
//(2740, 1309)	
//
//South west
//(2719, -1014)
//
//North west
//(397, -1020)

public class VisionListener extends Thread {

	private static VisionListener vl = null;
	
	private InetAddress group;
	private MulticastSocket s;
	private Position lastPosition;
	private long lastPosTime;

	private Socket proxySocket;

	private PrintWriter outStream;

	private VisionListener() {
		try {
			group = InetAddress.getByName("224.5.23.2");
			s = new MulticastSocket(10002);
			s.joinGroup(group);
			s.setReceiveBufferSize(4096);
			System.out.println(s.getReceiveBufferSize());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			proxySocket = new Socket("odroid", 63111);
			outStream = new PrintWriter(proxySocket.getOutputStream(),true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		lastPosTime = 0;

		start();
	}

	@Override
	public void run() {
		while (true) {
			getRobotPosition();
			outStream.println(positionToString());
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	public static void main(String[] args) {
		getVisionListener();
	}

	private static VisionListener getVisionListener() {
		if (vl == null)
			vl  = new VisionListener();
		
		return vl;
	}
}
