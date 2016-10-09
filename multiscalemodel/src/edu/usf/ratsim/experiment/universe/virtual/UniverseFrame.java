package edu.usf.ratsim.experiment.universe.virtual;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.media.j3d.Canvas3D;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.SimpleUniverse;

import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawPolarGraph;
import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawingFunction;

public class UniverseFrame extends java.awt.Frame {

	private static final long serialVersionUID = -698020368303861261L;

	private Canvas3D topViewCanvas, robotViewCanvas;
	private Canvas3D[] robotViewsCanvas;

	private java.awt.Button button1;
	private java.awt.Button leftBtn;
	private java.awt.Button button3;
	private java.awt.Button rightBtn;
	private java.awt.Button forwardBtn;
	private java.awt.Button backBtn;
	private java.awt.Button button9;
	private java.awt.Button turnRightBtn;
	private java.awt.Button turnLeftBtn;

	private java.awt.Panel panel1;
	private java.awt.Panel topViewPanel;
	private java.awt.Panel wideViewPanel;

	private java.awt.Label posRat;

	private VirtUniverse expUniv;
	
	LinkedList<Runnable> drawingFunctions = new LinkedList<Runnable>();
	Globals g = Globals.getInstance();

	public UniverseFrame(VirtUniverse world) {
		this.expUniv = world;

		initComponents();

		// Create the canvases
		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();

		// Wide view canvases
		robotViewsCanvas = new Canvas3D[RobotNode.NUM_ROBOT_VIEWS];
		for (int i = 0; i < RobotNode.NUM_ROBOT_VIEWS; i++) {
			robotViewsCanvas[i] = new Canvas3D(config);
			robotViewsCanvas[i].setSize(80, 80);
			world.getRobotViews()[i].addCanvas3D(robotViewsCanvas[i]);
			wideViewPanel.add(robotViewsCanvas[i]);
		}

		// Main robot view canvas
		// robotViewCanvas = new Canvas3D(config);
		// robotViewCanvas.setSize(240, 240);
		// world.getRobotViews()[RobotNode.NUM_ROBOT_VIEWS / 2]
		// .addCanvas3D(robotViewCanvas);
		// robotViewPanel.add(robotViewCanvas);
		// Top view canvas
		topViewCanvas = new Canvas3D(config){

			private static final long serialVersionUID = 2278728176596780651L;
			
			
			public void postRender()
	        {
				
				for (Runnable r : drawingFunctions)
					r.run();
				
	        }
			
			
		};
		world.getTopView().addCanvas3D(topViewCanvas);
		topViewCanvas.setSize(500, 500);
		topViewPanel.add(topViewCanvas);

	}
	
	public void addDrawingFunction(DrawingFunction function){
		function.setGraphics(topViewCanvas.getGraphics2D());
		drawingFunctions.push(function);
	}

