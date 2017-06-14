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

public class PlaceRobotFile extends Task {
	
	private LinkedList<Point4f> positions = new LinkedList<Point4f>();
	int nextPos = 0;
	static private int deltaId = 40;

	public PlaceRobotFile(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
		
		String file = params.getChild("filename").getText();
		System.out.println("FILE: "+file);
		String[][] strPoints = CSVReader.loadCSV(file, ",", "Place robot file not found");
		for (String[] s : strPoints){
			Float x = Float.parseFloat(s[0]);
			Float y = Float.parseFloat(s[1]);
			System.out.println("coord: "+x+ " "+y);
			positions.add(new Point4f(x,y,0,0));
		}
		
		for(int i=0;i<positions.size();i++)
		{
			if (i+deltaId < positions.size())
			{
				Point4f p1 = positions.get(i);
				Point4f p2 = positions.get(i+deltaId);
				p1.w = (float)Math.atan2(p2.y-p1.y, p2.x-p1.x);
			}else{
				int minPos = positions.size()-1-deltaId < 0 ? 0 : positions.size()-1-deltaId;
				Point4f p1 = positions.get(minPos);
				Point4f p2 = positions.get(positions.size()-1);
				p2.w = (float)Math.atan2(p2.y-p1.y, p2.x-p1.x);
			}
		}
		
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
		System.out.println("set to: "+p.x+" "+p.y+ " "+p.w);
		universe.setRobotPosition(new Point2D.Float(p.x, p.y), p.w);
		if (nextPos+1 < positions.size()) nextPos++;
		else Globals.getInstance().put("done", true);
		
	}

}
