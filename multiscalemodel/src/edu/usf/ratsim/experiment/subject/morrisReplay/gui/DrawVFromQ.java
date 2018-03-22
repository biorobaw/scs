package edu.usf.ratsim.experiment.subject.morrisReplay.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.util.List;

import javax.vecmath.Point3f;

import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawingFunction;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;



public class DrawVFromQ extends DrawingFunction {
	
	Float2dSparsePort QTable;
	int column;
	List<PlaceCell> PCs;
	int dotWidth = 9;
	
	
	public DrawVFromQ(Float2dSparsePort QTable,int column, List<PlaceCell> PCs){
		
		this.column = column;
		this.QTable = QTable;
		this.PCs = PCs;
		


	}

	@Override
	public void run() {
		if(!active) return;
		// TODO Auto-generated method stub
		
		
		//find min and max V values
		float min = Float.MAX_VALUE;
		float max = -Float.MIN_VALUE;
		for(int i=0;i<PCs.size();i++){
			float val = QTable.get(i,column);
			if(val<min) min = val;
			if(val>max) max = val;
		}
		
		
		
		
		//for each place cell draw V:
		
		for(int i=0;i<PCs.size();i++){
			
			float val = QTable.get(i, column);
			
			Color c = GuiUtils.getHSBAColor(0f, 0, 0.8f,0.5f);;
			if(max!=min){
				c = GuiUtils.getHSBAColor(0f, (float)(0.2+0.6*val/(max-min)), 0.5f,0.8f);
			}
			
			graphics.setColor(c);
			Point3f center = PCs.get(i).getPreferredLocation();
			
			
			int[] screenXY = GuiUtils.worldToScreen(center);
			
			
			
			//graphics.fillOval(screenXY[0], screenXY[1], dotWidth, dotWidth);
			graphics.fillRect(screenXY[0]-dotWidth, screenXY[1]-dotWidth, 2*dotWidth, 2*dotWidth);
			
			
		}
		
		
		

		
		
	}
	
	

}
