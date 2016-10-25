package edu.usf.experiment.universe.morse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.vecmath.Point3f;

import org.json.JSONArray;
import org.json.JSONObject;

public class IRSensorProxy extends Thread {

	private int port;
	private Socket socket;
	private BufferedReader reader;
	private boolean terminate;
	private float distance;
	private boolean distFresh;

	public IRSensorProxy(int port){
		this.port = port;
		
		try {
			socket = new Socket("localhost", port);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		distance = 0;
		
		terminate = false;
		distFresh = false;
	}
	
	public void run(){
		while (!terminate){
			try {
				String line = reader.readLine();
				JSONObject obj = new JSONObject(line);
				setDistnace(obj);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void setDistnace(JSONObject obj) {
		JSONArray ranges = obj.getJSONArray("range_list");
		float minDist = Float.MAX_VALUE;
		for (Object range : ranges){
			float val = ((Double)range).floatValue();
			if (val < minDist)
				minDist = val;
		}
		distance = minDist;
		distFresh = true;
		notifyAll();
	}

	public synchronized float getDistance() {
		while(!distFresh)
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		distFresh = false;
		
		return distance;
	}
	

	public static void main (String[] args){
		new IRSensorProxy(60005).start();
	}
}
