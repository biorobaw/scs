package com.github.biorobaw.scs.simulation.scs_simulator;

import java.util.HashMap;
import java.util.LinkedList;

import com.github.biorobaw.scs.robot.commands.RotateT;
import com.github.biorobaw.scs.robot.commands.SetSpeedVW;
import com.github.biorobaw.scs.robot.commands.SetSpeedXYW;
import com.github.biorobaw.scs.robot.commands.StepD;
import com.github.biorobaw.scs.robot.commands.TranslateXY;
import com.github.biorobaw.scs.simulation.object.SimulatedObject;

/**
 * Class that implements the SCSRobot on the SCSSimulator.
 * This class should never be used directly instead use the SCSRobotProxy
 * OBS: Although SCSRobot and SCSRobotProxy could have been implemented as the same class,
 * we kept them separate to reflect the difference between the proxy and the robot.
 * The robot should be implemented in each simulator (external to SCS, e.g: in webots).
 * The proxy is implemented in SCS to communicate with the simulator.
 * @author bucef
 * 		set_speed_vw  	   [v,w]		  // differential drive
 * 		set_speed_xyw      [v_x, v_y, w]  // holonomic drive
 * 		translate		   [d_x, d_y]     // translate by vector d_x, d_y
 * 		rotate			   [d_tita]       // rotate by angle d_tita
 * 		step			   [d]			  // move forward by distance d  
 *
 */
public class SCSRobot extends SimulatedObject {
	
	// list of pending commands
	private LinkedList<Object> cmds = new LinkedList<>();
	
	// pointer to last command that needs execution over multiple time steps
	private Object multi_step_command = null;
	
	// position and orientation of the robot in the SCS simulator
	protected float x, y, tita;

	// hash that maps commands (classes) to their executing function
	static HashMap<Class<?>, ExecuteCommand> command_functions = new HashMap<>();
	{
		// initialize the hashmap statically
		command_functions.put(SetSpeedXYW.class, (r,o,t)-> r.simulateSpeedXYW(o,t));
		command_functions.put(TranslateXY.class, (r,o,t)-> r.simulateTranslateXY(o,t));
		command_functions.put(SetSpeedVW.class,  (r,o,t)-> r.simulateSpeedVW(o,t));
		command_functions.put(RotateT.class, 	 (r,o,t)-> r.simulateRotateT(o,t));
		command_functions.put(StepD.class, 		 (r,o,t)-> r.simulateStepD(o,t));
	}
	
	/**
	 * Constructs the SCS Robot specifying it's initial position
	 * @param x		robot coordinate x
	 * @param y		robot coordinate y
	 * @param tita 	robot orientation
	 */
	protected SCSRobot(float x, float y, float tita) {
		//set initial position
		this.x = x;
		this.y = y;
		this.tita = tita;
	}
	
	/**
	 * Function called when a command is received
	 * @param cmd
	 */
	public void receive_command(Object cmd) {
		cmds.add(cmd);
	}
	
	/**
	 * Defines how the simulation behaved based on the actions of the robot
	 * @param time_ms amount of time to be simulated in milliseconds
	 */
	public void simulate(long time_ms) {
		// if there are commands, execute all of them
//		System.out.println("simulating: "+ cmds.size());
//		System.out.println("multi: " + multi_step_command);
		if (cmds.size()>0) {
			multi_step_command=null;
			for(var cmd : cmds) {
				float dt = (float)time_ms/1000;
				command_functions
					.get(cmd.getClass())
					.execute(this, cmd, dt);
			}
			cmds.clear();
		}
		// if there are no commands and the last action requires multiple steps
		else if (multi_step_command!=null){
			command_functions
				.get(multi_step_command.getClass())
				.execute(this, multi_step_command, (float)time_ms/1000);
		}
	}
	
	
	// ========= METHODS FOR SIMULATING MOTIONS ===========
	
	// function simulates hollonomic drive command
	void simulateSpeedXYW(Object o, float time_s) {
//		System.out.println("xyw");
		SetSpeedXYW cmd = (SetSpeedXYW)o;
		x = x + cmd.vx*time_s; 
		y = y + cmd.vy*time_s;
		tita = tita + cmd.w*time_s;
		multi_step_command = cmd;
	}
	
	// function simulates a translation by (x,y)
	void simulateTranslateXY( Object o, float time_s) {
//		System.out.println("xy");
		TranslateXY cmd = (TranslateXY)o;
		x = x + cmd.dx;
		y = y + cmd.dy;
	}
	
	// function simulates a differential drive command
	void simulateSpeedVW( Object o, float time_s) {
//		System.out.println("vw");
		SetSpeedVW cmd = (SetSpeedVW)o;
		// MUST IMPLEMENT
		float ct = (float)Math.cos(tita);
		float st = (float)Math.sin(tita);
		if(cmd.w==0) {
//			System.out.println("w: "+cmd.w + " " + cmd.v +" " + tita);
//			System.out.println(ct + " " + st);
			x = x + cmd.v*ct*time_s;
			y = y + cmd.v*st*time_s;
		} else {
			float dt = cmd.w*time_s;
			tita = (float)((tita + dt) % (2*Math.PI));
			float ct2 = (float)Math.cos(tita);
			float st2 = (float)Math.sin(tita);
			float r = cmd.v/cmd.w;
			x = x + r*(st2-st);
			y = y + r*(ct-ct2);
		}
		multi_step_command=cmd;
	}
	
	// function simulates a rotation by tita radians
	void simulateRotateT( Object o, float time_s) {
//		System.out.println("T");
		RotateT cmd = (RotateT)o;
		tita = tita + cmd.tita;
	}
	
	// function simulates a step forward of distance d
	void simulateStepD( Object o, float time_s) {
//		System.out.println("D");
		StepD cmd = (StepD)o;
		x = x + cmd.d*(float)Math.cos(tita);
		y = y + cmd.d*(float)Math.sin(tita);
	}
	
	// auxiliary interface required to create the hash of functions
	interface ExecuteCommand {
		void execute(SCSRobot r, Object o, float time_s);
	}
	

}
