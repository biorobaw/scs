package edu.usf.vlwsim.display.j3d;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;

import edu.usf.experiment.utils.ElementWrapper;

public class FeederNode extends ExpUniverseNode {

	private Point3f position;
	/**
	 * Determines wheather the feeder can provide food
	 */
	private boolean active;
	/**
	 * Determines if the feeder is calling the animals atention throughout
	 * flashing
	 */
	private boolean flashing;
	private Color3f flashingColor;
	private Color3f normalColor;
	private Color3f activeColor;
	private Appearance app;
	private boolean wanted;

	private boolean terminated;
	private Thread flashThread;
	private boolean hasFood;
	private int id;

	class FlashThread implements Runnable {

		private boolean lightsUp;

		public void run() {
			terminated = false;
			lightsUp = false;
			while (!terminated) {
				if (flashing && !lightsUp) {
					app.setColoringAttributes(new ColoringAttributes(
							flashingColor, 1));
					lightsUp = true;
				} else if (!active) {
					app.setColoringAttributes(new ColoringAttributes(
							normalColor, 1));
					lightsUp = false;
				} else {
					app.setColoringAttributes(new ColoringAttributes(
							activeColor, 1));
					lightsUp = false;
				}

				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

	public FeederNode(ElementWrapper params) {
		active = false;
		flashing = false;
		wanted = false;
		hasFood = false;
		

		normalColor = new Color3f(params.getChildFloat("cr"),
				params.getChildFloat("cg"), params.getChildFloat("cb"));
		flashingColor = new Color3f(1f, 1f, 1f);
		activeColor = new Color3f(1f, .4f, 0f);
		float xp = params.getChildFloat("x");
		float yp = params.getChildFloat("y");
		float zp = params.getChildFloat("z");
		float r = params.getChildFloat("r");
		float h = params.getChildFloat("h");
		id = params.getChildInt("id");
		app = new Appearance();
		app.setColoringAttributes(new ColoringAttributes(normalColor, 1));
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		Primitive vol = new Cylinder(r, h, app);
		addVolume(null, vol, xp, yp, zp);

		position = new Point3f(xp, yp, zp);

		flashThread = new Thread(new FlashThread());
		flashThread.start();
	}
	
	public FeederNode(int id, float x, float y) {
		active = false;
		flashing = false;
		wanted = false;
		hasFood = false;
		

		normalColor = new Color3f(1f, 0, 0);
		flashingColor = new Color3f(1f, 1f, 1f);
		activeColor = new Color3f(1f, .4f, 0f);
		float xp = x;
		float yp = y;
		float zp = 0;
		float r = .03f;
		float h = 0;

		app = new Appearance();
		app.setColoringAttributes(new ColoringAttributes(normalColor, 1));
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		Primitive vol = new Cylinder(r, h, app);
		addVolume(null, vol, xp, yp, zp);

		position = new Point3f(xp, yp, zp);

		flashThread = new Thread(new FlashThread());
		flashThread.start();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		// flashing = active;
		if (!flashing)
			if (!active)
				app.setColoringAttributes(new ColoringAttributes(normalColor, 1));
			else {
				app.setColoringAttributes(new ColoringAttributes(activeColor, 1));
			}
	}

	public void setWanted(boolean wanted) {
		this.wanted = wanted;
		// When flashing, just let the node do the work

	}

	public boolean isFlashing() {
		return flashing;
	}

	public void setFlashing(boolean flashing) {
		this.flashing = flashing;
	}

	public Point3f getPosition() {
		return position;
	}

	public boolean isWanted() {
		return wanted;
	}

	public void terminate() {
		terminated = true;
		try {
			flashThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		flashThread = null;
	}

	public void releaseFood() {
		hasFood = true;
	}

	public void clearFood() {
		hasFood = false;
	}

	public boolean hasFood() {
		return hasFood;
	}

	public int getId() {
		return id;
	}
}
