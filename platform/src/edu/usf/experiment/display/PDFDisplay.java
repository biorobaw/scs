package edu.usf.experiment.display;

import java.awt.geom.Rectangle2D.Float;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import de.erichseifert.vectorgraphics2d.PDFGraphics2D;
import edu.usf.experiment.Globals;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.BoundedUniverse;

public class PDFDisplay implements Display {

	private static final double WIDTH = 200;
	private static final double HEIGHT = 200;
	private static final float XMARGIN = 10;
	private static final float YMARGIN = 10;
	private static final String FRAMES_FOLDER = "frames";
	private int image;
	private List<Drawer> drawers;
	private Scaler s;
	private File frameDir;

	public PDFDisplay() {
		this(Globals.getInstance().get("logPath") + File.separator + FRAMES_FOLDER);
	}
	
	public PDFDisplay(String logPath) {
		image = 0;
		drawers = new LinkedList<Drawer>();
		
		frameDir = new File(logPath);
		frameDir.mkdirs();
	}

	@Override
	public void addPanel(DrawPanel panel,String id,int gridx, int gridy, int gridwidth, int gridheight) {
	}

	@Override
	public void log(String s) {
	}

	@Override
	public void repaint() {
		// Create a new PDF document with a width of 210 and a height of 297
		PDFGraphics2D g = new PDFGraphics2D(0.0, 0.0, WIDTH, HEIGHT);

		for (Drawer d : drawers)
			d.draw(g, s);

		// Write the PDF output to a file
		FileOutputStream file;
		try {
			file = new FileOutputStream(
					frameDir.getAbsolutePath() + File.separator + "frame" + (image++) + ".pdf");
			file.write(g.getBytes());
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}
	}

	@Override
	public void setupUniversePanel(BoundedUniverse bu) {
		Float worldCoordiantes = bu.getBoundingRect();
		Float panelCoordinates = new Float(XMARGIN, YMARGIN, (float)(WIDTH-2*XMARGIN), (float)(HEIGHT-2*YMARGIN));
		s = new Scaler(worldCoordiantes,panelCoordinates,true);
	}

	@Override
	public void addDrawer(String panelID, String drawerID, Drawer d) {
		drawers.add(d);
	}

	@Override
	public void addDrawer(String panelID, String drawerID, Drawer d, int pos) {
		drawers.add(pos, d);
	}

	@Override
	public void newEpisode() {
		for (Drawer d : drawers)
			d.clearState();
	}

	@Override
	public void addKeyAction(int key, Runnable action) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sync(int cycle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void waitUntilDoneRendering() {
		// TODO Auto-generated method stub
		
	}

}
