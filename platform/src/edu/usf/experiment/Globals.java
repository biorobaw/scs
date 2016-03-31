package edu.usf.experiment;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Globals {
	
	public HashMap<String,Object> global;
	
	private static Globals instance = null;
	protected Globals(){
		global = new LinkedHashMap<String,Object>();
	}
	
	public static Globals getInstace(){
		if(instance==null) {
			instance = new Globals();
		}
		return instance;
	}
	
	public void put(String id,Object globalVar){
		global.put(id,globalVar);
	}
	
	public Object get(String id){
		return global.get(id);
	}
	
	public void remove(String id){
		global.remove(id);
	}
}
