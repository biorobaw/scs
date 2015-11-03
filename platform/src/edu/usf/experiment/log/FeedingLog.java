package edu.usf.experiment.log;

public class FeedingLog {

	public int feederId;
	public boolean ate;
	public boolean wasFlashing;
	public boolean wasEnabled;
	public String cycle;
	
	public FeedingLog(int feederId, String cycle, boolean ate, boolean wasFlashing, boolean enabled) {
		super();
		this.feederId = feederId;
		this.cycle = cycle;
		this.ate = ate;
		this.wasFlashing = wasFlashing;
		this.wasEnabled = enabled;
	}
	
}
