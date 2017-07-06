package edu.usf.experiment.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D.Float;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.UIManager;

import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.BoundedUniverse;

/**
 * A panel to show VirtualUniverse data, including walls, feeders, platforms and
 * the robot position.
 * 
 * @author martin
 *
 */
public class UniversePanel extends JPanel {

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
	private List<Drawer> drawers;
	private BoundedUniverse bu;
	private JPanel cbPanel;
	private HashMap<Drawer,JCheckBox> cBoxes;

	public UniversePanel(BoundedUniverse bu) {
		this.drawers = new LinkedList<Drawer>();
		
		cBoxes = new HashMap<Drawer, JCheckBox>();
		cbPanel = new JPanel();
		cbPanel.setLayout(new BoxLayout(cbPanel, BoxLayout.X_AXIS));
		
		this.bu = bu;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
			
		// Define scaling factors
		Float univRect = bu.getBoundingRect();
		Dimension panelRect = getSize();

		// The scaling factors are the relation between effective draw space and
		// the universe bounding box (taken from the xml file for the maze)
		float xscale = (float) ((panelRect.width - 2 * XMARGIN) / univRect.width);
		float yscale = (float) ((panelRect.height - 2 * YMARGIN) / univRect.height);
		// Take the minimum of both scales to keep aspect ratio
		float defScale = Math.min(xscale, yscale);
		// The x offset is just the lowest x coordinate of the universe
		float xoffset = -(univRect.x - XMARGIN / defScale);
		// The y offset includes the bounding box height, to be able to invert
		// the y component (it grows to the bottom in the screen)
		float yoffset = -(univRect.height + univRect.y + YMARGIN / defScale);
		Scaler s = new Scaler(defScale, defScale, xoffset, yoffset);
		
		// Erase previous paintings
		g.setColor(new Color(239, 239, 239, 255));
		g.fillRect(0, 0, panelRect.width, panelRect.height);

		// Draw all layers
		for (Drawer d : drawers){
			if (cBoxes.get(d).isSelected())
				d.draw(g, s);
		}
		
			

	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(600, 600);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(600, 600);
	}
	
	public void removeDrawer(Drawer d){
		drawers.remove(d);
	}

	public void addDrawer(Drawer d, int pos) {
		JCheckBox cb = new JCheckBox(d.getClass().getSimpleName(), true);
		cbPanel.add(cb);
		
		cBoxes.put(d, cb);
		
		drawers.add(pos, d);
	}
	
	public void addDrawer(Drawer d){
		addDrawer(d, drawers.size());
	}
	
	public JPanel getCheckBoxPanel(){
		return cbPanel;
	}

	public void clearState() {
		for (Drawer d : drawers)
			d.clearState();
	}
}
