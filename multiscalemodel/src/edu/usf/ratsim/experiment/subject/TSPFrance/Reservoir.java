package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.util.LinkedList;

import TRN4JAVA.Api;

public class Reservoir {
	
	
	static final int INDEX = 0; // CPU
	static final int SEED = 12345;
	boolean testing = false;
	boolean target_declared = false; 
	int id = 42;
	int stimulus_size;

	//float stimulus[]= new float[OBSERVATIONS * STIMULUS_SIZE];
	
	TRN4JAVA.Api.Loop loop = null;	 
	
	static {
		TRN4JAVA.Api.initialize_local(INDEX, SEED);
	}
	
	public Reservoir(int id,int stimulus_size,int reservoir_size, float leak_rate, 
			         float initial_state_scale, float learning_rate,int epochs){
		
		this.id = id;
		this.stimulus_size = stimulus_size;
		

		TRN4JAVA.Api.allocate(id);
		

		TRN4JAVA.Api.configure_begin(id);
		
		TRN4JAVA.Api.configure_reservoir_widrow_hoff(id, stimulus_size, stimulus_size, reservoir_size, 
				                                 leak_rate, initial_state_scale, learning_rate);
		/*TRN4JAVA.Api.setup_performances(id, new TRN4JAVA.Api.Performances()
		{
				@Override
				 public void callback(String phase, final float cycles_per_second)
				 {
					System.out.println("Performances callback : phase = " + phase + ", cycles_per_second= " + cycles_per_second);
				 }
		});*/
		
		/*TRN4JAVA.Api.setup_states(id, new TRN4JAVA.Api.Matrix()
		{
				@Override
				 public void callback(String label,  final float samples[], final int rows, final int cols)
				 {
				    //display(label, samples, rows, cols);
					System.out.println("States callback : label = " + label + ", rows = " + rows + ", cols = " + cols);
				 }
		});*/
		/*TRN4JAVA.Api.setup_weights(id, new TRN4JAVA.Api.Matrix()
		{
				@Override
				 public void callback(String label,  final float samples[], final int rows, final int cols)
				 {
				    //display(label, samples, rows, cols);
					System.out.println("Weights callback : label = " + label + ", rows = " + rows + ", cols = " + cols);
				 }
		});*/
		TRN4JAVA.Api.configure_scheduler_tiled(id, epochs);

		TRN4JAVA.Api.configure_feedforward_uniform(id, -1.0f, 1.0f, 0.0f);
		TRN4JAVA.Api.configure_recurrent_gaussian(id, 0.0f, 0.5f/(float)Math.sqrt(reservoir_size));
		TRN4JAVA.Api.configure_feedback_uniform(id, -1.0f, 1.0f, 0.0f);
		TRN4JAVA.Api.configure_readout_uniform(id, -1e-2f, 1e-2f, 0.0f);		
		
		
	}
	
	void finishInitialization(TRN4JAVA.Api.Loop receive){
		this.loop = receive;
		
		TRN4JAVA.Api.configure_loop_custom(id ,stimulus_size , receive);
		TRN4JAVA.Api.configure_end(id);
		
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
		float[] reward = new float[observations];
		
		for(int i=0;i<observations;i++){
			
			
			for(int j=0;j<num_pc; j++){
				incoming[i*num_pc + j] = pcHistory.get(i)[j];
				expected[i*num_pc + j] = pcHistory.get(i+1)[j];
			}
			
			reward[i] = ateHistory.get(i+1) ? 1f : 0f;
		}
			
		TRN4JAVA.Api.declare_sequence(id, "target", incoming, expected, reward, observations);
		TRN4JAVA.Api.train(id, "target");

		target_declared = true;
	}
	
	public void transmit(final float stimuli[]){
		if (testing)
			loop.notify(stimuli);
	}
	
	public void newEpisode(){
		if (target_declared)
		{
			testing = true;
			TRN4JAVA.Api.test(id, "target", 10); // at beginning of new episode - this provides predictions
			testing = false;
		}

	}
	
	void setup(){
		
	}
	
	void deallocate(){
		
		TRN4JAVA.Api.deallocate(id);
		
	}
	


	
}
