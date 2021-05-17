package com.github.biorobaw.scs.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.experiment.ExperimentController.State;
import com.github.biorobaw.scs.gui.Display.EventType;
import com.github.biorobaw.scs.gui.utils.Window;
import com.github.biorobaw.scs.simulation.SimulationControl;
import com.github.biorobaw.scs.tasks.cycle.CycleTask;
import com.github.biorobaw.scs.utils.Debug;
import com.github.biorobaw.scs.utils.files.XML;

/**
 * This interface represents a means of displaying information. 
 * @author martin, bucef
 *
 */
public abstract class Display extends CycleTask {
	
	// PANELS, DRAWERS AND GUI FUNCTIONS RELATED
	protected HashMap<String,DrawPanel> drawPanels 	= new HashMap<>(); 	// Panels to contain drawers
	private HashMap<String,Drawer>	drawers 		= new HashMap<>(); 	// Drawers to draw data (may be contained by multiple panels)
	HashSet<Drawer> drawers_append = new HashSet<>();			   	// Drawers that implement append function
	HashSet<Drawer> drawers_new_episode = new HashSet<>();		// Drawers that implement append function
	HashSet<Drawer> drawers_end_episode = new HashSet<>();		// Drawers that implement append function
	HashSet<Drawer> drawers_new_trial = new HashSet<>();			// Drawers that implement append function
	HashSet<Drawer> drawers_end_trial = new HashSet<>();			// Drawers that implement append function
	protected Window defaultCoordinates 			= new Window(-1f, -1f, 2f, 2f);
	
	private HashMap<Integer,Runnable> keyActions = new HashMap<>();
	
	// SYNCHRONIZATION 
	protected GoToScheduler go_to_scheduler = new GoToScheduler();
	private boolean update_display = true; 	// whether the display should be updated or not
	private boolean syncDisplay = false; 	// whether to sync the display with the simulation or not
	private boolean forceSyncDisplay = false;
	private Semaphore wait_for_render_lock = new Semaphore(0);
	private Integer mutexSync =0;
	private Boolean waitingPreviousFrame =false; // boolean which indicates whether a thread is waiting to finish rendering last frame
	private boolean doneRenderingLastCycle = true; //indicates whether last cycle has been drawn
	private int renderCycle = -2; // cycle being rendered
	private int remainingPanels = -1; //panels that still need to render current cycle
	
	private long max_frequency_time_ms = 25; // defines the period of the maximum display refresh rate
	protected boolean gui_closed = false;

	

	
	// DEBUGGING AND PROFILING
	private long renderCycleTime = 0;	// 
	
	
	public Experiment experiment = Experiment.get();
	
	// =============================================================================================
	// ============= CONSTRUCTOR AND METHODS TO BE OVERWRITEN BY SUBCLASSES ========================
	// =============================================================================================
	
	
	
	public Display(XML xml) {
		super(xml);
		if(xml.hasAttribute("window")) {
			var w = xml.getFloatArrayAttribute("window");
			defaultCoordinates = new Window(w[0],w[1],w[2],w[3]);
		}
		
		if(xml.hasAttribute("syncDisplay")) {
			syncDisplay = xml.getBooleanAttribute("syncDisplay");
		}
	}

	
	/**
	 * Add a component (e.g. a panel) to display information or include controls.
	 * @param panel The JPanel to display
	 * @param gridx The grid x coordinate, see GridBagConstraints
	 * @param gridy The grid y coordinate, see GridBagConstraints
	 * @param gridwidth The grid width, see GridBagConstraints
	 * @param gridheight The grid height, see GridBagConstraints
	 */
	public void addPanel(DrawPanel panel,String id,int gridx, int gridy, int gridwidth, int gridheight) {
		if(gui_closed) return;
		// NOTE: we assume panels cannot be added while rendering a cycle
		// If they can it may mess the synchronization mechanism and this method may require synchronization with the render cycle
		if(panel.getWorldCoordinates()==null) 
			panel.setWorldCoordinates(defaultCoordinates.copy());
		panel.setParent(this);
		panel.setName(id);
		drawPanels.put(id, panel);
	};
	
