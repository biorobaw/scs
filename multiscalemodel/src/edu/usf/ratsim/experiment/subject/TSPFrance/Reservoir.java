package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.*;
import javax.vecmath.Point3f;

import java.util.Arrays;
import TRN4JAVA.Engine;
import TRN4JAVA.Simulation;

public class Reservoir {
	
	static final String INC_TAG = "Incoming";
	static final String EXP_TAG = "Expected";
	static final String REW_TAG = "Reward";
	static final String POS_TAG = "Position";
	static final String TARGET_SEQUENCE = "Target";
	static final String TRAINING_SEQUENCES[] = {TARGET_SEQUENCE};
	static final String TRAINING_SET = "training_set"; 

	private TRN4JAVA.Simulation.Loop position = null;	 
	private TRN4JAVA.Simulation.Loop stimulus = null;
	
	private int snippets_size, time_budget;
	
	private long batch_size, stimulus_size, reservoir_size;
	private float leak_rate, initial_state_scale, learning_rate;
	private long id;
	private int preamble;
	
	private float response[];
	private long rows, cols;
	private float x_min, x_max, y_min, y_max;
	private float sigma, radius, scale;
	private long seed;
	
	
	private boolean evaluating;
	private long trial, evaluation;
	private float priming_sequence[];
	private  java.util.Queue<Point3f> pending_points = new ConcurrentLinkedQueue<Point3f>();
	
	private boolean trained = false;
	private final Lock trained_lock = new ReentrantLock();
	private final Condition trained_condition = trained_lock.newCondition();
	private boolean injected = false;
	public Reservoir(long id, long stimulus_size, long reservoir_size, float leak_rate, float initial_state_scale, float learning_rate,
			int snippets_size, int time_budget,
			long rows, long cols, float xmin, float xmax, float ymin, float ymax, float response[], float sigma, float radius, float scale,
			int preamble)
	{
		this.evaluating = false;
		this.id = id;
		this.batch_size = 1;
		this.preamble=preamble;
		this.stimulus_size = stimulus_size;
		this.reservoir_size = reservoir_size;
		this.leak_rate = leak_rate;
		this.initial_state_scale = initial_state_scale;
		this.learning_rate = learning_rate;
		this.snippets_size = snippets_size;
		this.time_budget = time_budget;
		this.rows = rows;
		this.cols = cols;
		this.x_min = xmin;
		this.x_max = xmax;
		this.y_min = ymin;
		this.y_max = ymax;
		this.x_min = xmin;
		this.x_max = xmax;
		this.response = response;
		this.sigma = sigma;
		this.radius = radius;
		this.scale = scale;
		
		TRN4JAVA.Simulation.allocate(id);	
	}
	
