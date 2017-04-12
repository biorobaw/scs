//package edu.usf.experiment;
//
//import java.util.List;
//
//import edu.usf.experiment.log.Logger;
//import edu.usf.experiment.log.LoggerLoader;
//import edu.usf.experiment.plot.Plotter;
//import edu.usf.experiment.plot.PlotterLoader;
//import edu.usf.experiment.subject.Subject;
//import edu.usf.experiment.task.Task;
//import edu.usf.experiment.task.TaskLoader;
//import edu.usf.experiment.utils.ElementWrapper;
//
//public abstract class Activity implements Runnable{
//
//	private ElementWrapper params;
//	private String logPath;
//	private List<Task> beforeTasks;
//	private List<Task> afterTasks;
//	private List<Plotter> beforePlotters;
//	private List<Logger> beforeLoggers;
//	private List<Plotter> afterPlotters;
//	private List<Logger> afterLoggers;
//	private Subject subject;
//
//	public Activity(ElementWrapper params, String logPath, Subject subject) {
//		this.setParams(params);
//		this.setLogPath(logPath);
//		this.setSubject(subject);
//
//		if (params.getChild("beforeTasks") != null)
//			beforeTasks = TaskLoader.getInstance().load(
//					params.getChild("beforeTasks"));
//		if (params.getChild("afterTasks") != null)
//			afterTasks = TaskLoader.getInstance().load(
//					params.getChild("afterTasks"));
//		if (params.getChild("beforePlotters") != null)
//			beforePlotters = PlotterLoader.getInstance().load(
//					params.getChild("beforePlotters"));
//		if (params.getChild("afterPlotters") != null)
//			afterPlotters = PlotterLoader.getInstance().load(
//					params.getChild("afterPlotters"));
//		if (params.getChild("beforeLoggers") != null)
//			beforeLoggers = LoggerLoader.getInstance().load(
//					params.getChild("beforeLoggers"));
//		if (params.getChild("afterLoggers") != null)
//			afterLoggers = LoggerLoader.getInstance().load(
//					params.getChild("afterLoggers"));
//	}
//	
//	public void run(){
////		synchronized (getSubject()) {
////			// Do all before trial tasks
////			for (Task task : beforeTasks)
////				task.perform(this);
////
////			if (beforeLoggers != null)
////				for (Logger l : beforeLoggers)
////					l.log(this);
////
////			getSubject().newTrial();
////
////			// Run each episode
////			for (Episode episode : episodes) {
////				episode.run();
////			}
////
////			// After trial tasks
////			for (Task task : afterTasks)
////				task.perform(this);
////
////			// Plotters
////			for (Plotter p : plotters)
////				p.plot();
////		}
//	}
//
//	public ElementWrapper getParams() {
//		return params;
//	}
//
//	public void setParams(ElementWrapper params) {
//		this.params = params;
//	}
//
//	public String getLogPath() {
//		return logPath;
//	}
//
//	public void setLogPath(String logPath) {
//		this.logPath = logPath;
//	}
//	
//	public List<Task> getBeforeTasks() {
//		return beforeTasks;
//	}
//
//	public List<Task> getAfterTasks() {
//		return afterTasks;
//	}
//
//	public List<Plotter> getBeforePlotters() {
//		return beforePlotters;
//	}
//
//	public List<Logger> getBeforeLoggers() {
//		return beforeLoggers;
//	}
//
//	public List<Plotter> getAfterPlotters() {
//		return afterPlotters;
//	}
//
//	public List<Logger> getAfterLoggers() {
//		return afterLoggers;
//	}
//
//	public Subject getSubject() {
//		return subject;
//	}
//
//	public void setSubject(Subject subject) {
//		this.subject = subject;
//	}
//
//}
