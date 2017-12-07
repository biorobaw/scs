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
import edu.usf.experiment.Globals;

public class Reservoir {
	
	static final String INC_TAG = "Incoming";
	static final String EXP_TAG = "Expected";
	static final String REW_TAG = "Reward";
	static final String POS_TAG = "Position";


	java.util.List<String> accumulated_sequences;

	static final String TRAINING_SET = "training_set"; 
	

	enum State
	{
		EMPTY,
		TRAINING, 
		TRAINED,
		READY,
		PRIMING,
		EVALUATING,
		EVALUATED
	}
	private State state = State.EMPTY; 
	private final Lock state_lock = new ReentrantLock();
	private final Condition state_condition = state_lock.newCondition();
	
	private TRN4JAVA.Custom.Simulation.Loop position = null;	 
	private TRN4JAVA.Custom.Simulation.Loop stimulus = null;
	
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
	
	private boolean callbacks_installed;
	private long trial, evaluation;
	private float priming_sequence[];
	private  java.util.Queue<Point3f> pending_points = new ConcurrentLinkedQueue<Point3f>();
	
	private boolean injected = false;
	public Reservoir(boolean callbacks_installed, long id, long stimulus_size, long reservoir_size, float leak_rate, float initial_state_scale, float learning_rate,
			int snippets_size, int time_budget,
			long rows, long cols, float xmin, float xmax, float ymin, float ymax, float response[], float sigma, float radius, float scale,
			int preamble)
	{
		this.callbacks_installed = callbacks_installed;
		this.accumulated_sequences = new java.util.ArrayList<String>();
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
		
		//customEventQueue.enqueue(ALLOCATE_SIMULATION, id);
	}
	
	private State getState()
	{
		State result;

		state_lock.lock();
		result = state;
		state_lock.unlock();
		
		return result;
	}
	
	private void waitForState(State expected)
	{
		try
		{
			state_lock.lock();

			while (state != expected)
				state_condition.await();
		}
		catch (InterruptedException ie)
		{
			ie.printStackTrace();
		}
		finally
		{
			state_lock.unlock();
		}
	}
	
