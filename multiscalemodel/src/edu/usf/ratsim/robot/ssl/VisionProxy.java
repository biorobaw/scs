package edu.usf.ratsim.robot.ssl;

import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.vecmath.Point3f;

import edu.usf.ratsim.support.Position;

public class VisionProxy extends Thread {

	private final float nex = 11.154078f;
	private final float ney = 1143.6995f;
	private final float sex = 2731.3103f;
	private final float sey = 1212.2427f;
	private final float swx = 2753.2412f;
	private final float swy = -1533.0186f;
	private final float nwx = 45.255f;
	private final float nwy = -1619.2607f;
	private final float fl = 2;

	private static final long FRESH_THRS = 300;
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

	public synchronized Position getLastPosition() {
		return lastPosition;
	}

	@Override
	protected void finalize() throws Throwable {
		reader.close();
		socket.close();
	}

	public Point3f getRobotPoint() {
		while (!isInfoFresh())
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Position p;
		synchronized (this) {
			p = lastPosition;
		}
		return scale(new Point3f(p.getX(), p.getY(), 0));
	}

	public float getRobotOrientation() {
		while (!isInfoFresh())
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Position p;
		synchronized (this) {
			p = lastPosition;
		}
		// System.out.println(p.getOrient());
		return p.getOrient();
	}

	// public static void main(String[] args) {
	// VisionProxy p = VisionProxy.getVisionProxy();
	//
	// while (true){
	// System.out.println(p.getRobotPoint() + " " + p.getRobotOrientation());
	// try {
	// Thread.sleep(100);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

	public static VisionProxy getVisionProxy() {
		if (vp == null)
			vp = new VisionProxy();

		return vp;
	}

	public synchronized boolean isInfoFresh() {
		return System.currentTimeMillis() - lastPosTime < FRESH_THRS;
	}

	private Point3f scale(Point3f p) {
		float x = (-fl / (nwx - swx)) * p.x + (1 + (fl / (nwx - swx)) * swx);
		float y = (fl / (ney - nwy)) * p.y + (-1 + (-fl / (ney - nwy)) * nwy);
		x = Math.min(fl / 2, x);
		x = Math.max(-fl / 2, x);
		y = Math.min(fl / 2, y);
		y = Math.max(-fl / 2, y);
		// System.out.println(x + " " + y);
		return new Point3f(x, y, 0);
	}

	public static void main(String[] args) {

		VisionProxy v = new VisionProxy();
		while (true) {
			System.out.println(v.getRobotPoint());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		Position p = null;
//		try {
//			VisionProxy v = new VisionProxy();
//			Thread.sleep(10000);
//			p = v.getLastPosition();
//			System.out.println("private final float nex = " + p.getX() + "f;");
//			System.out.println("private final float ney = " + p.getY() + "f;");
//			Thread.sleep(10000);
//			p = v.getLastPosition();
//			System.out.println("private final float sex = " + p.getX() + "f;");
//			System.out.println("private final float sey = " + p.getY() + "f;");
//			Thread.sleep(10000);
//			p = v.getLastPosition();
//			System.out.println("private final float swx = " + p.getX() + "f;");
//			System.out.println("private final float swy = " + p.getY() + "f;");
//			Thread.sleep(10000);
//			p = v.getLastPosition();
//			System.out.println("private final float nwx = " + p.getX() + "f;");
//			System.out.println("private final float nwy = " + p.getY() + "f;");
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
