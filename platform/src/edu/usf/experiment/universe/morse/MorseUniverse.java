package edu.usf.experiment.universe.morse;

import java.awt.geom.Point2D.Float;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;

public class MorseUniverse extends Universe {

	private PosSensorProxy posSensor;
	private BufferedWriter writer;
	private BufferedReader reader;

	public MorseUniverse(ElementWrapper params, String logPath) {
		super(params, logPath);
		
		MorseUtils.startSimulator();

		Map<String, Integer> streamPorts = MorseUtils.getStreamPorts();
		
		if (streamPorts.containsKey("robot.pose")) {
			System.out.println("[+] Starting pose sensor proxy");
			posSensor = new PosSensorProxy(streamPorts.get("robot.pose"));
			posSensor.start();
		}
		
		try {
			Socket s = new Socket("localhost", 4000);
			writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Point3f getRobotPosition() {
		return posSensor.getPosition();
	}

	@Override
	public Quat4f getRobotOrientation() {
		return GeomUtils.angleToRot(posSensor.getOrientation());
	}

	@Override
	public float getRobotOrientationAngle() {
		return posSensor.getOrientation();
	}

	@Override
	public void setRobotPosition(Float p, float w) {
		// Move the robot by teleporting it
		try {
			writer.write("id robot.tele teleport [" + p.getX() + ", " + p.getY() + ",0.2,0,0," + w + "]\n");
			writer.flush();
			if(!reader.readLine().contains("SUCCESS"))
				System.err.println("Teleport of the robot failed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

	}

}
