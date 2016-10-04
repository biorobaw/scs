package edu.usf.ratsim.robot.ssl;
import edu.usf.ratsim.robot.ssl.slam.SlamStateProxy;

public class SlamSetup {
	private SSLPilot pilot;
	private IRReader irReader;
	private SlamStateProxy slamState;
	
	public SlamSetup()
	{
		pilot = new SSLPilot();
    	irReader = new IRReader();
    	slamState = new SlamStateProxy();
    	slamState.start();
	}
	
	// Strafe side to side while keeping the camera focused on a point
	// in order to initialize from parallax
    public void initialize()
	{
		while(slamState.getTrackingState() <= 1){
			// Go right for a few seconds
			pilot.strafeRight();
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Go left for a few seconds
			pilot.strafeLeft();
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		pilot.still();
	}
	
	// After initialization, move in a large circle to recognize 
	// features in all directions	
	void map()
	{		
		// While we are tracking, we circle the maze
		while(slamState.getTrackingState() < 3)
		{
			while(irReader.somethingClose())
				pilot.left();
			
			pilot.forward();
		}
		
		// If we lose tracking, we move backwards
		pilot.backward();
	}
	
	public static void main(String[] args)
	{
		SlamSetup ss = new SlamSetup();
		ss.initialize();
		while(true)
			ss.map();
	}
}
