package com.github.biorobaw.scs.gui.displays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.gui.Display;
import com.github.biorobaw.scs.gui.DrawPanel;
import com.github.biorobaw.scs.gui.DrawPanel.GuiPanel;
import com.github.biorobaw.scs.gui.displays.scs_swing.DrawerSwing;
import com.github.biorobaw.scs.gui.displays.scs_swing.WrapLayout;
import com.github.biorobaw.scs.gui.Drawer;
import com.github.biorobaw.scs.gui.utils.VideoRecorder;
import com.github.biorobaw.scs.gui.utils.Window;
import com.github.biorobaw.scs.simulation.SimulationControl;
import com.github.biorobaw.scs.utils.files.XML;

/**
 * A Java Swing frame to display the SCS data.
 * @author martin
 *
 */
public class DisplaySwing extends Display  {

	// =============================================================================================
	// ==================== VARIABLE DECLARATION ===================================================
	// =============================================================================================
	
	private static final int PADDING = 10;
	private final JFrame mainFrame;
	private final DrawPanel uPanel; // universe panel
	private final JPanel plotsPanel; 	 // panel to place plots
	private JPanel cbPanel;				 // panel to contain check boxes
	
	private JButton buttonPause;		// button to pause simulation
	private JButton buttonStep;			// button to advance simulation by one cycle
	
	private HashMap<Drawer,JCheckBox> cBoxes; // drawer check boxes
	
	VideoRecorder recorder = null;
	
	// =============================================================================================
	// ============= CONSTRUCTOR AND OVERWRITEN METHODS ============================================
	// =============================================================================================
	
