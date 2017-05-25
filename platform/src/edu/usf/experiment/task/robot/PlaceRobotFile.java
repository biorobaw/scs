package edu.usf.experiment.task.robot;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import javax.vecmath.Point4f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.MovableRobotUniverse;
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
		String[][] strPoints = CSVReader.loadCSV(file, ",", "Place robot file not found");
		for (String[] s : strPoints){
			positions.add(new Point4f(Float.parseFloat(s[0]),Float.parseFloat(s[1]),0,0));
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

	public void perform(Universe u, Subject s){
		if (!(u instanceof MovableRobotUniverse))
			throw new IllegalArgumentException("");
		
		MovableRobotUniverse mru = (MovableRobotUniverse) u;
		
		Point4f p = positions.get(nextPos);
		mru.setRobotPosition(new Point2D.Float(p.x, p.y), p.w);
		if (nextPos+1 < positions.size()) nextPos++;
		else Globals.getInstance().put("done", true);
		
	}

}
