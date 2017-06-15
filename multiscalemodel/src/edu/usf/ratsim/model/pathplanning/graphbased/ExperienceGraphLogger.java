package edu.usf.ratsim.model.pathplanning.graphbased;

import java.io.PrintWriter;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.log.DistributedLogger;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.model.GraphModel;
import edu.usf.ratsim.nsl.modules.pathplanning.Edge;
import edu.usf.ratsim.nsl.modules.pathplanning.PointNode;

public class ExperienceGraphLogger extends DistributedLogger {

	private UndirectedGraph<PointNode, Edge> g;

	public ExperienceGraphLogger(ElementWrapper params, String logPath) {
		super(params, logPath);

		g = new UndirectedSparseGraph<PointNode, Edge>();
	}

	@Override
	public void log(Episode e) {
		log(e.getSubject());
	}

	@Override
	public void log(Trial t) {
		log(t.getSubject());
	}

	@Override
	public void log(Experiment e) {
		log(e.getSubject());
	}

	private void log(Subject subject) {
		if (!(subject.getModel() instanceof GraphModel))
			throw new IllegalArgumentException("ExperienceGraphLogger needs a BugandGraphSubject");
		GraphModel gs = (GraphModel) subject.getModel();

		g = gs.getGraph();

	}

	@Override
	public void finalizeLog() {
		PropertyHolder props = PropertyHolder.getInstance();
		String trialName = props.getProperty("trial");
		String groupName = props.getProperty("group");
		String subName = props.getProperty("subject");
		String episode = props.getProperty("episode");
		String cycle = props.getProperty("cycle");
		PrintWriter writer = getWriter();
		for (Edge e : g.getEdges()) {
			PointNode n1 = g.getEndpoints(e).getFirst();
			PointNode n2 = g.getEndpoints(e).getSecond();
			writer.println(trialName + '\t' + groupName + '\t' + subName + '\t' + episode + '\t' + cycle + '\t'
					+ n1.prefLoc.x + "\t" + n1.prefLoc.y + "\t" + n2.prefLoc.x + '\t' + n2.prefLoc.y);
		}
	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tcycle\tx1\ty1\tx2\ty2";
	}

	@Override
	public String getFileName() {
		return "exproadmap.csv";
	}

}
