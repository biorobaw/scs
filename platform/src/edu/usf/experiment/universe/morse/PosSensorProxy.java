package edu.usf.experiment.universe.morse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;

public class PosSensorProxy extends Thread {

	private int port;
	private Socket socket;
	private BufferedReader reader;
	private boolean terminate;
	private Coordinate point;
	private float theta;
	private boolean posFresh;
	private boolean rotFresh;

	public PosSensorProxy(int port){
		this.port = port;
		
		try {
			socket = new Socket("localhost", port);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		point = new Coordinate();
		theta = 0;
		
		terminate = false;
		posFresh = false;
		rotFresh = false;
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
		
		point = new Coordinate(x,y,0);
		theta = ya;
		
		posFresh = true;
		rotFresh = true;
		notifyAll();
	}

	public static void main (String[] args){
		new PosSensorProxy(60005).start();
	}

	public synchronized Coordinate getPosition() {
		while (!posFresh)
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		posFresh = false;
		return point;
	}

	public synchronized float getOrientation() {
		while (!rotFresh)
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		rotFresh = false;
		return theta;
	}
}
