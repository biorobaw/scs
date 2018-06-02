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
		Float worldCoordinates = new Rectangle2D.Float(0, 0, u.getGridWidth(), u.getGridWidth());
		Dimension panelRect = getSize();
		Float panelCoordinates = new Float(XMARGIN,YMARGIN,panelRect.width-2*XMARGIN,panelRect.height-2*YMARGIN);


		Scaler s = new Scaler(worldCoordinates,panelCoordinates,true);

		for (Drawer d : drawers)
			d.draw(g, s);

	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 400);
	}
}
