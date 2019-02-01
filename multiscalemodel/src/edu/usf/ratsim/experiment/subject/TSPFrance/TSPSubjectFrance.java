package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.experiment.subject.interfaces.ActivityLoggerSubject;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import platform.simulatorVirtual.robots.PuckRobot;


public class TSPSubjectFrance extends Subject implements ActivityLoggerSubject {

	public float step;
	public float leftAngle;
	public float rightAngle;
	
	public TSPModelFrance model;
	
	public PuckRobot robot;

	static 
	{	
		try 
		{
			System.out.println("Loading DLLs from : " + System.getProperty("java.library.path"));
		
		 	InputStream input =  TSPSubjectFrance.class.getResourceAsStream("/resources/trn_dll.txt");
		 	BufferedReader br = new BufferedReader(new InputStreamReader(input));
		 	
		 	for(String line; (line = br.readLine()) != null; ) 
		 	{
		 		String shared_library = line.trim();
		 	 	if (!shared_library.isEmpty() && !shared_library.startsWith("#"))
		 	 	{
		 	 		System.out.println("Loading shared library '" + shared_library + "'");
		 	  		System.loadLibrary(shared_library);
		 	 	}
		 	}
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}	

	
	public TSPSubjectFrance(String name, String group,
			ElementWrapper params, Robot robot) {
		super(name, group, params, robot);

		this.robot = (PuckRobot)robot;
		
		
		model = new TSPModelFrance(params, this, this.robot);
	}
	
	@Override
	public void stepCycle() {
		model.simRun();
//		setHasEaten(false);
		VirtUniverse vu = VirtUniverse.getInstance();
		vu.render(true);
	}
	

	@Override
	public void newEpisode() {
		Globals.getInstance().put("done",false);
		model.newEpisode();
		this.clearTriedToEAt();
		this.setHasEaten(false);
	}

	@Override
	public void endEpisode() {
		// TODO Auto-generated method stub
		super.endEpisode();
		
		model.endEpisode();
		
	}
	
	@Override
	public void newTrial() {
		model.newTrial();
	}



	

	public List<PlaceCell> getPlaceCells() {
		return model.getPlaceCells();
	}


	public Map<Integer, Float> getPCActivity() {
		return model.getCellActivation();
	}

	

}
