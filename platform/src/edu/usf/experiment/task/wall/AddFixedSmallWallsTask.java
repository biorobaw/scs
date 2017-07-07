package edu.usf.experiment.task.wall;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.RobotLoader;
import edu.usf.experiment.subject.ModelLoader;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.universe.wall.WallUniverse;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.XMLExperimentParser;
import edu.usf.micronsl.Model;

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

	public void perform(Universe u, Subject s) {
		if (!(u instanceof WallUniverse))
			throw new IllegalArgumentException("");
		
		WallUniverse wu = (WallUniverse) u;
		
//		addOuterWall(Math.PI/8, false, wu);
//		addOuterWall(3*Math.PI/8, true, wu);
//		addOuterWall(7*Math.PI/8, false, wu);
//		addOuterWall(11*Math.PI/8, true, wu);
//		addOuterWall(13*Math.PI/8, false, wu);
//		addOuterWall(15*Math.PI/8, true, wu);
//		
//		addInnerWall(-.25,.25, Math.PI/8, wu);
//		addInnerWall(-.1,.2, 12*Math.PI/8, wu);
//		addInnerWall(0.05,0.05, 3*Math.PI/8, wu);
//		addInnerWall(-.3,-.1, 5*Math.PI/8, wu);
//		addInnerWall(-.1,-.2, 0*Math.PI/8, wu);
//		addInnerWall(.25,.05, 0*Math.PI/8, wu);
//		addInnerWall(-.25,-.1, Math.PI/8, wu);
//		addInnerWall(.25,-.3, 6*Math.PI/16, wu);
//		addInnerWall(.2,.25,30*Math.PI/16, wu);
//		addInnerWall(.0,.3, 0*Math.PI/8, wu);
		
		wu.addWall(-0.19134194f,0.4619397f,0.19134143f,0.46193987f);
		wu.addWall(-0.088414215f,0.43102545f,-0.06329654f,0.30857503f);
		wu.addWall(-0.15002857f,-0.413632f,-0.06478506f,-0.17861381f);
		wu.addWall(0.4105273f,0.1583267f,0.17727314f,0.068368345f);
		wu.addWall(0.43567467f,-0.06154343f,0.31190345f,-0.0440595f);
		wu.addWall(0.0909464f,0.43049827f,0.06510935f,0.30819762f);
		wu.addWall(-0.38670117f,0.20991f,-0.16698459f,0.09064296f);
		wu.addWall(0.19369878f,-0.3950706f,0.083642654f,-0.17059867f);
		wu.addWall(-0.17237598f,-0.123725295f,-0.13085988f,-0.005821039f);
		wu.addWall(0.059495747f,0.075841784f,0.038606096f,0.19908391f);
		wu.addWall(0.1622448f,-0.08624524f,0.25793806f,-0.16666788f);
		wu.addWall(-0.2710842f,0.00835216f,-0.29687265f,-0.11395874f);
		wu.addWall(-0.043139875f,0.07127708f,-0.08298153f,0.18975765f);
	}

	private void addInnerWall(double x, double y, double angle, WallUniverse wu) {
		double x2, y2;
		x2 = x + LENGTH * Math.cos(angle);
		y2 = y + LENGTH * Math.sin(angle);
		
		LineSegment wall = new LineSegment(new Coordinate(x, y),
				new Coordinate(x2, y2));
		wu.addWall(wall);
	}

	private void addOuterWall(double d, boolean doubleWall, WallUniverse wu) {
		Coordinate outerPoint = new Coordinate();
		outerPoint.x = (float) (Math.cos(d) * NEAR_WALL_RADIUS);
		outerPoint.y = (float) (Math.sin(d) * NEAR_WALL_RADIUS);
		
		float length = LENGTH;
		if (doubleWall)
			length *= 2;
		
		Coordinate innerPoint = new Coordinate();
		innerPoint.x = (float) (Math.cos(d) * (NEAR_WALL_RADIUS - length));
		innerPoint.y = (float) (Math.sin(d) * (NEAR_WALL_RADIUS - length));
		
		LineSegment wall = new LineSegment(new Coordinate(outerPoint.x, outerPoint.y),
				new Coordinate(innerPoint.x, innerPoint.y));
		wu.addWall(wall);
	}

	public static void main(String[] args) {
//		for (int i = 0; i < 10000; i++){
			ElementWrapper root = XMLExperimentParser
					.loadRoot("src/edu/usf/ratsim/experiment/xml/multiFeedersTrainRecallSmallObs.xml");
			WallUniverse wu = (WallUniverse) UniverseLoader.getInstance().load(root, ".");
			Robot robot = RobotLoader.getInstance().load(root);
			Model model = ModelLoader.getInstance().load(root.getChild("model"), robot);
			new AddFixedSmallWallsTask(null).perform(wu, new Subject("sub", "group", model, robot));
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
