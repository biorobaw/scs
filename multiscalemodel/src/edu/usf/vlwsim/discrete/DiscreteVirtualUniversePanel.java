package edu.usf.vlwsim.discrete;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.display.drawer.WallDrawer;
import edu.usf.experiment.display.drawer.discrete.DiscretePlatformDrawer;
import edu.usf.experiment.display.drawer.discrete.DiscreteRobotDrawer;
import edu.usf.experiment.display.drawer.discrete.GridDrawer;

/**
 * A panel to show VirtualUniverse data, including walls, feeders, platforms and
 * the robot position.
 * 
 * @author martin
 *
 */
public class DiscreteVirtualUniversePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 181343702650705507L;
	/**
	 * Margins to pad the component and ensure good visualization of elements in
	 * the borders
	 */
	private static final float XMARGIN = 10;
	private static final float YMARGIN = 10;
	private DiscreteVirtualUniverse u;
	private List<Drawer> drawers;

	public DiscreteVirtualUniversePanel(DiscreteVirtualUniverse u) {
		this.drawers = new LinkedList<Drawer>();

		// drawers.add(new RectangleDrawer());
		drawers.add(new DiscretePlatformDrawer(u));
		drawers.add(new GridDrawer(u));
		drawers.add(new WallDrawer(u));
		drawers.add(new DiscreteRobotDrawer(u));

		
		this.u = u;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Define scaling factors
		Float univRect = new Rectangle2D.Float(0, 0, u.getGridWidth(), u.getGridWidth());
		Dimension panelRect = getSize();

		// The scaling factors are the relation between effective draw space and
		// the universe bounding box (taken from the xml file for the maze)
		float xscale = (float) ((panelRect.getWidth() - 2 * XMARGIN) / univRect.width);
		float yscale = (float) ((panelRect.getHeight() - 2 * YMARGIN) / univRect.height);
		// Take the minimum of both scales to keep aspect ratio
		float defScale = Math.min(xscale, yscale);
		// The x offset is just the lowest x coordinate of the universe
		float xoffset = -(univRect.x - XMARGIN / defScale);
		// The y offset includes the bounding box height, to be able to invert
		// the y component (it grows to the bottom in the screen)
		float yoffset = -(univRect.height + univRect.y + YMARGIN / defScale);
		Scaler s = new Scaler(defScale, defScale, xoffset, yoffset);

		for (Drawer d : drawers)
			d.draw(g, s);

	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 400);
	}
}
