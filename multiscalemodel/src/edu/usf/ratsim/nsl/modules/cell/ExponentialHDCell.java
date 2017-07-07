package edu.usf.ratsim.nsl.modules.cell;

import edu.usf.experiment.utils.GeomUtils;

/**
 * Head direction cell are modulated only by the animat's current orientation.
 * @author Martin Llofriu
 *
 */
public class ExponentialHDCell {

	
	private static final double RADIUS_THRS = .2;

	private float preferredOrientation;
	private float width;

	private boolean agnostic;

	public ExponentialHDCell(float preferredOrientation, float radius) {
		super();
		this.preferredOrientation = preferredOrientation;
		// System.out.println(radius);
		if (radius >= 6.28) {
			agnostic = true;
			// System.out.println("agnostic");
		} else {
			agnostic = false;
			this.width = (float) (-Math.pow(radius, 2) / Math.log(RADIUS_THRS));
		}

	}

	public float getActivation(float currOrientation) {
		if (!agnostic)
			return (float) Math.exp(-Math.pow(
					GeomUtils.relativeAngle(currOrientation, preferredOrientation), 2)
					/ width);
		else
			return 1;
		// return Utiles.gaussian(GeomUtils.angleDistance(currOrientation,
		// preferredOrientation), width);
	}

	

	public static void main(String[] args) {
		ExponentialHDCell hdc = new ExponentialHDCell(1, 1);
		System.out.println(GeomUtils.relativeAngle((float) (Math.PI / 2), 0f));
		System.out.println(GeomUtils.relativeAngle(0, 0f));
		System.out.println(GeomUtils.relativeAngle((float) (Math.PI),
				(float) (Math.PI / 2)));
		System.out.println(GeomUtils.relativeAngle(0.1f, -0.1f));
		System.out.println(GeomUtils.relativeAngle(-0.1f, 0.1f));
		System.out.println(GeomUtils.relativeAngle(-(float) (Math.PI),
				(float) (Math.PI)));
	}

}
