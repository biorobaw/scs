package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.util.LinkedList;

import TRN4JAVA.*;

public class Reservoir {
	
	
	static final int INDEX = 0; // CPU
	static final int SEED = 12345;
	
	int id = 42;
	int stimulus_size;

	//float stimulus[]= new float[OBSERVATIONS * STIMULUS_SIZE];
	
	TRN4JAVA.Loop loop = null;	 
	
	static {
		TRN4JAVA.initialize_local(INDEX, SEED);
	}
	
	public Reservoir(int id,int stimulus_size,int reservoir_size, float leak_rate, 
			         float initial_state_scale, float learning_rate,int epochs){
		
		this.id = id;
		this.stimulus_size = stimulus_size;
		
		
		TRN4JAVA.allocate(id);
		TRN4JAVA.configure_begin(id);
		
		TRN4JAVA.configure_reservoir_widrow_hoff(id, stimulus_size, stimulus_size, reservoir_size, 
				                                 leak_rate, initial_state_scale, learning_rate);
		

		TRN4JAVA.configure_scheduler_tiled(id, epochs);

		TRN4JAVA.configure_feedforward_uniform(id, -1.0f, 1.0f, 0.0f);
		TRN4JAVA.configure_recurrent_gaussian(id, 0.0f, 0.5f/(float)Math.sqrt(reservoir_size));
		TRN4JAVA.configure_feedback_uniform(id, -1.0f, 1.0f, 0.0f);
		TRN4JAVA.configure_readout_uniform(id, -1e-2f, 1e-2f, 0.0f);		
		
		
	}
	
	void finishInitialization(TRN4JAVA.Loop receive){
		this.loop = receive;
		
		TRN4JAVA.configure_loop_custom(id ,stimulus_size , receive);
		TRN4JAVA.configure_end(id);
		
	}
	
	void train(LinkedList<float[]> pcHistory ,LinkedList<Boolean> ateHistory){
		//incoming = whole pc sequence (time 1 to n-1)
		//expected = pc sequence (2 to n)
		//reward  = sequence of received rewards
		//observations = num rows 

		int observations = pcHistory.size()-1;
		int num_pc = pcHistory.get(0).length;
		float[] incoming = new float[observations*num_pc];
		float[] expected = new float[observations*num_pc];
		float[] reward = new float[observations*num_pc];
		
		for(int i=0;i<observations;i++){
			
			
			for(int j=0;j<num_pc; j++){
				incoming[i*num_pc + j] = pcHistory.get(i)[j];
				expected[i*num_pc + j] = pcHistory.get(i+1)[j];
			}
			
			reward[i] = ateHistory.get(i+1) ? 1f : 0f;
		}
		
		TRN4JAVA.declare_sequence(id, "target", incoming, expected, reward, observations);
		TRN4JAVA.train(id, "target");
		
		
	}
	
	public void transmit(final float stimuli[]){
		loop.stimulus(stimuli);
	}
	
	public void newEpisode(){
		TRN4JAVA.test(id, "target", 10); // at beginning of new episode - this provides predictions
		
	}
	
	void setup(){
		
	}
	
	void deallocate(){
		
		TRN4JAVA.deallocate(id);
		
	}
	


	
}
