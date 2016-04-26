package edu.usf.experiment.task;

import java.awt.geom.Point2D;

import javax.vecmath.Point4f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.CSVReader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.XMLUtils;

public class PlaceRobotInitally extends Task{

	private Point4f initPos;
	static private int deltaId = 40;

	public PlaceRobotInitally(ElementWrapper params) {
		super(params);
		
		ElementWrapper e;
		if ((e=params.getChild("point"))!=null)
			initPos = XMLUtils.parsePoint(e);
		else if ((e=params.getChild("pointXYW"))!=null)
		{
			String[] coords = e.getText().split(",");
			float x = Float.parseFloat(coords[0]);
			float y = Float.parseFloat(coords[1]);
			float w = Float.parseFloat(coords[2]);
			initPos = new Point4f(x,y,0,w);
		}
		else if ((e=params.getChild("filename"))!=null)
		{
			String[][] strPoints = CSVReader.loadCSV(e.getText(), ",", "Place robot file not found");
			float x,y,w;			
			x = Float.parseFloat(strPoints[0][0]);
			y = Float.parseFloat(strPoints[0][1]);
			if(strPoints[0].length > 2)
				w =  Float.parseFloat(strPoints[0][2]);
			else{
				int otherPoint = strPoints.length >= deltaId ? deltaId : strPoints.length; 
				float x2 = Float.parseFloat(strPoints[otherPoint][0]);
				float y2= Float.parseFloat(strPoints[otherPoint][1]);
				w = (float)Math.atan2(y2-y, x2-x);
			}
			initPos = new Point4f(x,y,0,w);
			
		}
		else System.out.println("ERROR: Start position not specified");

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
