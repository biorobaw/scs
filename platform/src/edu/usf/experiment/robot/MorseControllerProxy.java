package edu.usf.experiment.robot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.vecmath.Point3f;

import edu.usf.experiment.universe.morse.PosSensorProxy;
import edu.usf.experiment.utils.GeomUtils;

public class MorseControllerProxy {

	private static final float STEP = 0.15f;
	private static final float TOLERANCE = 0.04f;
	private static final float SPEED = 2.0f;
	private static final long SLEEP = 100;
	private static final float TURNSTEP = 3.14159f / 8;
	private static final float TURNSPEED = 1;
	private Socket s;
	private BufferedWriter writer;
	private PosSensorProxy poseSensor;
	private int msgNum;
	private BufferedReader reader;

	public MorseControllerProxy(PosSensorProxy posSensor) {
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
		Point3f fromPose = poseSensor.getPosition();
		try {
			String cmd = "id" + msgNum++ + " robot.vw set_speed [" + SPEED + ", 0]\n";
			System.out.println(cmd);
			writer.write(cmd);
			writer.flush();

			float dist;
			do {
				Thread.sleep(SLEEP);
				dist = poseSensor.getPosition().distance(fromPose);
			} while (dist < STEP - TOLERANCE);

			cmd = "id" + msgNum++ + " robot.vw set_speed [0, 0]\n";
			System.out.println(cmd);
			writer.write(cmd);
			writer.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void turnStep(float degrees) {
		float fromAngle = poseSensor.getOrientation();
		try {
			String cmd = "id" + msgNum++ + " robot.vw set_speed [0, " + TURNSPEED * Math.signum(degrees) + "]\n";
			System.out.println(cmd);
			writer.write(cmd);
			writer.flush();

			float dist;
			do {
				Thread.sleep(SLEEP);
				dist = Math.abs(GeomUtils.angleDiff(poseSensor.getOrientation(), fromAngle));
			} while (dist < TURNSTEP - TOLERANCE);

			cmd = "id" + msgNum++ + " robot.vw set_speed [0, 0]\n";
			System.out.println(cmd);
			writer.write(cmd);
			writer.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
