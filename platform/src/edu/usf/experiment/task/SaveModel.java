package edu.usf.experiment.task;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class SaveModel extends Task {

	//private ElementWrapper filename;

	public SaveModel(ElementWrapper params) {
		super(params);

		//filename = params.getChild("filename");
	}

	public void perform(Universe u, Subject s){
//		sub.save(PropertyHolder.getInstance().getProperty("log.directory") + filename);
		edu.usf.experiment.model.SaveModel m = (edu.usf.experiment.model.SaveModel) s.getModel();
		m.save();
		
	}

}
