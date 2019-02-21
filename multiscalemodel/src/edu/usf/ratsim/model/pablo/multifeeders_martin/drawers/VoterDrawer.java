package edu.usf.ratsim.model.pablo.multifeeders_martin.drawers;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D.Float;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.micronsl.port.onedimensional.Float1dPort;

public class VoterDrawer extends Drawer {


	Float localCoordinates = new Float(-0.1f,0,3.1f,1); 

	float origin[] = {0.1f,0.1f};
	float lengths[] = {0.8f, 0.8f};
	
	
	
	ArrayList<Coordinate[]> data = new ArrayList<>();
	ArrayList<Color> colors = new ArrayList<>();
	ArrayList<Float1dPort> ports = new ArrayList<>();
	String tags = "";
	int numActions;
	
	//min and max y coords
	float minY=0;
	float maxY=1;

	public boolean doLines = true;
	public int markerSize = 2;
	


	public VoterDrawer(int numActions) {
		this.numActions = numActions;
		
	}
	
	public void addVoter(String tag,Float1dPort port,Color color) {
		ports.add(port);
		colors.add(color);
		var coords = new Coordinate[numActions];
		for(int i=0;i<numActions;i++) coords[i] = new Coordinate(i,0);
		data.add(coords);
		tags = tags  + tag + ",";
		
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
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(3));
		for(int i=0; i<data.size() ; i++) {
						
			
			g.setColor(colors.get(i));
			int coords[][] = s.scale(data.get(i));
			
			//Draw Line
			if(doLines) g.drawPolyline(coords[0], coords[1], numActions);
			
			//Draw Markers
			for(int j=0; j<numActions ; j ++){
				g.drawOval(coords[0][j]-markerSize, coords[1][j]-markerSize,2*markerSize ,	2*markerSize);
				
			}
		
		}
		
		g.setColor(Color.BLUE);
		g.drawString("" + localCoordinates.y, (int)panelCoordinates.x+40,(int)panelCoordinates.y +15);
		g.setColor(Color.RED);
		g.drawString("" + (localCoordinates.y+localCoordinates.height), (int)(panelCoordinates.x+panelCoordinates.width-40),(int)panelCoordinates.y+15 );
		g.setColor(Color.BLACK);
		g.drawString(tags, (int)(panelCoordinates.x+panelCoordinates.width*0.25), (int)panelCoordinates.y+(int)panelCoordinates.height - 15);
		

	}


	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		minY = java.lang.Float.POSITIVE_INFINITY;
		maxY = java.lang.Float.NEGATIVE_INFINITY;
		for(int i=0;i<ports.size();i++) {
			for(int j=0;j<numActions;j++) {
				float val = ports.get(i).get(j);
				data.get(i)[j].y = val;
				 minY = Math.min(val,minY);
				 maxY = Math.max(val,maxY);
			}
		}

		localCoordinates.y = minY;
		localCoordinates.height = minY==maxY ? 1 : (maxY-minY);
		
	}
	
//	float clampY(int runtime) {
//		return runtime > maxY ? maxY :  runtime < minY ?  minY : runtime;
//	}
	
}
