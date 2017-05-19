package edu.usf.ratsim.robot.ssl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class PilotTest extends Thread{

	private SSLPilot pilot;
	private IRReader irReader;
	private Random r;
	private boolean stop;
	
	public PilotTest(){
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
		char state = 'f';
	
		// Two state machine. Have to reissue cmds every time due to timeout in teensy
		// THANKS A LOT SHAMSI!!!!
		boolean turningLeft = false;
		while (!stop){
			switch (state){
			case 'f':
				if (irReader.somethingClose()){
					state = 't';
					if (irReader.getRightIR() < 200){
						turningLeft = true;
						pilot.circleLeft();
					} else {
						turningLeft = false;
						pilot.circleRight();
					}
				} else {
					pilot.forward();
				}
				break;
			case 't':
				if (!irReader.somethingClose()){
					state = 'f';
					pilot.forward();
				} else {
					if (turningLeft)
						pilot.circleLeft();
					else
						pilot.circleRight();
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
		PilotTest pt = new PilotTest();
		System.out.println("Starting pilot");
		pt.start();

	        System.out.print("Press enter to finish");
		br.readLine();
		pt.setStop(true);
	
		pt.join();	


		System.exit(0);
	}

}
