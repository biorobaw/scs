package edu.usf.ratsim.model.pathplanning.graphbased;

import java.io.PrintWriter;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.log.DistributedLogger;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.model.GraphModel;
import edu.usf.ratsim.nsl.modules.pathplanning.Edge;
import edu.usf.ratsim.nsl.modules.pathplanning.PointNode;

public class ExperienceGraphLogger extends DistributedLogger {

	private UndirectedGraph<PointNode, Edge> graph;

	public ExperienceGraphLogger(ElementWrapper params) {
		super(params);

	}

	@Override
	public void initLog() {
		// TODO Auto-generated method stub
		super.initLog();
		graph = new UndirectedSparseGraph<PointNode, Edge>();
	}

	public void perform(Universe u, Subject sub){
		if (!(sub.getModel() instanceof GraphModel))
			throw new IllegalArgumentException("ExperienceGraphLogger needs a BugandGraphSubject");
		GraphModel gs = (GraphModel) sub.getModel();

		graph = gs.getGraph();

	}

	@Override
	public void finalizeLog() {
		Globals g = Globals.getInstance();
		String trialName = g.get("trial").toString();
		String groupName = g.get("group").toString();
		String subName = g.get("subName").toString();
		String episode = g.get("episode").toString();
		String cycle = g.get("cycle").toString();
		PrintWriter writer = getWriter();
		for (Edge e : graph.getEdges()) {
			PointNode n1 = graph.getEndpoints(e).getFirst();
			PointNode n2 = graph.getEndpoints(e).getSecond();
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
