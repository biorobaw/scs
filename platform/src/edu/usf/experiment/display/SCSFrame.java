package edu.usf.experiment.display;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
	private static final int INITIAL_SPEED = 1;
	private UniversePanel uPanel;
	private JPanel plotsPanel;
	private JPanel uViewPanel;
	

	public SCSFrame(){
		setLayout(new GridBagLayout());
		setTitle("Spatial Cognition Simulator");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Add slider for simulation velocity
		Globals g = Globals.getInstance();
		g.put("simulationSpeed", INITIAL_SPEED);
		JSlider simVel = new JSlider(JSlider.HORIZONTAL,
                0, 9, INITIAL_SPEED);
		simVel.setPreferredSize(new Dimension(300, 50));
		simVel.addChangeListener(this);
		
		// Layout panels
		plotsPanel = new JPanel();
		uViewPanel = new JPanel();
		plotsPanel.setLayout(new GridBagLayout());
		uViewPanel.setLayout(new GridBagLayout());
		add(plotsPanel, getConstraints(0, 0));
		add(uViewPanel, getConstraints(1, 0));
		
		//Turn on labels at major tick marks.
		simVel.setMajorTickSpacing(1);
		simVel.setMinorTickSpacing(1);
		simVel.setPaintTicks(true);
		simVel.setPaintLabels(true);
		GridBagConstraints cs = getConstraints(0, 1);
		cs.gridwidth = 2;
		uViewPanel.add(simVel, cs);
		
		pack();
		setVisible(true);
		
		simVel.requestFocus();
	}
	
	public void addPlot(JComponent comp, int gridx, int gridy, int gridwidth, int gridheight){
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.insets = new Insets(PADDING, PADDING, PADDING, PADDING);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		plotsPanel.add(comp, gridBagConstraints);
		
		pack();
	}

	@Override
	public void log(String s) {
		System.out.println(s);
	}
	
	@Override
	public void repaint(){
//		paintAll(getGraphics());
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
		pack();
	}

	@Override
	public void setupUniversePanel(BoundedUniverse bu) {
		uPanel = new UniversePanel(bu);
		uViewPanel.add(uPanel.getCheckBoxPanel(), getConstraints(0, 0));
		GridBagConstraints uPanelCons = getConstraints(1,0);
		uPanelCons.weighty = 100;
		uViewPanel.add(uPanel, uPanelCons);

		pack();
	}
	
	private GridBagConstraints getConstraints(int x, int y){
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = x;
		gridBagConstraints.gridy = y;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.insets = new Insets(PADDING, PADDING, PADDING, PADDING);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		return gridBagConstraints;
	}

	@Override
	public void addUniverseDrawer(Drawer d, int pos) {
		uPanel.addDrawer(d, pos);
	}

	@Override
	public void newEpisode() {
		uPanel.clearState();
	}
	
}
