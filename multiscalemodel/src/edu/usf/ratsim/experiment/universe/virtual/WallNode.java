package edu.usf.ratsim.experiment.universe.virtual;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import edu.usf.experiment.universe.Wall;
import edu.usf.experiment.utils.ElementWrapper;

public class WallNode extends ExpUniverseNode {

	private static final float WALL_HEIGHT = 0.1f;
	final float RADIO = 0.005f;
	public float x1;
	public float y1;
	public float z1;
	public float x2;
	public float y2;
	public float z2;

	public LineSegment segment;

	public WallNode(ElementWrapper params) {

		Color3f color = new Color3f(1, 1, 1);
		x1 = params.getChildFloat("x1");
		y1 = params.getChildFloat("y1");
		z1 = params.getChildFloat("z1");
		x2 = params.getChildFloat("x2");
		y2 = params.getChildFloat("y2");
		z2 = params.getChildFloat("z2");
		float h = WALL_HEIGHT;

		float wallLength = new Point3f(x1, y1, z1).distance(new Point3f(x2, y2,
				z2));
		for (int cylinder = 0; cylinder < wallLength / RADIO; cylinder++) {
			float lambda = cylinder / (wallLength / RADIO);
			addChild(new CylinderNode(RADIO, h, color, x1 + (x2 - x1) * lambda,
					y1 + (y2 - y1) * lambda, z1 + (z2 - z1) * lambda));
		}

		segment = new LineSegment(new Coordinate(x1, y1),
				new Coordinate(x2, y2));

		setCapability(BranchGroup.ALLOW_DETACH);
	}

	public WallNode(float x1, float y1, float z1, float x2, float y2, float z2,
			float h) {
		Color3f color = new Color3f(1, 0, 0);

		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.z1 = z1;
		this.z2 = z2;

		float wallLength = new Point3f(x1, y1, z1).distance(new Point3f(x2, y2,
				z2));
		for (int cylinder = 0; cylinder < wallLength / RADIO; cylinder++) {
			float lambda = cylinder / (wallLength / RADIO);
			addChild(new CylinderNode(RADIO, h, color, x1 + (x2 - x1) * lambda,
					y1 + (y2 - y1) * lambda, z1 + (z2 - z1) * lambda));
		}

		segment = new LineSegment(new Coordinate(x1, y1),
				new Coordinate(x2, y2));

		setCapability(BranchGroup.ALLOW_DETACH);
	}

	public WallNode(Wall wn) {
		Color3f color = new Color3f(1, 0, 0);

		this.x1 = wn.getX1();
		this.x2 = wn.getX2();
		this.y1 = wn.getY1();
		this.y2 = wn.getY2();
		this.z1 = 0;
		this.z2 = 0;

		float wallLength = new Point3f(x1, y1, z1).distance(new Point3f(x2, y2,
				z2));
		for (int cylinder = 0; cylinder < wallLength / RADIO; cylinder++) {
			float lambda = cylinder / (wallLength / RADIO);
			addChild(new CylinderNode(RADIO, .1f, color, x1 + (x2 - x1)
					* lambda, y1 + (y2 - y1) * lambda, z1 + (z2 - z1) * lambda));
		}

		segment = new LineSegment(new Coordinate(x1, y1),
				new Coordinate(x2, y2));
		
		setCapability(BranchGroup.ALLOW_DETACH);
	}

	public WallNode(LineSegment wSegment, float h) {
		this((float) wSegment.p0.x, (float) wSegment.p0.y, 0,
				(float) wSegment.p1.x, (float) wSegment.p1.y, 0, h);
	}




}
