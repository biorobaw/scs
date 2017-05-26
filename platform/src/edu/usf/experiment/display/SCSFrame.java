package edu.usf.experiment.display;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A Java Swing frame to display the SCS data.
 * @author martin
 *
 */
public class SCSFrame extends JFrame implements Display {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6489459171441343768L;
	private static final int PADDING = 10;
	

	public SCSFrame(){
		setLayout(new GridBagLayout());
		setTitle("Spatial Cognition Simulator");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		setVisible(true);
	}
	
	public void addPanel(JPanel panel, int gridx, int gridy, int gridwidth, int gridheight){
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.insets = new Insets(PADDING, PADDING, PADDING, PADDING);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		add(panel, gridBagConstraints);
		
		pack();
	}

	@Override
	public void log(String s) {
		System.out.println(s);
	}
}
