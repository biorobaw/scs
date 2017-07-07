package edu.usf.ratsim.model.taxi.discrete;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.robot.GlobalWallRobot;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.ratsim.nsl.modules.cell.DiscretePlaceCell;

/**
 * Draws the activation of a discrete place cell in the environment
 * @author martin
 *
 */
public class DPCDrawer implements Drawer {


	private BoundedUniverse bu;
	private DiscretePlaceCell pc;
	private GlobalWallRobot robot;

	public DPCDrawer(BoundedUniverse bu, DiscretePlaceCell pc, GlobalWallRobot robot) {
		this.bu = bu;
		this.pc = pc;
		this.robot = robot;
	}

	@Override
	public void draw(Graphics g, Scaler s) {
		
		for (int x = 0; x < bu.getBoundingRect().getWidth(); x++)
			for (int y = 0; y < bu.getBoundingRect().getHeight(); y++){
				Point ul = s.scale(new Coordinate(x, y + 1));
				Point lr = s.scale(new Coordinate(x + 1, y));
				float a = pc.getActivation(x, y, robot);
				Color b = new Color(1,0,0,a);
				System.out.println(a);
				g.setColor(b);
				g.fillRect(ul.x, ul.y, Math.abs(lr.x - ul.x),  Math.abs(ul.y - lr.y));
			}
	}
	
	@Override
	public void clearState() {
		
	}

}
