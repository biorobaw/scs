package edu.usf.ratsim.model.pablo.multiscale_memory.controllers;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class JoystickModule {

	Controller xBoxJoystick;

	public float yAxis;
	public float xAxis;

	public float yRotation;
	public float xRotation;
	public float zAxis;

	public boolean A;
	public boolean B;
	public boolean X;
	public boolean Y;

	public boolean select;
	public boolean start;

	public boolean l1;
	public boolean r1;

	public boolean lpov;
	public boolean rpov;

	public JoystickModule() {

		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for (Controller c : ca) 
			if (c.getName().equals("Controller (XBOX 360 For Windows)"))
				xBoxJoystick = c;

		if (xBoxJoystick == null) {
			System.err.println("ERROR - Controller (XBOX 360 For Windows) not found");
			System.exit(-1);
		}

	}

	public void poll() {
		xBoxJoystick.poll();
		Component[] c = xBoxJoystick.getComponents();

		yAxis = -c[0].getPollData();
		xAxis = c[1].getPollData();

		yRotation = -c[2].getPollData();
		xRotation = c[3].getPollData();
		zAxis = c[4].getPollData();

		A = c[5].getPollData() == 1f;
		B = c[6].getPollData() == 1f;
		X = c[7].getPollData() == 1f;
		Y = c[8].getPollData() == 1f;

		select = c[9].getPollData() == 1f;
		start = c[10].getPollData() == 1f;

		l1 = c[11].getPollData() == 1f;
		r1 = c[12].getPollData() == 1f;

		lpov = c[13].getPollData() == 1f;
		rpov = c[14].getPollData() == 1f;

	}

}
