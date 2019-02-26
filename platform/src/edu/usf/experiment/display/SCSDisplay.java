package edu.usf.experiment.display;

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
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

import edu.usf.experiment.Globals;
import edu.usf.experiment.SimulationControl;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.utils.Debug;

/**
 * A Java Swing frame to display the SCS data.
 * @author martin
 *
 */
public class SCSDisplay extends Display  {

	/**
	 * 
	 */
	private static final int PADDING = 10;
	private JFrame mainFrame;
	private DrawPanel uPanel;
	private JPanel plotsPanel;
	
	private JButton buttonPause;
	private JButton buttonStep;
	
	private HashMap<Integer,Runnable> keyActions = new HashMap<>();
	private HashMap<String,DrawPanel> drawPanels = new HashMap<>();
	
	
	private Semaphore doneRenderLock = new Semaphore(0);
	
	
	private JPanel cbPanel;
	private HashMap<Drawer,JCheckBox> cBoxes;
	
	//sync display variables
	boolean syncDisplay = false;
	private Integer mutexSync =0;
	private Boolean waitingPreviousFrame =false; // boolean which indicates whether a thread is waiting to finish rendering last frame
	private boolean doneRenderingLastCycle = true; //indicates whether last cycle has been drawn
	int renderCycle = -2; // cycle being rendered
	int remainingPanels = -1; //panels that still need to render current cycle
	
	
	public SCSDisplay(){
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
//		cbPanel.setBackground(Color.red);
		cBoxes = new HashMap<Drawer, JCheckBox>();		
//		cbPanel.setLayout(new BoxLayout(cbPanel, BoxLayout.Y_AXIS));
		cbPanel.setLayout(new WrapLayout());
//		for(int i=0;i<100;i++)cbPanel.add(new JCheckBox("Hola hola hola"));
		GridBagConstraints cbConstraints = getConstraints(0, 0);
		cbConstraints.gridwidth = 0;
		mainFrame.add(cbPanel, cbConstraints);
		
		// Create and add Plot panel
		plotsPanel = new JPanel();
		plotsPanel.setBackground(Color.GRAY);
		plotsPanel.setLayout(new GridBagLayout());
		GridBagConstraints cs = getConstraints(0, 1);
		cs.gridheight = 0;
//		cs.gridwidth = 0;
		mainFrame.add(plotsPanel, cs);
		
		// create and add universePanel
		uPanel = new DrawPanel(600,600);
		uPanel.setParent(this);
		drawPanels.put("universe", uPanel);
		uPanel.setName("univers");
		GridBagConstraints uPanelCons = getConstraints(1,1);
		uPanelCons.weighty = 100;
		mainFrame.add(uPanel, uPanelCons);

		
		// Create and add Control Panel
		mainFrame.add(createControlPanel(),getConstraints(1,2));
			
		
		//create and add key press listener (must be called after all gui has been created)
		initKeyListener();
		
		
		//set synchronization:
		System.out.println("Synchronizing display: " + Globals.getInstance().get("syncDisplay"));
		
		
		syncDisplay = (Boolean)Globals.getInstance().get("syncDisplay");
		
		//pack and set visibility
		mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.setSize(800,800 );
		repaint();

		
	}

	
	void repack(){
		mainFrame.setMinimumSize(mainFrame.getSize());
		mainFrame.pack();
		mainFrame.setMinimumSize(null);
	}
	
	@Override
	public void log(String s) {
		System.out.println(s);
	}
	
	@Override
	public void repaint(){
		mainFrame.repaint();
	}
	
	
	public void addPanel(DrawPanel panel,String id,int gridx, int gridy, int gridwidth, int gridheight) {
		panel.setParent(this);
		panel.setName(id);
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.insets = new Insets(PADDING, PADDING, PADDING, PADDING);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		plotsPanel.add(panel, gridBagConstraints);
		drawPanels.put(id, panel);
		
		repack();
		
		
	}
	
	
	
