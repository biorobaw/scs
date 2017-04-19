package edu.usf.ratsim.experiment.universe.virtual;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Vector3f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.universe.Universe;

public class CmdsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1964541609348059728L;

	private JButton button1;
	private JButton leftBtn;
	private JButton button3;
	private JButton rightBtn;
	private JButton forwardBtn;
	private JButton backBtn;
	private JButton button9;
	private JButton turnRightBtn;
	private JButton turnLeftBtn;
	private JLabel posRat;
	
	private VirtUniverse expUniv;

	public CmdsPanel(VirtUniverse expUniv) {
		this.expUniv = expUniv;
		
		GridBagConstraints gridBagConstraints;
		setLayout(new java.awt.GridBagLayout());

		button1 = new JButton("Pause");
		button1.setLabel("Publ. Space");
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Globals g = Globals.getInstance();
				g.put("pause", !(boolean) g.get("pause"));
				// TODO restore pause
				// Trial.cont = ! Trial.cont;
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 3;
		add(button1, gridBagConstraints);

		button3 = new JButton("-");
		button3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button3ActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 1;
		add(button3, gridBagConstraints);

		leftBtn = new JButton("<");
		leftBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				leftBtnAction(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		add(leftBtn, gridBagConstraints);

		rightBtn = new JButton(">");
		rightBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				rightBtnAction(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 2;
		add(rightBtn, gridBagConstraints);
		
		forwardBtn = new JButton("/\\");
		forwardBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				forwardBtnAction(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 1;
		add(forwardBtn, gridBagConstraints);

		backBtn = new JButton("\\/");
		backBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				backBtnAction(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 2;
		add(backBtn, gridBagConstraints);

		button9 = new JButton("+");
		button9.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button9ActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		add(button9, gridBagConstraints);

		turnLeftBtn = new JButton("<(");
		turnLeftBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				turnLeftBtnAction(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 3;
		add(turnLeftBtn, gridBagConstraints);

		turnRightBtn = new JButton(")>");
		turnRightBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				turnRightBtnAction(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 3;
		add(turnRightBtn, gridBagConstraints);

		posRat = new JLabel();
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 4;
		add(posRat, gridBagConstraints);

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
		Globals g = Globals.getInstance();
		int speed = (int) g.get("simulationSpeed");
		speed = --speed > 0 ? speed : 0;
		g.put("simulationSpeed", speed);
		// expUniv.moveRobot(new Vector3f(-.1f, 0f, 0f));
	}

	// accion asociada al boton de mover derecha
	private void rightBtnAction(java.awt.event.ActionEvent evt) {
		// expUniv.moveRobot(new Vector3f(0.1f, 0f, 0f));
		Globals g = Globals.getInstance();
		int speed = (int) g.get("simulationSpeed");
		speed = ++speed > 9 ? 9 : speed;
		g.put("simulationSpeed", speed);
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

}
