package edu.usf.ratsim.robot.ssl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class WallFollowTest extends Thread {

	private SSLPilot pilot;
	private IRReader irReader;
	private boolean stop;
	private float wallP, frontP, fwP;

	public WallFollowTest(float wallP, float frontP, float fwP){
		pilot = new SSLPilot();
		irReader = new IRReader();
		irReader.start();
		stop = false;

		this.wallP = wallP;
		this.frontP = frontP;
		this.fwP = fwP;
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		int toTurn = 0;
		Random r = new Random();
		while (!stop){
			
			// Get sensor readings from IR
			// Do proportional control, maybe PI
			float leftM = irReader.getLeftIR();
			float frontM = irReader.getFrontIR();
			float rightM = irReader.getRightIR();
	
			if ( (rightM < 250 || r.nextFloat() < .05) && toTurn == 0)
				toTurn = 15;

			if (toTurn > 0 )
			{
				pilot.sendVels(10,-1,-10,1);
				toTurn--;
			} else {

				// Errors
				float leftE = leftM - 150;
				float frontE = frontM - 250;
				float fwE = rightM - 200;
				// Vel = fwVel + p . err
				// Also, control power limited to 5
				int wallG = (int) Math.round(Math.max(-10, Math.min(10, wallP * leftE)));
				int frontG = (int) Math.round(Math.max(-10, Math.min(10, frontP * frontE)));
				int fwG = (int) Math.round(Math.max(-10, Math.min(10, fwP * fwE)));
				// Send command
				pilot.sendVels(fwG - frontG,  wallG,  wallG, fwG + frontG);
							
		
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
		float wallP = Float.parseFloat(args[0]);
		float frontP = Float.parseFloat(args[1]);
		float fwP = Float.parseFloat(args[2]);
		WallFollowTest wft = new WallFollowTest(wallP, frontP, fwP);
		System.out.println("Starting wall follow");
		wft.start();

	        System.out.print("Press enter to finish");
		br.readLine();
		wft.setStop(true);
	
		wft.join();	


		System.exit(0);
	}

}
