/**
 * 
 */
package edu.usf.ratsim.support;

/**
 * @author gtejera
 * 
 */
public class Percentage {
	private long porcentaje = 0;
	private String name;
	private int step;

	/**
	 * @param name
	 */
	public Percentage(String name, int step) {
		super();
		this.name = name;
		if (step < 1)
			step = 1;
		this.step = step;
	}

	/**
	 * @param epocasPorEnsayo
	 * @param currenTrial
	 */
	public void printPorcentage(long actual, long total) {
		if (100 * actual / total > porcentaje) {
			porcentaje++;
			if ((porcentaje % step) == 0)
				System.out.println(name + "::porcentaje de procesamiento: "
						+ porcentaje + "%");
		}
	}
}
