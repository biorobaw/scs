package edu.usf.ratsim.experiment.universe.virtual;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.util.LinkedList;

import javax.media.j3d.Canvas3D;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.j3d.utils.universe.SimpleUniverse;

import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawingFunction;

public class UniverseFrame extends JFrame {

	private static final long serialVersionUID = -698020368303861261L;

	private Canvas3D topViewCanvas;
	private JPanel panel1;
	private JPanel topViewPanel;

	private VirtUniverse expUniv;
	
	LinkedList<Runnable> drawingFunctions = new LinkedList<Runnable>();

	public UniverseFrame(VirtUniverse world) {
		this.expUniv = world;

		initComponents();

		// Create the canvases
		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();
		
		topViewCanvas = new Canvas3D(config){
			private static final long serialVersionUID = 2278728176596780651L;
			
			public void postRender()
	        {
				
				for (Runnable r : drawingFunctions)
					r.run();
				
	        }
		};
		world.getTopView().addCanvas3D(topViewCanvas);
		topViewCanvas.setSize(new Dimension(100,100));
		topViewPanel.add(topViewCanvas);
		
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);

	}
	
	public void addDrawingFunction(DrawingFunction function){
		function.setGraphics(topViewCanvas.getGraphics2D());
		drawingFunctions.push(function);
	}

	private void initComponents() {
		setFocusableWindowState(false);

		java.awt.GridBagConstraints gridBagConstraints;

		setLayout(new java.awt.GridBagLayout());
		setTitle("SCS Virtual Universe Simulator");

		panel1 = new CmdsPanel(expUniv);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		add(panel1, gridBagConstraints);

		topViewPanel = new JPanel(new BorderLayout());
		topViewPanel.setBackground(new java.awt.Color(153, 244, 51));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		add(topViewPanel, gridBagConstraints);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
