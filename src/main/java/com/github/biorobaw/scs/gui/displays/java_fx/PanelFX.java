package com.github.biorobaw.scs.gui.displays.java_fx;


import java.util.HashSet;

import com.github.biorobaw.scs.gui.DrawPanel;
import com.github.biorobaw.scs.gui.Drawer;
import com.github.biorobaw.scs.gui.DrawPanel.GuiPanel;
import com.github.biorobaw.scs.gui.displays.DisplayJavaFX;
import com.github.biorobaw.scs.gui.displays.java_fx.drawer.DrawerFX;
import com.github.biorobaw.scs.gui.displays.java_fx.drawer.DrawerFX.DrawerScene;
import com.github.biorobaw.scs.gui.utils.Scaler;
import com.github.biorobaw.scs.gui.utils.Window;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class PanelFX extends BorderPane implements GuiPanel {

	// =============================================================================================
	// ==================== CONSTANTS ==============================================================
	// =============================================================================================
	
	public static final String color_background = "-fx-background-color: #e8e8e8;";
	
	// =============================================================================================
	// ==================== VARIABLE DECLARATION ===================================================
	// =============================================================================================
	
	public DrawPanel draw_panel; 								// pointer the the display panel

	
	public StackPane center_pane = new StackPane();			// Center pane of border pane
	StackPane drawer_scene_roots = new StackPane();			// JavaFX group with all drawer scene roots
	Pane glass_pane = new Pane();							// panel used for drawing on top of all scenes
	HashSet<DrawerScene> drawer_scenes = new HashSet<>();	// set with the scenes of all drawers of this panel
	Rectangle clip = new Rectangle();						// used to clip graphics outside of the panel
	
	PanelMouseEvents mouse_events;	// sub class implementing mouse event functionalities
	
	
	// View to panel coordinates
	public Scaler view_to_panel = new Scaler(1, -1, 0, 0);	// transforms view coordinates to panel, by default represens identity
	public Affine affine_view_to_panel = Transform.affine(1, 0, 0, 1, 0, 0);
	
	// default world to panel coordinates transform
	public Scaler world_to_panel = new Scaler(1, -1, 0, 0);	// Scaler that converts world coordinates to panel coordinates	
	public Affine affine_world_to_panel = Transform.affine(1, 0, 0, 1, 0, 0);

	
	// default local to panel coordinates transform
	public Scaler local_to_panel = new Scaler(1, -1, 0, 0);	// Scales local coordinates to panel coordinates
	public Affine affine_local_to_panel = Transform.affine(1, 0, 0, 1, 0, 0);

	
	
	
	// =============================================================================================
	// ============= CONSTRUCTOR AND OVERWRITEN METHODS ============================================
	// =============================================================================================	
	
	
	public PanelFX(DrawPanel panel) {
		this.draw_panel = panel;
		panel.setGuiPanel(this);
		this.setPrefWidth(panel.min_size_x);
		this.setPrefHeight(panel.min_size_y);
		this.setStyle(color_background);
		center_pane.setClip(clip);	
		
		// Create panes and set layout:
		setCenter(center_pane);
		setTop(new Label(panel.panelName) );		
		center_pane.getChildren().addAll( drawer_scene_roots, glass_pane);
		BorderPane.setAlignment(getTop(), Pos.CENTER);

		glass_pane.setMouseTransparent(true);
		
		// set mouse events:
		this.mouse_events = new PanelMouseEvents();
		glass_pane.getChildren().add(mouse_events.rectangle);
		center_pane.setOnMouseDragged(event -> mouse_events.handleDragEvent(event));
		center_pane.setOnMouseReleased(e -> mouse_events.handleMouseUpEvent(e));		
		center_pane.setOnMouseClicked(event -> mouse_events.handleClickEvent(event));
		center_pane.setOnScroll(event -> mouse_events.handleScrollEvent(event));
		

		// add width and height listeners:
		center_pane.widthProperty().addListener((obs, old_val, new_val) -> {
			// update view, then resize center panel
			var ratio = (double)old_val == 0 ? 1 : (double)new_val/(double)old_val;
			view_to_panel.scaleOffsets(ratio, 1);
			resizeCenterPanel();
		});
		center_pane.heightProperty().addListener((obs, old_val, new_val) -> {
			var ratio = (double)old_val == 0 ? 1 : (double)new_val/(double)old_val;
			view_to_panel.scaleOffsets(1, ratio);
			resizeCenterPanel();
		});

	}
	
	public void resizeCenterPanel() {
		var w = center_pane.getWidth();
		var h = center_pane.getHeight();
		if(w==0 || h == 0) return;
		
		// update clip pane
		clip.setWidth(center_pane.getWidth());
		clip.setHeight(center_pane.getHeight());

		// update transforms
		var panel_coords = new Window(0, 0, w, h);
		var local_coords = draw_panel.getLocalCoordinates();
		var world_coords = draw_panel.getWorldCoordinates();
		
		// generate scalers::
		local_to_panel = new Scaler(local_coords, panel_coords, true);
		world_to_panel = new Scaler(world_coords, panel_coords, true); 
		
		updateTransforms(view_to_panel, affine_view_to_panel);
		updateTransforms(local_to_panel, affine_local_to_panel);
		updateTransforms(world_to_panel, affine_world_to_panel);
		
	}
	
	
	@Override
	public void addDrawer(Drawer drawer) {
		if(drawer instanceof DrawerFX) {
			DisplayJavaFX.run_in_gui(()->{
				var dfx = (DrawerFX)drawer;
				var scene = dfx.addPanelFX(this);
				drawer_scenes.add(scene);
				drawer_scene_roots.getChildren().add(scene.root);
			});
		}
		
	}
	
	@Override
	public void callDrawers(Object... args) {
		for(var scene : drawer_scenes) scene.update();
	}
	
	// =============================================================================================
	// ============= MOUSE EVENTS ==================================================================
	// =============================================================================================	
	
	
	
	class PanelMouseEvents  {
		
		int last_click_count = 0;		// number of consecutive clicks performed
		boolean dragging = false; 		// store whether drag action is in process
		boolean is_right_button = true;	// button being dragged
		double start_x, start_y;		// drag origin coordinates
		
		// rectangle used to highlight a selection
		Rectangle rectangle = new Rectangle(0, 0, Color.rgb(0, 0, 0, 0.2));
		
		/**
		 * Handles drag event.
		 * Primary button used for panning.
		 * Secondary button zoom to selection.
		 * @param event
		 */
		void handleDragEvent(MouseEvent event) {
			if(dragging) dragUpdate(event);
			else dragStart(event);
		}
	
		/**
		 * Initializes variables to track a drag event.
		 * @param event
		 */
		void dragStart(MouseEvent event) {
			start_x = event.getX();
			start_y = event.getY();
			rectangle.setWidth(0);
			rectangle.setHeight(0);
			dragging = true;
			is_right_button = event.getButton() != MouseButton.PRIMARY;
			rectangle.setVisible(is_right_button);
		}
		
		/**
		 * Updates a drag event
		 * @param event
		 */
		void dragUpdate(MouseEvent event) {
			
			if(is_right_button) dragUpdateRight(event);
			else dragUpdateLeft(event);
		}
		
		/**
		 * Updates left button drag event.
		 * Pans view.
		 * @param event
		 */
		void dragUpdateLeft(MouseEvent event) {
			// translate window and reset drag origin:
			translateView(event.getX() - start_x, event.getY() - start_y);
			start_x = event.getX();
			start_y = event.getY();
		}
		
		/**
		 * Updates right button drag event
		 * Highlights selection to be zoomed to.
		 * @param event
		 */
		void dragUpdateRight(MouseEvent event) {
			var delta_x = event.getX() - start_x;
			var delta_y = event.getY() - start_y;
			rectangle.setWidth(Math.abs(delta_x));
			rectangle.setHeight(Math.abs(delta_y));
			rectangle.setX(delta_x >= 0 ? start_x : start_x + delta_x);
			rectangle.setY(delta_y >= 0 ? start_y : start_y + delta_y);
		}
		
		/**
		 * Handle Mouse Up events.
		 * If drag in process, completes drag event. 
		 * @param event
		 */
		void handleMouseUpEvent(MouseEvent event) {
			// if dragging, update and complete drag
			if(dragging) {				
				dragUpdate(event); // update drag
				if(is_right_button) zoomToSelection(); // complete right drag
			}
			dragging = false;
			rectangle.setVisible(false);
		}
		
		/**
		 * Zooms to selected rectangle by dragging right button.
		 * If rectangle has 0 area then it does nothing. 
		 */
		void zoomToSelection() {
			var w = (float)rectangle.getWidth();
			var h = (float)rectangle.getHeight();
			if ( w == 0 || h == 0) return;
			
			var x = (float)rectangle.getX();
			var y = (float)rectangle.getY();
			zoomToPanelCoordinates(x, y, w, h);
		}
		
		/**
		 * Handle click events.
		 * Double right click: resets view
		 * @param event
		 */
		void handleClickEvent(MouseEvent event) {
			last_click_count = event.getClickCount();
			if(last_click_count == 2 && event.getButton() == MouseButton.SECONDARY)
				resetView();
		}
		
		/**
		 * Handles scroll events.
		 * Default action is panning.
		 * If control is pressed then it zooms in/out.
		 * @param event
		 */
		void handleScrollEvent(ScrollEvent event) {
			if(event.isControlDown()) {
				// zoom in or zoom out
				float zoom = event.getDeltaY() > 0 ? 1.1f : 0.9f;
				var x = (float)event.getX();
				var y = (float)event.getY();
				zoomAtPanelCoordinate(zoom, x, y);
								
			} else {
				// translate window
				translateView((float)event.getDeltaX(), (float)event.getDeltaY());
				
			}
		}
		

		
	}
	

	// =============================================================================================
	// ============= VIEW ZOOMING AND TRANLATION FUNCTIONS =========================================
	// =============================================================================================	
	
	/**
	 * Translate window panel by the specified number of pixels
	 * @param dx_pixels
	 * @param dy_pixels
	 */
	public void translateView(double dx_pixels, double dy_pixels) {
		
		// shift view and update panel
		view_to_panel.shift_output_coords(dx_pixels, dy_pixels);
		updateTransforms(view_to_panel, affine_view_to_panel);
		
	}
	
	/**
	 * Zooms the view to the specified panel coordinates
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void zoomToPanelCoordinates(float x, float y, float w, float h) {
			
		// set new coordinate frame and resize view	
		var view = view_to_panel.inverse().scaleWindowNoFlipping(x, y, w, h);
		var panel = new Window(0, 0, getWidth(), getHeight());
		view_to_panel = new Scaler(view, panel, true).flip_y_output(0, getHeight());
		updateTransforms(view_to_panel, affine_view_to_panel);

	}

	/**
	 * Zooms by the specified factor at the given panel coordinate
	 * @param factor
	 * @param panel_x
	 * @param panel_y
	 */
	public void zoomAtPanelCoordinate(float factor, float panel_x, float panel_y) {
		
		var scaler = new Scaler(factor, -factor, panel_x*(1-factor), panel_y*(1-factor));
		view_to_panel = scaler.combine(view_to_panel);
		updateTransforms(view_to_panel, affine_view_to_panel);

	}
	
	/**
	 * Resets the view to the original window
	 */
	public void resetView() {
		view_to_panel = new Scaler(1,-1,0,0);
		updateTransforms(view_to_panel, affine_view_to_panel);

	}
	

	// =============================================================================================
	// ================ UTILITIES ==================================================================
	// =============================================================================================
	
	public static Affine getAffine(Scaler scaler) {
		return Transform.affine(
				scaler.xscale	, 0, 
				0				, -scaler.yscale, 
				scaler.xoffset	, scaler.yoffset);
	}

	
	public static Affine updateTransforms(Scaler scaler, Affine affine) {
		affine.setMxx(scaler.xscale);
		affine.setMyy(-scaler.yscale);
		affine.setTx(scaler.xoffset);
		affine.setTy(scaler.yoffset);
		return affine;
	}
	

}
