package edu.usf.ratsim.robot.ssl;

public class SlamSetup {
	private SSLPilot pilot;
	private IRReader irReader;	
	
	// Strafe side to side while keeping the camera focused on a point
	// in order to initialize from parallax
	void initialize()
	{
		
		// Go right
		pilot.strafeRight();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		pilot.strafeLeft();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// After initialization, move in a large circle to recognize 
	// features in all directions	
	void map()
	{

		
		
	}
	
	
}