	protected void finalize() throws Throwable 
	{
	     try 
	     {
	    	 TRN4JAVA.Simulation.deallocate(id);
	    	 TRN4JAVA.Engine.uninitialize();
	     } 
	     finally 
	     {
	         super.finalize();
	     }
	 }
	public static void debug_2D_buffer(final String label, float buffer[], int rows, int cols)
	{
		   final BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g = (Graphics2D)img.getGraphics();
	  
	        
	        for(int j = 0; j < rows; j++) {
	        	for(int i = 0; i < cols; i++) {
	                float c = (float) buffer[j * cols + i];
	                float b = 0.2f;
	                
	                if (c >= 0.0f)
	                {
	                    g.setColor(new Color(0.0f, Math.abs(c), b));
	                }
	                else
	                {
	                	 g.setColor(new Color(Math.abs(c), 0.0f, b));
	                }
	       
	                
	           
	                g.fillRect(i, j, 1, 1);
	            }
	        }

	        
	        JFrame frame = new JFrame(label);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        JPanel panel = new JPanel() 
	        {
	            @Override
	            protected void paintComponent(Graphics g) 
	            {
	                Graphics2D g2d = (Graphics2D)g;
	                g2d.clearRect(0, 0, getWidth(), getHeight());
	                g2d.setRenderingHint(
	                        RenderingHints.KEY_INTERPOLATION,
	                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	                        // Or _BICUBIC
	                g2d.scale(2, 2);
	                g2d.drawImage(img, 0, 0, this);
	            }
	        };
	        panel.setPreferredSize(new Dimension(1024, 1024));
	        frame.getContentPane().add(panel);
	        frame.pack();
	        frame.setVisible(true);
	}
	
	
	 void finishInitialization
	 (
			 TRN4JAVA.Simulation.Loop position, 
			 TRN4JAVA.Simulation.Loop stimulus
	 )
	 {
		this.position = position;
		this.stimulus = stimulus;
	
		TRN4JAVA.Simulation.configure_begin(id);
		TRN4JAVA.Simulation.Loop.SpatialFilter.configure(id, batch_size, stimulus_size, seed, position, stimulus, rows, cols, x_min, x_max, y_min, y_max, response, sigma, radius, scale, POS_TAG);	
		TRN4JAVA.Simulation.Scheduler.Snippets.configure(id, seed, snippets_size, time_budget, "");
		//TRN4JAVA.Simulation.Scheduler.Tiled.configure(id, 100);
		TRN4JAVA.Simulation.Reservoir.WidrowHoff.configure(id, stimulus_size, stimulus_size, reservoir_size, leak_rate, initial_state_scale, learning_rate, seed, batch_size);
		TRN4JAVA.Simulation.Reservoir.Weights.Feedforward.Uniform.configure(id, -1.0f, 1.0f, 0.0f);
		TRN4JAVA.Simulation.Reservoir.Weights.Feedback.Uniform.configure(id, -1.0f, 1.0f, 0.0f);
		TRN4JAVA.Simulation.Reservoir.Weights.Recurrent.Uniform.configure(id, -1.0f/(float)Math.sqrt(reservoir_size), 1.0f/(float)Math.sqrt(reservoir_size), 0.0f);
		//TRN4JAVA.Simulation.Reservoir.Weights.Recurrent.Gaussian.configure(id, 0, 0.5f/(float)Math.sqrt(reservoir_size));
			
		TRN4JAVA.Simulation.Reservoir.Weights.Readout.Uniform.configure(id, -1e-3f, 1e-3f, 0.0f);
		
		/*TRN4JAVA.Simulation.Recording.States.configure(id, new TRN4JAVA.Simulation.Recording.States() 
		{

			@Override
			public void callback(long id, String phase, String label, long batch, long trial, long evaluation,
					float[] samples, long rows, long cols) 
			{
				debug_2D_buffer(label + "@" + phase, samples, (int)rows, (int)cols);
				
			
			}
		}, true, true, true);*/
		TRN4JAVA.Simulation.configure_end(id);
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
		
		for(int i=0;i<observations;i++)
		{
			for(int j=0;j<num_pc; j++)
			{
				incoming[i*num_pc + j] = pcHistory.get(i)[j];
				expected[i*num_pc + j] = pcHistory.get(i+1)[j];
			}
			position[i * 2] = posHistory.get(i)[0];
			position[i * 2 + 1] = posHistory.get(i)[1];
			
			reward[i] = ateHistory.get(i) ? 1f : 0f;
		}
			
		
		TRN4JAVA.Simulation.declare_sequence(id, TARGET_SEQUENCE, INC_TAG, incoming, observations);
		TRN4JAVA.Simulation.declare_sequence(id, TARGET_SEQUENCE, EXP_TAG, expected, observations);
		TRN4JAVA.Simulation.declare_sequence(id, TARGET_SEQUENCE, REW_TAG, reward, observations);
		TRN4JAVA.Simulation.declare_sequence(id, TARGET_SEQUENCE, POS_TAG, position, observations);

		TRN4JAVA.Simulation.declare_set(id,  TRAINING_SET,  INC_TAG, TRAINING_SEQUENCES);
		TRN4JAVA.Simulation.declare_set(id,  TRAINING_SET,  EXP_TAG, TRAINING_SEQUENCES);
		TRN4JAVA.Simulation.declare_set(id,  TRAINING_SET,  REW_TAG, TRAINING_SEQUENCES);
		TRN4JAVA.Simulation.declare_set(id,  TRAINING_SET,  POS_TAG, TRAINING_SEQUENCES);
		
		priming_sequence = new float[preamble * 2];
		System.arraycopy(position, 0, priming_sequence, 0, preamble * 2);
		
		

		
		try
		{
			trained_lock.lock();
			trained = false;
			TRN4JAVA.Simulation.train(id, TRAINING_SET, INC_TAG, EXP_TAG);
			while (trained == false)
				trained_condition.await();
			System.out.println("trained");
		}
		catch (InterruptedException ie)
		{
			ie.printStackTrace();
		}
		finally
		{
			trained_lock.unlock();
		}		
	}
	
	 public Point3f get_next_position()
	 {
		 return pending_points.poll();
	 }
	 
	 public void append_next_position(final long id, final long trial, final long evaluation, Point3f position)
	 {	 
		// assert(pending_points.isEmpty());
		 assert(this.id == id);
		 this.trial = trial;
		 this.evaluation = evaluation;
		 pending_points.add(position);
		 synchronized(this)
		 {
			 injected = false;
		 }
	 }
	 
	 public void set_current_position(Point3f position, float place_cell_activation_pattern[])
	 {
		 synchronized(this)
		 {
			 if (evaluating && pending_points.isEmpty() && !injected)
			 {
				 System.out.println("injecting robot position in TRN Loop : " + position.x + ", " + position.y);
				 this.position.notify(id, trial, evaluation, new float[]{position.x, position.y}, 1, 2);
				 this.stimulus.notify(id, trial, evaluation, place_cell_activation_pattern, 1, place_cell_activation_pattern.length);
				 trial = -1;
				 evaluation = -1;
				 injected = true;
			 }
		}
	 }
	
	public  void onTrained(final long id)
	{
		synchronized(this)
		{
			assert(this.id == id);
			assert(pending_points.isEmpty());
			
			try 
			{
			   trained_lock.lock();
			   trained = true;
		       trained_condition.signal();
		    }
			finally
			{
				trained_lock.unlock();
		    }
		}
	}
	public  void onPrimed(final long id)
	{
		synchronized(this)
		{
			assert(this.id == id);
			evaluating = true;
		}
	}
	public  void onTested(final long id)
	{
		synchronized(this)
		{
			assert(this.id == id);
			evaluating = false;
		}
	}
	
	public  void newEpisode()
	{
		if (priming_sequence != null)
		{
			for (int t = 0; t < preamble; t++)
				pending_points.add(new Point3f(priming_sequence[t * 2], priming_sequence[t * 2 + 1], 0.0f));
			priming_sequence = null;
			
			TRN4JAVA.Simulation.test(id, TARGET_SEQUENCE, INC_TAG, EXP_TAG, preamble, true, 0); // at beginning of new episode - this provides predictions
		}

	}
	
	void setup()
	{
		
	}
	
	 void  deallocate()
	 {	
		TRN4JAVA.Simulation.deallocate(id);	
	}
	


	
}
