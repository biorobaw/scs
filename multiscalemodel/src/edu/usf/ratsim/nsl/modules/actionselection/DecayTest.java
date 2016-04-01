package edu.usf.ratsim.nsl.modules.actionselection;

public class DecayTest {

	public static void main(String[] args) {
		double halfLife = 200;
		int iterCount = 2000;
		double alpha = -Math.log(.5) / halfLife;
		double val = Math.exp(-iterCount * alpha);
		System.out.println(val);
	}

}
