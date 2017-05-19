package edu.usf.ratsim.robot.ssl;
import java.util.Random;

import edu.usf.ratsim.robot.ssl.slam.SlamStateProxy;

public class SlamSetup {
	private SSLPilot pilot;
	private IRReader irReader;
	private SlamStateProxy slamState;
	long strafe_start_time;
	boolean need_sleep;
	
	private float frontP = .05f, wallP = .1f, fwP = .05f;
	
	public SlamSetup(SSLPilot p, IRReader r)
	{
		pilot = p;
    	irReader = r;
    	//slamState = new SlamStateProxy();
    	//slamState.start();
		strafe_start_time = System.currentTimeMillis();
	}
	
    public void initialize()
	{
		int toTurn = 0;
		Random r = new Random();
		for (int i = 0; i < 10 * 60 * 5; i++){		
			// Get sensor readings from IR
			// Do proportional control, maybe PI
			float leftM = irReader.getLeftIR();
			float frontM = irReader.getFrontIR();
			float rightM = irReader.getRightIR();
	
			if (irReader.somethingReallyClose()){
				toTurn = 0;
				pilot.right();
			} else {
			
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
	
	public static void main(String[] args)
	{
        IRReader irr = new IRReader();
        irr.start();
		SlamSetup ss = new SlamSetup(new SSLPilot(),irr);
		ss.initialize();
	}
}
