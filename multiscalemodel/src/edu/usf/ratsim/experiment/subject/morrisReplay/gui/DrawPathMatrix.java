package edu.usf.ratsim.experiment.subject.morrisReplay.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.micronsl.port.twodimensional.sparse.Entry;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawingFunction;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;



public class DrawPathMatrix extends DrawingFunction {
	
	
	public int arrowSize = 7;
	public int arrowThickness = 2;
	Color arrowColor = GuiUtils.getHSBAColor(0/360f, 1f, 0.8f,0.8f);
	
	
	

	int maxIndexes[];
	float maxValues[];
		
	
	
	Float2dSparsePort WTable;
	List<PlaceCell> PCs;
	float threshold;
	
	
	
	
	/**
	 * Draws a polar graph
	 * @param x		 Center of circle x coord	
	 * @param y		 Center of circle y coord
	 * @param radius 
	 * @param data
	 */
	
	public DrawPathMatrix(Float2dSparsePort WTable, List<PlaceCell> PCs,float threshold){
		
		this.WTable = WTable;
		this.PCs = PCs;
		this.threshold = threshold;
		
		maxIndexes = new int[WTable.getNRows()];
		maxValues =  new float[WTable.getNRows()];

	}

	@Override
	public void run() {
		
//		System.out.println("iterating path matrix...");
		if(!active) return;
		
		Map<Entry,Float> map= WTable.getNonZero();
		
		for(int i=0;i<maxIndexes.length;i++){
			maxIndexes[i]=-1;
			maxValues[i] = threshold;
		}
		
		for(Entry e : map.keySet()){
			
			if(map.get(e) > maxValues[e.i]) {
			
				maxValues[e.i] = map.get(e);
				maxIndexes[e.i] = e.j;
			}
		}
		for(int i=0;i<maxIndexes.length;i++){
			if(maxValues[i]>threshold){
				int[] start = GuiUtils.worldToScreen(PCs.get(i).getPreferredLocation());
				int[] end   = GuiUtils.worldToScreen(PCs.get(maxIndexes[i]).getPreferredLocation());
				
				GuiUtils.drawArrow(start[0], start[1], end[0], end[1], arrowColor, arrowSize, arrowThickness, graphics);
			}
		}
		
		
//		System.out.println("done iterating path matrix...");
		
		
	}
	
	
	
	
	

}
