package edu.usf.ratsim.nsl.modules.celllayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.WallRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.onedimensional.vector.PointPort;
import edu.usf.micronsl.port.twodimensional.Float2dSingleBlockMatrixPort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.nsl.modules.cell.ExponentialPlaceCell;
import edu.usf.ratsim.nsl.modules.cell.ExponentialPlaceCellForMultipleT;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.cell.ProportionalPlaceCell;

/**
 * This layer locates place cells in a tesselated grid laid over the
 * environment.
 * 
 * @author Martin Llofriu
 *
 */
public class TesselatedPlaceCellLayer extends Module {

	/**
	 * The list of all place cells
	 */
	private ArrayList<PlaceCell> cells;

	/**
	 * Whether the layer is active or not
	 */
	private boolean active;

	/**
	 * The activation output port. A sparse port is used for efficiency.
	 */
	private Float2dSingleBlockMatrixPort activationPort;

	private String placeCellType;

	private float radius;
	private float distanceXBetweenCells;
	private float distanceYBetweenCells;

	private float xmin;

	private float ymin;

	private int numCellsPerSide;

	/**
	 * Creates all the place cells and locates them in a tesselated grid laid
	 * over the environment.
	 * 
	 * @param name
	 *            The module's name
	 * @param robot
	 *            A robot capable of providing localization information
	 * @param radius
	 *            The radius of the place cells
	 * @param numCellsPerSide
	 *            Number of cells per side of the bounding box
	 * @param placeCellType
	 *            The type of place cells to use. Proportional and exponential
	 *            place cells are supported.
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
	 */
	public TesselatedPlaceCellLayer(String name, float radius, int numCellsPerSide,
			String placeCellType, float xmin, float ymin, float xmax, float ymax) {
		super(name);

		this.active = true;
		this.placeCellType = placeCellType;
		this.radius = radius;

		this.cells = new ArrayList<PlaceCell>();

		distanceYBetweenCells = (ymax - ymin) / (numCellsPerSide - 1);
		distanceXBetweenCells = (xmax - xmin) / (numCellsPerSide - 1);
		this.xmin = xmin;
		this.ymin = ymin;
		this.numCellsPerSide = numCellsPerSide;
		
		for (int i = 0; i < numCellsPerSide; i++) {
			float y = ymin + i *distanceYBetweenCells;
			for (int j = 0; j < numCellsPerSide; j++) {
				float x = xmin + j *distanceXBetweenCells;
				// Find if it intersects any wall
				if (placeCellType.equals("proportional"))
					cells.add(new ProportionalPlaceCell(new Coordinate(x, y), radius));
				else if (placeCellType.equals("exponential"))
					cells.add(new ExponentialPlaceCell(new Coordinate(x, y), radius));
				else if(placeCellType.equals("exponentialMultipleT"))
						cells.add(new ExponentialPlaceCellForMultipleT(new Coordinate(x, y),radius));
				else
					throw new RuntimeException("Place cell type not implemented");
			}
		}

		
		int maxActivePlaCellRows =  (int)(2*radius / distanceYBetweenCells) + 1;
		int maxActivePlaCellCols =  (int)(2*radius / distanceXBetweenCells) + 1;
		
		//activationPort = new Float1dSparsePortMap(this, cells.size(), 4000);
		activationPort = new Float2dSingleBlockMatrixPort(this, numCellsPerSide, numCellsPerSide,maxActivePlaCellRows,maxActivePlaCellCols,0,0);
		addOutPort("activation", activationPort);
		


	}
	
	
	/**
	 * Computes the current activation of all cells
	 */
	public void run() {
		run(((PointPort)getInPort("position")).get(), 0); 
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
	public void run(Coordinate pos, float distanceToClosestWall) {
		
		if(!active){
			activationPort.clearBlock();
		}else{
			
			int firstCol = Math.max( (int)Math.ceil((pos.x-radius-xmin)/distanceXBetweenCells),0);
			int firstRow = Math.max( (int)Math.ceil((pos.y-radius-ymin)/distanceYBetweenCells),0);
			
			activationPort.setWindowOrigin(firstRow, firstCol);
			
			
			for(int i=0;i<activationPort.getBlockRows();i++)
				for(int j=0;j<activationPort.getBlockCols();j++){
					
					//System.out.println("line: "+i + " " + j + " " + firstRow + " " + firstCol );
					activationPort.setBlock(i, j, getCell(firstRow+i,firstCol+j).getActivation(pos, distanceToClosestWall));
					
					
				}
			
			
//			activationPort
//			Map<Integer, Float> nonZero = activationPort.getNonZero();
//			nonZero.clear();
//			if (active) {
//				int i = 0;
//				float total = 0;
//				
//				for (PlaceCell pCell : cells) {
//					float val = pCell.getActivation(pos, distanceToClosestWall);
//					if (val != 0) {
//						nonZero.put(i, val);
//						total += val;
//					}
//					i++;
//				}
//				
//				if (Float.isNaN(total))
//					System.out.println("Numeric error");
//			}
		}
		
	}
	
	public PlaceCell getCell(int i, int j){
		return cells.get(i*numCellsPerSide + j);
	}
	
	
	/**
	 * Returns the activation of all cells
	 * @param pos The current position of the animat.
	 * @return An array of the activation values
	 */
	public float[] getActivationValues(Coordinate pos) {
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
		return false;
	}

	public void clear() {
		((Float1dPortArray) getOutPort("activation")).clear();
	}
	
//	public Float1dSparsePortMap getActivationPort(){
//		return activationPort;
//	}
	
	public Float2dSingleBlockMatrixPort getActivationPort(){
		return activationPort;
	}
	
	public void setPCs(float[][] centers){
		cells.clear();
		for(float[] c : centers) {
			float x = c[0];
			float y = c[1];
			if (placeCellType.equals("proportional"))
				cells.add(new ProportionalPlaceCell(new Coordinate(x, y), radius));
			else if (placeCellType.equals("exponential"))
				cells.add(new ExponentialPlaceCell(new Coordinate(x, y), radius));
			else if(placeCellType.equals("exponentialMultipleT"))
					cells.add(new ExponentialPlaceCellForMultipleT(new Coordinate(x, y),radius));
			else
				throw new RuntimeException("Place cell type not implemented");
		}
	}

}