	private void addCheckbox(Drawer d) {
		JCheckBox cb = new JCheckBox(d.getClass().getSimpleName(), true);
		cb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//repaint screen
				d.setDraw(cb.isSelected());
				repaint();
			}
		});
		
		cbPanel.add(cb);
		cBoxes.put(d, cb);
		repack();
	}
	
	@Override
	public void addDrawer(String panelID,String drawerID , Drawer d) {
		DrawPanel panel = drawPanels.get(panelID);
		d.setName(drawerID);
		panel.addDrawer(d);		
		drawers.put(drawerID, d);	
		addCheckbox(d);
		
	}
	
	@Override
	public void addDrawer(String panelID, String drawerID, Drawer d, int pos) {
		DrawPanel panel = drawPanels.get(panelID);
		panel.addDrawer(d,pos);
		drawers.put(drawerID, d);	
		addCheckbox(d);
		repack();
	}
	

	@Override
	public void setupUniversePanel(BoundedUniverse bu) {
		uPanel.setCoordinateFrame(bu.getBoundingRect());
		
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
		JSlider simVel = new JSlider(JSlider.HORIZONTAL,0, 9, simSpeed);
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
				// TODO Auto-generated method stub	
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
		controlPanel.add(simVel, getConstraints(0, 0));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(buttonPause);
		buttonPanel.add(buttonStep);
		controlPanel.add(buttonPanel,getConstraints(0,1));
				
		return controlPanel;
	}

	/*cycle
	 * links a key to an action
	 * when ever the key is pressed the action is executed
	 * sample key:  KeyEvent.VK_LEFT
	 */
	
	@Override
	public void addKeyAction(int key, Runnable action) {
		// TODO Auto-generated method stub
		keyActions.put(key, action);
		
	}
	
	/*
	 * Initializes the key listener
	 * Keys space and left are defaulted to pause and step respectively
	 */
	
	public void initKeyListener() {
		KeyEventDispatcher dispatcher = new KeyEventDispatcher() {
			  @Override
			  public boolean dispatchKeyEvent(final KeyEvent e) {
			    if (e.getID() == KeyEvent.KEY_PRESSED) {
			      Runnable run = keyActions.get(e.getKeyCode());
			      if(run!=null) run.run();
			    }
			    return true;
			  }
		};
		
		
		//Add key commands
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
		keyActions.put(KeyEvent.VK_SPACE, ()-> SimulationControl.togglePause() );
		keyActions.put(KeyEvent.VK_RIGHT,() -> SimulationControl.produceStep());
		keyActions.put(KeyEvent.VK_K, () -> toggleSync());
		
		
	}
	
	
	
	
	long renderCycleTime = 0;
	@Override
	public void  updateData() {	
		waitFinishRenderingLastCycle();
		
		boolean signalRepaint = false;

		for(Drawer d : drawers.values()) d.appendData();
		synchronized(mutexSync){	
			if (doneRenderingLastCycle ){
				
				renderCycleTime = Debug.tic();
				renderCycle++;
				doneRenderingLastCycle = false;
				remainingPanels = drawPanels.size();
				signalRepaint = true;	
			}	
		}
		if(signalRepaint) {
			synchronized(this) {
				for(Drawer d : drawers.values()) d.updateData();
				for(DrawPanel p :drawPanels.values()) p.setRenderCycle(renderCycle);
			}
			
			repaint();
		}
		
	}
	
	
	void waitFinishRenderingLastCycle() {
		if( isWaitingNecessary() ) { //checks whether waiting is necessary, if so it sets a flag before waiting
			//wait until done rendering last cycle:
			long stamp = Debug.tic();
			try {
				doneRenderLock.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
			if(Debug.profiling) System.out.println("Waiting time: " + Debug.toc(stamp));
		} //else System.out.println("Not waiting...");
	}
	
	public boolean isWaitingNecessary(){
		boolean result;
		synchronized(mutexSync) {
			waitingPreviousFrame = syncDisplay && !doneRenderingLastCycle;
			result = waitingPreviousFrame;
		}
//		System.out.println("waiting render cycle " + renderCycle + ": " + remainingPanels +"/" + drawPanels.size());
		return result;
	}
	

	


	
	public void sync(long cycle) {
//		System.out.println("finished rendering panel of cycle " + cycle + ", current: " + renderCycle + ", rem "+(remainingPanels-1));
		
		synchronized (mutexSync) {
			if(cycle==renderCycle) { 
				remainingPanels = Math.max(--remainingPanels, -1);
				if(remainingPanels==0){
					if(Debug.profiling) System.out.println("Render cycle time: " + Debug.toc(renderCycleTime));
					doneRenderingLastCycle = true;
					
					if( waitingPreviousFrame) {
						waitingPreviousFrame = false;
						doneRenderLock.release();
					}					
					
					
				}
			}
		}
	}
	
	
	public void toggleSync(){
		
		synchronized (mutexSync) {
			syncDisplay = !syncDisplay;
			if(!syncDisplay)
			{
				if(waitingPreviousFrame){
					waitingPreviousFrame = false;
					doneRenderLock.release();
				}				
			}	
			
		}
	}
	
	
	class DrawableContentPane extends JPanel {
		@Override
		public void paint(Graphics g) {
			// TODO Auto-generated method stub
			
			synchronized (SCSDisplay.this) {
				super.paint(g);
			}
			
		}
		

	}	
	
	
}
