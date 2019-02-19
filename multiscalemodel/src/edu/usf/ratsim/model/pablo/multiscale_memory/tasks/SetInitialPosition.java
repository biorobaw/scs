package edu.usf.ratsim.model.pablo.multiscale_memory.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Globals;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.experiment.utils.XMLExperimentParser;
import edu.usf.vlwsim.universe.VirtUniverse;

public class SetInitialPosition extends Task{
	
	static ArrayList<Float[]> positions = new ArrayList<>();

	static int[] permutation;
	static int currentPos = -1;
	
	public SetInitialPosition(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
		if(positions.size()!=0) return;
		String mazeFile = (String)Globals.getInstance().get("maze.file");
		var xml = XMLExperimentParser.loadRoot(mazeFile);
		for(var p : xml.getChild("startPositions").getChildren("pos")) {
			List<Float> pos = p.getFloatList();
			positions.add(new Float[] {pos.get(0),pos.get(1),pos.get(2)});
		}
			
	}

	@Override
	public void perform(Universe u, Subject s) {
		// TODO Auto-generated method stub
		//move current position
		currentPos = (currentPos+1) % positions.size();
		
		//if at start of new cycle, generate new random permutation
		if(currentPos==0) permutation = generatePermutation(positions.size());
		
		// set the position
		var pos = positions.get(getStartIndex());
		((VirtUniverse)u).setRobotPosition(new Coordinate(pos[0],pos[1]));
		((VirtUniverse)u).setRobotOrientation(pos[2]);
		
	}
	
	static public int getStartIndex() {
		return permutation[currentPos];
	}

	static private int[] generatePermutation(int size) {
		var random = RandomSingleton.getInstance();
		var perm = IntStream.range(0, size).toArray();
		for(int i=size-1; i>0 ;i--) {
			int j = random.nextInt(i+1);
			var aux = perm[i];
			perm[i] = perm[j];
			perm[j] = aux;
		}
		//System.out.println("Perm: " + perm[0] + " " + perm[1]);
		//System.out.println("Positions: " + currentPos + "/" + positions.size());
		return perm;
	}
}
