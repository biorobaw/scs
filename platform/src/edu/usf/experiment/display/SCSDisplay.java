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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Globals;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.utils.Debug;

/**
 * A Java Swing frame to display the SCS data.
 * @author martin
 *
 */
public class SCSDisplay extends JFrame implements Display, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6489459171441343768L;
	private static final int PADDING = 10;
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
	private boolean waitingPreviousFrame ; // boolean which indicates whether a thread is waiting to finish rendering last frame
	private boolean doneRenderingLastCycle = true; //indicates whether last cycle has been drawn
	int renderCycle = -2; // cycle being rednered
	int remainingPanels = -1; //panels that still need to render current cycle
	
	
	public SCSDisplay(){
		//create a synchronizable content pane
		setContentPane(new DrawableContentPane());
		
		
		//init frame properties
		setLayout(new GridBagLayout());
		setTitle("Spatial Cognition Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//create and add checkbox panel
		cbPanel = new JPanel();
//		cbPanel.setBackground(Color.red);
		cBoxes = new HashMap<Drawer, JCheckBox>();		
//		cbPanel.setLayout(new BoxLayout(cbPanel, BoxLayout.Y_AXIS));
		cbPanel.setLayout(new WrapLayout());
//		for(int i=0;i<100;i++)cbPanel.add(new JCheckBox("Hola hola hola"));
		GridBagConstraints cbConstraints = getConstraints(0, 0);
		cbConstraints.gridwidth = 0;
		add(cbPanel, cbConstraints);
		
		// Create and add Plot panel
		plotsPanel = new JPanel();
		plotsPanel.setBackground(Color.GRAY);
		plotsPanel.setLayout(new GridBagLayout());
		GridBagConstraints cs = getConstraints(0, 1);
		cs.gridheight = 0;
//		cs.gridwidth = 0;
		add(plotsPanel, cs);
		
		// create and add universePanel
		uPanel = new DrawPanel(600,600);
		uPanel.setParent(this);
		drawPanels.put("universe", uPanel);
		uPanel.setName("univers");
		GridBagConstraints uPanelCons = getConstraints(1,1);
		uPanelCons.weighty = 100;
		add(uPanel, uPanelCons);

		
		// Create and add Control Panel
		add(createControlPanel(),getConstraints(1,2));
			
		
		//create and add key press listener (must be called after all gui has been created)
		initKeyListener();
		
		
		//set synchronization:
		System.out.println("Synchronizing display: " + Globals.getInstance().get("syncDisplay"));
		
		synchronized(this){
			syncDisplay = (Boolean)Globals.getInstance().get("syncDisplay");
		}
		
		//pack and set visibility
		pack();
		setVisible(true);
		setSize(800,800 );
		repaint();

		
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
	
	void repack(){
		setMinimumSize(getSize());
		pack();
		setMinimumSize(null);
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
	

	@Override
	public synchronized void newEpisode() {
		Display.super.newEpisode();
	}
	
	@Override
	public synchronized void endEpisode() {
		Display.super.endEpisode();
	}
	
	@Override
	public synchronized void newTrial() {
		Display.super.newTrial();
	}
	
	@Override
	public synchronized void endTrial() {
		Display.super.endTrial();
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
		int simSpeed = (int)Globals.getInstance().get("simulationSpeed");
		JSlider simVel = new JSlider(JSlider.HORIZONTAL,0, 9, simSpeed);
		simVel.setPreferredSize(new Dimension(300, 50));
		simVel.addChangeListener(this);
		simVel.setMajorTickSpacing(1);
		simVel.setMinorTickSpacing(1);
		simVel.setPaintTicks(true);
		simVel.setPaintLabels(true);
		
		//create step button
		buttonStep = new JButton("Step");
		buttonStep.setEnabled(false);
		buttonStep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub	
				Episode.step();				
			}
		});
		
		//Create pause button
		buttonPause = new JButton("Pause");
		buttonStep.setEnabled(false);
		buttonPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub	
				if(Episode.togglePause()) {
					buttonPause.setText("Resume");
					buttonStep.setEnabled(true);
				}else {
					buttonPause.setText("Pause");
					buttonStep.setEnabled(false);
				}			
			}
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
//			      else System.out.println("No runnable found... "+e.getKeyCode() + " " + KeyEvent.VK_RIGHT);
			    }
			    // Pass the KeyEvent to the next KeyEventDispatcher in the chain
			    return true;
			  }
			};
		
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
		
		keyActions.put(KeyEvent.VK_SPACE, new Runnable() {
			
			@Override
			public void run() {
				if(Episode.togglePause()) {
					buttonPause.setText("Resume");
					buttonStep.setEnabled(true);
				}else {
					buttonPause.setText("Pause");
					buttonStep.setEnabled(false);
				}	
				
			}
		});
		
		keyActions.put(KeyEvent.VK_RIGHT,new Runnable() {
			
			@Override
			public void run() {
				Episode.step();
				
			}
		});
		
		keyActions.put(KeyEvent.VK_K, new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				toggleSync();
			}
			
		});
		
		
		
	}
	
	
	
	
	long renderCycleTime = 0;
	@Override
	public void  updateData() {	
		
		waitFinishRenderingLastCycle();
		
		boolean signalRepaint = false;

		
		for(Drawer d : drawers.values()) d.appendData();
		synchronized(SCSDisplay.this){	
		
			if (doneRenderingLastCycle ){
				
				renderCycleTime = Debug.tic();
				
				renderCycle++;
				doneRenderingLastCycle = false;
				for(Drawer d : drawers.values()) d.updateData();
				remainingPanels = drawPanels.size();
				for(DrawPanel p :drawPanels.values()) p.setRenderCycle(renderCycle);
				signalRepaint = true;
			}	
		}
		if(signalRepaint) repaint();
		
		
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
	
	public synchronized boolean isWaitingNecessary(){
		waitingPreviousFrame = syncDisplay && !doneRenderingLastCycle;
//		System.out.println("waiting render cycle " + renderCycle + ": " + remainingPanels +"/" + drawPanels.size());
		return waitingPreviousFrame;
	}
	

	


	
	public synchronized void sync(long cycle) {
//		System.out.println("finished rendering panel of cycle " + cycle + ", current: " + renderCycle + ", rem "+(remainingPanels-1));
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
	
	
	public synchronized void toggleSync(){
		syncDisplay = !syncDisplay;
		if(!syncDisplay)
		{
				
			if(waitingPreviousFrame){
				waitingPreviousFrame = false;
				doneRenderLock.release();
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
