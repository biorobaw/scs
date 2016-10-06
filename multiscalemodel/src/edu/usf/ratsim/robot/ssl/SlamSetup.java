package edu.usf.ratsim.robot.ssl;
import edu.usf.ratsim.robot.ssl.slam.SlamStateProxy;

public class SlamSetup {
	private SSLPilot pilot;
	private IRReader irReader;
	private SlamStateProxy slamState;
	long strafe_start_time;
	boolean need_sleep;
	public SlamSetup()
	{
		pilot = new SSLPilot();
	    	irReader = new IRReader();
	    	irReader.start();
	    	slamState = new SlamStateProxy();
	    	slamState.start();
		strafe_start_time = System.currentTimeMillis();
	}
	
	// Strafe side to side while keeping the camera focused on a point
	// in order to initialize from parallax
	 
    public void initialize()
	{
		/*while(slamState.getTrackingState() <= 1)
		{
			if (System.currentTimeMillis() - strafe_start_time < 4000)
				pilot.strafeRight();							
			
			else if (System.currentTimeMillis() - strafe_start_time < 8000)
				pilot.strafeLeft();

			else
				strafe_start_time = System.currentTimeMillis(); 
		}*/

		for(long i = 0; i < 1800; ++i)
		{
			pilot.circleLeftConcave();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Stay still for a bit to signal initialization
		pilot.still();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// After initialization, move in a large circle to recognize 
	// features in all directions	
		// After initialization, move in a large circle to recognize 
	// features in all directions	
	void map()
	{		
		// Wall Follow
		for(int i = 0; i < 3000; ++i)
		{
			if(irReader.somethingClose())
				pilot.left();
			else
				pilot.forward();

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
		SlamSetup ss = new SlamSetup();
		ss.initialize();
		ss.map();
	}
}
