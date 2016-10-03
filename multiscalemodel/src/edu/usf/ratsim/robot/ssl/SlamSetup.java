package edu.usf.ratsim.robot.ssl;
import edu.usf.ratsim.robot.ssl.slam.SlamStateProxy;

public class SlamSetup {
	private SSLPilot pilot;
	private IRReader irReader;
	private SlamStateProxy slamState;
	
	// Strafe side to side while keeping the camera focused on a point
	// in order to initialize from parallax
    public void initialize()
	{
		// Get slam state
    	pilot = new SSLPilot();
    	irReader = new IRReader();
    	slamState = new SlamStateProxy();
    	slamState.start();
    			
		while(slamState.getTrackingState() <= 1){
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
	}
	
	// After initialization, move in a large circle to recognize 
	// features in all directions	
	void map()
	{		
		
	}
	
	public static void main(String[] args)
	{
		SlamSetup ss = new SlamSetup();
		ss.initialize();		
	}
}
