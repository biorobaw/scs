package edu.usf.experiment.log;

import java.io.PrintWriter;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.model.ValueModel;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class ValueLogger extends DistributedLogger {

	private PrintWriter writer;

	public ValueLogger(ElementWrapper params) {
		super(params);

		
		
	}

	@Override
	public void perform(Universe u, Subject sub) {
		Globals g = Globals.getInstance();
		String trialName = g.get("trial").toString();
		String groupName = g.get("group").toString();
		String subName = g.get("subName").toString();
		String episode = g.get("episode").toString();
		String cycle = g.get("cycle").toString();
		


		System.out.println("Starting to log value");


		ValueModel vm = (ValueModel) sub.getModel();
		
		Map<Coordinate, Float> valPoints = vm.getValuePoints();
	
		for (Coordinate p : valPoints.keySet())
			writer.println(trialName + '\t' + groupName + '\t'
					+ subName + '\t' + episode + '\t' + cycle+ "\t" + p.x
					+ "\t" + p.y + "\t" +  valPoints.get(p));

		System.out.println("Finished loggin value");
	}


	private boolean inCircle(float x, float y, double width) {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) <= width / 2;
	}

	@Override
	public void initLog() {
		// TODO Auto-generated method stub
		super.initLog();
		writer = getWriter();
	}
	
	@Override
	public void finalizeLog() {
		writer.close();
	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tcycle\tx\ty\tval";
	}

	@Override
	public String getFileName() {
		return "value.csv";
	}


}
