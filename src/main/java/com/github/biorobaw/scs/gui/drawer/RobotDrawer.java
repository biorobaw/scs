package com.github.biorobaw.scs.gui.drawer;

import java.awt.Color;
import java.awt.Graphics;

import com.github.biorobaw.scs.gui.Drawer;
import com.github.biorobaw.scs.gui.utils.Scaler;
import com.github.biorobaw.scs.gui.utils.Window;
import com.github.biorobaw.scs.simulation.object.RobotProxy;

public class RobotDrawer extends Drawer {

	private static final float RADIUS = .075f;
	
	float[] position = new float[] {-1000000f,-1000000f};
	Float angle = 0f;
	RobotProxy robot;
	
	public RobotDrawer(RobotProxy robot) {
		this.robot = robot;
	}
	
	@Override
	public void draw(Graphics g, Window<Float> panelCoordinates) {
		if(!doDraw) return;
		
		Scaler s = new Scaler(worldCoordinates, panelCoordinates, true);
		var p = s.scale(position);
		var r = s.scaleDistanceX((float)RADIUS);
		var r2 = (int)Math.round(2*r);
		

		g.setColor(Color.BLACK);
		g.drawOval((int)Math.round(p[0] -r) , (int)Math.round(p[1] - r), r2, r2);
		g.drawLine(p[0], p[1], 
				(int)Math.round(p[0] + r * Math.cos(angle)),
				(int)Math.round(p[1] - r * Math.sin(angle)));
	}

	@Override
	public void endEpisode() {
		
	}

	@Override
	public void updateData() {
		var pos = robot.getPosition();
		position = new float[] {(float)pos.getX(), (float)pos.getY()};
		angle 	 = robot.getOrientation2D();
		
	}
}
