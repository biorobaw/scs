package com.github.biorobaw.scs.experiment;

import java.util.LinkedList;
import java.util.List;

import com.github.biorobaw.scs.simulation.scripts.Script;
import com.github.biorobaw.scs.utils.files.XML;

/**
 * This class defines a trial (set of episodes) of an experiment.
 * The trial defines before and after trial tasks, before and after episode tasks,
 * and episode tasks (tasks that run during the episode). 
 * 
 * @author gtejera, mllofriu, bucef
 * 
 */
public class Trial {
//	private Subject subject;
	public Boolean endTrial = false;
	protected String  trialName;

	protected int numEpisodes = -1;
	protected int startingEpisode = 0;
	protected List<Script> trialTasks = new LinkedList<>();
	protected List<Script> episodeTasks = new LinkedList<>();
	protected List<Script> cycleTasks = new LinkedList<>();
	
	
	public Trial(XML trialNode) {
		this.trialName 	 = trialNode.getId();
		
		//load trial tasks
		if(trialNode.hasChild("trialTasks"))
			trialTasks = trialNode.getChild("trialTasks").loadObjectList();
		
		//load episode tasks
		if(trialNode.hasChild("episodeTasks"))
			episodeTasks 	= trialNode.getChild("episodeTasks").loadObjectList();
		
		//load cycle tasks
		if(trialNode.hasChild("cycleTasks"))
			cycleTasks = trialNode.getChild("cycleTasks").loadObjectList();		
		
		//get number of episodes, if the number is not defined, possibly
		//the old xml format is being used, give error and hint
		assert trialNode.hasAttribute("numberOfEpisodes") : "ERROR: number of episodes in trial \"+ trialName + \" was not specified."; 
		numEpisodes = trialNode.getIntAttribute("numEpisodes");

	}
	
	public String getName() {
		return trialName;
	}

	public String toString() {
		return trialName;
	}

	
}
