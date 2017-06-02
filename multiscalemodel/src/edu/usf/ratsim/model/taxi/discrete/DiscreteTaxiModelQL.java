//package edu.usf.ratsim.model.taxi.discrete;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.vecmath.Point3f;
//
//import edu.usf.experiment.display.DisplaySingleton;
//import edu.usf.experiment.model.PolicyModel;
//import edu.usf.experiment.model.ValueModel;
//import edu.usf.experiment.robot.GlobalWallRobot;
//import edu.usf.experiment.robot.LocalizableRobot;
//import edu.usf.experiment.robot.Robot;
//import edu.usf.experiment.robot.affordance.AffordanceRobot;
//import edu.usf.experiment.utils.ElementWrapper;
//import edu.usf.micronsl.Model;
//import edu.usf.micronsl.module.Module;
//import edu.usf.micronsl.plot.float1d.Float1dDiscPlot;
//import edu.usf.micronsl.port.onedimensional.Float1dPort;
//import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
//import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
//import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
//import edu.usf.ratsim.nsl.modules.actionselection.ActionFromProbabilities;
//import edu.usf.ratsim.nsl.modules.actionselection.MaxAffordanceActionPerformer;
//import edu.usf.ratsim.nsl.modules.actionselection.ProportionalVotes;
//import edu.usf.ratsim.nsl.modules.actionselection.Softmax;
//import edu.usf.ratsim.nsl.modules.celllayer.DiscretePlaceCellLayer;
//import edu.usf.ratsim.nsl.modules.input.Position;
//import edu.usf.ratsim.nsl.modules.input.SubFoundPlatform;
//import edu.usf.ratsim.nsl.modules.multipleT.ActionGatingModule;
//import edu.usf.ratsim.nsl.modules.multipleT.UpdateQModule;
//import edu.usf.ratsim.nsl.modules.rl.QLDeltaError;
//import edu.usf.ratsim.nsl.modules.rl.Reward;
//
//public class DiscreteTaxiModelQL extends Model implements ValueModel, PolicyModel {
//
//	public DiscretePlaceCellLayer placeCells;
//
//	public ProportionalVotes currentStateQ;
//
//	private FloatMatrixPort QTable;
//
//	private Position pos;
//
//	private QLDeltaError deltaError;
//
//	private int gridSize;
//
//	private int numActions;
//
//	private UpdateQModule updateQ;
//
//	private int numCells;
//
//	public DiscreteTaxiModelQL() {
//	}
//
//	public DiscreteTaxiModelQL(ElementWrapper params, Robot robot) {
//
//		// Model parameters
//		float discountFactor = params.getChildFloat("discountFactor");
//		float learningRate = params.getChildFloat("learningRate");
//		float foodReward = params.getChildFloat("foodReward");
//		float nonFoodReward = params.getChildFloat("nonFoodReward");
//		boolean multiScale = params.getChildBoolean("multiScale");
//		
//		// Universe parameters
//		gridSize = params.getChildInt("gridSize");
//
//		LocalizableRobot lRobot = (LocalizableRobot) robot;
//		AffordanceRobot affRobot = (AffordanceRobot) robot;
//
//		numActions = affRobot.getPossibleAffordances().size();	
//		
//		// Create pos module
//		pos = new Position("position", lRobot);
//		addModulePost(pos);
//
//		// Create Place Cells module
//		placeCells = new DiscretePlaceCellLayer("PCLayer", gridSize, gridSize, multiScale, (GlobalWallRobot) robot);
//		placeCells.addInPort("position", pos.getOutPort("position"));
//		addModulePost(placeCells);
//		
//		numCells = placeCells.getCells().size();
//		
//		float[][] qvals = new float[numCells][numActions];
//		this.QTable = new FloatMatrixPort(null, qvals);
//
//		// Create currentStateQ Q module
//		currentStateQ = new ProportionalVotes("currentStateQ", numActions);
//		currentStateQ.addInPort("states", placeCells.getOutPort("output"));
//		currentStateQ.addInPort("value", QTable);
//		addModulePost(currentStateQ);
//		DisplaySingleton.getDisplay().addComponent(new Float1dDiscPlot((Float1dPort)currentStateQ.getOutPort("votes")), 0, 0, 1, 1);
//		
//		// Create ActionGatingModule -- sets the probabilities of impossible
//		// actions to 0 and then normalizes them
//		ActionGatingModule actionGating = new ActionGatingModule("actionGating", robot);
//		actionGating.addInPort("input", currentStateQ.getOutPort("votes"));
//		addModulePre(actionGating);
//		DisplaySingleton.getDisplay().addComponent(new Float1dDiscPlot((Float1dPort)actionGating.getOutPort("probabilities")), 0, 1, 1, 1);
//
//		// Create SoftMax module
//		Softmax softmax = new Softmax("softmax", numActions);
//		softmax.addInPort("input", actionGating.getOutPort("probabilities")); 
//		addModulePre(softmax);
//		DisplaySingleton.getDisplay().addComponent(new Float1dDiscPlot((Float1dPort)softmax.getOutPort("probabilities")), 0, 2, 1, 1);
//
//		// Create action selection module -- choose action according to
//		// probability distribution
//		Module actionSelection = new ActionFromProbabilities("actionFromProbabilities");
//		actionSelection.addInPort("probabilities", softmax.getOutPort("probabilities"));
//		addModulePre(actionSelection);
//
//		// Create Action Performer module
//		MaxAffordanceActionPerformer actionPerformer = new MaxAffordanceActionPerformer("actionPerformer", robot);
//		actionPerformer.addInPort("action", actionSelection.getOutPort("action"));
//		addModulePre(actionPerformer);
//		
////		// Cells are only computed after performing an action
////		placeCells.addPreReq(actionPerformer);
//
//		// create subAte module
//		SubFoundPlatform subFoundPlat = new SubFoundPlatform("Subject Found Plat", robot);
//		addModulePost(subFoundPlat);
//
//		// Create reward module
//		Reward r = new Reward("foodReward", foodReward, nonFoodReward);
//		r.addInPort("rewardingEvent", subFoundPlat.getOutPort("foundPlatform")); 
//		addModulePost(r);
//
//		// Create deltaSignal module
//		deltaError = new QLDeltaError("error", discountFactor);
//		deltaError.addInPort("reward", r.getOutPort("reward"));
//		deltaError.addInPort("Q", currentStateQ.getOutPort("votes"));
//		deltaError.addInPort("action", actionSelection.getOutPort("action"));
//		addModulePost(deltaError);
//
//		// Create update Q module
//		updateQ = new UpdateQModule("updateQ", learningRate);
//		updateQ.addInPort("delta", deltaError.getOutPort("delta"));
//		updateQ.addInPort("action", actionSelection.getOutPort("action"));
//		updateQ.addInPort("Q", QTable);
//		updateQ.addInPort("placeCells", placeCells.getOutPort("output"));
//		addModulePost(updateQ);
//		
//		DisplaySingleton.getDisplay().addUniverseDrawer(new QValueDrawer(this), 0);
//		DisplaySingleton.getDisplay().addUniverseDrawer(new QPolicyDrawer(this, (AffordanceRobot) robot));
//	}
//
//	public void newEpisode() {
//		super.newEpisode();
////		 Compute place cell output before making the first decision
//		pos.run();
//		placeCells.run();
//		currentStateQ.run();
//		
//		deltaError.saveQ();
//		updateQ.savePCs();
//	}
//
//	@Override
//	public Map<Point3f, Float> getValuePoints() {
//		Map<Point3f, Float> valuePoints = new HashMap<Point3f, Float>();
//		
//		ProportionalVotes votes = new ProportionalVotes("currentStateQ", numActions);
//		currentStateQ.addInPort("states", placeCells.getOutPort("output"));
//		currentStateQ.addInPort("value", QTable);
//		
//		for (int x = 0; x < gridSize; x++)
//			for (int y = 0; y < gridSize; y++){
//				Point3f pos = new Point3f(x,y,0);
//				Map<Integer, Float> active = placeCells.getActive( pos);
//				votes.run(active, QTable);
//				
//				float max = -Float.MAX_VALUE;
//				for (float v : votes.actionVote)
//					max = v > max ? v : max;
//				
//				valuePoints.put(pos, max);
//			}
//			
//		return valuePoints;
//	}
//
//	@Override
//	public float getValueEntropy() {
//		return 0;
//	}
//
//	public Map<Point3f, Integer> getPolicyPoints() {
//		Map<Point3f, Integer> policyPoints = new HashMap<Point3f, Integer>();
//		
//		ProportionalVotes votes = new ProportionalVotes("currentStateQ", numActions);
//		currentStateQ.addInPort("states", placeCells.getOutPort("output"));
//		currentStateQ.addInPort("value", QTable);
//		
//		Float1dSparsePort activeCells = new Float1dSparsePortMap(null, numCells, 6/gridSize*gridSize);
//		for (int x = 0; x < gridSize; x++)
//			for (int y = 0; y < gridSize; y++){
//				Point3f pos = new Point3f(x,y,0);
//				activeCells.clear();
//				Map<Integer, Float> active = placeCells.getActive(pos);
//				votes.run(active, QTable);
//				
//				float maxVal = -Float.MAX_VALUE;
//				int maxAction = 0;
//				int i = 0;
//				for (float v : votes.actionVote){
//					maxAction = v > maxVal ? i : maxAction;
//					maxVal = v > maxVal ? v : maxVal;
//					i++;
//				}
//				
//				policyPoints.put(pos, maxAction);
//			}
//			
//		return policyPoints;
//	}
//
//}
