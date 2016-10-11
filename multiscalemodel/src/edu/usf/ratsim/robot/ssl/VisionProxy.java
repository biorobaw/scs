package edu.usf.ratsim.robot.ssl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.vecmath.Point3f;

import edu.usf.ratsim.support.Position;

public class VisionProxy extends Thread {

	private static VisionProxy vp;
	private Position lastPosition;
	private long lastPosTime;
	private ServerSocket listenSocket;
	private Socket socket;
	private Scanner reader;

	private VisionProxy() {
		try {
			listenSocket = new ServerSocket(63111);
			socket = listenSocket.accept();
			reader = new Scanner(socket.getInputStream());
			reader.nextLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lastPosTime = 0;

		start();
	}

	@Override
	public void run() {
		while (true) {
			getRobotPosition();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void getRobotPosition() {
		float x = reader.nextFloat();
		float y = reader.nextFloat();
		float theta = reader.nextFloat();
		setPosition(new Position(x, y, theta));
		reader.nextLine();
		
		
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
		reader.close();
		socket.close();
		listenSocket.close();
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
		VisionProxy p = VisionProxy.getVisionProxy();
		
		while (true){
			System.out.println(p.getRobotPoint() + " " + p.getRobotOrientation());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static VisionProxy getVisionProxy() {
		if (vp == null)
			vp  = new VisionProxy();
		
		return vp;
	}
}