	private void changeState(State target)
	{
		try 
		{
		   state_lock.lock();
		   state = target;
		   state_condition.signal();
	    }
		finally
		{
			state_lock.unlock();
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
			 TRN4JAVA.Custom.Simulation.Loop position, 
			 TRN4JAVA.Custom.Simulation.Loop stimulus
	 )
	 {
		this.position = position;
		this.stimulus = stimulus;
	
		TRN4JAVA.Extended.Simulation.configure_begin(id);
		TRN4JAVA.Advanced.Simulation.Loop.SpatialFilter.configure(id, batch_size, stimulus_size, seed, position, stimulus, rows, cols, x_min, x_max, y_min, y_max, response, sigma, radius, scale, POS_TAG);	
		TRN4JAVA.Extended.Simulation.Scheduler.Snippets.configure(id, seed, snippets_size, time_budget, "");
		//TRN4JAVA.Simulation.Scheduler.Tiled.configure(id, 100);
		TRN4JAVA.Extended.Simulation.Reservoir.WidrowHoff.configure(id, stimulus_size, stimulus_size, reservoir_size, leak_rate, initial_state_scale, learning_rate, seed, batch_size);
		TRN4JAVA.Extended.Simulation.Reservoir.Weights.Feedforward.Uniform.configure(id, -1.0f, 1.0f, 0.0f);
		TRN4JAVA.Extended.Simulation.Reservoir.Weights.Feedback.Uniform.configure(id, -1.0f, 1.0f, 0.0f);
		TRN4JAVA.Extended.Simulation.Reservoir.Weights.Recurrent.Uniform.configure(id, -1.0f/(float)Math.sqrt(reservoir_size), 1.0f/(float)Math.sqrt(reservoir_size), 0.0f);
		//TRN4JAVA.Simulation.Reservoir.Weights.Recurrent.Gaussian.configure(id, 0, 0.5f/(float)Math.sqrt(reservoir_size));
			
		TRN4JAVA.Extended.Simulation.Reservoir.Weights.Readout.Uniform.configure(id, -1e-3f, 1e-3f, 0.0f);
		
		/*TRN4JAVA.Simulation.Recording.States.configure(id, new TRN4JAVA.Simulation.Recording.States() 
		{

			@Override
			public void callback(long id, String phase, String label, long batch, long trial, long evaluation,
					float[] samples, long rows, long cols) 
			{
				debug_2D_buffer(label + "@" + phase, samples, (int)rows, (int)cols);
				
			
			}
		}, true, true, true);*/
		if (callbacks_installed)
		{
			TRN4JAVA.Extended.Simulation.Recording.Performances.configure(id, true, true, true);
			TRN4JAVA.Extended.Simulation.Recording.Scheduling.configure(id);

			TRN4JAVA.Extended.Simulation.Measurement.Position.Raw.configure(id, 1);
		}
		TRN4JAVA.Extended.Simulation.configure_end(id);
	}
	
	void train()
	{
		//String[][] name = new String [size1][size]();
		String sequences[] = accumulated_sequences.toArray(new String[accumulated_sequences.size()]);
		TRN4JAVA.Extended.Simulation.declare_set(id,  TRAINING_SET,  INC_TAG, sequences);
		TRN4JAVA.Extended.Simulation.declare_set(id,  TRAINING_SET,  EXP_TAG, sequences);
		TRN4JAVA.Extended.Simulation.declare_set(id,  TRAINING_SET,  REW_TAG, sequences);
		TRN4JAVA.Extended.Simulation.declare_set(id,  TRAINING_SET,  POS_TAG, sequences);
		accumulated_sequences.clear();
		changeState(State.TRAINING);
		TRN4JAVA.Extended.Simulation.train(id, TRAINING_SET, INC_TAG, EXP_TAG);
	 }
	 void gather(final String label, LinkedList<float[]> pcHistory ,LinkedList<float[]> posHistory ,LinkedList<Boolean> ateHistory)
	 {
	
	
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
			
		
		TRN4JAVA.Extended.Simulation.declare_sequence(id, label, INC_TAG, incoming, observations);
		TRN4JAVA.Extended.Simulation.declare_sequence(id, label, EXP_TAG, expected, observations);
		TRN4JAVA.Extended.Simulation.declare_sequence(id, label, REW_TAG, reward, observations);
		TRN4JAVA.Extended.Simulation.declare_sequence(id, label, POS_TAG, position, observations);
		

		if (getState() == State.TRAINING)
			waitForState(State.TRAINED);
			
		if (getState() == State.TRAINED)
		{
			
			priming_sequence = new float[preamble * 2];
			System.arraycopy(position, 0, priming_sequence, 0, preamble * 2);
			changeState(State.READY);
		}
		
		accumulated_sequences.add(label);
	
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
		if (getState() == State.EVALUATING && pending_points.isEmpty() && !injected)
		{
			System.out.println("injecting robot position in TRN Loop : " + position.x + ", " + position.y);
			 this.position.notify(id, trial, evaluation, new float[]{position.x, position.y}, 1, 2);
			 this.stimulus.notify(id, trial, evaluation, place_cell_activation_pattern, 1, place_cell_activation_pattern.length);
			 trial = -1;
			 evaluation = -1;
			 injected = true;
		}
	 }
	
	public  void onTrained(final long id)
	{
		changeState(State.TRAINED);
	}
	public  void onPrimed(final long id)
	{
		changeState(State.EVALUATING);
	}
	public  void onTested(final long id)
	{
		changeState(State.EVALUATED);

		Globals.getInstance().put("done",true);
	}
	
	public  void newEpisode()
	{
		if (getState() == State.READY)
		{
			changeState(State.PRIMING);
			try
			{
				assert(accumulated_sequences.size() == 1);
				String target_sequence = accumulated_sequences.get(0);
				TRN4JAVA.Extended.Simulation.test(id, target_sequence, INC_TAG, EXP_TAG, preamble, true, 0); // at beginning of new episode - this provides predictions
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			for (int t = 0; t < preamble; t++)
				pending_points.add(new Point3f(priming_sequence[t * 2], priming_sequence[t * 2 + 1], 0.0f));
			priming_sequence = null;
		}
	}
	
	public void endEpisode()
	{
	
	}
	
	void setup()
	{
		
	}
	
	 void  deallocate()
	 {	
		//TRN4JAVA.Simulation.deallocate(id);	
	}
	


	
}
