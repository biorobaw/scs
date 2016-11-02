package edu.usf.ratsim.experiment.subject.multipleT;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.utils.BinaryFile;
import edu.usf.experiment.utils.CSVReader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.Datatypes.SparseMatrix;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.experiment.subject.NotImplementedException;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.robot.virtual.VirtualRobot;


public class MultipleTSubject extends Subject {

	public float step;
	//private float leftAngle;
	//private float rightAngle;
	public enum State {AWAKE,ASLEEP};
	public State state = State.AWAKE;
	
	private MultipleTModel model;
	private MultipleTModelAsleep  modelAsleep;
	private MultipleTModelAwake modelAwake;
	
	
//	public SparseMatrix<Float> QTable;
//	public SparseMatrix<Float> WTable;
	
	public float[][] QTable;
	public float[][] WTable;
	
	int numActions;
	int numPC;
	
	int laps;
	int noFoodLaps;
	int cantReplay;
	int episodesPerSession;
	float replayThres;
	
	public int iteration;
	public int episode =0;
	public int trial = -1;

	public VirtualRobot lRobot;
	public float awakeFoodDistanceThreshold;
	public float asleepFoodDistanceThreshold;
	
	public MultipleTSubject(String name, String group,
			ElementWrapper params, Robot robot) {
		super(name, group, params, robot);
		
		Globals g = Globals.getInstance();
		
		
		if (!(robot instanceof LocalizableRobot))
			throw new RuntimeException("TSPSubject "
					+ "needs a Localizable Robot");
		lRobot = (VirtualRobot) robot;
		
		
		//load parametrs:
		step = params.getChildFloat("step");
		
		numPC 		= params.getChildInt("numPC");
		numActions	= params.getChildInt("numActions");
		
		
		replayThres		= params.getChildFloat("replayThres");
		cantReplay		= params.getChildInt("cantReplay");
		laps			= params.getChildInt("laps");
		noFoodLaps		= params.getChildInt("noFoodLaps");
		
		awakeFoodDistanceThreshold  = params.getChildFloat("awakeFoodDistanceThreshold");
		asleepFoodDistanceThreshold = params.getChildFloat("asleepFoodDistanceThreshold");
		
		
		
		
		QTable = new float[numPC][numActions];
		WTable = new float[numPC][numPC];
		
		Integer loadEpisode = (Integer)g.get("loadEpisode");
		if(loadEpisode!=null){
			episode = loadEpisode;
			String loadPath = (String)g.get("logPath")  +"/"+(String)g.get("loadTrial")+ "/" + loadEpisode + "/" + (String)g.get("groupName") + "/" + g.get("subName") + "/";
			
			
			if(g.get("loadType").equals("bin")){
				QTable = BinaryFile.loadMatrix(loadPath+"QTable.bin");
				WTable = BinaryFile.loadSparseMatrix(loadPath+"WTable.bin");
				
			}else{
				
				String[][] qStringValues = CSVReader.loadCSV(loadPath+"QTable.txt",";");
				String[][] wStringValues = CSVReader.loadCSV(loadPath+"WTable.txt",";");
				
				for (int i =0;i<numPC;i++)
				{
					for(int j = 0; j<numActions;j++)
						QTable[i][j] = Float.parseFloat(qStringValues[i][j]);
					for(int j = 0; j<numPC;j++)
						WTable[i][j] = Float.parseFloat(wStringValues[i][j]);
				}
				
			}
			
			
				
			//QTable = new SparseMatrix<Float>(QTableCopy);
			//WTable = new SparseMatrix<Float>(WTableCopy);
			
			System.out.println("loadpath: "+loadPath);
			
			modelAwake = new MultipleTModelAwake(params, this, lRobot,numActions,numPC);
		}else{
			modelAwake = new MultipleTModelAwake(params, this, lRobot,numActions,numPC);
			//QTable = new SparseMatrix<Float>(numPC,numActions);
			//WTable = new SparseMatrix<Float>(numPC,numPC);
		}
		
		
		modelAsleep = new MultipleTModelAsleep(params, this, lRobot,numActions,numPC,(LinkedList<PlaceCell>)modelAwake.placeCells.getCells());
		model = modelAwake;
		
		iteration = 0;
	}
	
	public void swapState(){
		if(state==State.AWAKE) setAsleep();
		else setAwake();
	}
	
	public void setAwake(){
		state = State.AWAKE;
		model = modelAwake;
		lRobot.setCloseThreshold(awakeFoodDistanceThreshold);
	}
	
	public void setAsleep(){
		state = State.ASLEEP;
		model = modelAsleep;
		lRobot.setCloseThreshold(asleepFoodDistanceThreshold);
	}

