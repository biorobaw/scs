package edu.usf.ratsim.nsl.modules;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Float1dPort;
import edu.usf.micronsl.Float1dPortArray;
import edu.usf.micronsl.Float1dSparsePortMap;
import edu.usf.micronsl.Module;

public class ArtificialConjCellLayer extends Module {

	public float[] activation;

	private List<ExponentialConjCell> cells;

	// private boolean active;

	private LocalizableRobot robot;

	private Random random;

	private float ymax;

	private float ymin;

	private float xmax;

	private float xmin;

	private float nearGoalProb;

	private List<Feeder> goals;

	private float layerLength;

	private Float1dSparsePortMap activationPort;

	public ArtificialConjCellLayer(String name, LocalizableRobot robot,
			float placeRadius, float minDirectionRadius,
			float maxDirectionRadius, int numIntentions, int numCells,
			String placeCellType, float xmin, float ymin, float xmax,
			float ymax, List<Feeder> goals, float nearGoalProb, float layerLength, float wallInhibition) {
		super(name);

		this.goals = goals;
		this.nearGoalProb = nearGoalProb;
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.layerLength = layerLength;

		cells = new LinkedList<ExponentialConjCell>();
		random = RandomSingleton.getInstance();
		int i = 0;
		do {
			if (placeCellType.equals("goalExponential")
					|| placeCellType.equals("wallGoalExponential")) {
				Point3f prefLocation = createrPreferredLocation(nearGoalProb,
						goals, xmin, xmax, ymin, ymax);
				float preferredDirection = (float) (random.nextFloat()
						* Math.PI * 2);
				// Using Inverse transform sampling to sample from k/x between
				// min and max
				// https://en.wikipedia.org/wiki/Inverse_transform_sampling. k =
				// 1/(ln (max) - ln(min)) due to normalization
				float k = (float) (1 / (Math.log(maxDirectionRadius) - Math
						.log(minDirectionRadius)));
				float s = random.nextFloat();
				float directionRadius = (float) Math.exp(s / k
						+ Math.log(minDirectionRadius));

				int preferredIntention = random.nextInt(numIntentions);
				if (placeCellType.equals("goalExponential")) {
					cells.add(new ExponentialConjCell(prefLocation,
							preferredDirection, placeRadius, directionRadius,
							preferredIntention));
				} else if (placeCellType.equals("wallGoalExponential")) {
					cells.add(new ExponentialWallConjCell(prefLocation,
							preferredDirection, placeRadius, directionRadius,
							preferredIntention, wallInhibition, random));
				}
			} else {
				System.err.println("Place cell type not implemented");
				System.exit(1);
			}
			i++;
		} while (i < numCells);

		activationPort = new Float1dSparsePortMap(this, cells.size(), 4000);
		addOutPort("activation", activationPort);

		this.robot = robot;
	}

	private Point3f createrPreferredLocation(float nearGoalProb,
			List<Feeder> goals, float xmin, float xmax, float ymin, float ymax) {
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

	public void run() {
		// Find the intention
		Float1dPort intention = (Float1dPort) getInPort("intention");
		int intentionNum = -1;
		int i = 0;
		while (intentionNum == -1 && i < intention.getSize()) {
			if (intention.get(i) == 1)
				intentionNum = i;
			i++;
		}

		simRun(robot.getPosition(), robot.getOrientationAngle(), intentionNum,
				robot.isFeederClose());
	}

	public float[] getActivationValues(Point3f pos, float angle, int intention) {
		float distanceToClosestWall = robot.getDistanceToClosestWall();
		float[] res = new float[cells.size()];

		for (int i = 0; i < cells.size(); i++) {
			res[i] = cells.get(i).getActivation(pos, angle, intention,
					distanceToClosestWall);
		}

		return res;
	}

	public int getSize() {
		return cells.size();
	}

	public List<ExponentialConjCell> getCells() {
		return cells;
	}

	public void anesthtizeRadial(float constant) {
		// active = false;
		for (ExponentialConjCell cell : cells){
			float distanceFromInj = random.nextFloat() * layerLength/2;
			float deact;
			float volume = (float) (3./4*Math.PI*Math.pow(distanceFromInj, 3));
			if (volume != 0)
				deact = (float) Math.min(1, constant / volume);
			else 
				deact = 0;
			cell.setBupiModulation(1 - deact);
		}

	}
	
	public void anesthtizeProportion(float proportion) {
		// active = false;
		for (ExponentialConjCell cell : cells){
			if (random.nextFloat() < proportion)
				cell.setBupiModulation(0);
		}

	}

	public void simRun(Point3f pos, float direction, int intention,
			boolean isFeederClose) {
		simRun(pos, direction, intention, isFeederClose,
				robot.getDistanceToClosestWall());
	}

	public void simRun(Point3f pos, float direction, int intention,
			boolean isFeederClose, float distanceToClosestWall) {
		// if (active) {
		int i = 0;
		float total = 0;
		Map<Integer,Float> nonZero = activationPort.getNonZero();
		nonZero.clear();
		for (ExponentialConjCell pCell : cells) {
			float val = pCell.getActivation(pos, direction, intention,
					distanceToClosestWall);
			if (val != 0)
				nonZero.put(i, val);
			total += val;
			i++;
		}

		if (Float.isNaN(total))
			System.out.println("Numeric error");

		// if (total != 0)
		// for (i = 0; i < activation.length; i++)
		// activation[i] = activation[i] / total * layerEnergy ;
		// } else {
		// for (int i = 0; i < activation.length; i++)
		// activation[i] = 0;
		// }
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	public void clear() {
		((Float1dSparsePortMap) getOutPort("activation")).clear();
	}

	public void remap() {
		for (ExponentialConjCell cell : cells){
			Point3f prefLocation = createrPreferredLocation(nearGoalProb,
					goals, xmin, xmax, ymin, ymax);
			float preferredDirection = (float) (random.nextFloat()
					* Math.PI * 2);
			cell.setPreferredLocation(prefLocation);
			cell.setPreferredDirection(preferredDirection);
		}
	}

	public void reactivate() {
		for (ExponentialConjCell cell : cells){
			cell.setBupiModulation(1);
		}
	}

}
