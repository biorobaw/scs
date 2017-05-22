package edu.usf.experiment.task;

import edu.usf.experiment.model.SaveModel;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class SaveSubject extends Task {

	//private ElementWrapper filename;

	public SaveSubject(ElementWrapper params) {
		super(params);

		//filename = params.getChild("filename");
	}

	public void perform(Universe u, Subject s){
//		sub.save(PropertyHolder.getInstance().getProperty("log.directory") + filename);
		SaveModel m = (SaveModel) s.getModel();
		m.save();
		
	}

}
