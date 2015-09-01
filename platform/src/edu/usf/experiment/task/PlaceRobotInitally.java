package edu.usf.experiment.task;

import java.awt.geom.Point2D;

import javax.vecmath.Point4f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.XMLUtils;

public class PlaceRobotInitally extends Task{

	private Point4f initPos;

	public PlaceRobotInitally(ElementWrapper params) {
		super(params);
		initPos = XMLUtils.parsePoint(params.getChild("point"));
	}

	@Override
	public void perform(Experiment experiment) {
		
	}

	@Override
	public void perform(Trial trial) {
		
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getUniverse());
	}

	private void perform(Universe universe) {
		universe.setRobotPosition(new Point2D.Float(initPos.x, initPos.y), initPos.w);
	}

}
