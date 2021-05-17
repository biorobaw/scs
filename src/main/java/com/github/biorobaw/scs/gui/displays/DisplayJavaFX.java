package com.github.biorobaw.scs.gui.displays;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.gui.Display;
import com.github.biorobaw.scs.gui.DrawPanel;
import com.github.biorobaw.scs.gui.Drawer;
import com.github.biorobaw.scs.gui.displays.java_fx.PanelFX;
import com.github.biorobaw.scs.gui.displays.java_fx.data.Hotkey;
import com.github.biorobaw.scs.gui.displays.java_fx.modals.ModalHelp;
import com.github.biorobaw.scs.gui.utils.VideoRecorder;
import com.github.biorobaw.scs.simulation.SimulationControl;
import com.github.biorobaw.scs.utils.files.XML;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DisplayJavaFX extends Display{

	// =============================================================================================
	// ==================== CONSTANTS ==============================================================
	// =============================================================================================
	
	static final int padding = 4;
	static final int defulat_width = 1280;
	static final int default_height = 720;
	static final float pane_plot_max_display_percentage = 0.44f;
	static final String color_background = "-fx-background-color: #336699;";
	static final String color_pane_plot = "-fx-background-color: #6e6e6e;";
	static final String color_pane_control = PanelFX.color_background;
	
	// =============================================================================================
	// ==================== VARIABLE DECLARATION ===================================================
	// =============================================================================================
	
	final Thread gui_thread;
	static FX_Gui gui;
	static DisplayJavaFX display;
	CountDownLatch init_latch = new CountDownLatch(1);
	
	
	
	DrawPanel universe_pane;
	XML xml = null; // Not null only during constructor, used to pass arguments to the application
		
	
	// =============================================================================================
	// ============= CONSTRUCTOR AND OVERWRITEN METHODS ============================================
	// =============================================================================================
	
	public DisplayJavaFX(XML xml) {
		super(xml);
		// store pointer to display object:
		display = this;
		
		// Open JavaFX application
		this.xml = xml; // set parameters so that the application can use them
		gui_thread = new Thread(() -> {
			Application.launch(FX_Gui.class);
		});
		gui_thread.start();
		
		// Wait for application to be open:
		try {
			init_latch.await();
			this.xml = null; // remove parameters so that memory can be freed
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		

	}

	@Override
	public void log(String s) {
		System.out.println(s);
		
	}
	
	@Override
	public void addPanel(DrawPanel panel, String id, int gridx, int gridy, int gridwidth, int gridheight) {
		super.addPanel(panel, id, gridx, gridy, gridwidth, gridheight);
		
		run_in_gui(() -> {			
			var fx_pane = gui.guiElementForDrawPanel(panel);
			gui.plot_pane.add(fx_pane, gridx, gridy, gridwidth, gridheight);
		});

	}
	
	@Override
	public void addDrawer(String panelID, String drawerID, Drawer d) {
		super.addDrawer(panelID, drawerID, d);
	}
	
	@Override
	public void repaint() {
		Platform.runLater(() -> gui.repaint());
	}

	@Override
	protected void recordRenderCycle() {
		// Observation: this function is called from Display.signalPanelFinishedRendering
		// which is a function called by the GUI, thus this is the GUI thread and I can call
		// display.recordRenderCycle(). Otherwise do 
		gui.recordScreen();
		
	}

	@Override
	public void endExperiment() {
		super.endExperiment();
		// signal close to gui, then wait for it to close.
		try {
			Platform.exit();
			gui_thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// =============================================================================================
	// ===================== GUI CONSTRUCTION CLASSES AND METHODS ==================================
	// =============================================================================================
	
	
	public static class FX_Gui extends Application {

		GridPane plot_pane;
		
		Stage stage;
		Scene scene;
		
		Button buttonPause;
		Button buttonStep;
		ToggleButton buttonRecord;
		
		
		VideoRecorder recorder = null;
		boolean recording = false;
		
				
		@Override
		public void start(Stage primaryStage) throws Exception {
			gui = this;
			
			// get XML parameters:
			var xml = display.xml;
			var initial_width  = xml.hasAttribute("width") ? xml.getIntAttribute("width") : defulat_width;
			var initial_height = xml.hasAttribute("height") ? xml.getIntAttribute("height") : default_height;
			var record = xml.hasAttribute("record") && xml.getBooleanAttribute("record");
			
			// init screen rocoding if necessary:
			if(record) init_record_screen(initial_width, initial_height);
			
			// Create stage and root element:
			stage = primaryStage;
			stage.setWidth(initial_width);
			stage.setHeight(initial_height);
			var root = new BorderPane();
			scene = new Scene(root);
	        primaryStage.setScene(scene);
			primaryStage.setTitle("SCS JavaFX gui");
			
			// Create panes:
			plot_pane = createPanePlots();
			var u_pane = createPaneUniverse();
			var menu_bar = createMenuBar();
			
						
			// set layout
			root.setPadding(new Insets(padding));
			root.setStyle(color_background);
			root.setTop(menu_bar);
			root.setLeft(plot_pane);
			root.setCenter(u_pane);
			BorderPane.setMargin(menu_bar, new Insets(0, 0, padding, 0));
			
			
			
			// create scene, add it to stage, add listener and then show stage
	        initKeyListener(root);
	        
	        primaryStage.show();
	        
	        // set close signal: we may eliminate this call, but currently it is sometimes necessary
	        primaryStage.setOnCloseRequest(e -> {
	        	display.gui_closed = true;
	        	Platform.exit();
	        });
			
	        // signal main thread that gui is ready
			display.init_latch.countDown();

		}
		
		@Override
		public void stop() throws Exception {
			super.stop();
			recording = false;
			if(recorder!= null) recorder.endRecording();
			Experiment.get().controller.quit();
		}
		
		GridPane createPanePlots() {
			var plot_pane = new GridPane();
			plot_pane.setHgap(padding);
			plot_pane.setVgap(padding);
			plot_pane.setPadding(new Insets(padding));
			plot_pane.setStyle(color_pane_plot);
			final var m = pane_plot_max_display_percentage;
			plot_pane.setMaxWidth(m*scene.getWidth());
			scene.widthProperty().addListener(e -> plot_pane.setMaxWidth(m*scene.getWidth()));
			return plot_pane;
		}
		
		Pane createPaneUniverse() {
			
			// create the universe draw panel
			var draw_panel = new DrawPanel(10, 10);
			draw_panel.setParent(display);
			draw_panel.setWorldCoordinates(display.defaultCoordinates.copy());
			draw_panel.setName("universe");
			display.drawPanels.put("universe", draw_panel);
			
			// Get universe pane from DrawPanel			
			PanelFX u_pane = guiElementForDrawPanel(draw_panel);
			
			// create control box:
			var control_box = createControlBox();
			
			// create layout
			var full_pane = new BorderPane();
			full_pane.setCenter(u_pane);
			full_pane.setBottom(control_box);
			full_pane.setPadding(new Insets(0,0,0,padding));
			BorderPane.setMargin(control_box, new Insets(padding,0,0,0));
//			control_box.setPadding();
//			u_pane.setPadding(new Insets(padding,padding,padding,padding));
			
	        return full_pane;
		}
		
		Pane createControlBox() {
			// create buttons
			boolean sim_paused = SimulationControl.getPause();
			buttonPause = new Button( sim_paused ? "Resume" : "Pause");
			buttonStep = new Button("Step");
			buttonRecord = new ToggleButton("Record");
			
			// add actions to buttons and set properties:
//			buttonPause.
			buttonPause.setOnAction( event -> {
				SimulationControl.togglePause();				
			});
			
			buttonStep.setOnAction( event -> {
				SimulationControl.produceStep();				
			});
			
			buttonRecord.setOnAction(event -> {
				recording = !recording;
				if(recording) {
					// try to init the recording, if it has already been started, the call will be ignored
					init_record_screen((int)stage.getWidth(), (int)stage.getHeight());
				} 
				recorder.pauseRecording(!recording);
			});
			buttonRecord.setSelected(recording);
			
			// add listener for the simulation control to update buttons
			buttonStep.setDisable(!sim_paused);
			SimulationControl.addPauseStateListener(  paused -> {
				Platform.runLater(() -> {
					buttonPause.setText(paused ? "Resume" : "Pause");
					buttonStep.setDisable(!paused);					
				});
				return null;
			});
			
			// create layout for buttons
			HBox buttons = new HBox();
			buttons.setAlignment(Pos.CENTER);
			buttons.setSpacing(5);
			buttons.getChildren().addAll(buttonStep, buttonPause, buttonRecord);
			buttons.setPadding(new Insets(0,0,padding,0 ));
			
			
			// create slider for simulation speed:
			int simSpeed = SimulationControl.getSimulationSpeed();
			Slider slider = new Slider(0, SimulationControl.sleepValues.length-1, simSpeed);
//			slider.setPrefSize(300, 50);
			slider.setMajorTickUnit(1);
			slider.setMinorTickCount(0);
			slider.setShowTickLabels(true);
			slider.setShowTickMarks(true);
			slider.setSnapToTicks(true);
			slider.setPadding(new Insets(0,padding,0,padding));
			slider.valueProperty().addListener((value, old_val, new_val)->{
				int prev = (int)Math.round(old_val.floatValue());
				int val = (int)Math.round(new_val.floatValue());
				if(prev!=val) {
					SimulationControl.setSimulationSpeed(val);					
				}
			});


			VBox controls_layout = new VBox();
			controls_layout.getChildren().addAll(slider, buttons);
			controls_layout.setStyle(color_pane_control);

			return controls_layout;
		}
		
		MenuBar createMenuBar() {
			
			// MENU HELP
			var menu_help = new Menu("Help");
			var item_help = new MenuItem("Show Hotkeys");
			item_help.setOnAction( e -> new ModalHelp(stage).showAndWait());
			
			menu_help.getItems().addAll(item_help);
			
			
			// CREATE MENU BAR AND ADD MENUS
			return new MenuBar(menu_help);
			
			
		}
		
		void initKeyListener(Pane root) {			
			display.addKeyAction(KeyCode.SPACE.getCode(), "", ()-> SimulationControl.togglePause());
			Hotkey.addHotkey("Space", "Toggles pause");
			
			display.addKeyAction(KeyCode.RIGHT.getCode(), "", () -> SimulationControl.produceStep());
			Hotkey.addHotkey("Right arrow", "If paused, advances the simulation by one cycle.");
			
			display.addKeyAction(KeyCode.RIGHT.getCode(), "ctrl", () -> display.runNEvents(EventType.EPISODE_END, 1));
			Hotkey.addHotkey("Ctrl + Right arrow", "Advance the simulation to next end episode. Display skiped until event reached.");
			
			display.addKeyAction(KeyCode.RIGHT.getCode(), "ctrl shift", () -> display.runNEvents(EventType.EPISODE_NEW, 1));
			Hotkey.addHotkey("Ctrl + Shift + right arrow", "Advance the simulation to next new episode. Display skiped until event reached.");
			
			display.addKeyAction(KeyCode.RIGHT.getCode(), "alt", () -> display.runNEvents(EventType.TRIAL_END, 1));
			Hotkey.addHotkey("Alt + Right arrow", "Advance the simulation to next end trial. Display skiped until event reached.");
			
			display.addKeyAction(KeyCode.RIGHT.getCode(), "alt shift", () -> display.runNEvents(EventType.TRIAL_NEW, 1));
			Hotkey.addHotkey("Alt + Shift + right arrow", "Advance the simulation to next new trial. Display skiped until event reached.");
						
			display.addKeyAction(KeyCode.R.getCode(), "", () -> buttonRecord.fire());
			Hotkey.addHotkey("R",  "Toggles screen recording");
			
			display.addKeyAction(KeyCode.K.getCode(), "", () -> display.toggleSync());
			Hotkey.addHotkey("K", "Toggles the synchronization between the display and the simulation.");
			
			display.addKeyAction(KeyCode.ESCAPE.getCode(), "", () -> Platform.exit());
			Hotkey.addHotkey("Escape", "Close active window.");
			
			display.addKeyAction(KeyCode.H.getCode(), "", () -> new ModalHelp(stage).showAndWait());
			Hotkey.addHotkey("H", "Opens this help panel.");
			
			root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
	        	var code = e.getCode().getCode();
	        	if(display.performKeyAction(code, e.isControlDown(), e.isShiftDown(), e.isAltDown()))
	        		e.consume();
	        });
			
		}
		
		void repaint() {
			for(var panel : display.drawPanels.values())
				panel.paintPanel();
		}
		
		public PanelFX guiElementForDrawPanel(DrawPanel panel) {
			var node = new PanelFX(panel);
			return node;
		}
		
		
		/**
		 * Initializes screen recording
		 * @param width  output video width in pixels
		 * @param height output video height in pixels
		 */
		void init_record_screen(int width, int height) {
			if(recorder != null) return;
			recording = true;
			// generate path for video file:
			final var g = Experiment.get();
			var video_path = g.getGlobal("baseLogPath").toString() + "/videos/";
			new File(video_path ).mkdirs();
			var run_id = g.getGlobal("run_id").toString();
			var config = g.getGlobal("config").toString();
			var video_file = video_path + "video_" + config + "_" + run_id + ".avi";
			recorder = new VideoRecorder( video_file, width, height);
		}
		
		void recordScreen() {
			if(recorder == null || !recording) return;
			var x = (int)(stage.getX() );
			var y = (int)(stage.getY());
			var w = (int)stage.getWidth();
			var h = (int)stage.getHeight();
//			var tic = Debug.tic();
			recorder.update_bounds(x, y, w, h);
//			System.out.println(Debug.toc(tic));
		}
	
	}
	
	
	static public void run_in_gui(Runnable run) {
		CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(()->{
			run.run();
			latch.countDown();
		});
		wait(latch);
	}
	
	static void wait(CountDownLatch latch) {
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	@Override
	public boolean is_minimized() {
		return gui.stage.isIconified();
	}

	
	

	

}
