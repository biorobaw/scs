package com.github.biorobaw.scs.experiment.task.episode.robot;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.simulation.object.RobotProxy;
import com.github.biorobaw.scs.simulation.scripts.Script;
import com.github.biorobaw.scs.utils.CSVReader;
import com.github.biorobaw.scs.utils.Debug;
import com.github.biorobaw.scs.utils.XML;


public class PlaceRobotInitially implements Script {

	private Vector3D initPos;
	private float initOrientation;
	private RobotProxy robot;
	static private int deltaId = 40;

	public PlaceRobotInitially(XML xml) {
		
		float[] xyw = null;
		if (xml.hasAttribute("pointXYW"))
		{
			xyw = xml.getFloatArrayAttribute("pointXYW");
		}
		else if (xml.hasAttribute("file"))
		{
			String[][] strPoints = CSVReader.loadCSV(xml.getAttribute("file"), ",", "Place robot file not found");
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
			xyw = new float[] {x,y,w};
			
		}
		else {
			System.err.println("ERROR: Start position not specified");
			System.exit(-1);
		}
		
		initPos = new Vector3D(xyw[0],xyw[1],0);
		initOrientation = xyw[2];
		robot = Experiment.get()
						  .subjects
						  .get(xml.getAttribute("robot_id"))
						  .getRobot()
						  .getRobotProxy();

	}

	
	@Override
	public void newEpisode() {
		robot.setPosition(initPos);
		robot.setOrientation2D(initOrientation);
	}
	

	

}
