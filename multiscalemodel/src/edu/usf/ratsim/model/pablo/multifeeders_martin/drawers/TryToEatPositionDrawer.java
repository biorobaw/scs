package edu.usf.ratsim.model.pablo.multifeeders_martin.drawers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.GuiUtils;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.micronsl.port.onedimensional.vector.PointPort;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.input.SubjectTriedToEat;

public class TryToEatPositionDrawer extends Drawer {

	private static final int RADIUS = 6;
	
	
	ArrayList<Coordinate> positions = new ArrayList<>();

	
	Color color = GuiUtils.getHSBAColor(0.33f, 0.8f, 0.6f, 1f);
	
	SubjectTriedToEat triedToEat;


	private Position pos;
	
	SubjectAte ate;
	
	public TryToEatPositionDrawer(SubjectTriedToEat triedToEat,Position pos,SubjectAte ate){
		this.triedToEat = triedToEat;
		this.pos = pos;
		this.ate = ate;
	}

	@Override
	public void draw(Graphics g, java.awt.geom.Rectangle2D.Float panelCoordinates) {
		if(!doDraw) return;
		
		BoundedUniverse bu = (BoundedUniverse)Universe.getUniverse();
		if(bu==null) return;
		Scaler s = new Scaler(bu.getBoundingRect(), panelCoordinates, true);
		
		for (Coordinate c : positions){
			Point pos = s.scale(c);
			g.setColor(Color.MAGENTA);
			g.fillOval(pos.x - RADIUS, pos.y - RADIUS,2*RADIUS,2*RADIUS);
		}

		
	}

	//array to store position that had been added since last update
	ArrayList<Coordinate> aux = new ArrayList<>();
	Boolean ateLastIteration = false;
	
	@Override
	public void newEpisode() {
		aux.clear();
		positions.clear();
		ateLastIteration = false;
	}
	
	
	
	@Override
	public void appendData() {
		// TODO Auto-generated method stub
		super.appendData();
		
		if(ateLastIteration) {
			ateLastIteration = false;
			aux.clear();
			positions.clear();
		}
		ateLastIteration = ate.outPort.get();
		
		if(((Bool0dPort)triedToEat.getOutPort("subTriedToEat")).get()) {
			Coordinate p = ((PointPort)pos.getOutPort("position")).get();
			aux.add(new Coordinate(p));
		}
	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		positions.addAll(aux);
		aux.clear();
		
	}

	public void setColor(Color c){
		color = c;
	}

}
