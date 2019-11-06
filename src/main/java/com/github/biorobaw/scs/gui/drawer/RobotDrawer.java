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
		

		g.setColor(Color.BLACK);
		g.drawOval(p[0] - (int) (RADIUS * s.xscale), p[1] - (int) (RADIUS * s.yscale),
				(int) (RADIUS * s.xscale * 2), (int) (RADIUS * s.yscale * 2));
		g.drawLine(p[0], p[1], p[0] + (int) (RADIUS * Math.cos(angle) * s.xscale),
				p[1] - (int) (RADIUS * Math.sin(angle) * s.yscale));
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
