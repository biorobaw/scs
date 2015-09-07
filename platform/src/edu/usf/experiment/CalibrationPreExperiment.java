package edu.usf.experiment;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;
import edu.usf.experiment.utils.XMLExperimentParser;

public class CalibrationPreExperiment {

	public CalibrationPreExperiment(String calibrationFile, String logPath,
			int individualNumber) {
		logPath += File.separator;

		IOUtils.copyFile(calibrationFile, logPath + "/calibration.xml");
		ElementWrapper calibrationRoot = XMLExperimentParser
				.loadRoot(calibrationFile);

		String experimentFile = calibrationRoot.getChildText("experiment");

		IOUtils.copyFile(experimentFile, logPath + "/experiment.xml");

		Map<String, List<String>> paramsToCalibrate = calibrationRoot
				.getCalibrationList(calibrationRoot);

		// Change values in model according to individual number
		List<String> remainingParams = new LinkedList<String>(paramsToCalibrate.keySet());
		// Sort remaining params to keep folder names fixed
		Collections.sort(remainingParams);
		initializeAllExperiments(remainingParams, new HashMap<String, String>(),
				paramsToCalibrate, experimentFile, logPath);

	}

	private void initializeAllExperiments(List<String> remainingParams,
			Map<String, String> currentParamValues,
			Map<String, List<String>> paramsToCalibrate, String experimentFile,
			String logPath) {
		String currentParam = remainingParams.get(0);
		remainingParams.remove(0);

		for (String value : paramsToCalibrate.get(currentParam)) {
			Map<String, String> newParamValues = new HashMap<String, String>(
					currentParamValues);
			newParamValues.put(currentParam, value);
			if (remainingParams.isEmpty()) {
				String config = "";
				for (String param : newParamValues.keySet())
					config += param + "-" + newParamValues.get(param) + "--";
				new PreExperiment(experimentFile, logPath + File.separator
						+ config + File.separator).run();
			} else {
				initializeAllExperiments(remainingParams, newParamValues,
						paramsToCalibrate, experimentFile, logPath);
			}
		}
	}

	public static void main(String[] args) {
		new CalibrationPreExperiment(args[0],
				args[1], Integer.parseInt(args[2]));
	}
}
