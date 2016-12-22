package edu.usf.micronsl.ModelAction.SpecificActions;

import edu.usf.micronsl.ModelAction.ModelAction;

public class FeederTaxicAction extends ModelAction{
	public FeederTaxicAction(Integer id) {
		super("FeederTaxicAction",id);
	}
	
	public Integer id(){
		return (Integer)params.get(0);
	}
	
	public void setId(Integer id){
		params.set(0, id);
	}

	
}
