package edu.usf.experiment.task;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point2f;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.RobotLoader;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.SubjectLoader;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.experiment.utils.XMLExperimentParser;

public class AddFixedSmallWallsTask extends Task {

	private final float RADIUS = .40f;
	private static final float MIN_DIST_TO_FEEDERS = 0.05f;
	private static final float LENGTH = .125f;
	private static final int NUM_WALLS = 16;
	private static final float NEAR_WALL_RADIUS = .49f;
	private static final float DISTANCE_INTERIOR_WALLS = .05f;
	private static final float MIN_DIST_TO_FEEDERS_INTERIOR = 0.1f;
	private static final double NUM_INTERIOR_WALLS = 6;

	public AddFixedSmallWallsTask(ElementWrapper params) {
		super(params);

	}

	@Override
	public void perform(Experiment experiment) {
		perform(experiment.getUniverse(), experiment.getSubject());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getUniverse(), trial.getSubject());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getUniverse(), episode.getSubject());
	}

	private void perform(Universe univ, Subject sub) {
		addOuterWall(Math.PI/8, false, univ);
		addOuterWall(3*Math.PI/8, true, univ);
		addOuterWall(7*Math.PI/8, false, univ);
		addOuterWall(11*Math.PI/8, true, univ);
		addOuterWall(13*Math.PI/8, false, univ);
		addOuterWall(15*Math.PI/8, true, univ);
		
		addInnerWall(-.25,.25, Math.PI/8, univ);
		addInnerWall(-.1,.2, 12*Math.PI/8, univ);
		addInnerWall(0.05,0.05, 3*Math.PI/8, univ);
		addInnerWall(-.3,-.1, 5*Math.PI/8, univ);
		addInnerWall(-.1,-.2, 0*Math.PI/8, univ);
		addInnerWall(.25,.05, 0*Math.PI/8, univ);
		addInnerWall(-.25,-.1, Math.PI/8, univ);
		addInnerWall(.25,-.3, 6*Math.PI/16, univ);
		addInnerWall(.2,.25,30*Math.PI/16, univ);
		addInnerWall(.0,.3, 0*Math.PI/8, univ);
		
	}

	private void addInnerWall(double x, double y, double angle, Universe univ) {
		double x2, y2;
		x2 = x + LENGTH * Math.cos(angle);
		y2 = y + LENGTH * Math.sin(angle);
		
		LineSegment wall = new LineSegment(new Coordinate(x, y),
				new Coordinate(x2, y2));
		univ.addWall(wall);
	}

	private void addOuterWall(double d, boolean doubleWall, Universe univ) {
		Point2f outerPoint = new Point2f();
		outerPoint.x = (float) (Math.cos(d) * NEAR_WALL_RADIUS);
		outerPoint.y = (float) (Math.sin(d) * NEAR_WALL_RADIUS);
		
		float length = LENGTH;
		if (doubleWall)
			length *= 2;
		
		Point2f innerPoint = new Point2f();
		innerPoint.x = (float) (Math.cos(d) * (NEAR_WALL_RADIUS - length));
		innerPoint.y = (float) (Math.sin(d) * (NEAR_WALL_RADIUS - length));
		
		LineSegment wall = new LineSegment(new Coordinate(outerPoint.x, outerPoint.y),
				new Coordinate(innerPoint.x, innerPoint.y));
		univ.addWall(wall);
	}

	public static void main(String[] args) {
//		for (int i = 0; i < 10000; i++){
			ElementWrapper root = XMLExperimentParser
					.loadRoot("src/edu/usf/ratsim/experiment/xml/multiFeedersTrainRecallSmallObs.xml");
			Universe univ = UniverseLoader.getInstance().load(root, ".");
			Robot robot = RobotLoader.getInstance().load(root);
			Subject subject = SubjectLoader.getInstance().load("a", "a",
					root.getChild("model"), robot);
			new AddFixedSmallWallsTask(null).perform(univ, subject);
			System.out.println("walls added");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//		}
//		System.exit(0);
	}
}
