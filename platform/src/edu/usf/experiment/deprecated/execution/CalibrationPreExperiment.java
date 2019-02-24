package edu.usf.experiment.deprecated.execution;
//package edu.usf.experiment;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import edu.usf.experiment.utils.ElementWrapper;
//import edu.usf.experiment.utils.IOUtils;
//import edu.usf.experiment.utils.XMLExperimentParser;
//
//public class CalibrationPreExperiment {
//
//	public CalibrationPreExperiment(String calibrationFile, String logPath) {
//		logPath += File.separator;
//
//		IOUtils.copyFile(calibrationFile, logPath + "calibration.xml");
//		ElementWrapper calibrationRoot = XMLExperimentParser
//				.loadRoot(calibrationFile);
//
//		String experimentFile = calibrationRoot.getChildText("experiment");
//
//		IOUtils.copyFile(experimentFile, logPath + "/experiment.xml");
//
//		Map<String, List<String>> paramsToCalibrate = calibrationRoot
//				.getCalibrationList(calibrationRoot);
//
//		// Change values in model according to individual number
//		List<String> remainingParams = new LinkedList<String>(paramsToCalibrate.keySet());
//		initializeAllExperiments(remainingParams, new HashMap<String, String>(),
//				paramsToCalibrate, experimentFile, logPath);
//
//	}
//
//	private void initializeAllExperiments(List<String> remainingParams,
//			Map<String, String> currentParamValues,
//			Map<String, List<String>> paramsToCalibrate, String experimentFile,
//			String logPath) {
//		String currentParam = remainingParams.get(0);
//		remainingParams.remove(0);
//
//		for (String value : paramsToCalibrate.get(currentParam)) {
//			Map<String, String> newParamValues = new LinkedHashMap<String, String>(
//					currentParamValues);
//			newParamValues.put(currentParam, value);
//			if (remainingParams.isEmpty()) {
//				String config = "";
//				for (String param : newParamValues.keySet())
//					config += param + "-" + newParamValues.get(param) + "--";
//				new PreExperiment(experimentFile, logPath + File.separator
//						+ config + File.separator).run();
//			} else {
//				initializeAllExperiments(new LinkedList<String>(remainingParams), newParamValues,
//						paramsToCalibrate, experimentFile, logPath);
//			}
//		}
//	}
//
//	public static void main(String[] args) {
//		new CalibrationPreExperiment(args[0],
//				args[1]);
//	}
//}
