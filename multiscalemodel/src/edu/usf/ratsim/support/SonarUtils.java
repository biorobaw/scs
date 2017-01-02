package edu.usf.ratsim.support;

import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.port.onedimensional.Float1dPort;

public class SonarUtils {
	private static final float MIN_ANGLE_MAX_DIFF_THRS = (float) (Math.PI / 8);

	public static float getReading(float angle, Float1dPort readings, Float1dPort angles) {
		// Get the closest sonar
		float minAngleDiff = Float.MAX_VALUE;
		int minSonar = 0;
		for (int i = 0; i < angles.getSize(); i++) {
			float angleDiff = Math.abs(GeomUtils.angleDiff(angles.get(i), angle));
			if (angleDiff < minAngleDiff) {
				minAngleDiff = angleDiff;
				minSonar = i;
			}
		}
		
		return readings.get(minSonar);
	}

	public static boolean validSonar(float angle, Float1dPort readings, Float1dPort angles) {
		// Get the closest sonar
		float minAngleDiff = Float.MAX_VALUE;
		for (int i = 0; i < angles.getSize(); i++) {
			float angleDiff = Math.abs(GeomUtils.angleDiff(angles.get(i), angle));
			if (angleDiff < minAngleDiff) {
				minAngleDiff = angleDiff;
			}
		}
		
		return minAngleDiff < MIN_ANGLE_MAX_DIFF_THRS;
	}
}
