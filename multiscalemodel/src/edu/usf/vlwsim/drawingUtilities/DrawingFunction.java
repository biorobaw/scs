package edu.usf.vlwsim.drawingUtilities;

import javax.media.j3d.J3DGraphics2D;

public abstract class DrawingFunction implements Runnable {
	
	public J3DGraphics2D graphics;
	
	public void setGraphics(J3DGraphics2D graphics){
		this.graphics = graphics;
	}

}
