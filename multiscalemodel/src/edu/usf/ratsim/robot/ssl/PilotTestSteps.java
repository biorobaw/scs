package edu.usf.ratsim.robot.ssl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class PilotTestSteps extends Thread{

	private SSLPilot pilot;
	private IRReader irReader;
	private Random r;
	private boolean stop;
	
	public PilotTestSteps(){
		pilot = new SSLPilot();
		irReader = new IRReader();
		irReader.start();
		r = new Random();
		stop = false;
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		char state;	
		if (!irReader.somethingClose()){
			state = 'f';
			pilot.forward();
		} else {
			state = 't';
			pilot.left();
		} 
	
		
		while (!stop){
			switch (state){
			case 'f':
				if (irReader.somethingClose()){
					state = 't';
					if (r.nextBoolean())
						while(irReader.somethingClose()){
							pilot.stepLeft();
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					else
						while(irReader.somethingClose()){
							pilot.stepRight();
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
				} else {
					pilot.forward();
				}
				break;
			case 't':
				if (!irReader.somethingClose()){
					state = 'f';
					pilot.forward();
				}
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		pilot.still();
		
	}

	public void setStop(boolean val){
		stop = val;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		PilotTestSteps pt = new PilotTestSteps();
		System.out.println("Starting pilot");
		pt.start();

	        System.out.print("Press enter to finish");
		br.readLine();
		pt.setStop(true);
	
		pt.join();	


		System.exit(0);
	}

}
