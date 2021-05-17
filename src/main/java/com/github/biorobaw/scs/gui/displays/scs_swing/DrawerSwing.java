package com.github.biorobaw.scs.gui.displays.scs_swing;

import java.awt.Graphics;

import com.github.biorobaw.scs.gui.Drawer;
import com.github.biorobaw.scs.gui.utils.Window;

public abstract class DrawerSwing extends Drawer{

	abstract public void draw(Graphics g, Window panelCoordinates);
	
	
}
