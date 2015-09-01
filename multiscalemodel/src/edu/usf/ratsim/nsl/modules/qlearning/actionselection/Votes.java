package edu.usf.ratsim.nsl.modules.qlearning.actionselection;

public class Votes implements Comparable<Votes> {
	private int action;
	private int votes;

	public Votes(int action, int votes) {
		this.action = action;
		this.votes = votes;
	}

	public int getAction() {
		return action;
	}

	public int getVotes() {
		return votes;
	}

	public void incrementVotes() {
		votes++;
	}

	public int compareTo(Votes o) {
		if (votes < o.votes)
			return -1;
		else if (votes == o.votes)
			return 0;
		else
			return 1;
	}

	public String toString() {
		return action + " voted " + votes + " times";
	}

}