	public DisplaySwing(XML xml){
		super(xml);
		//create main frame:
		mainFrame = new JFrame();
		
		//create a synchronizable content pane
		mainFrame.setContentPane(new DrawableContentPane());
		
		
		//init frame properties
		mainFrame.setLayout(new GridBagLayout());
		mainFrame.setTitle("Spatial Cognition Simulator");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		//create and add checkbox panel
		cbPanel = new JPanel();
		cBoxes = new HashMap<Drawer, JCheckBox>();		
		cbPanel.setLayout(new WrapLayout());
		mainFrame.add(cbPanel, createGridConstraints(0, 0, 0, 1));
		
		// Create and add Plot panel
		plotsPanel = new JPanel();
		plotsPanel.setBackground(Color.GRAY);
		plotsPanel.setLayout(new GridBagLayout());
		mainFrame.add(plotsPanel, createGridConstraints(0, 1, 1, 0));
		
		// create and add universePanel
		uPanel = createUniversePanel(); 

		
		// Create and add Control Panel
		mainFrame.add(createControlPanel(),createGridConstraints(1,2, 1, 1));
			
		
		//create and add key press listener (must be called after all gui has been created)
		initKeyListener();
		
		
		// set a close operation:
		mainFrame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	if(recorder!= null) recorder.endRecording();
		    }
		});
		
		
		//pack and set visibility
		mainFrame.pack();
		int w = xml.hasAttribute("width") ? xml.getIntAttribute("width") : 800;
		int h = xml.hasAttribute("height") ? xml.getIntAttribute("height") : 800;
		mainFrame.setVisible(true);
		mainFrame.setSize(w, h);
		mainFrame.repaint();
		
		// generate
		if(xml.hasAttribute("record") && xml.getBooleanAttribute("record")) init_record_screen(w, h);

		
		
	}

	@Override
	public void log(String s) {
		System.out.println(s);
	}
	
	@Override
	public void addPanel(DrawPanel panel,String id,int gridx, int gridy, int gridwidth, int gridheight) {
		super.addPanel(panel, id, gridx, gridy, gridwidth, gridheight);
		
		plotsPanel.add(new SwingPanel(panel), createGridConstraints(gridx, gridy, gridwidth, gridheight));		
		repack();
		
		
	}
	
	@Override
	public void addDrawer(String panelID,String drawerID , Drawer d) {
		super.addDrawer(panelID, drawerID, d);	
		addCheckbox(d);
	}
	
	@Override
	public void repaint() {
		mainFrame.repaint();
	}

	@Override 
	protected void recordRenderCycle() {
		if(recorder == null) return;
		var bounds = mainFrame.getBounds();
		recorder.update_bounds(bounds);
	}
	
	// =============================================================================================
	// ===================== GUI CONSTRUCTION METHODS ==============================================
	// =============================================================================================
	
	/**
	 * Add a check box that enables / disables a drawer
	 * @param d Drawer to be controlled by the check box
	 */
	private void addCheckbox(Drawer d) {
		JCheckBox cb = new JCheckBox(d.getClass().getSimpleName(), true);
		cb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//repaint screen
				d.setDraw(cb.isSelected());
				mainFrame.repaint();
			}
		});
		
		cbPanel.add(cb);
		cBoxes.put(d, cb);
		repack();
	}
	
	/**
	 * Creates default grid constraints to add panels in a grid at coordinates x, y
	 * @param x	
	 * @param y
	 * @return
	 */
	private GridBagConstraints createGridConstraints(int x, int y, int w, int h){
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = x;
		gridBagConstraints.gridy = y;
		gridBagConstraints.gridwidth = w;
		gridBagConstraints.gridheight = h;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.insets = new Insets(PADDING, PADDING, PADDING, PADDING);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		return gridBagConstraints;
	}
	
	
	/*
	 * creates the panel with simulation controls:
	 * example speed, and step by step execution controls
	 */
	JPanel createControlPanel() {
		//Create the panel and set layout
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridBagLayout());
		
		// Create slider for simulation velocity
		int simSpeed = SimulationControl.getSimulationSpeed();
		JSlider simVel = new JSlider(JSlider.HORIZONTAL,0, SimulationControl.sleepValues.length-1, simSpeed);
		simVel.setPreferredSize(new Dimension(300, 50));
		simVel.setMajorTickSpacing(1);
		simVel.setMinorTickSpacing(1);
		simVel.setPaintTicks(true);
		simVel.setPaintLabels(true);
		simVel.addChangeListener(e -> {
		    if (!simVel.getValueIsAdjusting()) {
		        int vel = (int)simVel.getValue();
		        SimulationControl.setSimulationSpeed(vel);
		    }
		});
		
		//create step button
		buttonStep = new JButton("Step");
		buttonStep.setEnabled(false);
		buttonStep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SimulationControl.produceStep();
			}
		});
		
		//Create pause button
		buttonPause = new JButton("Pause");
		buttonStep.setEnabled(false);
		buttonPause.addActionListener(e-> {
			SimulationControl.togglePause();
			
		});
		SimulationControl.addPauseStateListener(  pauseValue -> {
			if(pauseValue) {
				buttonPause.setText("Resume");
				buttonStep.setEnabled(true);
			}else {
				buttonPause.setText("Pause");
				buttonStep.setEnabled(false);
			}			
			return null;
		});
		
		
		
		//Add elements in the panel:
		controlPanel.add(simVel, createGridConstraints(0, 0, 1, 1));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(buttonPause);
		buttonPanel.add(buttonStep);
		controlPanel.add(buttonPanel,createGridConstraints(0,1,1,1));
				
		return controlPanel;
	}

	
	/*
	 * Initializes the key listener
	 * Keys space and left are defaulted to pause and step respectively
	 */
	public void initKeyListener() {
		KeyEventDispatcher dispatcher = new KeyEventDispatcher() {
			  @Override
			  public boolean dispatchKeyEvent(final KeyEvent e) {
				  return e.getID() == KeyEvent.KEY_PRESSED && 
						  performKeyAction(e.getKeyCode(), e.isControlDown(), e.isShiftDown(), e.isAltDown());

			  }
		};
		
		
		//Add key commands
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
		addKeyAction(KeyEvent.VK_SPACE, "", ()-> SimulationControl.togglePause() );
		addKeyAction(KeyEvent.VK_RIGHT,	"", () -> SimulationControl.produceStep());
		addKeyAction(KeyEvent.VK_K, 	"", () -> toggleSync());
		
		
	}
	

	/**
	 * Initializes screen recording
	 * @param width  output video width in pixels
	 * @param height output video height in pixels
	 */
	void init_record_screen(int width, int height) {
		if(recorder != null) return;
		// generate path for video file:
		final var g = Experiment.get();
		var video_path = g.getGlobal("baseLogPath").toString() + "/videos/";
		new File(video_path ).mkdirs();
		var run_id = g.getGlobal("run_id").toString();
		var config = g.getGlobal("config").toString();
		var video_file = video_path + "video_" + config + "_" + run_id + ".avi";
		recorder = new VideoRecorder( video_file, width, height);
	}
	
	/**
	 * Make the GUI fit its preferred size and layout
	 */
	void repack(){
		mainFrame.setMinimumSize(mainFrame.getSize());
		mainFrame.pack();
		mainFrame.setMinimumSize(null);
	}

	DrawPanel createUniversePanel() {
		var uPanel = new DrawPanel(600,600);
		uPanel.setParent(this);
		uPanel.setWorldCoordinates(defaultCoordinates.copy());
		uPanel.setName("universe");
		drawPanels.put("universe", uPanel);
		GridBagConstraints uPanelCons = createGridConstraints(1, 1, 1, 1);
		uPanelCons.weighty = 100;
		
		mainFrame.add(new SwingPanel(uPanel), uPanelCons);
		return uPanel;
	}
	
	// =============================================================================================
	// ======================== UTILITY METHODS AND CHILD CLASSES ==================================
	// =============================================================================================
	
	class DrawableContentPane extends JPanel {

		private static final long serialVersionUID = -3864715479896892293L;

		@Override
		public void paint(Graphics g) {
			
//			synchronized (DisplaySwing.this) {
				super.paint(g);
//			}
			
		}
		

	}
	
	
	public class SwingPanel extends JPanel implements GuiPanel {
		
		private static final long serialVersionUID = 1L;
		public DrawPanel draw_panel;
		
		public SwingPanel(DrawPanel panel) {
			this.draw_panel = panel;
			panel.setGuiPanel(this);
			panel.gui_panel = this;
			setMinimumSize(new Dimension(panel.min_size_x, panel.min_size_y));
			this.setName(panel.panelName);
		}
		
		@Override
		public void paint(Graphics g) {

			// Since we don't have control over when paint executes,
			// we need to synchronize it with the panels append function (which updates data to be displayed)
//			synchronized (draw_panel) {
				super.paint(g);
				
				draw_panel.paintPanel(g, getCoordinates());
//			}
		}
		
		public Window getCoordinates(){
			return new Window(0f, 0f, (float)getWidth(), (float)getHeight());
		}

		@Override
		public void addDrawer(Drawer drawer) {
			// In the swing framework, drawers are just functions to paint a panel
			// so nothing needs to be done.
		}
		
		@Override
		public void callDrawers(Object... args) {
			if(args.length != 2) return;
			if(!(args[0] instanceof Graphics)) return;
			if(!(args[1] instanceof Window)) return;
			
			for (Drawer d : draw_panel.drawers){
				if(d instanceof DrawerSwing) 
					synchronized(d) {
						((DrawerSwing)d).draw((Graphics)args[0], (Window)args[1]);						
					}
					
		
			}
			
			
		}
		
	}
	
	
	

}