	public void save(){
		Globals g = Globals.getInstance();
		String logPath = (String)g.get("episodeLogPath");
		
		//WTable.getDataAsArray(WTableCopy);
		//QTable.getDataAsArray(QTableCopy);
		
		PrintWriter writer;
		try {
			//save WTable if not asleep:
			if(state != State.ASLEEP){
				String filename = logPath + "WTable.bin";
				BinaryFile.saveSparseBinaryMatrix(WTable, filename);
				
//				writer = new PrintWriter(logPath + "WTable.txt", "UTF-8");
//				for(int i=0;i<numPC;i++){
//					for(int j=0;j<numPC;j++){
//						writer.print(WTable[i][j]+";");
//					}
//					writer.println();
//				}
//				writer.close();
			}
			
			//save QTable
			String filename = logPath + "QTable.bin";
			BinaryFile.saveBinaryMatrix(QTable, filename);
			
			
//			writer = new PrintWriter(logPath + "QTable.txt", "UTF-8");
//			for(int i=0;i<numPC;i++){
//				for(int j=0;j<numActions;j++){
//					writer.print(QTable[i][j]+";");
//				}
//				writer.println();
//			}
//			writer.close();
			
			//save cells
			writer = new PrintWriter(logPath + "cellCenters.txt", "UTF-8");
			for(PlaceCell pc : getPlaceCells()){
				Point3f p = pc.getPreferredLocation();
				writer.println(""+p.x+";"+p.y+";");
			}
			writer.close();
			
			System.out.println("SAVED SUBJECT");
			
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	@Override
	public void stepCycle() {
		iteration++;
		//System.out.println("iteration: "+iteration);
		//if( i==642) new java.util.Scanner(System.in).nextLine();
		
		setHasEaten(false);
		clearTriedToEAt();
		
		model.simRun();
		
//		MultipleTModelAwake awake = (MultipleTModelAwake)model;
//		System.out.println("Q: "+awake.currentStateQ.voteString());
//		Float1dSparsePortMap pcs = (Float1dSparsePortMap)awake.placeCells.getOutPort("activation");
//		if( episode==2 && pcs.get(37)!=0) {
//			
//			System.out.println("MODEL Q:");
//			for(int i=0;i<8;i++) System.out.print(QTable[37][i]+" ");
//			
//			new java.util.Scanner(System.in).nextLine();
//		}
	}
	
	@Override
	public List<Affordance> getPossibleAffordances() {
		throw new NotImplementedException();

	}

	@Override
	public float getMinAngle() {
		throw new NotImplementedException();
	}

	@Override
	public void newEpisode() {
		Globals g = Globals.getInstance();
		int e = (int)g.get("episode");
		VirtUniverse u = VirtUniverse.getInstance();
		
		//noFoodLaps = 0;
		//cantReplay = 10;
		
		//food activation:
		if(e <noFoodLaps){
			for (Integer f : u.getEnabledFeeders()){
				u.setActiveFeeder(f, false);
				u.clearFoodFromFeeder(f);
			}
			setAwake(); //no need to do replay since it wont have any effect
		}
		else{
			for (Integer f : u.getEnabledFeeders())
				u.setActiveFeeder(f, true);
			
			if((e-noFoodLaps) % (cantReplay + 1) == 0){//do 1 awake 10 asleep 
				setAwake();
				
			}
			else{
				setAsleep();
			}
			
		}	
		
		
		
		//RESET TRANSITION MATRIX
//		if(e % 20 == 0)  //reset transition matrix every episodesPerSession
//		{
//			for (int i =0;i<numPC;i++)
//				for(int j = 0; j<numPC;j++)
//					WTable[i][j] = 0.0f;
//			for (int i =0;i<numPC;i++)
//				for(int j = 0; j<numActions;j++)
//					QTable[i][j] = 0.0f;
//		}

		
		model.newEpisode();
		iteration=0;
		episode++;
		System.out.println("T,E: "+trial+" "+e);
		System.out.println("State: "+state);
	}

	@Override
	public void newTrial() {
		model.newTrial();
		iteration=0;
		trial++;
		episode=0;
	}

	@Override
	public Affordance getHypotheticAction(Point3f pos, float theta,
			int intention) {
		return null;
	}

	@Override
	public void deactivateHPCLayersRadial(LinkedList<Integer> indexList, float constant) {
		throw new NotImplementedException();
	}

	@Override
	public void setExplorationVal(float val) {
		throw new NotImplementedException();
	}

	@Override
	public float getStepLenght() {
		return step;
	}

	@Override
	public Map<Float,Float> getValue(Point3f point, int intention, float angleInterval, float distToWall) {
		throw new NotImplementedException();
	}

	public List<PlaceCell> getPlaceCells() {
		return model.getPlaceCells();
	}

	@Override
	public void deactivateHPCLayersProportion(LinkedList<Integer> indexList,
			float proportion) {
		throw new NotImplementedException();
	}

	@Override
	public void remapLayers(LinkedList<Integer> indexList) {
		throw new NotImplementedException();
	}

	public Map<Integer, Float> getPCActivity() {
		return model.getCellActivation();
	}

	@Override
	public float getValueEntropy() {
		throw new NotImplementedException();
	}

	@Override
	public void reactivateHPCLayers(LinkedList<Integer> indexList) {
		throw new NotImplementedException();
	}
	
	public boolean replayAboveThreshold(){
		if (state==State.AWAKE) return true;
		//System.out.println("max activation: "+model.getMaxActivation());
		return model.getMaxActivation() > replayThres ;
	}
	
	public boolean loopInReactivationPath(){
		if (state==State.AWAKE) return false;
		//System.out.println("max activation: "+model.getMaxActivation());
		return ((MultipleTModelAsleep)model).loopInReactivationPath() ;
	}
	
	

}
