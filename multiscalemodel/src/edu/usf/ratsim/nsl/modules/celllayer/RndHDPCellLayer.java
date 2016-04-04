package edu.usf.ratsim.nsl.modules.celllayer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.ratsim.nsl.modules.cell.ConjCell;
import edu.usf.ratsim.nsl.modules.cell.ExponentialConjCell;
import edu.usf.ratsim.nsl.modules.cell.ExponentialHDCell;
import edu.usf.ratsim.nsl.modules.cell.ExponentialHDPC;
import edu.usf.ratsim.nsl.modules.cell.ExponentialWallConjCell;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;

public class RndHDPCellLayer extends Module {

	/**
	 * The layer's cells current activation
	 */
	public float[] activation;

	/**
	 * The list of place cells
	 */
	private List<ConjCell> cells;

	/**
	 * A pointer to the robot. This is used to get the robot's current location
	 */
	private LocalizableRobot robot;

	/**
	 * A copy of the random number generator
	 */
	private Random random;

	/**
	 * The bounding rectangle coordinates
	 */
	private float ymax;
	private float ymin;
	private float xmax;
	private float xmin;

	/**
	 * The proportion of cells that are located near a goal
	 */
	private float nearGoalProb;

	/**
	 * The list of goal locations
	 */
	private List<Feeder> goals;

	/**
	 * The physical length of the layer, used when simulating the effects of
	 * inactivation
	 */
	private float layerLength;

	/**
	 * The output port. A sparse port is used for efficiency sake.
	 */
	private Float1dSparsePortMap activationPort;

	/**
	 * Create all cells in the layer.
	 * 
	 * @param name
	 *            The module's name.
	 * @param robot
	 *            A robot able to provide localization information.
	 * @param placeRadius
	 *            The radius of the place cells.
	 * @param minDirectionRadius
	 *            The minimum radius for head direction modulation
	 * @param maxDirectionRadius
	 *            The maximum radius for head direction modulation
	 * @param numCells
	 *            The number of cells in the layer
	 * @param placeCellType
	 *            The type of place cell. ExponentialConjCell and
	 *            WallExponentialConjCell are supported.
	 * @param xmin
	 *            The minimum x value of the box in which place cells are
	 *            located
	 * @param ymin
	 *            The minimum y value of the box in which place cells are
	 *            located
	 * @param xmax
	 *            The maximum x value of the box in which place cells are
	 *            located
	 * @param ymax
	 *            The maximum y value of the box in which place cells are
	 *            located
	 * @param goals
	 *            The list of possible goals. This is used to locate place cells
	 *            near goals.
	 * @param nearGoalProb
	 *            The probability of a place cell being place near a goal
	 *            instead of a generic place
	 * @param layer
	 *            Length The physical length of the layer (mm), used when
	 *            simulating the effects of inactivation.
	 * @param wallInhibition
	 *            A parameter passed to wall modulated cells
	 *            (WallExponentialConjCell).
	 */
	public RndHDPCellLayer(String name, LocalizableRobot robot, float placeRadius, float minDirectionRadius,
			float maxDirectionRadius, int numCells, String placeCellType, float xmin, float ymin,
			float xmax, float ymax, List<Feeder> goals, float nearGoalProb, float layerLength) {
		super(name);

		if (!(placeCellType.equals("ExponentialHDPC"))) {
			System.err.println("Place cell type not implemented");
			System.exit(1);
		}

		this.goals = goals;
		this.nearGoalProb = nearGoalProb;
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.layerLength = layerLength;

		cells = new LinkedList<ConjCell>();
		random = RandomSingleton.getInstance();
		int i = 0;
		do {
			Point3f prefLocation;
			float preferredDirection;
			float directionRadius;

			// All cells have a preferred location
			prefLocation = createrPreferredLocation(nearGoalProb, goals, xmin, xmax, ymin, ymax);
			preferredDirection = (float) (random.nextFloat() * Math.PI * 2);
			// Using Inverse transform sampling to sample from k/x between
			// min and max
			// https://en.wikipedia.org/wiki/Inverse_transform_sampling. k =
			// 1/(ln (max) - ln(min)) due to normalization
			float k = (float) (1 / (Math.log(maxDirectionRadius) - Math.log(minDirectionRadius)));
			float s = random.nextFloat();
			directionRadius = (float) Math.exp(s / k + Math.log(minDirectionRadius));
			

			if (placeCellType.equals("ExponentialHDPC")) {
				cells.add(new ExponentialHDPC(prefLocation, preferredDirection, placeRadius, directionRadius));
			} 

			i++;
		} while (i < numCells);

		activationPort = new Float1dSparsePortMap(this, cells.size(), 1f/4000);
		addOutPort("activation", activationPort);

		this.robot = robot;
	}

