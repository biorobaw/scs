package edu.usf.ratsim.model.pablo.morris_replay.submodules;



import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Int1dPort;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Checks whether two elements on an array are equal
 * The module expects an array of floats as input
 * and two parameters which can be integer or floats.
 * Int parameters are assumed to be array indexes and 
 * thus converted to float
 * 
 */
public class AreEqualModule extends Module {

	public Bool0dPort areEqual = new Bool0dPort(null,false);
	private float epsilon; //max error for two floats to be considered as equal
	
	public AreEqualModule(String name,float epsilon){
		super(name);

		this.epsilon = epsilon;
		addOutPort("areEqual", areEqual);
		
	}

	
	public void run() {
		Float1dPortArray vals = (Float1dPortArray)getInPort("values");
		Float val1 = 0f;
		Float val2 = 0f;
		Port in1 = getInPort("input1");
		Port in2 = getInPort("input2");
		
		if(in1 instanceof Float0dPort) val1 = ((Float0dPort)in1).get();
		else val1 = vals.get(((Int0dPort)in1).get());
		
		if(in2 instanceof Float0dPort) val2 = ((Float0dPort)in2).get();
		else val2 = vals.get(((Int0dPort)in2).get());
		
		areEqual.set(Math.abs(val1-val2)<epsilon);
		
		
	}

	@Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		super.newEpisode();
		areEqual.set(false);
	}

	@Override
	public boolean usesRandom() {
		
		return false;
	}
}
