package edu.usf.ratsim.model.pablo.multiplet.drawers;


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
import edu.usf.micronsl.port.twodimensional.Float2dSingleBlockMatrixPort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;

public class PCDrawerSparsePort extends Drawer {

	Coordinate[] topLeftCoord;
	Float1dSparsePortMap activations;
	Integer[] nonZeroIndexes;
	Float[] nonZeroValues;
	int numNonZero = 0;
	float diameter;
	private int size;
	
//	HashMap<Integer, Float> values = new HashMap<>();
	

	public PCDrawerSparsePort(List<PlaceCell> pcs,Float1dSparsePortMap pcActivationPort) {
		float radius = pcs.get(0).getPlaceRadius();
		diameter = 2*radius;
		this.size = pcs.size();

		topLeftCoord = new Coordinate[size];
		int id = 0;
		for(int i=0;i<size;i++){
			PlaceCell pc = pcs.get(id++);
			Coordinate center = pc.getPreferredLocation();
			topLeftCoord[i]= new Coordinate(center.x-radius,center.y+radius);
		}
		activations = pcActivationPort;
		
		nonZeroIndexes = new Integer[size];
		nonZeroValues  = new Float[size];
		
		
	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		BoundedUniverse bu = (BoundedUniverse)UniverseLoader.getUniverse();
		Scaler s = new Scaler(bu.getBoundingRect(), panelCoordinates, true);
		
		
		
		int coords[][] = s.scale(  topLeftCoord);
		int diam = s.scaleDistanceX(diameter);
		
		
		//draw base rings
		int grayLevel = 180;
		g.setColor(new Color(grayLevel,grayLevel,grayLevel));
		for(int i=0;i<size;i++){
			
			g.drawOval(coords[0][i], coords[1][i],diam ,diam);			
		}
		
		
		
		//fill background of nonzero place cells
		grayLevel = 210;
		g.setColor(new Color(grayLevel,grayLevel,grayLevel));
		for(int i=0;i<numNonZero;i++){
			int id = nonZeroIndexes[i];
			g.fillOval(coords[0][id], coords[1][id],diam ,diam);
		}
		
//		for(Integer key : values.keySet()){
//			g.fillOval(coords[0][key], coords[1][key],diam ,diam);
//		}
		
		
//		Float minValue = Float.MAX_VALUE;
//		Float maxValue = -minValue;
//		for(Integer key : values.keySet()){
//			float val = values.get(key);
//			minValue = Math.min(minValue,val);
//			maxValue = Math.max(maxValue, val);
//		}
		
		
		//give hue to non zero place cells
		for(int i=0;i<numNonZero;i++){
			int id = nonZeroIndexes[i];
			float val = nonZeroValues[i];
			g.setColor(getColor(val));
			g.fillOval(coords[0][id], coords[1][id],diam ,diam);
		}
//		for(Integer key : values.keySet()){			
//			float val = values.get(key);
//			g.setColor(getColor(val));
//			g.fillOval(coords[0][key], coords[1][key],diam ,diam);
//		}
		
		

	}

	@Override
	public void endEpisode() {
		
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		numNonZero = activations.getNonZero().size();
		int i=0;
		for(var kv : activations.getNonZero().entrySet()) {
			nonZeroIndexes[i] = kv.getKey();
			nonZeroValues[i]  = kv.getValue();
			i++;
		}		
	}
	

	
	public Color getColor(float val){
		float i = 0.4f;
		float alpha = i+(1f-i)*(val-0.2f)/(1f-0.2f);
		alpha =0.8f;
		float m = 0.2f;
		float beta  = m+(0.73f-m)*(val-0.2f)/(1f-0.2f);
//		max = (float)Math.max(max, alpha);
//		alpha = (float)Math.sqrt(alpha);
		return  GuiUtils.getHSBAColor(0f,0.9f,alpha,beta);
	}
	
}
