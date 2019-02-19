package edu.usf.ratsim.model.pablo.multiscale_memory.tasks;

import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.BinaryFile;
import edu.usf.micronsl.port.twodimensional.Float2dPort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePortMatrix;

public class LogData {
	
	int numEpisodes;
	int startPositions[];
	int stepsTaken[];
	
	public LogData() {
		// TODO Auto-generated constructor stub
		numEpisodes = Integer.parseInt(Globals.getInstance().get("numEpisodes").toString());
		startPositions = new int[numEpisodes];
		stepsTaken = new int[numEpisodes];
	}
	
	public void logSteps(int startPos,int steps) {
		int episode = (int)Globals.getInstance().get("episode");
		startPositions[episode] = startPos;
		stepsTaken[episode] = steps;
	}

	public void storeLog(Float2dSparsePort[] V , Float2dSparsePort[] Q) {
		
		//get experiment configuration id:
		Globals g = Globals.getInstance();
		String logFolder = g.get("logPath").toString();
//		String configId = g.get("config").toString();
		String ratId	 = g.get("subName").toString();
		
		//create folders
		String prefix = logFolder  +"/r" + ratId + "-";  
		
		var file = BinaryFile.openFileToWrite(prefix + "steps.bin");
		BinaryFile.writeArray(file, startPositions,true);
		BinaryFile.writeArray(file, stepsTaken,true);
		BinaryFile.close(file);
		
		for(int i=0;i<V.length;i++) {
			
			BinaryFile.saveBinaryMatrix(V[i].getNonZero(), V[i].getNRows(), V[i].getNCols(), prefix + "V" + i+ ".bin", true);
			BinaryFile.saveBinaryMatrix(Q[i].getNonZero(), Q[i].getNRows(), Q[i].getNCols(), prefix + "Q" + i+ ".bin", true);
		}
		
		
		
	}
	
}
