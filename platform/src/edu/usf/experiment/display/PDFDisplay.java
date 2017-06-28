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
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.Scaler;
import edu.usf.experiment.universe.BoundedUniverse;

public class PDFDisplay implements Display {

	private static final double WIDTH = 200;
	private static final double HEIGHT = 200;
	private static final int XMARGIN = 10;
	private static final int YMARGIN = 10;
	private static final String FRAMES_FOLDER = "frames";
	private int image;
	private List<Drawer> drawers;
	private Scaler s;
	private File frameDir;

	public PDFDisplay() {
		this(PropertyHolder.getInstance().getProperty("log.directory") + File.separator + FRAMES_FOLDER);
	}
	
	public PDFDisplay(String logPath) {
		image = 0;
		drawers = new LinkedList<Drawer>();
		
		frameDir = new File(logPath);
		frameDir.mkdirs();
	}

	@Override
	public void addPlot(JComponent component, int gridx, int gridy, int gridwidth, int gridheight) {
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
		Float univRect = bu.getBoundingRect();
		// The scaling factors are the relation between effective draw space and
		// the universe bounding box (taken from the xml file for the maze)
		float xscale = (float) ((WIDTH - 2 * XMARGIN) / univRect.width);
		float yscale = (float) ((HEIGHT - 2 * YMARGIN) / univRect.height);
		// Take the minimum of both scales to keep aspect ratio
		float defScale = Math.min(xscale, yscale);
		// The x offset is just the lowest x coordinate of the universe
		float xoffset = -(univRect.x - XMARGIN / defScale);
		// The y offset includes the bounding box height, to be able to invert
		// the y component (it grows to the bottom in the screen)
		float yoffset = -(univRect.height + univRect.y + YMARGIN / defScale);
		s = new Scaler(defScale, defScale, xoffset, yoffset);
	}

	@Override
	public void addUniverseDrawer(Drawer d) {
		drawers.add(d);
	}

	@Override
	public void addUniverseDrawer(Drawer d, int pos) {
		drawers.add(pos, d);
	}

}