	/**
	 * Generates a uniformly distributed location for a place cell.
	 * 
	 * A nearGoalProb proportion of cells are placed near goal positions
	 * instead.
	 * 
	 * @param nearGoalProb
	 *            The proportion of cells to be placed near goal positions.
	 * @param goals
	 *            The list of possible goals.
	 * @param xmin
	 *            The minimum x value of the box in which place cells are
	 *            located
	 * @param ymin
	 *            The minimum y value of the box in which place cells are
	 *            located
	 * @param xmax
	 *            The maximum x value of the box in which place cells are
	 *            located
	 * @param ymax
	 *            The maximum y value of the box in which place cells are
	 *            located
	 * @return The location of the new cell.
	 */
	private Point3f createrPreferredLocation(float nearGoalProb, List<Feeder> goals, float xmin, float xmax, float ymin,
			float ymax) {
		float x, y;
		if (random.nextFloat() < nearGoalProb) {
			int fIndex = random.nextInt(goals.size());
			Point3f p = goals.get(fIndex).getPosition();
			x = (float) (p.x + random.nextFloat() * .2 - .1);
			y = (float) (p.y + random.nextFloat() * .2 - .1);
		} else {
			// TODO change them to have different centers among layers
			x = random.nextFloat() * (xmax - xmin) + xmin;
			y = random.nextFloat() * (ymax - ymin) + ymin;
		}
		return new Point3f(x, y, 0);
	}

	/**
	 * Computes the current activation of all cells
	 */
	public void run() {
		run(robot.getPosition(), robot.getOrientationAngle(), robot.getDistanceToClosestWall());
	}

	/**
	 * Computes the current activation of all cells given the current
	 * parameters.
	 * 
	 * @param point
	 *            The current location of the animat
	 * @param angle
	 *            The current heading of the animat
	 * @param distToWall
	 *            The distance to the closest wall
	 */
	public void run(Point3f point, float angle, float distToWall) {
		int i = 0;
		float total = 0;
		Map<Integer, Float> nonZero = activationPort.getNonZero();
		nonZero.clear();
		for (ConjCell pCell : cells) {
			float val = pCell.getActivation(point, angle, 0, distToWall);
			if (val != 0)
				nonZero.put(i, val);
			total += val;
			i++;
		}

		if (Float.isNaN(total))
			System.out.println("Numeric error");
	}

	public List<ConjCell> getCells() {
		return cells;
	}

	/**
	 * Set the modulation parameter of each cell based on a randomize distance
	 * to the center of inactivation. This inactivation supposes a inactivation
	 * efficiency inversely proportional to the cube of the distance to the
	 * point of injection (based on the volume of the sphere)
	 * 
	 * @param constant A constant multiplying the modulation 
	 */
	public void anesthtizeRadial(float constant) {
		// active = false;
		for (ConjCell cell : cells) {
			float distanceFromInj = random.nextFloat() * layerLength / 2;
			float deact;
			float volume = (float) (3. / 4 * Math.PI * Math.pow(distanceFromInj, 3));
			if (volume != 0)
				deact = (float) Math.min(1, constant / volume);
			else
				deact = 0;
			cell.setBupiModulation(1 - deact);
		}

	}

	/**
	 * A proportion of the cell are fully deactivated
	 * @param proportion The proportion of cells to be deactivated
	 */
	public void anesthtizeProportion(float proportion) {
		// active = false;
		for (ConjCell cell : cells) {
			if (random.nextFloat() < proportion)
				cell.setBupiModulation(0);
		}

	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	public void clear() {
		((Float1dSparsePortMap) getOutPort("activation")).clear();
	}

	/**
	 * Remaps the layer, relocating all place cells
	 */
	public void remap() {
		for (ConjCell cell : cells) {
			Point3f prefLocation = createrPreferredLocation(nearGoalProb, goals, xmin, xmax, ymin, ymax);
			float preferredDirection = (float) (random.nextFloat() * Math.PI * 2);
			cell.setPreferredLocation(prefLocation);
			cell.setPreferredDirection(preferredDirection);
		}
	}

	/**
	 * Disable the effect of inactivation
	 */
	public void reactivate() {
		for (ConjCell cell : cells) {
			cell.setBupiModulation(1);
		}
	}

}
