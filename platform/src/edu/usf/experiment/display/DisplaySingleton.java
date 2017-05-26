package edu.usf.experiment.display;

public class DisplaySingleton {

	/**
	 * A global instance for the display object
	 */
	private static Display display;

	public static void setDisplay(Display display) {
		DisplaySingleton.display = display;
	}

	public static Display getDisplay(){
		return display;
	}
}
