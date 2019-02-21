package edu.usf.ratsim.model.pablo.morris_replay.drawers;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D.Float;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Globals;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;

public class RuntimesDrawer extends Drawer {


	Float localCoordinates; 
	
	float origin[] = {0.1f,0.1f};
	float lengths[] = {0.8f, 0.8f};
	
	
		
	ArrayList<Coordinate> runtimes = new ArrayList<>();
	int numEpisodes = 0;
	int minY;
	int maxY;

	public boolean doLines = true;
	public int markerSize = 2;

	public RuntimesDrawer(int numEpisodes,int minY, int maxY) {
		
		localCoordinates = new Float(0,0,1,1);
		this.numEpisodes = numEpisodes;
		this.minY = minY;
		this.maxY = maxY;
	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		
		Scaler s = new Scaler(localCoordinates, panelCoordinates, false);
		
		
		Point o = s.scale(new Coordinate(origin[0],origin[0]));
		Point x = s.scale(new Coordinate(origin[0]+lengths[0],origin[0]));
		Point y = s.scale(new Coordinate(origin[0],origin[0]+lengths[1]));
		
		
		g.setColor(Color.black);
		
		
		
		
		int coords[][] = s.scale(runtimes);
		

		//draw X and Y axis
		g.drawLine(o.x, o.y, x.x, x.y);
		g.drawLine(o.x, o.y, y.x, y.y);
		
		//Draw Line
		if(doLines) g.drawPolyline(coords[0], coords[1], runtimes.size());
		
		//Draw Markers
		for(int i=0; i < runtimes.size() ; i ++){
			g.drawOval(coords[0][i]-markerSize, coords[1][i]-markerSize,2*markerSize ,	2*markerSize);
			
		}
		

	}

	@Override
	public void endEpisode() {
		updateData();
		nextValue = 0;
		int nextID = runtimes.size();
		float x = origin[0]+lengths[0]*(float)nextID/numEpisodes;
		float y = origin[1];
		runtimes.add(new Coordinate(x, y));
	}

	int nextValue;
	@Override 
	public void appendData(){
		nextValue = (int)Globals.getInstance().get("cycle");
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		if(runtimes.size()==0) return;
		int cycle = nextValue > maxY ? maxY : ( nextValue < minY ?  minY : nextValue);
		float y = origin[1] + lengths[1]*(float)cycle/(maxY-minY);
		runtimes.get(runtimes.size()-1).y = y;		
	}
	
	
	
}
