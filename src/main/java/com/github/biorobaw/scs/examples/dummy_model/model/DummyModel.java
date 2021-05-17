package com.github.biorobaw.scs.examples.dummy_model.model;

import java.awt.Color;
import java.util.LinkedList;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.experiment.Subject;
import com.github.biorobaw.scs.gui.displays.scs_swing.drawer.CycleDataDrawer;
import com.github.biorobaw.scs.gui.displays.scs_swing.drawer.RobotDrawer;
import com.github.biorobaw.scs.gui.displays.scs_swing.drawer.WallDrawer;
import com.github.biorobaw.scs.robot.commands.RotateT;
import com.github.biorobaw.scs.robot.commands.SetSpeedVW;
import com.github.biorobaw.scs.robot.commands.SetSpeedXYW;
import com.github.biorobaw.scs.robot.commands.StepD;
import com.github.biorobaw.scs.robot.commands.TranslateXY;
import com.github.biorobaw.scs.robot.proxies.SCSRobotProxy;
import com.github.biorobaw.scs.utils.files.XML;

/**
 * A dummy model exemplifying how to use SCS.
 * @author bucef
 *
 */
public class DummyModel extends Subject {

	LinkedList<Object> list = new LinkedList<Object>();
	
	public DummyModel(XML xml) {
		super(xml);
		// This function must be implemented.
		list.add(xml.getAttribute("var_name_1"));
		list.add(xml.getIntAttribute("var_name_2"));
		list.add(xml.getChild("child_1").getText());
		setDisplay();
	}
	

	void setDisplay() {
		var d = Experiment.get().display;
		RobotDrawer rDrawer = new RobotDrawer(robot.getRobotProxy());
		System.out.println("r" + subject_id);
		
		// note that 2 drawers get added, one per robot
		
		
		// avoid adding the following drawers multiple times:
		d.addDrawer("universe", "r" + subject_id, rDrawer);
		if(subject_id == "subject_1") {
			
			var wdrawer =new WallDrawer(1);
			wdrawer.setColor(Color.black);
			d.addDrawer("universe", "walls", wdrawer);
			
			var cdrawer = new CycleDataDrawer();
			d.addDrawer("universe", "data", cdrawer);
		}
		
		
	}

	int i =0;
	int j =0;
	int cycles  = 100;
	float seconds = cycles/1000f*30;
	@Override
	public long runModel() {
		var proxy = (SCSRobotProxy)robot.getRobotProxy();
		
		// simulating 30ms per step and using sleeps of 30 ms  between steps
		// thus reset every 500 iterations (15s)
		if(i==0) {
			if(subject_id == "subject_1") {
				proxy.setPosition(new Vector3D(0.5, 0, 0));
				proxy.setOrientation2D(1.57079f);
			}
			else {
//				proxy.setPosition(new Vector3D(1,0.3,0));
				if(j==0) robot.getRobotProxy().send_command(new SetSpeedVW(2f/seconds, 0)); //move straight left
				if(j==4) robot.getRobotProxy().send_command(new SetSpeedXYW(2f/seconds, 0.6f/seconds, 2*(float)Math.PI/seconds));
			}
		}
		long time = Experiment.get().simulator.getTime();
		for(var o : list) System.out.println("Dummy Model "+ subject_id +" (" + time + "): " + o.toString());
		
		if(subject_id == "subject_1") {
			//rotate 1 circle every 15s
			robot.getRobotProxy().send_command(new SetSpeedVW((float)Math.PI/seconds, 2*(float)Math.PI/seconds));
		}
		if(subject_id=="subject_2") {
			switch(j) {
			case 1:
				robot.getRobotProxy().send_command(new StepD(-2f/cycles));
				break;
			case 2:
				robot.getRobotProxy().send_command(new RotateT(2*(float)Math.PI/cycles));
				break;
			case 3:
				robot.getRobotProxy().send_command(new TranslateXY(-2f/cycles, -0.6f/cycles));
				break;
			default:
				break;
			}
		}
		i=(i+1) % cycles;
		if(i==0) j=(j+1)%5;
		return 30; // must return the time to wait in ms until next execution
	}
	
	@Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		super.newEpisode();
		i=0;
		j=0;
	}

}
