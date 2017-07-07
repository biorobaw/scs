package edu.usf.experiment.task.robot;

import java.util.LinkedList;

import edu.usf.experiment.Globals;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.MovableRobotUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.CSVReader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RigidTransformation;

public class PlaceRobotFile extends Task {
	
	private LinkedList<RigidTransformation> positions = new LinkedList<RigidTransformation>();
	int nextPos = 0;
	static private int deltaId = 40;

	public PlaceRobotFile(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
		
		String file = params.getChild("filename").getText();
		String[][] strPoints = CSVReader.loadCSV(file, ",", "Place robot file not found");
		for (String[] s : strPoints){
			positions.add(new RigidTransformation(Float.parseFloat(s[0]),Float.parseFloat(s[1]),0f));
		}
		
		
//		for(int i=0;i<positions.size();i++)
//		{
//			if (i+deltaId < positions.size())
//			{
//				RigidTransformation p1 = positions.get(i);
//				RigidTransformation p2 = positions.get(i+deltaId);
		// TODO: recover this code, what is the following line?
//				p1.w = (float)Math.atan2(p2.y-p1.y, p2.x-p1.x);
//			}else{
//				int minPos = positions.size()-1-deltaId < 0 ? 0 : positions.size()-1-deltaId;
//				Point4f p1 = positions.get(minPos);
//				Point4f p2 = positions.get(positions.size()-1);
//				p2.w = (float)Math.atan2(p2.y-p1.y, p2.x-p1.x);
//			}
//		}
//		
		//XMLUtils.parsePoint(p


		
		
	}

	public void perform(Universe u, Subject s){
		if (!(u instanceof MovableRobotUniverse))
			throw new IllegalArgumentException("");
		
		MovableRobotUniverse mru = (MovableRobotUniverse) u;
		
		RigidTransformation p = positions.get(nextPos);
		mru.setRobotPosition(p.getTranslation());
		mru.setRobotOrientation(p.getRotation());
		if (nextPos+1 < positions.size()) nextPos++;
		else Globals.getInstance().put("done", true);
		
	}

}
