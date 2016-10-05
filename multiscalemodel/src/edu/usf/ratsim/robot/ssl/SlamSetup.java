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

		while (slamState.getTrackingState() <= 3)
		{
			pilot.circleLeftConcave();
			 try {
                                       Thread.sleep(100);
                               } catch (InterruptedException e) {
                                       // TODO Auto-generated catch block
                                       e.printStackTrace();
                               }
		}

	
	}
	
	// After initialization, move in a large circle to recognize 
	// features in all directions	
		// After initialization, move in a large circle to recognize 
	// features in all directions	
	void map()
	{		
		if(slamState.getTrackingState() < 3)
		{
			
			if(need_sleep){
				try {
                                        Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                }
				need_sleep = false;
			}

			if(irReader.somethingClose())
			{
				pilot.left();
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				pilot.circleLeft();
			}			
		}
		else
		{
			// If we lose tracking, we move backwards
			pilot.circleRight();
			need_sleep = true;
		}	
	}
	
	public static void main(String[] args)
	{
		SlamSetup ss = new SlamSetup();
		while(true)
			ss.initialize();
		/*while(true)
		{
			ss.map();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}
}
