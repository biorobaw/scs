package edu.usf.ratsim.experiment.universe.virtual.drawingUtilities;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.J3DGraphics2D;

public abstract class DrawingFunction implements Runnable{
	
	public J3DGraphics2D graphics;
	public boolean active = true; //indicates if its activated, if not, do not draw
	public Canvas3D canvas;

	public void setGraphics(J3DGraphics2D graphics){
		this.graphics = graphics;
	}

	
	public void setActive(boolean active){
		this.active = active;
	}


	public void setCanvas3D(Canvas3D topViewCanvas) {
		// TODO Auto-generated method stub
		this.canvas = topViewCanvas;
		
	}
}
