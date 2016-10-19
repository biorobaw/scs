package edu.usf.experiment.universe.morse;

import java.awt.geom.Point2D.Float;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;

public class MorseUniverse extends Universe {

	private PosSensorProxy posSensor;

	public MorseUniverse(ElementWrapper params, String logPath) {
		super(params, logPath);
		
		MorseUtils.startSimulator();

		Map<String, Integer> streamPorts = MorseUtils.getStreamPorts();
		
		if (streamPorts.containsKey("robot.pose")) {
			System.out.println("[+] Starting pose sensor proxy");
			posSensor = new PosSensorProxy(streamPorts.get("robot.pose"));
			posSensor.start();
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
	public void setRobotPosition(Float float1, float w) {
		// TODO
	}

	public static void main(String[] args) {

	}

}