	/**
	 * Adds a drawer layer to the universe panel
	 * @param d
	 */
	public void addDrawer(String panelID, String drawerID, Drawer d) {
		if(gui_closed) return;
		if(d.worldCoordinates == null) d.setWorldCoordinates(defaultCoordinates);
		d.setName(drawerID);		// set drawer name
		drawers.put(drawerID, d); 	// add drawer to the display's list of drawers
		if(isMethodImplemented(d, "newEpisode")) drawers_new_episode.add(d);
		if(isMethodImplemented(d, "endEpisode")) drawers_end_episode.add(d);
		if(isMethodImplemented(d, "newTrial")) drawers_new_trial.add(d);
		if(isMethodImplemented(d, "endTrial")) drawers_end_trial.add(d);
		if(isMethodImplemented(d, "appendData"))   drawers_append.add(d);
		
		drawPanels.get(panelID).addDrawer(d); // add the drawer to the panel
		
	}
	
	
	/**
	 * Binds a runnable to a key combination, when the key combination is press the action is executed.
	 * @param key		the key to which the binding is added
	 * @param modifiers modifiers of the key combinations in format "[ctrl ][shift ][alt ]". Where brackets indicate optional substrings.
	 * @param action	the action to be performed when the key combination is pressed
	 */
	public void addKeyAction(int key, String  modifiers, Runnable action) {
		if(gui_closed) return;
		int code = key;
		for(var token : modifiers.toUpperCase().split(" ")) {
			token = token.trim();
			if(token.length() == 0) continue;
			switch(token) {
			case "CTRL":
				code |= 1 << 31;
				break;
			case "SHIFT":
				code |= 1 << 30;
				break;
			case "ALT":
				code |= 1 << 29;
				break;
			default:
				System.err.println("GUI ERROR: INVALID KEY MODIFIER: " + token + " of length " + token.length());
				System.exit(-1);
			}
			
		}
		keyActions.put(code, action);
	}

	
	/**
	 * Log a certain string using the display specific method (e.g. textbox or system.out)
	 * @param s
	 */
	public abstract void log(String s);
	
	
	/**
	 * Singal the display that it must be repainted
	 */
	public abstract void repaint();
	
	/**
	 * Abstract function called after last cycle was fully rendered to record frame into a video
	 */
	protected abstract void recordRenderCycle();
	
	/**
	 * Perform the function associated with the given key.
	 * @param key
	 * @return True if an action was found for the given key, False otherwise.
	 */
	protected boolean performKeyAction(int key, boolean ctrl, boolean shift, boolean alt) {
		int code = key;
		if(ctrl)  code |= 1 << 31;
		if(shift) code |= 1 << 30;
		if(alt)   code |= 1 << 29;
		
		var action = keyActions.get(code);
		if(action == null) return false;
		action.run();
		return true;
	}
	
	
	// =============================================================================================
	// ======================== SCRIPT RELATED OVERWRITTEN METHODS =================================
	// =============================================================================================


	
	/**
	 * Tells the display that a new episode began. Some drawers might have to clear stateful information due to this.
	 */
	@Override
 	public void newEpisode() {
		if(gui_closed) return;
		for(Drawer d : drawers_new_episode)  {d.newEpisode();}
		go_to_scheduler.checkSchedule(EventType.EPISODE_NEW);
		updateData();
	};
	
	@Override
	public void endEpisode() {
		if(gui_closed) return;
		for(Drawer d : drawers_end_episode)  {d.endEpisode();}
		go_to_scheduler.checkSchedule(EventType.EPISODE_END);
		updateData();
	}
	
	@Override
	public void newTrial() {
		if(gui_closed) return;
		for(Drawer d : drawers_new_trial)  {d.newTrial();}
		go_to_scheduler.checkSchedule(EventType.TRIAL_NEW);
		updateData();
	}
	
	@Override
	public void endTrial() {
		if(gui_closed) return;
		for(Drawer d : drawers_end_trial)  {d.endTrial();}
		go_to_scheduler.checkSchedule(EventType.TRIAL_END);
		updateData();
	}
	
	/**
	 * Since display is a cycle script, the run function is an alias of updateData
	 */
	public long perform() {
		if(gui_closed) return 0; // TODO: add signal special value avoid rescheduling
		go_to_scheduler.checkSchedule(EventType.CYCLE);
		updateData();
		return 0;
	}
	
	/**
	 * Displays have the least priority so that they always run last
	 */
	@Override
	final public int getPriority() {
		return Integer.MAX_VALUE;
	}
	

	
	// =============================================================================================
	// =========================== DRAWING AND SYNCHRONIZATION =====================================
	// =============================================================================================

