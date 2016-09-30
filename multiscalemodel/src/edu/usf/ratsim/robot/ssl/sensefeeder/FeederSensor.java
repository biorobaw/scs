package edu.usf.ratsim.robot.ssl.sensefeeder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.vecmath.Point3f;

import edu.usf.ratsim.robot.ssl.sensefeeder.protobuf.FeedersOuterClass.Feeders;

public class FeederSensor extends Thread {

	private static final String HOST = "locahost";
	private static final int PORT = 12345;
	private Socket protoSocket;
	private boolean terminated;
	private Feeders fs;
	private InputStream protoInputStream;
	private ServerSocket serverSocket;

	public FeederSensor() {
		establishConnection();

		fs = Feeders.getDefaultInstance();

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
				Feeders newFs = Feeders.parseDelimitedFrom(protoInputStream);

				if (fs == null) {
					setFeeders(Feeders.getDefaultInstance());
					establishConnection();
				} else
					setFeeders(newFs);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private synchronized void setFeeders(Feeders fs) {
		this.fs = fs;
	}

	public synchronized boolean isSeeinFeeder() {
		return !fs.getFeedersList().isEmpty();
	}

	public synchronized Point3f getFeederLocation() {
		Point3f p;
		if (fs.getFeedersCount() > 0)
			p = new Point3f(fs.getFeeders(0).getX(), fs.getFeeders(0).getY(), 0);
		else
			p = null;

		return p;
	}

	public static void main(String[] args) {
		FeederSensor fSensor = new FeederSensor();
		fSensor.start();

		while (true) {
			Point3f p = fSensor.getFeederLocation();
			System.out.println("Marker at " + p);
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
