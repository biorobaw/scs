package edu.usf.experiment.task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to halt execution based on a socket connection. Each socket connection halts or allows execution.
 * 
 * @author ludo
 *
 */
public class CheckHalt extends Task implements Runnable {

	private static int port = 15679;
	private static boolean cont;
	private static Thread listenerThread = null;

	public CheckHalt(ElementWrapper params) {
		super(params);
		
		// Let processes continue by default
		cont = true;
		
		synchronized (CheckHalt.class) {
			if (listenerThread == null){
				listenerThread = new Thread(this);
				listenerThread.start();
			}
		}
		
	}

	@Override
	public void perform(Experiment experiment) {
		perform();
	}

	@Override
	public void perform(Trial trial) {
		perform();
	}

	@Override
	public void perform(Episode episode) {
		perform();
	}

	private void perform() {
		while (!canContinue()){
			try	 {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}
	
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		new Socket("odroid", 15679);
	}

	@Override
	public void run() {
		
		while (true){
			System.out.println("[+] Waiting for connection to halt");
			try {
				ServerSocket sock = new ServerSocket(port);
				sock.accept();
				flipCont();
				sock.close();
			} catch (IOException e) {
				
				System.err.println("Error trying to accept connection");
				e.printStackTrace();
				System.exit(1);
			}
		}
		
	}

	private synchronized void flipCont() {
		cont = !cont;
	}
	
	private synchronized boolean canContinue() {
		return cont;
	}


}
