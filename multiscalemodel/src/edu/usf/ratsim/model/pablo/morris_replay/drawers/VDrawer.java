package edu.usf.ratsim.model.morris_replay.drawers;


import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.List;
import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.GuiUtils;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.twodimensional.sparse.Entry;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;

public class VDrawer extends Drawer {

	Coordinate[] centers;
	Float2dSparsePort stateValues;
	int radius = 2;
	int diameter= 4;
	
	HashMap<Entry,Float> nonZero = new HashMap<>();
	

	public VDrawer(List<PlaceCell> pcs,Float2dSparsePort stateValuePort) {
		centers = new Coordinate[pcs.size()];
		for(int i=0;i<pcs.size();i++){
			centers[i]= pcs.get(i).getPreferredLocation();
		}
		stateValues = stateValuePort;
	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		BoundedUniverse bu = (BoundedUniverse)UniverseLoader.getUniverse();
		Scaler s = new Scaler(bu.getBoundingRect(), panelCoordinates, true);
		
		int coords[][] = s.scale(centers);

		
		float maxValue = GuiUtils.findMaxInMap(nonZero);
		
		for(int i=0; i <stateValues.getNRows();i++){
			
			Float value = nonZero.get(new Entry(i, 0));
			if(value==null) value = 0f;
			
			g.setColor(getColor(value,maxValue));
			g.fillOval(coords[0][i]-radius, coords[1][i]-radius, diameter, diameter);
			
		}
		
		
		

	}

	@Override
	public void clearState() {
		
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		nonZero.clear();
		nonZero.putAll(stateValues.getNonZero());
		
	}
	

	
	public Color getColor(float val,float max){
		
		float h = val < 0 ? 0.66f : 0f;
		float s = (float)Math.abs(val)/max;
		float b = 0.8f;
		float alpha = 1f;

		return  GuiUtils.getHSBAColor(h,s,b,alpha);
	}
	
}