	/*
	 * The function should update all data to be drawn
	 * This function should execute atomically from the rendering cycle
	 * That is, as long as the function is running no rendering should be done
	 */
	public void  updateData() {	
		if(gui_closed ) return;
		// append new data to the drawers
		for(Drawer d : drawers_append) d.appendData();
		
		if(!update_display) return;
		
		// If necessary (syncDisplay == True), wait till done rendering last cycle
		waitFinishRenderingLastCycle();
				
		// check whether a new render cycle should be started
		// if so, update drawer data, update cycle and send repaint signal
		if(!is_minimized() && startNewRenderCycle()) {
			// if list of drawers or panels might change, we might need add sync, for the time being this can't happen
			for(Drawer d : drawers.values()) synchronized(d) { d.updateData(); };
			for(DrawPanel p :drawPanels.values()) p.setRenderCycle(renderCycle);
			
			repaint();
		}
		
	}
	
	private void waitFinishRenderingLastCycle() {
		
		// Check whether waiting is necessary:
		boolean wait = false;
		synchronized(mutexSync) {
			// if I have to wait set a waiting flag
			wait = waitingPreviousFrame = (syncDisplay || forceSyncDisplay) && !doneRenderingLastCycle;
			forceSyncDisplay = false; // this is one time use only
		}
		
		// if I don't have to wait, then return:
		if(!wait) return;
		
		// Else, wait till last render cycle is done		
		long stamp = Debug.tic();
		try {
			wait_for_render_lock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		if(Debug.profiling) System.out.println("Waiting time: " + Debug.toc(stamp));
	}
	
	private boolean startNewRenderCycle() {
		
		// if last render cycle finished, start a new render cycle only if:
		//		syncing display (since I have to show all simulation frames)
		// 		or time elapsed since last frame is above the max frequency time.
		synchronized(mutexSync){
			if (doneRenderingLastCycle && (syncDisplay || Debug.toc(renderCycleTime) > max_frequency_time_ms)){
				
				// prepare to render new cycle
				renderCycleTime = Debug.tic();
				renderCycle++;
				doneRenderingLastCycle = false;
				remainingPanels = drawPanels.size();
				return true;	
			}
			return false;
		}
	}

	
	/**
	 * Set whether to sync the display with the simulation or not.
	 * If True, the simulator will lock waiting for the display to finish rendering the previous render cycle
	 * before sending a new repaint signal.
	 * Else, the simulator will go ahead, and the display will show outdated data.
	 * @param new_value
	 * @return
	 */
	public boolean setSync(boolean new_value) {
		synchronized (mutexSync) {
			boolean old_value = syncDisplay;
			
			if(new_value == old_value) return old_value; 
			syncDisplay = new_value;
			if(!syncDisplay)
			{
				if(waitingPreviousFrame){
					waitingPreviousFrame = false;
					wait_for_render_lock.release();
				}				
			}else {
				repaint();
			}
			return old_value;
			
		}
	}
	
	/**
	 * Toggle synchronization between simulation cycle and render cycle.
	 * See function @see {@link Display#setSync(boolean)}
	 */
	public void toggleSync(){
		setSync(!syncDisplay);
	}
	
	/**
	 * Function called by @see {@link DrawPanel} to signal they finished rendering the data of the respective cycle.
	 * @param cycle
	 */
	protected void signalPanelFinishedRendering(long cycle) {
//		System.out.println("finished rendering panel of cycle " + cycle + ", current: " + renderCycle + ", rem "+(remainingPanels-1));
		
		synchronized (mutexSync) {
			// if cycle is old, ignore signal
			if(cycle!=renderCycle) return;
			 
			// else decrement remaining panels that I'm waiting for
			remainingPanels = Math.max(--remainingPanels, -1);
			
			// If no panels left, signal done rendering cycle
			if(remainingPanels==0){
				if(Debug.profiling) System.out.println("Render cycle time: " + Debug.toc(renderCycleTime));
				doneRenderingLastCycle = true;
				
				// if done rendering last cycle, record frame
				// note we assume remaining panels will turn 0 only once for the current render cycle.
				// worst case, we end up with a duplicate frame in the video
				recordRenderCycle();
				
				if( waitingPreviousFrame) {
					waitingPreviousFrame = false;
					wait_for_render_lock.release();
				}					
				
				
			}
			
		}
	}
	

	public boolean is_minimized() {
		return false;
	}

	
	/**
	 * Checks whether an object re-implements a method.
	 * Use to append an objects to event lists when they re-implement functions such as newEpisode, endEpisode, etc.
	 * @param obj
	 * @param name
	 * @return
	 */
	private boolean isMethodImplemented(Object obj, String name)
	{
//		System.out.println("AETET: " + drawers_append.size() + " " + drawers_new_episode.size()
//		+ " " + drawers_end_episode.size()
//		+ " " + drawers_new_trial.size()
//		+ " " + drawers_end_trial.size()
//				);
		
	    try
	    {
	        Class<? extends Object> clazz = obj.getClass();
	        var ret = clazz.getMethod(name).getDeclaringClass().equals(clazz);
//	        if(ret) System.out.println("AETET: " + name + " " + obj);
	        return ret;
	    }
	    catch (Exception e)
	    {
	    }


	    return false;
	}
	
	// =============================================================================================
	// =========================== SIMULATION CONTROL UTILITIES ====================================
	// =============================================================================================
	
	protected enum  EventType {CYCLE, EPISODE_NEW, EPISODE_END, TRIAL_NEW, TRIAL_END, NONE}
	
	private class GoToScheduler {
		
		void checkSchedule(EventType event_type) {
			
		}
		
		void updateDisplay() {
			System.out.println("Updating display");
			update_display = true;
			forceSyncDisplay = true;
			SimulationControl.setPause(true);
			go_to_scheduler = new GoToScheduler();
		}
	}
	
	private class GoToNSteps extends GoToScheduler {

		EventType type;
		long remaining_steps;
		
		public GoToNSteps(EventType type, long steps) {
			this.type = type;
			this.remaining_steps = steps;
		}
		
		@Override
		void checkSchedule(EventType event_type) {
			if(this.type == event_type) {
				remaining_steps--;
				if(remaining_steps == 0) {
					updateDisplay();
				}
			}
			
		}
		
	}
	
	private class GoToTime extends GoToScheduler {
		
		EventType final_event_type = EventType.NONE;
		EventType next_event_type  = EventType.CYCLE;

		String  trial = "";
		int 	episode = -1;
		long 	cycle  = 0;
		
		Experiment e = Experiment.get();
		
		GoToTime(EventType type, String trial, int episode, long cycle){
			final_event_type = type;
			this.trial = trial;
			this.episode = episode;
			this.cycle = cycle;
			
			boolean find_trial = experiment.getGlobal("trial") != trial;
			boolean find_ep = (int)experiment.getGlobal("episode") != episode;
			
			next_event_type = switch(type) {
				case TRIAL_NEW, TRIAL_END -> type;
				case EPISODE_NEW, EPISODE_END -> find_trial ? EventType.TRIAL_NEW : type;
				case CYCLE -> find_trial ? EventType.TRIAL_NEW : 
					(find_ep ? EventType.EPISODE_NEW : EventType.CYCLE);
				default -> EventType.CYCLE;
			};
		}
		
		@Override
		void checkSchedule(EventType event_type) {
			if( event_type != next_event_type) return;
			
			boolean next_condition_met = switch(event_type) {
				case TRIAL_END, TRIAL_NEW -> (e.getGlobal("trial") == trial);
				case EPISODE_NEW, EPISODE_END -> (Integer) e.getGlobal("episode") == episode;
				case CYCLE -> (Long)e.getGlobal("cycle") == cycle;
				default -> false;
			};
			
			if(next_condition_met) {
				if(event_type != final_event_type) {
					next_event_type = switch(event_type) {
						case TRIAL_NEW -> EventType.EPISODE_NEW;
						case EPISODE_NEW -> EventType.CYCLE;
						default -> EventType.CYCLE;
					};
				} else updateDisplay();
			} 
			
		}
		
	}

	
	/**
	 * Goes to the given episode in the current trial
	 * @param episode		The episode to go to
	 * @param episode_end	Whether to go to the start or end of the episode
	 */
	public void goToEpisode(int episode, boolean episode_end) {
		
		SimulationControl.pauseAndGetControl();
		update_display = false;
		var trial = experiment.getGlobal("trial").toString();
		var type = episode_end ? EventType.EPISODE_END : EventType.EPISODE_NEW;
		go_to_scheduler = new GoToTime(type, trial, episode, 0L);
		SimulationControl.releaseControl();
	}
	
	
	public void runNEvents(EventType type, long N) {
		SimulationControl.pauseAndGetControl();
		update_display = false;
		go_to_scheduler = new GoToNSteps(type, N);
		SimulationControl.releaseControl();
	}
	
}
