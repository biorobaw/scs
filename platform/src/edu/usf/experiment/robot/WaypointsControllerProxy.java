package edu.usf.experiment.robot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.vecmath.Point3f;

import edu.usf.experiment.universe.morse.PosSensorProxy;

public class WaypointsControllerProxy {

	private static final float STEP = 0.1f;
	private static final float TOLERANCE = 0.02f;
	private static final float SPEED = 1.0f;
	private Socket s;
	private BufferedWriter writer;
	private PosSensorProxy poseSensor;
	private int msgNum;
	private BufferedReader reader;

	public WaypointsControllerProxy(PosSensorProxy posSensor, int port) {
		try {
			s = new Socket("localhost", port);
			writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.poseSensor = posSensor;
		msgNum = 0;
	}

	public void stepForward() {
		Point3f pose = poseSensor.getPosition();
		float yaw = poseSensor.getOrientation();
		Point3f dst = new Point3f(pose.x + STEP * (float) Math.cos(yaw), pose.y + (float) Math.sin(yaw), pose.z);
		try {
			writer.write("id" + msgNum++ + " robot.waypoint goto [" + dst.x + ", " + dst.y + "," + dst.z + ", " + TOLERANCE
					+ ", " + SPEED + "]");
			
			boolean arrived = false;
			while (!arrived){
				String line = reader.readLine();
				line.split(" ")[2];
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

	}

}
