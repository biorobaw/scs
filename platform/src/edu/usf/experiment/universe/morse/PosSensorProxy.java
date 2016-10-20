package edu.usf.experiment.universe.morse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.vecmath.Point3f;

import org.json.JSONObject;

public class PosSensorProxy extends Thread {

	private int port;
	private Socket socket;
	private BufferedReader reader;
	private boolean terminate;
	private Point3f point;
	private float theta;

	public PosSensorProxy(int port){
		this.port = port;
		
		try {
			socket = new Socket("localhost", port);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		point = new Point3f();
		theta = 0;
		
		terminate = false;
	}
	
	public void run(){
		while (!terminate){
			try {
				String line = reader.readLine();
				JSONObject obj = new JSONObject(line);
				setPosition(obj);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private synchronized void setPosition(JSONObject obj) {
		float x = (float) obj.getDouble("x");
		float y = (float) obj.getDouble("y");
		float z = (float) obj.getDouble("z");
		float pi = (float) obj.getDouble("pitch");
		float ya = (float) obj.getDouble("yaw");
		float ro = (float) obj.getDouble("roll");
		
		point = new Point3f(x,y,0);
		theta = ya;
	}

	public static void main (String[] args){
		new PosSensorProxy(60005).start();
	}

	public synchronized Point3f getPosition() {
		
		return point;
	}

	public synchronized float getOrientation() {
		return theta;
	}
}
