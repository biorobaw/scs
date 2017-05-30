package edu.usf.experiment.display;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.usf.experiment.Globals;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.universe.BoundedUniverse;

/**
 * A Java Swing frame to display the SCS data.
 * @author martin
 *
 */
public class SCSFrame extends JFrame implements Display, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6489459171441343768L;
	private static final int PADDING = 10;
	private UniversePanel uPanel;
	

	public SCSFrame(){
		setLayout(new GridBagLayout());
		setTitle("Spatial Cognition Simulator");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		// Add slider for simulation velocity
		Globals g = Globals.getInstance();
		g.put("simulationSpeed", 9);
		JSlider simVel = new JSlider(JSlider.HORIZONTAL,
                0, 9, 9);
		simVel.addChangeListener(this);
		
		//Turn on labels at major tick marks.
		simVel.setMajorTickSpacing(1);
		simVel.setMinorTickSpacing(1);
		simVel.setPaintTicks(true);
		simVel.setPaintLabels(true);
		
		addComponent(simVel, 1, 1, 1, 1);
		
		setVisible(true);
	}
	
	public void addComponent(JComponent comp, int gridx, int gridy, int gridwidth, int gridheight){
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.insets = new Insets(PADDING, PADDING, PADDING, PADDING);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		add(comp, gridBagConstraints);
		
		pack();
	}

	@Override
	public void log(String s) {
		System.out.println(s);
	}
	
	@Override
	public void repaint(){
		super.repaint();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
	    if (!source.getValueIsAdjusting()) {
	        int vel = (int)source.getValue();
	        Globals g = Globals.getInstance();
			g.put("simulationSpeed", vel);
	    }
	}

	@Override
	public void addUniverseDrawer(Drawer d) {
		uPanel.addDrawer(d);
	}

	@Override
	public void setupUniversePanel(BoundedUniverse bu) {
		uPanel = new UniversePanel(bu);
		addComponent(uPanel, 1, 0, 1, 1);		
	}

	@Override
	public void addUniverseDrawer(Drawer d, int pos) {
		uPanel.addDrawer(d, pos);
		
	}
	
}
