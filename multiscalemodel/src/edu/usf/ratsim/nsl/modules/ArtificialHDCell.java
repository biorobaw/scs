package edu.usf.ratsim.nsl.modules;

import edu.usf.ratsim.support.GeomUtils;

public class ArtificialHDCell {

	private static final double RADIUS_THRS = .2;

	private float preferredOrientation;
	private float width;

	private boolean agnostic;

	public ArtificialHDCell(float preferredOrientation, float radius) {
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
					GeomUtils.angleDistance(currOrientation, preferredOrientation), 2)
					/ width);
		else
			return 1;
		// return Utiles.gaussian(GeomUtils.angleDistance(currOrientation,
		// preferredOrientation), width);
	}

	

	public static void main(String[] args) {
		ArtificialHDCell hdc = new ArtificialHDCell(1, 1);
		System.out.println(GeomUtils.angleDistance((float) (Math.PI / 2), 0f));
		System.out.println(GeomUtils.angleDistance(0, 0f));
		System.out.println(GeomUtils.angleDistance((float) (Math.PI),
				(float) (Math.PI / 2)));
		System.out.println(GeomUtils.angleDistance(0.1f, -0.1f));
		System.out.println(GeomUtils.angleDistance(-0.1f, 0.1f));
		System.out.println(GeomUtils.angleDistance(-(float) (Math.PI),
				(float) (Math.PI)));
	}

}
