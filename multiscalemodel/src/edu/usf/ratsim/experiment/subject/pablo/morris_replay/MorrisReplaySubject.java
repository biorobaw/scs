package edu.usf.ratsim.experiment.subject.pablo.morris_replay;

import java.util.List;
import java.util.Map;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.experiment.subject.interfaces.ActivityLoggerSubject;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import platform.simulatorVirtual.robots.PuckRobot;


public class MorrisReplaySubject extends Subject implements ActivityLoggerSubject {

	public float step;
	public float leftAngle;
	public float rightAngle;
	
	private MorrisReplayModel model;
	
	
	//path matrix for replay:
	public Float2dSparsePort WTable;
	
	
	public PuckRobot robot;

	public MorrisReplaySubject(String name, String group,
			ElementWrapper params, Robot robot) {
		super(name, group, params, robot);

		this.robot = (PuckRobot)robot;
		
		
		model = new MorrisReplayModel(params, this, this.robot);
	}
	
	@Override
	public void stepCycle() {
		model.simRun();
		
		VirtUniverse vu = VirtUniverse.getInstance();
		vu.render(false);
	}
	

	@Override
	public void newEpisode() {
		model.newEpisode();

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
