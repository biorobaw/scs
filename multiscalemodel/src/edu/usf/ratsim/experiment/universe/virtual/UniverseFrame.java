package edu.usf.ratsim.experiment.universe.virtual;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.media.j3d.Canvas3D;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.SimpleUniverse;

import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawPolarGraph;
import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawingFunction;

public class UniverseFrame extends JFrame {

	private static final long serialVersionUID = -698020368303861261L;

	private Canvas3D topViewCanvas, robotViewCanvas;
	private Canvas3D[] robotViewsCanvas;

	private JButton button1;
	private JButton leftBtn;
	private JButton button3;
	private JButton rightBtn;
	private JButton forwardBtn;
	private JButton backBtn;
	private JButton button9;
	private JButton turnRightBtn;
	private JButton turnLeftBtn;

	private JPanel panel1;
	private JPanel topViewPanel;

	private JLabel posRat;

	private VirtUniverse expUniv;
	
	LinkedList<Runnable> drawingFunctions = new LinkedList<Runnable>();
	Globals g = Globals.getInstance();

	public UniverseFrame(VirtUniverse world) {
		this.expUniv = world;

		initComponents();

		// Create the canvases
		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();
		
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
		topViewCanvas.setSize(new Dimension(100,100));
		topViewPanel.add(topViewCanvas);
		
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);

	}
	
	public void addDrawingFunction(DrawingFunction function){
		function.setGraphics(topViewCanvas.getGraphics2D());
		drawingFunctions.push(function);
	}

	private void initComponents() {
		setFocusableWindowState(false);

		java.awt.GridBagConstraints gridBagConstraints;

		panel1 = new JPanel();
		button1 = new JButton();
		button3 = new JButton();
		leftBtn = new JButton();
		rightBtn = new JButton();
		forwardBtn = new JButton();
		backBtn = new JButton();
		button9 = new JButton();
		turnRightBtn = new JButton();

		posRat = new JLabel();

		turnLeftBtn = new JButton();
		topViewPanel = new JPanel(new BorderLayout());

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
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		add(panel1, gridBagConstraints);

		// panel1 es el de los botones
		// panel2 -> world1, panel3 -> world2, panel4 -> world3

		topViewPanel.setBackground(new java.awt.Color(153, 244, 51));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		add(topViewPanel, gridBagConstraints);

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
