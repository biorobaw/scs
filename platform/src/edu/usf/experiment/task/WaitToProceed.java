package edu.usf.experiment.task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
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

	@Override
	public void perform(Experiment experiment) {
		perform(experiment.getUniverse());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getUniverse());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getUniverse());
	}

	private void perform(Universe u) {
		boolean gotit = false;
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
		new Socket("localhost", 15678);
	}

}
