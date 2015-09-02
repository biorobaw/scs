package edu.usf.experiment;

import java.io.File;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;
import edu.usf.experiment.utils.XMLExperimentParser;

public class CalibrationExperiment {

	public CalibrationExperiment(String calibrationFile, String logPath,
			int individualNumber) {
		logPath = logPath + "/";

		IOUtils.copyFile(calibrationFile, logPath + "/calibration.xml");
		ElementWrapper calibrationRoot = XMLExperimentParser
				.loadRoot(calibrationFile);

		String experimentFile = calibrationRoot.getChildText("experiment");

		IOUtils.copyFile(experimentFile, logPath + "/experiment.xml");
		ElementWrapper experimentRoot = XMLExperimentParser
				.loadRoot(experimentFile);

		Map<String, List<String>> paramsToCalibrate = calibrationRoot
				.getCalibrationList(calibrationRoot);

		// Compute total individuals per experiment
		List<ElementWrapper> groupNodes = experimentRoot.getChildren("group");
		int totalExperimentIndividuals = 0;
		for (ElementWrapper gNode : groupNodes) {
			totalExperimentIndividuals += gNode.getChildInt("numMembers");
		}

		// Change values in model according to individual number
		int paramIndex = individualNumber / totalExperimentIndividuals;
		for (String param : paramsToCalibrate.keySet()) {
			int numValues = paramsToCalibrate.get(param).size();
			String value = paramsToCalibrate.get(param).get(
					paramIndex % numValues);
			logPath += param + "-" + value + "--";
			paramIndex /= numValues;
			experimentRoot.changeModelParam(param, value, experimentRoot);
		}
		logPath += File.separator;

		int experimentIndividualNumber = individualNumber
				% totalExperimentIndividuals;
		RunIndividualByNumber.runIndividualByNumber(experimentRoot, logPath,
				experimentIndividualNumber);

	}

	public static void main(String[] args) {
		CalibrationExperiment cexp = new CalibrationExperiment(args[0],
				args[1], Integer.parseInt(args[2]));
	}
}
