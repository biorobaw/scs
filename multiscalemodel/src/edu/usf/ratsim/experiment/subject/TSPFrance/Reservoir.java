package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.util.LinkedList;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.Arrays;


public class Reservoir {
	
	static final String INC_TAG = "Incoming";
	static final String EXP_TAG = "Expected";
	static final String REW_TAG = "Reward";
	static final String POS_TAG = "Position";
	static final String TARGET_SEQUENCE = "Target";
	static final String TRAINING_BATCH []= {TARGET_SEQUENCE};
	static final int INDEX = 0; // 0 = CPU, > 0 = GPU
	static final int SEED = 12345;

	int preamble;

	boolean testing = false;
	boolean target_declared = false; 
	int id = 42;
	int stimulus_size;
	int rows;
	int cols;
	float x_min;
	float x_max;
	float y_min;
	float y_max;
	float response[];
	float sigma;
	float radius;
	
	int remaining_steps;
	//float stimulus[]= new float[OBSERVATIONS * STIMULUS_SIZE];
	
	TRN4JAVA.Api.Loop position = null;	 
	TRN4JAVA.Api.Loop stimulus = null;
	
	static {
		TRN4JAVA.Api.initialize_local(INDEX, SEED);
	}
	public static void display(String label, float samples[], final int rows, final int cols)
	{ 
		final int WIDTH=cols;
		final int HEIGHT=rows;

		JFrame frame = new JFrame(label);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 
		final BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D)img.getGraphics();
		for(int j = 0; j < HEIGHT; j++)
		{
			for(int i = 0; i < WIDTH; i++)
			{	
				float c = samples[j*WIDTH+i];
				if (c > 1.0f)
				 c = 1.0f;
				 else if (c < -1.0f)
				 c = -1.0f;
				float red = c >= 0.0f ? c : 0.0f;
				float green = c < 0.0f ? -c : 0.0f;
				g.setColor(new Color(red, green, 0.0f));
				g.fillRect(i, j, 1, 1);
			 }
		 }
		 JPanel panel = new JPanel() 
		 {
			@Override
			protected void paintComponent(Graphics g)
			{
				Graphics2D g2d = (Graphics2D)g;
				g2d.clearRect(0, 0, getWidth(), getHeight());
				g2d.drawImage(img, 0, 0, this);
			 }
		  };
		  panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		  frame.getContentPane().add(panel);
		  frame.pack();
	      frame.setVisible(true);
	  }
  
	public Reservoir(
			int id,
			int stimulus_size,int reservoir_size, float leak_rate, float initial_state_scale, float learning_rate,
			int snippets_size,
			int snippets_per_burst,
			int burst_per_trial,
			int rows, int cols, float x_min, float x_max, float y_min, float y_max, float response[], float sigma, float radius,
			int preamble
			){
		
		
		this.id = id;
		this.stimulus_size = stimulus_size;
		
		this.rows = rows;
		this.cols = cols;
		this.x_min = x_min;
		this.x_max = x_max;
		this.y_min = y_min;
		this.y_max = y_max;
		this.response = response;
		this.sigma = sigma;
		this.radius = radius;

		this.preamble = preamble;
		
		

		TRN4JAVA.Api.allocate(id);
		

		TRN4JAVA.Api.configure_begin(id);
		
		TRN4JAVA.Api.configure_reservoir_widrow_hoff(id, stimulus_size, stimulus_size, reservoir_size, 
				                                 leak_rate, initial_state_scale, learning_rate);
		TRN4JAVA.Api.setup_performances(id, new TRN4JAVA.Api.Performances()
		{
				@Override
				 public void callback(String phase, final float cycles_per_second)
				 {
					System.out.println("Performances callback : phase = " + phase + ", cycles_per_second= " + cycles_per_second);
				 }
		});
		
		TRN4JAVA.Api.setup_states(id, new TRN4JAVA.Api.Matrix()
		{
				@Override
				 public void callback(String label,  final float samples[], final int rows, final int cols)
				 {
				    display(label, samples, rows, cols);
					System.out.println("States callback : label = " + label + ", rows = " + rows + ", cols = " + cols);
				 }
		});
		/*TRN4JAVA.Api.setup_weights(id, new TRN4JAVA.Api.Matrix()
		{
				@Override
				 public void callback(String label,  final float samples[], final int rows, final int cols)
				 {
				    //display(label, samples, rows, cols);
					System.out.println("Weights callback : label = " + label + ", rows = " + rows + ", cols = " + cols);
				 }
		});*/
		//TRN4JAVA.Api.configure_scheduler_snippets(id, snippets_size, snippets_size * snippets_per_burst * burst_per_trial, REW_TAG);
		TRN4JAVA.Api.configure_scheduler_tiled(id, 100);
		TRN4JAVA.Api.configure_feedforward_uniform(id, -1.0f, 1.0f, 0.0f);
		TRN4JAVA.Api.configure_recurrent_gaussian(id, 0.0f, 1f/(float)Math.sqrt(reservoir_size));
		TRN4JAVA.Api.configure_feedback_uniform(id, -1.0f, 1.0f, 0.0f);
		TRN4JAVA.Api.configure_readout_uniform(id, -1e-2f, 1e-2f, 0.0f);		
		
		
	}
	
	void finishInitialization(TRN4JAVA.Api.Loop position, TRN4JAVA.Api.Loop stimulus){
		this.position = position;
		this.stimulus = stimulus;
		TRN4JAVA.Api.configure_loop_spatial_filter(id ,stimulus_size , position, stimulus, rows, cols, x_min, x_max, y_min, y_max, response, sigma, radius, POS_TAG);
		TRN4JAVA.Api.configure_end(id);
		
	}
	
	
	void train(LinkedList<float[]> pcHistory ,LinkedList<float[]> posHistory ,LinkedList<Boolean> ateHistory){
		//incoming = whole pc sequence (time 1 to n-1)
		//expected = pc sequence (2 to n)
		//reward  = sequence of received rewards
		//observations = num rows 

		int observations = pcHistory.size()-1;
		int num_pc = pcHistory.get(0).length;
		float[] incoming = new float[observations*num_pc];
		float[] expected = new float[observations*num_pc];
		float[] position = new float[observations*2];
		float[] reward = new float[observations];
		
		for(int i=0;i<observations;i++){
			
			
			for(int j=0;j<num_pc; j++){
				incoming[i*num_pc + j] = pcHistory.get(i)[j];
				expected[i*num_pc + j] = pcHistory.get(i+1)[j];
			}
			position[i * 2] = posHistory.get(i)[0];
			position[i * 2 + 1] = posHistory.get(i)[1];
			
			reward[i] = ateHistory.get(i) ? 1f : 0f;
		}
			
		TRN4JAVA.Api.declare_sequence(id, TARGET_SEQUENCE, INC_TAG, incoming, observations); 
		TRN4JAVA.Api.declare_sequence(id, TARGET_SEQUENCE, EXP_TAG, expected, observations);
		TRN4JAVA.Api.declare_sequence(id, TARGET_SEQUENCE, REW_TAG, reward, observations);
		TRN4JAVA.Api.declare_sequence(id, TARGET_SEQUENCE, POS_TAG, position, observations);
		TRN4JAVA.Api.declare_batch(id, TARGET_SEQUENCE, INC_TAG, TRAINING_BATCH);
		TRN4JAVA.Api.declare_batch(id, TARGET_SEQUENCE, EXP_TAG, TRAINING_BATCH);
		TRN4JAVA.Api.declare_batch(id, TARGET_SEQUENCE, REW_TAG, TRAINING_BATCH);
		TRN4JAVA.Api.train(id, TARGET_SEQUENCE, INC_TAG, EXP_TAG);

		target_declared = true;
		remaining_steps = observations;
	}
	
	public void reinject(final float estimated_position[], final float activation_pattern[]){
		if (testing)
		{
			position.notify(estimated_position);
			stimulus.notify(activation_pattern);
			remaining_steps--;
			if (remaining_steps == 0)
				testing = false;
		}
			
	}
	
	public void newEpisode(){
		if (target_declared)
		{
			testing = true;
			remaining_steps -= preamble;
			TRN4JAVA.Api.test(id, TARGET_SEQUENCE, INC_TAG, EXP_TAG, preamble); // at beginning of new episode - this provides predictions
			
		}

	}
	
	void setup(){
		
	}
	
	void deallocate(){
		
		TRN4JAVA.Api.deallocate(id);
		
	}
	


	
}
