package edu.usf.ratsim.robot.ssl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class VisionListener extends Thread {

	private static VisionListener vl = null;


	private ServerSocket proxySocket;

	private PrintWriter outStream;

	private VisionListener() {
		try {
			proxySocket = new ServerSocket(63111);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		start();
	}

	@Override
	public void run() {
		while (true) {
			Socket clientSocket;
			try {
				clientSocket = proxySocket.accept();
				new VisionListenerHandler(clientSocket).start();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}

	public static void main(String[] args) {
		getVisionListener();
	}

	private static VisionListener getVisionListener() {
		if (vl == null)
			vl  = new VisionListener();
		
		return vl;
	}
}
