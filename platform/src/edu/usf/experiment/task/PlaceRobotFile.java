package edu.usf.experiment.task;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import javax.vecmath.Point4f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.CSVReader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.XMLUtils;

public class PlaceRobotFile extends Task {
	
	private LinkedList<Point4f> positions = new LinkedList<Point4f>();
	int nextPos = 0;

	public PlaceRobotFile(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
		
		String file = params.getChild("filename").getText();
		String[][] strPoints = CSVReader.loadCSV(file, ",", "Place robot file not found");
		for (String[] s : strPoints){
			positions.push(new Point4f(Float.parseFloat(s[0]),Float.parseFloat(s[1]),0,0));
		}
		
		for(int i=0;i<positions.size()-1;i++)
		{
			Point4f p1 = positions.get(i);
			Point4f p2 = positions.get(i+1);
			p1.w = (float)Math.atan2(p2.y-p1.y, p2.x-p1.x);
		}
		Point4f p1 = positions.get(positions.size()-2);
		Point4f p2 = positions.get(positions.size()-1);
		p2.w = (float)Math.atan2(p2.y-p1.y, p2.x-p1.x);
		
		//XMLUtils.parsePoint(p


		
		
	}

	@Override
	public void perform(Experiment experiment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void perform(Trial trial) {
		// TODO Auto-generated method stub

	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getUniverse());
	}

	private void perform(Universe universe) {
		Point4f p = positions.get(nextPos);
		universe.setRobotPosition(new Point2D.Float(p.x, p.y), p.w);
		if (nextPos+1 < positions.size()) nextPos++;
		else Globals.getInstace().put("done", true);
		
	}

}
