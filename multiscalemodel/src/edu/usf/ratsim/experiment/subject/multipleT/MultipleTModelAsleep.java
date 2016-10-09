package edu.usf.ratsim.experiment.subject.multipleT;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.module.copy.Float1dSparseCopyModule;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
import edu.usf.ratsim.nsl.modules.actionselection.ActionFromProbabilities;
import edu.usf.ratsim.nsl.modules.actionselection.FeederTraveler;
import edu.usf.ratsim.nsl.modules.actionselection.ProportionalVotes;
import edu.usf.ratsim.nsl.modules.actionselection.Softmax;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.celllayer.TesselatedPlaceCellLayer;
import edu.usf.ratsim.nsl.modules.celllayer.TmazeRandomPlaceCellLayer;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.multipleT.PlaceCellTransitionMatrixUpdater;
import edu.usf.ratsim.nsl.modules.rl.Reward;

public class MultipleTModelAsleep extends MultipleTModel {

	private int numActions;
	private TmazeRandomPlaceCellLayer placeCells;
	
	private float[][] QTable;
	private float[][] WTable;
	
	public MultipleTModelAsleep() {
	}

	public MultipleTModelAsleep(ElementWrapper params, Subject subject,
			LocalizableRobot lRobot, int numActions,int numPC) {
		
		//Get parameters frorm xml file
		float PCRadius 			= params.getChildFloat("PCRadius");
		
		String placeCellType 	= params.getChildText("placeCells");
		
		float discountFactor	= params.getChildFloat("discountFactor");
		float learningRate		= params.getChildFloat("learningRate");
		float wTransitionLR		= params.getChildFloat("wTransitionLR");
		float foodReward		= params.getChildFloat("foodReward");
		
		int cantReplay			= params.getChildInt("cantReplay");
		float replayThres		= params.getChildFloat("replayThres");
		
		
		
		//Model overview:
		
		/**
		 * SARSA Q LEARNING:
		 * 
		 * Variables:
		 * 		Q 			Table
		 * 		W 			Transition Table
		 *      sleepState 	State: AWAKE/SLEEPING
		 * MODEL:
		 * //cambiar subAte de lugar
		 * Reward--------------->*-------->*-------------------------------------------------------->*--------->*----------->deltaSignal----->UpdateQ
		 * 			            /\        /\                                                        /\         /\
		 *                       |         |                                                         |          |
		 * 	                  PCCopy----*--|--------> UpdateW 								      ActionCopy    |
		 *					     |	   /   |											  			 |			|	 
		 * 					    \/	  /	   |											  		 	\/          |
		 * Pos------------>	PlaceCells---->*------->currentStateQ- ---------->SoftMax------------->ActionSelection---------->ActionPerformer--->subAte
		 * 
		 * 
		 * NOTES:
		 * 		-The model is only a reference to understand the flow, modules do not correspond 1 to 1 with the model components
		 * 		-Sleep Action should only get calculated when SLEEPING
		 * 		-calcCombinedQ, SoftMax and updataW should only be performed while awake
		 * 		-subAte = subjectAte (already existing module)
		 * 		-backDep = backward dependency
		 * 
		 * 		
		 */
		
		
		//Create Variables Q,W, note sleepState has already been initialized.

		QTable = ((MultipleTSubject)subject).QTable;
		FloatMatrixPort QPort = new FloatMatrixPort((Module) null, QTable);
		
		WTable = ((MultipleTSubject)subject).WTable;
		FloatMatrixPort WPort = new FloatMatrixPort((Module)null, WTable);
		
		
		//create subAte module
		SubjectAte subAte = new SubjectAte("Subject Ate",subject);
		addModule(subAte);
		
		//Create reward module
		float nonFoodReward = 0;
		Reward r = new Reward("foodReward", foodReward, nonFoodReward);
		r.addInPort("subAte", subAte.getOutPort("subAte"), true);
		addModule(r);
		
		
		//Create pos module 
		Position pos = new Position("position", lRobot);
		addModule(pos);
		
		//Create Place Cells module
		placeCells = new TmazeRandomPlaceCellLayer("PCLayer", PCRadius, numPC, placeCellType);
		placeCells.addInPort("position", pos.getOutPort("position"));
		addModule(placeCells);
		
		
		//Create currentStateQ Q module
		Module currentStateQ = new ProportionalVotes("currentStateQ",numActions,false);
		currentStateQ.addInPort("state", placeCells.getOutPort("activation"));
		currentStateQ.addInPort("value", QPort);
		addModule(currentStateQ);
		
		//Create SoftMax module
		Module softmax = new Softmax("softmax", numActions);
		softmax.addInPort("input", currentStateQ.getOutPort("votes"));
		addModule(softmax);
		
		//Create probabilityActionChooser
		Module selectActionFromProbabilities = new ActionFromProbabilities("actionFromProbabilities");
		selectActionFromProbabilities.addInPort("probabilities", softmax.getOutPort("probabilities"));
		addModule(selectActionFromProbabilities);
		
		
		//Create PC copy module
		Float1dSparseCopyModule pcCopy = new Float1dSparseCopyModule("PCCopy");
		pcCopy.addInPort("toCopy",placeCells.getOutPort("activation"), true);
		addModule(pcCopy);
		
		//Create UpdateW module
		PlaceCellTransitionMatrixUpdater wUpdater = new PlaceCellTransitionMatrixUpdater("wUpdater", numPC, wTransitionLR);
		wUpdater.addInPort("PC", placeCells.getOutPort("activation"));
		wUpdater.addInPort("PCcopy", pcCopy.getOutPort("copy"));
		wUpdater.addInPort("wPort", WPort);
		addModule(wUpdater);
		
		
		//Create actionSelection module 
		
		//Create actionCopy module
		
		//Create deltaSignal module
		
		//Create update Q module
		
		//Create Action Performer module
		
		
				

	}

	public void newTrial() {
	}

	public List<PlaceCell> getPlaceCells() {
		return placeCells.getCells();
	}

	public void newEpisode() {
		// TODO Auto-generated method stub
		
	}

	public Map<Integer, Float> getCellActivation() {
		Map<Integer, Float> activation = new LinkedHashMap<Integer, Float>();
		activation.putAll(((Float1dSparsePortMap) placeCells
					.getOutPort("activation")).getNonZero());
		return activation;
	}



}
