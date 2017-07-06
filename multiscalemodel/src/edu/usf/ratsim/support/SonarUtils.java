package edu.usf.ratsim.support;

import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.port.onedimensional.Float1dPort;

public class SonarUtils {
	private static final float MIN_ANGLE_MAX_DIFF_THRS = (float) (Math.PI / 8);
	private static final float ANGLE_EPS = 0.1f;

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

	public static float getSmallerReading(Float1dPort readings) {
		float smaller = Float.MAX_VALUE;
		for (int i = 0; i < readings.getSize(); i++) {
			float reading = readings.get(i);
			if (reading < smaller) {
				smaller = reading;
			}
		}
		return smaller;
	}

	/**
	 * Returns the minimum reading for a range of angles
	 * @param minAngle
	 * @param maxAngle
	 * @return
	 */
	public static float getMinReading(Float1dPort readings, Float1dPort angles, float angle, float range) {
		float minReading = Float.MAX_VALUE;
		for (int a = 0; a < angles.getSize(); a++){
			float sensorAngle = angles.get(a);
			if (Math.abs(GeomUtils.angleDiff(angle, sensorAngle)) <= range + ANGLE_EPS)
				if (readings.get(a) < minReading)
					minReading = readings.get(a);
		}
		return minReading;
	}
}
