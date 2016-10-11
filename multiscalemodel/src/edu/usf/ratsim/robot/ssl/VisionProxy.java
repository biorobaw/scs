package edu.usf.ratsim.robot.ssl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.vecmath.Point3f;

import edu.usf.ratsim.support.Position;

public class VisionProxy extends Thread {

	private static VisionProxy vp;
	private Position lastPosition;
	private long lastPosTime;
	private Socket socket;
	private Scanner reader;
	private boolean terminate;

	private VisionProxy() {
		try {
			System.out.println("[+] Connecting to vision listener");
			socket = new Socket("cmac1", 63111);
			reader = new Scanner(socket.getInputStream());
			System.out.println("[+] Connected to vision listener");
			reader.nextLine();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lastPosTime = 0;
		lastPosition = new Position(0, 0, 0);
		terminate = false;
		
		start();
	}

	@Override
	public void run() {
		while (!terminate) {
			getRobotPosition();
		}
	}

	private void getRobotPosition() {
		try {
			float x = reader.nextFloat();
			float y = reader.nextFloat();
			float theta = reader.nextFloat();
			setPosition(new Position(x, y, theta));
			reader.nextLine();
		} catch (NoSuchElementException e) {
			System.err.println("Broken channel, exiting");
			System.exit(1);
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
		reader.close();
		socket.close();
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
