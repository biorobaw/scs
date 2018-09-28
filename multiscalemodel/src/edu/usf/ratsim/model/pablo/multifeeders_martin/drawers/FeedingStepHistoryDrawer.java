package edu.usf.ratsim.model.pablo.multifeeders_martin.drawers;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D.Float;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Globals;
import edu.usf.experiment.display.GuiUtils;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.twodimensional.sparse.Entry;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.rl.Reward;

public class FeedingStepHistoryDrawer extends Drawer {


	Float localCoordinates = new Float(0,0,1,1); 

	float origin[] = {0.1f,0.1f};
	float lengths[] = {0.8f, 0.8f};
	
	
	ArrayList<Coordinate> feedingStepHistory = null;
	ArrayList<ArrayList<Coordinate>> allFeedingHistory = new ArrayList<>();
	int maxHistoryLength = 0; //maximum number of eats in one episode
	int lastFeedingTime  = 0;
	
	
	//min and max y coords
	int minY;
	int maxY;

	public boolean doLines = true;
	public int markerSize = 2;
	
	SubjectAte subjectAte;

	private boolean newRun = true;

	public FeedingStepHistoryDrawer(int minY, int maxY, SubjectAte ate) {
		 
		this.minY = minY;
		this.maxY = maxY;
		localCoordinates.y = minY;
		localCoordinates.height = maxY-minY;
		this.subjectAte = ate;
	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw ) return;
		
		//draw axis
		int ox = (int)(panelCoordinates.x + origin[0]*panelCoordinates.width);
		int oy = (int)(panelCoordinates.x +(1- origin[1])*panelCoordinates.height);
		int x = ox + (int)(lengths[0]*panelCoordinates.width);
		int y = oy - (int)(lengths[1]*panelCoordinates.height);
		g.drawLine(ox,oy,x,oy);
		g.drawLine(ox, oy, ox, y);
		
		var drawCoords = new java.awt.geom.Rectangle2D.Float(ox,y,x-ox,oy-y);
		
		
		Scaler s = new Scaler(localCoordinates, drawCoords, false);
//		float ox = localCoordinates.getMinX()
//		Point o = s.scale(new Coordinate(origin[0],origin[1]));
//		Point x = s.scale(new Coordinate(origin[0]+lengths[0],origin[0]));
//		Point y = s.scale(new Coordinate(origin[0],origin[0]+lengths[1]));
		
		
//		Point o = 
		
		for(ArrayList<Coordinate> runtimes : allFeedingHistory) {
			
			g.setColor(Color.black);

			int coords[][] = s.scale(runtimes);
			
			
			//draw X and Y axis
			
			//Draw Line
			if(doLines) g.drawPolyline(coords[0], coords[1], runtimes.size());
			
			//Draw Markers
			for(int i=0; i < runtimes.size() ; i ++){
				g.drawOval(coords[0][i]-markerSize, coords[1][i]-markerSize,2*markerSize ,	2*markerSize);
				
			}
		
		}
		
		
		
		
		
		
		
		
		
		
		

	}

	
	@Override
	public void newEpisode() {
		feedingStepHistory = new ArrayList<>();
		allFeedingHistory.clear();
		allFeedingHistory.add(feedingStepHistory);
		lastFeedingTime =0;
		newRun = true;
	}
	
	
	@Override
	public void endEpisode() {
		if(!newRun) updateData(); //update in case the last step was skipped
		lastRunLength = -1;
		lastRunId =-1;
	}
	
	@Override
	public void newTrial() {
		
	}
	
	@Override
	public void endTrial() {
	}

	
	//hold values that will need to be added
	ArrayList<Coordinate> aux 		  = new ArrayList<>();
	float lastRunLength = -1;
	int   lastRunId = -1;
	
	
	@Override 
	public void appendData(){
		
		if(newRun) {
			newRun = false;
			int cycle 		= (int)Globals.getInstance().get("cycle");
			int numSteps 	= cycle-lastFeedingTime;
			
			int runId = feedingStepHistory.size() + aux.size();
			Coordinate newCoord = new Coordinate(runId,clampY(numSteps));
			feedingStepHistory.add(newCoord);
			maxHistoryLength = Math.max(maxHistoryLength,runId);
		}
		
		//the second condition prevents 
		if(subjectAte.outPort.get() ) {
			int cycle 		= (int)Globals.getInstance().get("cycle");
			int numSteps 	= cycle-lastFeedingTime;
			if(aux.size() ==0) {
				lastRunLength = clampY(numSteps);
				lastRunId = feedingStepHistory.size()-1;
			}else {
				aux.get(aux.size()-1).y = clampY(numSteps);				
			}
			lastFeedingTime = cycle;
			newRun = true;
		} 	

	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		
		feedingStepHistory.addAll(aux);
		aux.clear();
		if(lastRunLength!=-1) {
			feedingStepHistory.get(lastRunId).y = lastRunLength;
			lastRunId = -1;
			lastRunLength = -1;
		}
		
		if(!newRun) {
			int cycle 		= (int)Globals.getInstance().get("cycle");
			float numSteps 	= clampY(cycle-lastFeedingTime);
			feedingStepHistory.get(feedingStepHistory.size()-1).y = numSteps;
		}
		
		localCoordinates.width = maxHistoryLength;
		
		
	}
	
	float clampY(int runtime) {
		return runtime > maxY ? maxY :  runtime < minY ?  minY : runtime;
//		return origin[1] + lengths[1]*(float)clamped/(maxY-minY);
	}
	
}
