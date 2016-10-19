package edu.usf.experiment.robot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.vecmath.Point3f;

import edu.usf.experiment.universe.morse.PosSensorProxy;

public class MorseControllerProxy {

	private static final float STEP = 0.15f;
	private static final float TOLERANCE = 0.04f;
	private static final float SPEED = 2.0f;
	private Socket s;
	private BufferedWriter writer;
	private PosSensorProxy poseSensor;
	private int msgNum;
	private BufferedReader reader;

	public MorseControllerProxy(PosSensorProxy posSensor, int port) {
		try {
			s = new Socket("localhost", 4000);
			writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
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
			String cmd = "id" + msgNum++ + " robot.wpy goto [" + dst.x + ", " + dst.y + "," + dst.z + ", " + TOLERANCE
					+ ", " + SPEED + "]\n";
			System.out.println(cmd);
			writer.write(cmd);
			writer.flush();
			if(!reader.readLine().contains("Arrived")){
				System.err.println("[-] Controller could not step forward");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

	}

}
