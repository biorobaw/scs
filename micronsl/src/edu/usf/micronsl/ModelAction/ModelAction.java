package edu.usf.micronsl.ModelAction;

import java.util.LinkedList;

public class ModelAction {

	public String actionId;
	public LinkedList<Object> params = new LinkedList<Object>();
	
	public ModelAction(String id,Object... varargs){
		actionId = id;
		for (Object o : varargs) params.addLast(o);		

	}
		


}
