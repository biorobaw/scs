package edu.usf.experiment.deprecated.execution;

import java.io.File;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.XMLExperimentParser;

public class CalibrationExperiment {

	public CalibrationExperiment(String logPath,int individualNumber) {
		
		logPath = logPath + File.separator;

		// Assumes pre calibration put it on the folder
		ElementWrapper calibrationRoot = XMLExperimentParser.loadRoot(logPath + "calibration.xml");

		String experimentFile = calibrationRoot.getChildText("experiment");

		ElementWrapper experimentRoot = XMLExperimentParser.loadRoot(experimentFile);

		Map<String, List<String>> paramsToCalibrate = calibrationRoot.getCalibrationList(calibrationRoot);

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
			System.out.println("[+] Setting param " + param + " to value " + value);
			experimentRoot.changeModelParam(param, value, experimentRoot);
		}
		logPath += File.separator;

		int experimentIndividualNumber = individualNumber % totalExperimentIndividuals;
//		RunIndividualByNumber.runIndividualByNumber(experimentRoot, logPath,experimentIndividualNumber);
		System.out.println("ERROR ===> Calibration Experiment needs to be fixed");

	}

	public static void main(String[] args) {
		CalibrationExperiment cexp = new CalibrationExperiment(args[0],Integer.parseInt(args[1]));
	}
}
