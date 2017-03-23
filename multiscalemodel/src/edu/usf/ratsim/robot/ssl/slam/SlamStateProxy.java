package edu.usf.ratsim.robot.ssl.slam;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.vecmath.Point3f;

import edu.usf.ratsim.robot.ssl.slam.protobuf.Slamstate.SlamState;

public class SlamStateProxy extends Thread {

	private static final String HOST = "locahost";
	private static final int PORT = 12346;
	private Socket protoSocket;
	private boolean terminated;
	private InputStream protoInputStream;
	private ServerSocket serverSocket;

	private SlamState ss;

	public SlamStateProxy() {
		establishConnection();

		ss = SlamState.getDefaultInstance();

		terminated = false;
	}

	private void establishConnection() {
		try {
			if (protoSocket != null)
				protoSocket.close();

			if (serverSocket != null)
				serverSocket.close();

			System.out.println("Listening to " + PORT);
			serverSocket = new ServerSocket(PORT);
			protoSocket = serverSocket.accept();
			protoInputStream = protoSocket.getInputStream();
			System.out.println("Connection stablished");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (!terminated) {
			try {
				SlamState newSS = SlamState.parseDelimitedFrom(protoInputStream);
				if (newSS == null) {
					setSlamState(SlamState.getDefaultInstance());
					establishConnection();
				} else {
					System.out.println("Robot at " + newSS.getX() + "," + newSS.getY() + "," + newSS.getHeading());
					setSlamState(newSS);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private synchronized void setSlamState(SlamState ss) {
		this.ss = ss;
	}

	public synchronized int getTrackingState(){
		return ss.getState();
	}

	public synchronized Point3f getPosition(){
		return new Point3f(ss.getX(), ss.getY(), 0);
	}

	public synchronized float getOrientation(){
		return ss.getHeading();
	}

	public static void main(String[] args) {
		SlamStateProxy ssProxy = new SlamStateProxy();
		ssProxy.start();

		while (true) {
			int state = ssProxy.getTrackingState();
			System.out.println("Tracking state " + state);
			Point3f p = ssProxy.getPosition();
			System.out.println("Robot at " + p);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void close() {
		try {
			protoInputStream.close();
			serverSocket.close();
			protoSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
