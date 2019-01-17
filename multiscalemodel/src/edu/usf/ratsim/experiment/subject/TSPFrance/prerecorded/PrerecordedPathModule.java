package edu.usf.ratsim.experiment.subject.TSPFrance.prerecorded;

import java.util.ArrayList;
import java.util.LinkedList;
import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.specificActions.MoveToAction;
import edu.usf.experiment.robot.specificActions.TeleportToAction;
import edu.usf.experiment.utils.CSVReader;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.nsl.modules.port.ModelActionPort;

/**
 * 
 * @author biorob
 * 
 */
public class PrerecordedPathModule extends Module {	
	
	//Port to hold output
	public ModelActionPort outport  = new ModelActionPort(this);
	
	//List of positions - x,y,z,theta
	private ArrayList<ArrayList<TeleportToAction>> paths = new ArrayList<>();
	private ArrayList<ArrayList<Boolean>> rewards = new ArrayList<>();
	
	
	int nextPos = 0;
	int path = 0;
	
	
	public boolean expectReward = false;
	public boolean pathEnded = false;
	
	
	//to smooth the orientation, the orientation is set using positions[t+deltaId] - positions[t]
	static private int deltaId = 3;
	

	public PrerecordedPathModule(String name, String pathFiles) {
		super(name);

		addOutPort("action", outport);
		
			
		
		//System.out.println("FILE: "+pathFile);
		for(String pathFile : pathFiles.split(",")) {
			
			ArrayList<TeleportToAction> positions = new ArrayList<>();
			ArrayList<Boolean> rewardHistory = new ArrayList<>();
			
			String[][] strPoints = CSVReader.loadCSV(pathFile, "\t", "Place robot file not found");
			
			boolean ignoreLine = true;
			int cycle = 0;
			for (String[] s : strPoints){
				
				if(ignoreLine) {
					ignoreLine = false;
					continue;
				}
				
				Float x = Float.parseFloat(s[4]);
				Float y = Float.parseFloat(s[5]);
				Boolean reward = Boolean.parseBoolean(s[8].trim());
				//System.out.println(""+(cycle++) +" " +  s[8].trim() + " " + reward);
				positions.add(new TeleportToAction(x,y,0f,0f));
				rewardHistory.add(reward);
			}
			
			
			for(int i=0;i<positions.size();i++)
			{
				if (i+deltaId < positions.size())
				{
					TeleportToAction p1 = positions.get(i);
					TeleportToAction p2 = positions.get(i+deltaId);
					p1.setTheta( (float)Math.atan2(p2.y()-p1.y(), p2.x()-p1.x()));
				}else{
					int minPos = positions.size()-1-deltaId < 0 ? 0 : positions.size()-1-deltaId;
					TeleportToAction p1 = positions.get(minPos);
					TeleportToAction p2 = positions.get(positions.size()-1);
					p2.setTheta((float)Math.atan2(p2.y()-p1.y(), p2.x()-p1.x()));
				}
			}	
			
			
			rewards.add(rewardHistory);
			paths.add(positions);
			
			
		}

	}

	public void run() {
		//System.out.println();
		
		if(++nextPos < paths.get(path).size()) {
			TeleportToAction p = paths.get(path).get(nextPos);
			expectReward = rewards.get(path).get(nextPos);
			outport.set(p);
		}else {
			pathEnded = true;
		}


	}

	
	public void printPathPercentage() {
		System.out.println("" + nextPos+"/"  + paths.get(path).size() + " "+rewards.get(path).get(nextPos-1)+" "  + path);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
	
	@Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		super.newEpisode();
		nextPos = 0;
		pathEnded = false;
		expectReward = rewards.get(path).get(nextPos);
	}
	
	public void nextPathInList() {
		path++;
	}
	
	public TeleportToAction firstPosition() {
		return  paths.get(path).get(0);
	}
}
