package edu.usf.ratsim.nsl.modules.celllayer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.universe.element.MultipleT;
import edu.usf.experiment.utils.CSVReader;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.ratsim.nsl.modules.cell.ExponentialPlaceCell;
import edu.usf.ratsim.nsl.modules.cell.ExponentialPlaceCellForMultipleT;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.cell.ProportionalPlaceCell;
import edu.usf.ratsim.nsl.modules.input.Position;

/**
 * This layer locates place cells in a tesselated grid laid over the
 * environment.
 * 
 * @author Martin Llofriu
 *
 */
public class TmazeRandomPlaceCellLayer extends Module {

	/**
	 * The list of all place cells
	 */
	private LinkedList<PlaceCell> cells;

	/**
	 * Whether the layer is active or not
	 */
	private boolean active;

	/**
	 * The activation output port. A sparse port is used for efficiency.
	 */
	private Float1dSparsePortMap activationPort;

	/**
	 * Creates and locates place cells randomly in the multiple T maze. It assumes uniform distribution
	 * 
	 * @param name
	 *            The module's name
	 * @param robot
	 *            A robot capable of providing localization information
	 * @param radius
	 *            The radius of the place cells
	 * @param numCells
	 *            Number of cells to be sampled
	 * @param placeCellType
	 *            The type of place cells to use. Proportional and exponential
	 *            place cells are supported.
	 *            
	 */
	public TmazeRandomPlaceCellLayer(String name, float radius, int numCells,String placeCellType) {
		super(name);

		active = true;

		cells = new LinkedList<PlaceCell>();

		MultipleT mT = (MultipleT) Globals.getInstance().get("tMaze");
		
		
		//CHECK IF WE ARE LOADING A MODEL INSTEAD OF INITIALIZING A NEW ONE
		Globals g = Globals.getInstance();
		Integer loadEpisode = (Integer)g.get("loadEpisode");
		String[][] loadedValues = null;
		if(loadEpisode!=null){
			
			String loadPath = (String)g.get("logPath")  +"/"+(String)g.get("loadTrial")+ "/" + loadEpisode + "/" + (String)g.get("groupName") + "/" + g.get("subName") + "/";
			loadedValues = CSVReader.loadCSV(loadPath + "cellCenters.txt",";"); 
		}
			
		
		for (int i = 0; i < numCells; i++) {
			
			float[] xy = loadedValues !=null ? new float[] {Float.parseFloat(loadedValues[i][0]),Float.parseFloat(loadedValues[i][1])} : mT.getRandomPosition();			
			// Find if it intersects any wall
			if (placeCellType.equals("proportional"))
				cells.add(new ProportionalPlaceCell(new Point3f(xy[0], xy[1], 0), radius));
			else if (placeCellType.equals("exponential"))
				cells.add(new ExponentialPlaceCell(new Point3f(xy[0], xy[1], 0), radius));
			else if(placeCellType.equals("exponentialMultipleT"))
				cells.add(new ExponentialPlaceCellForMultipleT(new Point3f(xy[0], xy[1],0),radius));
			else
				throw new RuntimeException("Place cell type not implemented");
		}

		activationPort = new Float1dSparsePortMap(this, cells.size(), 1f/8);
		addOutPort("activation", activationPort);

	}
	
	
	/**
	 * Creates pc layer using the given cells
	 * @param name
	 * @param radius
	 * @param numCells
	 * @param placeCellType
	 */
	public TmazeRandomPlaceCellLayer(String name, LinkedList<PlaceCell> cells) {
		super(name);

		active = true;

		this.cells = cells;		

		activationPort = new Float1dSparsePortMap(this, cells.size(), 1f/8);
		addOutPort("activation", activationPort);

	}
	
	/**
	 * Computes the current activation of all cells
	 */
	public void run() {
		
		run(((Point3fPort)getInPort("position")).get(), 0); //second parameter 'distanceToWall' is not used and therefore set to 0
	}
	
	/**
	 * Computes the current activation of all cells given the current
	 * parameters.
	 * 
	 * @param pos
	 *            The current location of the animat
	 * @param distToWall
	 *            The distance to the closest wall
	 */
	public void run(Point3f pos, float distanceToClosestWall) {
		Map<Integer, Float> nonZero = activationPort.getNonZero();
		nonZero.clear();
		if (active) {
			int i = 0;
			float total = 0;
			float max = 0;
			for (PlaceCell pCell : cells) {
				float val = pCell.getActivation(pos, distanceToClosestWall);
				if (val != 0) {
					nonZero.put(i, val);
					total += val;
					max = val>max? val : max;
				}
				i++;
			}

			if(max==0) throw new IllegalArgumentException("No active place cells");
			for(Integer key : nonZero.keySet()){
				nonZero.put(key, nonZero.get(key)/max);
			}
			
			//System.out.println(cells);
			//System.out.println(pos);
			//System.out.println(nonZero.keySet());
			
			if (Float.isNaN(total))
				System.out.println("Numeric error");
		}
	}
	
	/**
	 * Returns the activation of all cells
	 * @param pos The current position of the animat.
	 * @return An array of the activation values
	 */
	public float[] getActivationValues(Point3f pos) {
		float distanceToClosestWall = 0; //value not used
		float[] res = new float[cells.size()];

		for (int i = 0; i < cells.size(); i++) {
			res[i] = cells.get(i).getActivation(pos, distanceToClosestWall);
		}

		return res;
	}

	public List<PlaceCell> getCells() {
		return cells;
	}

	public void deactivate() {
		active = false;
	}

	@Override
	public boolean usesRandom() {
		return true;
	}

	public void clear() {
		((Float1dPortArray) getOutPort("activation")).clear();
	}

}
