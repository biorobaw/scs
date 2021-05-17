package com.github.biorobaw.scs.gui.displays.java_fx.drawer;

import java.util.HashMap;

import com.github.biorobaw.scs.gui.Drawer;
import com.github.biorobaw.scs.gui.displays.java_fx.PanelFX;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public abstract class DrawerFX extends Drawer {

	/**
	 * Hash that stores the scenes for each panel that uses the drawer.
	 */
	protected HashMap<Integer, DrawerScene> scenes = new HashMap<>(); 
	
	static public enum Units { LOCAL, WORLD }
	

	/**
	 * Called when adding the drawer to a PanelFX. 
	 * Creates the graphics to be added to the panel.
	 * @return
	 */
	abstract protected DrawerScene createGraphics(PanelFX panel);
		
	/**
	 * Function called by PanelFX when a resize happens
	 * @param panel
	 */
	public void resize(PanelFX panel) {
		
	}
	
	/**
	 * Add a panel where the drawer will draw
	 * @param panel
	 * @return
	 */
	public DrawerScene addPanelFX(PanelFX panel) {
		int id = panel.draw_panel.id;
		var data = scenes.get(id);
		if(data == null) {
			data = createGraphics(panel);
			scenes.put(id, data);
		}
		return data;
	}
	
	
	/**
	 * Class containing the graphics of the drawer
	 * @author bucef
	 *
	 */
	abstract public class DrawerScene {
		
		
		public Pane root = new Pane();   // default root pane
		public PanelFX panel;			 // pointer to java fx panel conatining drawer
		
		// transformations
		
		

		
		public DrawerScene(PanelFX panel) {
			this.panel = panel;
			setUnits(root, Units.WORLD);
		}
				
		public abstract void update();
		
		
		
		public <T extends Node> T setUnits(T node, Units units) {
			
			var t = node.getTransforms();
			t.clear();
			switch(units) {
			case LOCAL:
				t.addAll(panel.affine_view_to_panel, panel.affine_local_to_panel);
				break;
			case WORLD:
				t.addAll(panel.affine_view_to_panel, panel.affine_world_to_panel);
				break;
			}
			return node;
		}
		
		
		
		
	}

	
}
