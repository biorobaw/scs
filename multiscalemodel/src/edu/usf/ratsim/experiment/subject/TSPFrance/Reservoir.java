package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.*;
import javax.vecmath.Point3f;

import java.util.ArrayList;
import java.util.Arrays;
import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.specificActions.TeleportToAction;
import edu.usf.experiment.utils.CSVReader;

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
	
	private TRN4JAVA.Custom.Simulation.Encoder agent = null;	 
		
	private int snippets_size, time_budget;
	
	private long batch_size, stimulus_size, reservoir_size;
	private float leak_rate, initial_state_scale, learning_rate;
	private long sid;
	private int preamble;
	private int supplementary_steps;
	
	private float cx[];
	private float cy[];
	private float width[];
	private long rows, cols;
	private float x_min, x_max, y_min, y_max;
	private float sigma, radius, scale, angle;
	private int mini_batch_size;
	private float learn_reverse_rate;
	private float generate_reverse_rate;
	private float reverse_learning_rate;
	private float discount;

	
	private long replay_seed;
	private long consolidation_seed;
	private long decoder_seed; 
	
	private short trial, train, test, repeat;
	private boolean callbacks_installed;
	private float feedforward_scale, recurrent_scale;
	private float priming_sequence[];
	private  java.util.Queue<Point3f> pending_points = new ConcurrentLinkedQueue<Point3f>();
	
	private boolean injected = false;
	public Reservoir(boolean callbacks_installed, long id, 
			long replay_seed, long consolidation_seed, long decoder_seed, 
			long stimulus_size, long reservoir_size, float leak_rate, float initial_state_scale, float learning_rate, int mini_batch_size,
			float feedforward_scale, float recurrent_scale,
			int snippets_size, int time_budget, float learn_reverse_rate, float generate_reverse_rate, float reverse_learning_rate, float discount,
			long rows, long cols, float xmin, float xmax, float ymin, float ymax, float cx[], float cy[], float width[], float sigma, float radius, float scale, float angle,
			int preamble, int supplementary_steps)
	{
		
		this.callbacks_installed = callbacks_installed;
		this.replay_seed = replay_seed;
		this.consolidation_seed = consolidation_seed;
		this.decoder_seed = decoder_seed;
		this.accumulated_sequences = new java.util.ArrayList<String>();
		this.sid =id;
		this.batch_size = 1;
		this.learn_reverse_rate = learn_reverse_rate;
		this.generate_reverse_rate = generate_reverse_rate; 
		this.reverse_learning_rate = reverse_learning_rate;
		this.discount = discount;
		this.mini_batch_size = mini_batch_size;
		this.preamble=preamble;
		this.supplementary_steps = supplementary_steps;
		
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
		this.cx = cx;
		this.cy = cy;
		this.width = width;
		this.sigma = sigma;
		this.radius = radius;
		this.scale = scale;
		this.angle = angle;
		
		this.recurrent_scale = recurrent_scale;
		this.feedforward_scale = feedforward_scale;
		
		this.trial = 1;
		this.train = 0;
		this.test = 0;
		this.repeat = 0;

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
			 TRN4JAVA.Custom.Simulation.Encoder agent
	 )
	 {

			TRN4JAVA.Custom.Simulation.Encoder.install(agent);
		 this.agent = agent;
		TRN4JAVA.Extended.Simulation.configure_begin(sid);
		//TRN4JAVA.Extended.Simulation.Scheduler.Tiled.configure(id, 100);
		
		TRN4JAVA.Extended.Simulation.Scheduler.Snippets.configure(sid, replay_seed, snippets_size, time_budget, learn_reverse_rate, generate_reverse_rate, reverse_learning_rate, discount, REW_TAG);
		//TRN4JAVA.Extended.Simulation.Scheduler.Tiled.configure(sid, 100);
		TRN4JAVA.Extended.Simulation.Reservoir.WidrowHoff.configure(sid, stimulus_size, stimulus_size, reservoir_size, leak_rate, initial_state_scale, learning_rate, consolidation_seed, batch_size, mini_batch_size);
		
		float feedforward_a = -1.0f * feedforward_scale;
		float feedforward_b = 1.0f * feedforward_scale;
		float s = 1.0f / (float)Math.sqrt((float)reservoir_size);
		float recurrent_a = -1.0f * recurrent_scale * s ;
		float recurrent_b = 1.0f * recurrent_scale * s;
		
		TRN4JAVA.Extended.Simulation.Reservoir.Weights.Feedforward.Uniform.configure(sid, feedforward_a, feedforward_b, 0.0f);
		TRN4JAVA.Extended.Simulation.Reservoir.Weights.Recurrent.Uniform.configure(sid, recurrent_a, recurrent_b, 0.0f);
		TRN4JAVA.Extended.Simulation.Reservoir.Weights.Readout.Uniform.configure(sid, -1e-3f, 1e-3f, 0.0f);
		
		TRN4JAVA.Extended.Simulation.Encoder.Custom.configure(sid, batch_size, stimulus_size);
		TRN4JAVA.Extended.Simulation.Decoder.Kernel.Model.configure(sid, batch_size, stimulus_size, 
				rows, cols, x_min, x_max, y_min, y_max, sigma, radius, angle, scale,   decoder_seed , cx, cy, width);
		TRN4JAVA.Extended.Simulation.Loop.SpatialFilter.configure(sid, batch_size, stimulus_size, POS_TAG);
			
		/*TRN4JAVA.Advanced.Simulation.Recording.States.configure(sid, new TRN4JAVA.Callbacks.Simulation.Recording.States(
				) {
			
			@Override
			public void callback(long simulation_id, long evaluation_id, String phase, String label, long batch,
					float[] samples, long rows, long cols) {
				// TODO Auto-generated method stub
				debug_2D_buffer(label + "@" + phase, samples, (int)rows, (int)cols);
			}
		}, true, true, true);*/

		if (callbacks_installed)
		{
			TRN4JAVA.Extended.Simulation.Recording.Performances.configure(sid, true, true, true);
			/*TRN4JAVA.Extended.Simulation.Recording.States.configure(sid, true, true, true);
			TRN4JAVA.Extended.Simulation.Recording.Weights.configure(sid, true, true);*/
			TRN4JAVA.Extended.Simulation.Recording.Scheduling.configure(sid);
			TRN4JAVA.Extended.Simulation.Measurement.Position.Raw.configure(sid, batch_size);
			/*TRN4JAVA.Extended.Simulation.Measurement.Readout.Raw.configure(sid, batch_size);*/
		}
		TRN4JAVA.Extended.Simulation.configure_end(sid);
	}
	
	void train()
	{
		train++;
		long eid = TRN4JAVA.Basic.Simulation.Evaluation.encode(new TRN4JAVA.Basic.Simulation.Evaluation.Identifier((short)trial, (short)train, (short)0, (short)0));
		//String[][] name = new String [size1][size]();
		String sequences[] = accumulated_sequences.toArray(new String[accumulated_sequences.size()]);
		TRN4JAVA.Extended.Simulation.declare_set(sid,  TRAINING_SET,  INC_TAG, sequences);
		TRN4JAVA.Extended.Simulation.declare_set(sid,  TRAINING_SET,  EXP_TAG, sequences);
		TRN4JAVA.Extended.Simulation.declare_set(sid,  TRAINING_SET,  REW_TAG, sequences);
		TRN4JAVA.Extended.Simulation.declare_set(sid,  TRAINING_SET,  POS_TAG, sequences);
		
		/*
		TRN4JAVA.Extended.Simulation.declare_sequence(sid, "target", INC_TAG, incoming, observations);
		TRN4JAVA.Extended.Simulation.declare_sequence(sid, "target", EXP_TAG, expected, observations);
		TRN4JAVA.Extended.Simulation.declare_sequence(sid, "target", REW_TAG, reward, observations);
		TRN4JAVA.Extended.Simulation.declare_sequence(sid, "target", POS_TAG, position, observations);*/
		
		//accumulated_sequences.clear();
		changeState(State.TRAINING);
		TRN4JAVA.Extended.Simulation.train(sid, eid, TRAINING_SET, INC_TAG, EXP_TAG);
		//if (getState() == State.TRAINING)
			waitForState(State.TRAINED);
			accumulated_sequences.clear();
			changeState(State.READY);
		
	 }
	private static void dump_txt(String filename, float data[], int rows, int cols)
	{
		  try {
			PrintWriter pw = new PrintWriter(new File(filename));
			
			for (int row = 0; row < rows; row++)
			{
				StringBuilder sb = new StringBuilder();
				int col = 0;
				for (; col < cols-1; col++)
				{
					sb.append(data[row * cols + col]);
				    sb.append(',');
				}
				sb.append(data[row * cols + col]);
			 
	  

			    pw.println(sb.toString());
			}
	        pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 void gather(final String label, LinkedList<float[]> pcHistory ,LinkedList<float[]> posHistory ,LinkedList<Boolean> ateHistory)
	 {
	
	
			//incoming = whole pc sequence (time 1 to n-1)
			//expected = pc sequence (2 to n)
			//reward  = sequence of received rewards
			//observations = num rows 
			 
			int observations = pcHistory.size();
			if (observations == 0)
				return;
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
					expected[i*num_pc + j] = pcHistory.get(i)[j];
				}
				position[i * 2] = posHistory.get(i)[0];
				position[i * 2 + 1] = posHistory.get(i)[1];
				
				reward[i] = ateHistory.get(i) ? 1f : 0f;
			}
				
			
			/*dump_txt("Y:/SCS-TRN/ABCDE/results/" + label + ".incoming.csv", incoming, observations, num_pc);
			dump_txt("Y:/SCS-TRN/ABCDE/results/" + label + ".expected.csv", expected, observations, num_pc);
			dump_txt("Y:/SCS-TRN/ABCDE/results/" + label + ".reward.csv", reward, observations, 1);
			dump_txt("Y:/SCS-TRN/ABCDE/results/" + label + ".position.csv", position, observations, 2);*/
			
			
			TRN4JAVA.Extended.Simulation.declare_sequence(sid, label, INC_TAG, incoming, observations);
			TRN4JAVA.Extended.Simulation.declare_sequence(sid, label, EXP_TAG, expected, observations);
			TRN4JAVA.Extended.Simulation.declare_sequence(sid, label, REW_TAG, reward, observations);
			TRN4JAVA.Extended.Simulation.declare_sequence(sid, label, POS_TAG, position, observations);
			
	
		
				
			/*if (getState() == State.TRAINED)
			{*/
				
				priming_sequence = new float[preamble * 2];
				System.arraycopy(position, 0, priming_sequence, 0, preamble * 2);
				//changeState(State.READY);
			/*}*/
			
			accumulated_sequences.add(label);
		
	}
	
	 public Point3f get_next_position()
	 {
		 //system.out.println(Globals.getInstance().get("cycle") + " " + pending_points.size());
		return pending_points.poll();
	 }
	 
	 public void append_next_position(final long simulation_id, final long evaluation_id, Point3f position)
	 {	 
		 assert(pending_points.isEmpty());
		 pending_points.add(position);
		 
		 synchronized(this)
		 {
			 injected = false;
		 }
	 }
	 
	 public void set_current_position(Point3f position, float place_cell_activation_pattern[])
	 {
		 //system.out.println(Globals.getInstance().get("cycle").toString() + " " + getState() + " " + pending_points.isEmpty() + " " + (!injected));
		 
		if (getState() == State.EVALUATING && pending_points.isEmpty() && !injected)
		{
			long eid = TRN4JAVA.Basic.Simulation.Evaluation.encode(new TRN4JAVA.Basic.Simulation.Evaluation.Identifier((short)trial, (short)train, (short)test, (short)repeat));
			//system.out.println("injecting robot position in TRN Loop : " + position.x + ", " + position.y);
			 this.agent.notify(sid, eid, new float[]{position.x, position.y}, place_cell_activation_pattern, 1, place_cell_activation_pattern.length);
			 injected = true;
		}
	 }
	
	public  void onTrained(final long simulation_id, final long evaluation_id)
	{
		changeState(State.TRAINED);

	}
	public  void onPrimed(final long simulation_id, final long evaluation_id)
	{
		changeState(State.EVALUATING);
	}
	public  void onTested(final long simulation_id, final long evaluation_id)
	{
		changeState(State.EVALUATED);

		TRN4JAVA.Extended.Simulation.deallocate(simulation_id);
		Globals.getInstance().put("done",true);

	}
	
	public  void newEpisode()
	{
		if (getState() == State.READY)
		{
			changeState(State.PRIMING);
			try
			{
				test++;
				repeat++;
				pending_points.clear();
				for (int t = 0; t < preamble; t++) {
					//system.out.println("adding " +t+": " + (priming_sequence[t * 2] + " " + priming_sequence[t * 2 + 1]));
					pending_points.add(new Point3f(priming_sequence[t * 2], priming_sequence[t * 2 + 1], 0.0f));
				}
				priming_sequence = null;
				
				//assert(accumulated_sequences.size() == 1);
				String target_sequence = "target_Sequence";
				long eid = TRN4JAVA.Basic.Simulation.Evaluation.encode(new TRN4JAVA.Basic.Simulation.Evaluation.Identifier((short)trial, (short)train, (short)test, (short)repeat));
				TRN4JAVA.Extended.Simulation.test(sid, eid, target_sequence, INC_TAG, EXP_TAG, preamble, true, supplementary_steps); // at beginning of new episode - this provides predictions
		
				waitForState(State.EVALUATING);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		
		}
	}
	
	public void endEpisode()
	{
		trial++;
	}
	
	void setup()
	{
		
	}
	
	 void  deallocate()
	 {	
		//TRN4JAVA.Simulation.deallocate(id);	
	}
	
	 
	 
	 


	
}
