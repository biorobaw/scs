package com.github.biorobaw.scs.gui.displays.scs_swing.drawer;

import java.awt.Color;
import java.awt.Graphics;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.gui.displays.scs_swing.DrawerSwing;
import com.github.biorobaw.scs.gui.utils.Scaler;
import com.github.biorobaw.scs.gui.utils.Window;

public class CycleDataDrawer extends DrawerSwing {
	
	
	Window localCoordinates = new Window(0f,0f,1f,1f);
	Experiment e;
	
	final static int DEFAULT_WALL_THICKNESS = 2;
	int wallThickness;
	private float[] relativePosition;
	private String group;
	private String run_id;
	private String trial;
	private String episode;
	private int size;
	private String cycle;
	
	private Color color = Color.BLACK;
	

	public CycleDataDrawer() {
		this(0.8f,0.95f,16);
	}
	
	
	public CycleDataDrawer(float x, float y,int size){
		relativePosition = new float[] {x, y};
		this.size = size;
		e = Experiment.get();
		this.group   = e.getGlobal("group");
		this.run_id = e.getGlobal("run_id").toString();
		
		
	}

	@Override
	public void draw(Graphics g, Window panelCoordinates) {
		if(!doDraw) return;
		
		Scaler s = new Scaler(localCoordinates, panelCoordinates, false);
		
		var upperLeftCorner = s.scale(relativePosition);
		int x = (int)upperLeftCorner[0];
		int y = (int)upperLeftCorner[1];
		
		
		g.setColor(color);
//		graphics.setFont(font);
		g.drawString("GroupID: " + group + " - " + run_id , x, y);
		g.drawString("Trial:   " + trial, x , y+(size+2));
		g.drawString("Episode: " + episode, x, y+2*(size+2));
		g.drawString("Cycle:   " + cycle, x, y+3*(size+2));
//		graphics.drawString("Cycle: " + Globals.getInstance().get("cycle").toString(), x, y);
		
		
		
	}
	

	@Override
	public void updateData() {
		
		trial   = e.getGlobal("trial");
		episode = e.getGlobal("episode").toString();
		cycle   = e.getGlobal("cycle").toString();
				

		
	}

}
