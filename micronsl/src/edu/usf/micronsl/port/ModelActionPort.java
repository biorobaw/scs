package edu.usf.micronsl.port;

import edu.usf.micronsl.ModelAction.ModelAction;
import edu.usf.micronsl.module.Module;

public class ModelActionPort extends Port{

	public ModelAction data;
	
	public ModelActionPort(Module owner,ModelAction data) {
		super(owner);
		this.data = data;
		// TODO Auto-generated constructor stub
	}
	
	public ModelActionPort(Module owner) {
		super(owner);
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		data = null;
		
	}
	
	public void set(ModelAction data){
		this.data = data;
	}
	
	public ModelAction get(){
		return data;
	}

}
