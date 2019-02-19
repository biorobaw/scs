package edu.usf.platform.drawers;


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

public class PCDrawer extends Drawer {

	Coordinate[][] topLeftCoord;
	Float2dSingleBlockMatrixPort activations;
	Float2dSingleBlockMatrixPort dataCopy;
	float diameter;
	private int rows;
	private int cols;
	
//	HashMap<Integer, Float> values = new HashMap<>();
	

	public PCDrawer(List<PlaceCell> pcs,Float2dSingleBlockMatrixPort pcActivationPort) {
		float radius = pcs.get(0).getPlaceRadius();
		diameter = 2*radius;
		this.rows = pcActivationPort.getNRows();
		this.cols = pcActivationPort.getNCols();
		topLeftCoord = new Coordinate[rows][cols];
		int id = 0;
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++){
				PlaceCell pc = pcs.get(id++);
				Coordinate center = pc.getPreferredLocation();
				topLeftCoord[i][j]= new Coordinate(center.x-radius,center.y+radius);
			}
		activations = pcActivationPort;
		dataCopy = pcActivationPort.copyPort();
		
		
	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		BoundedUniverse bu = (BoundedUniverse)UniverseLoader.getUniverse();
		Scaler s = new Scaler(bu.getBoundingRect(), panelCoordinates, true);
		
		int firstRow = dataCopy.getStartRow();
		int firstCol = dataCopy.getStartCol();
		
		
		int coords[][][] = s.scale(topLeftCoord,rows,cols);
		int diam = s.scaleDistanceX(diameter);
		
		
		//draw base rings
		int grayLevel = 180;
		g.setColor(new Color(grayLevel,grayLevel,grayLevel));
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++){
			
				g.drawOval(coords[0][i][j], coords[1][i][j],diam ,diam);			
			}
		
		
		
		//fill background of nonzero place cells
		grayLevel = 210;
		g.setColor(new Color(grayLevel,grayLevel,grayLevel));
		for(int i=firstRow;i<dataCopy.getEndRow();i++)
			for(int j=firstCol;j<dataCopy.getEndCol();j++){
				if(dataCopy.get(i,j)!=0)
					g.fillOval(coords[0][i][j], coords[1][i][j],diam ,diam);
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
		for(int i=firstRow;i<firstRow+dataCopy.getBlockRows();i++)
			for(int j=firstCol;j<firstCol+dataCopy.getBlockCols();j++){
				float val = dataCopy.get(i,j);
				if(val!=0) {
					g.setColor(getColor(val));
					g.fillOval(coords[0][i][j], coords[1][i][j],diam ,diam);					
				}
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
		activations.copyDataTo(dataCopy);
		
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
