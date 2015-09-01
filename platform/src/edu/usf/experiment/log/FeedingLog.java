package edu.usf.experiment.log;

public class FeedingLog {

	public int feederId;
	public boolean ate;
	public boolean wasFlashing;
	
	public FeedingLog(int feederId, boolean ate, boolean wasFlashing) {
		super();
		this.feederId = feederId;
		this.ate = ate;
		this.wasFlashing = wasFlashing;
	}
	
}
