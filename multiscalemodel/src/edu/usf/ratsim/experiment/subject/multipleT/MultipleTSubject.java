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
import edu.usf.experiment.utils.CSVReader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.experiment.subject.NotImplementedException;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;


public class MultipleTSubject extends Subject {

	public float step;
	//private float leftAngle;
	//private float rightAngle;
	
	
	private MultipleTModel model;
	private MultipleTModelAsleep  modelAsleep;
	private MultipleTModelAwake modelAwake;
	
	
	public float[][] QTable;
	public float[][] WTable;
	
	int numActions;
	int numPC;
	
	public int iteration;
	public int episode =0;
	public int trial = -1;

	public MultipleTSubject(String name, String group,
			ElementWrapper params, Robot robot) {
		super(name, group, params, robot);
		
		Globals g = Globals.getInstance();
		
		
		step = params.getChildFloat("step");
		//leftAngle = params.getChildFloat("leftAngle");
		//rightAngle = params.getChildFloat("rightAngle");
		
		if (!(robot instanceof LocalizableRobot))
			throw new RuntimeException("TSPSubject "
					+ "needs a Localizable Robot");
		LocalizableRobot lRobot = (LocalizableRobot) robot;

		numPC 		= params.getChildInt("numPC");
		numActions	= params.getChildInt("numActions");
		
		
		
		QTable = new float[numPC][numActions];
		WTable = new float[numPC][numPC];
		
		Integer loadEpisode = (Integer)g.get("loadEpisode");
		if(loadEpisode!=null){
			episode = loadEpisode;
			String loadPath = (String)g.get("logPath")  +"/"+(String)g.get("loadTrial")+ "/" + loadEpisode + "/" + (String)g.get("groupName") + "/" + g.get("subName") + "/";
			
			String[][] qStringValues = CSVReader.loadCSV(loadPath+"QTable.txt",";");
			String[][] wStringValues = CSVReader.loadCSV(loadPath+"WTable.txt",";");
			
			for (int i =0;i<numPC;i++)
			{
				for(int j = 0; j<numActions;j++)
					QTable[i][j] = Float.parseFloat(qStringValues[i][j]);
				for(int j = 0; j<numPC;j++)
					WTable[i][j] = Float.parseFloat(wStringValues[i][j]);
			}
				
				
			
			System.out.println("loadpath: "+loadPath);
			
			modelAwake = new MultipleTModelAwake(params, this, lRobot,numActions,numPC);
		}else{
			modelAwake = new MultipleTModelAwake(params, this, lRobot,numActions,numPC);
		}
		
		
		modelAsleep = null;//new MultipleTModelAwake(params, this, lRobot,numActions,numPC);
		model = modelAwake;
		
		iteration = 0;
	}
	
	public void setAwake(){
		model = modelAwake;
	}
	
	public void setAsleep(){
		model = modelAsleep;
	}

	public void save(){
		Globals g = Globals.getInstance();
		String logPath = (String)g.get("episodeLogPath");
		
		PrintWriter writer;
		try {
			//save WTable
			writer = new PrintWriter(logPath + "WTable.txt", "UTF-8");
			for(int i=0;i<numPC;i++){
				for(int j=0;j<numPC;j++){
					writer.print(WTable[i][j]+";");
				}
				writer.println();
			}
			writer.close();
			
			//save QTable
			writer = new PrintWriter(logPath + "QTable.txt", "UTF-8");
			for(int i=0;i<numPC;i++){
				for(int j=0;j<numActions;j++){
					writer.print(QTable[i][j]+";");
				}
				writer.println();
			}
			writer.close();
			
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
		
		MultipleTModelAwake awake = (MultipleTModelAwake)model;
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
		//List<Affordance> res = new LinkedList<Affordance>();
		throw new NotImplementedException();
		//res.add(new TurnAffordance(leftAngle, step));
		//res.add(new ForwardAffordance(step));
		//res.add(new TurnAffordance(rightAngle, step));
		//res.add(new EatAffordance());
		
		//return res;
	}

	@Override
	public float getMinAngle() {
		throw new NotImplementedException();
		//return leftAngle;
	}

	@Override
	public void newEpisode() {
		model.newEpisode();
		iteration=0;
		episode++;
		System.out.println("T,E: "+trial+" "+episode);
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
	
	

}