	private void initComponents() {
		setFocusableWindowState(false);

		java.awt.GridBagConstraints gridBagConstraints;

		panel1 = new java.awt.Panel();
		button1 = new java.awt.Button();
		button3 = new java.awt.Button();
		leftBtn = new java.awt.Button();
		rightBtn = new java.awt.Button();
		forwardBtn = new java.awt.Button();
		backBtn = new java.awt.Button();
		button9 = new java.awt.Button();
		turnRightBtn = new java.awt.Button();

		posRat = new java.awt.Label();

		turnLeftBtn = new java.awt.Button();
		topViewPanel = new java.awt.Panel();
		wideViewPanel = new java.awt.Panel();

		setLayout(new java.awt.GridBagLayout());

		setResizable(false);
		setTitle("NSLWorld");
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitForm(evt);
			}
		});

		panel1.setLayout(new java.awt.GridBagLayout());

		button1.setLabel("Publ. Space");
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Globals g = Globals.getInstance();
				g.put("pause",!(boolean)g.get("pause"));
				// TODO restore pause
				// Trial.cont = ! Trial.cont;
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 3;
		panel1.add(button1, gridBagConstraints);

		button3.setLabel("-");
		button3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button3ActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 1;
		panel1.add(button3, gridBagConstraints);

		leftBtn.setLabel("<");
		leftBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				leftBtnAction(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		panel1.add(leftBtn, gridBagConstraints);

		rightBtn.setLabel(">");
		rightBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				rightBtnAction(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 2;
		panel1.add(rightBtn, gridBagConstraints);

		forwardBtn.setLabel("/\\");
		forwardBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				forwardBtnAction(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 1;
		panel1.add(forwardBtn, gridBagConstraints);

		backBtn.setLabel("\\/");
		backBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				backBtnAction(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 2;
		panel1.add(backBtn, gridBagConstraints);

		button9.setLabel("+");
		button9.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button9ActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		panel1.add(button9, gridBagConstraints);

		// boton rotar horario
		turnLeftBtn.setLabel("<(");
		turnLeftBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				turnLeftBtnAction(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 3;
		panel1.add(turnLeftBtn, gridBagConstraints);

		// boton rotar anti-horario
		turnRightBtn.setLabel(")>");
		turnRightBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				turnRightBtnAction(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 3;
		panel1.add(turnRightBtn, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 4;
		panel1.add(posRat, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		add(panel1, gridBagConstraints);

		// panel1 es el de los botones
		// panel2 -> world1, panel3 -> world2, panel4 -> world3

		topViewPanel.setBackground(new java.awt.Color(153, 244, 51));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		add(topViewPanel, gridBagConstraints);

		wideViewPanel.setBackground(new java.awt.Color(204, 153, 0));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 3;
		add(wideViewPanel, gridBagConstraints);

		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize();
		int size = 800;
		setBounds((screenSize.width - size) / 2,
				(screenSize.height - size) / 2, size, size);

	}

	// accion asociada al boton de girar horario
	private void turnRightBtnAction(java.awt.event.ActionEvent evt) {
		expUniv.rotateRobot(Math.toRadians(-45));
	}

	// accion asociada al boton de girar anti-horario
	private void turnLeftBtnAction(java.awt.event.ActionEvent evt) {
		expUniv.rotateRobot(Math.toRadians(45));
	}

	// accion asociada al boton de mover izquierda
	private void leftBtnAction(java.awt.event.ActionEvent evt) {
		int speed = (int)g.get("simulationSpeed");
		speed = --speed > 0 ? speed : 0; 
		g.put("simulationSpeed",speed);
		//expUniv.moveRobot(new Vector3f(-.1f, 0f, 0f));
	}

	// accion asociada al boton de mover derecha
	private void rightBtnAction(java.awt.event.ActionEvent evt) {
		//expUniv.moveRobot(new Vector3f(0.1f, 0f, 0f));
		int speed = (int)g.get("simulationSpeed");
		speed = ++speed > 9 ? 9 : speed; 
		g.put("simulationSpeed",speed);
	}

	// accion asociada al boton de retroceder
	private void backBtnAction(java.awt.event.ActionEvent evt) {
		expUniv.moveRobot(new Vector3f(-.1f, 0f, 0f));
	}

	private void button9ActionPerformed(java.awt.event.ActionEvent evt) {
	}

	// accion asociada al boton de avanzar
	private void forwardBtnAction(java.awt.event.ActionEvent evt) {
		expUniv.moveRobot(new Vector3f(.1f, 0f, 0f));
	}

	private void button3ActionPerformed(java.awt.event.ActionEvent evt) {
		// w2Canvas.moveCamera(new Vector3f(0f, 1f, 0f));
	}

	private void exitForm(java.awt.event.WindowEvent evt) {
		System.exit(0);
	}

	// public static void main(String args[]) {
	// VirtualExpUniverse expUniv = new VirtualExpUniverse();
	// UniverseFrame worldFrame = new UniverseFrame(expUniv);
	//
	// worldFrame.setVisible(true);
	// }

}
