package edu.usf.experiment.task.robot;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.MovableRobotUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.CSVReader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RigidTransformation;
import edu.usf.experiment.utils.XMLUtils;

public class PlaceRobotInitally extends Task{

	private RigidTransformation initPos;
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
			// TODO: verify this
			initPos = new RigidTransformation(x,y,w);
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
			// TODO: verify this
			initPos = new RigidTransformation(x,y,w);
			
		}
		else System.out.println("ERROR: Start position not specified");

	}

	public void perform(Universe u, Subject s){
		if (!(u instanceof MovableRobotUniverse))
			throw new IllegalArgumentException("");
		
		MovableRobotUniverse mru = (MovableRobotUniverse) u;
		
		mru.setRobotPosition(initPos.getTranslation());
		mru.setRobotOrientation(initPos.getRotation());
		
		
	}
	

}
