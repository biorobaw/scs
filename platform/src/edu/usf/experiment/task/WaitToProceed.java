package edu.usf.experiment.task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to wait a remote connection before proceeding with the experiment
 * 
 * @author ludo
 *
 */
public class WaitToProceed extends Task {

	private int port = 15678;

	public WaitToProceed(ElementWrapper params) {
		super(params);
	}

	public void perform(Universe u, Subject s){
		boolean gotit = false;
		System.out.println("[+] Waiting for connection to continue");
		while (!gotit)
			try {
				ServerSocket sock = new ServerSocket(port);
				sock.accept();
				gotit = true;
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		new Socket("odroid", 15678);
	}

}
