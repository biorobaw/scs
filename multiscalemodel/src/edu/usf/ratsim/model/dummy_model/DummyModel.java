package edu.usf.ratsim.model.dummy_model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Globals;
import edu.usf.experiment.display.Display;
import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.display.DrawPanel;
import edu.usf.experiment.display.GuiUtils;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.PathDrawer;
import edu.usf.experiment.display.drawer.RobotDrawer;
import edu.usf.experiment.display.drawer.WallDrawer;
import edu.usf.experiment.display.drawer.simulation.CycleDataDrawer;
import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.TeleportRobot;
import edu.usf.experiment.robot.WallRobot;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.utils.BinaryFile;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.twodimensional.sparse.Entry;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePortMatrix;
import edu.usf.platform.drawers.PCDrawer;
import edu.usf.platform.drawers.PolarArrowDrawer;
import edu.usf.platform.drawers.PolarDataDrawer;
import edu.usf.ratsim.model.pablo.morris_replay.drawers.RuntimesDrawer;
import edu.usf.ratsim.model.pablo.multiplet.drawers.VDrawer;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.cell.WallExponentialPlaceCell;
import edu.usf.vlwsim.robot.VirtualRobot;
import edu.usf.vlwsim.universe.VirtUniverse;

//TODO: some trials are starting from a different place 
public class DummyModel extends Model  {



	public VirtualRobot lRobot;
	private FeederRobot fRobot;
	private WallRobot wRobot;

	WallExponentialPlaceCell pc;
	
	
	
	public DummyModel(ElementWrapper params, Robot robot) {

		Globals g = Globals.getInstance();

		if (!(robot instanceof LocalizableRobot))
			throw new RuntimeException("TSPSubject " + "needs a Localizable Robot");
		lRobot = (VirtualRobot) robot;
		fRobot = (FeederRobot) robot;
		wRobot = (WallRobot) robot;

		
		pc = new WallExponentialPlaceCell(new Coordinate(0, 0), 0.08f, false);
//		pc = new WallExponentialPlaceCell(new Coordinate(0, 0), 0.08f, true,10f,0.1f,0.01f);
		

		setDisplay();
		
		VirtUniverse vu = VirtUniverse.getInstance();
		vu.addWall(new LineSegment(-0.02, -0.2, -0.02, 0.2));

	}
	
	
	Coordinate lastPos = new Coordinate(-100f,-100f);

	@Override
	public void run() {

		Coordinate pos = lRobot.getPosition();
		if(!pos.equals(lastPos)){
			float d = wRobot.getDistanceToClosestWall();
			System.err.println(pos + ":\t" + d  + "\t" + pc.getActivation(lRobot.getPosition(), d));
			lastPos= pos;
		}
	}

	@Override
	public void newEpisode() {
		

	}

	@Override
	public void endEpisode() {
		// TODO Auto-generated method stub
		super.endEpisode();

		Display display = DisplaySingleton.getDisplay();

		


	}

	@Override
	public void newTrial() {

	}

	public List<PlaceCell> getPlaceCells() {
		return null;
	}

	public Map<Integer, Float> getPCActivity() {
		return null;
	}

	@Override
	public void load() {
		

	}

//	static boolean savedOnce = false;
	@Override
	public void save() {
		
		
		
		

	}

	
	class KeyMover implements Runnable {
		
		float dx,dy;
		
		public KeyMover(float dx,float dy){
			this.dx = dx; 
			this.dy = dy;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Coordinate pos = lRobot.getPosition();
			((TeleportRobot)lRobot).setPosition(new Coordinate(pos.x+dx,pos.y+dy));
			
		}
		
	}
	

	void setDisplay() {
		Display d = DisplaySingleton.getDisplay();
		
		
		d.addKeyAction(KeyEvent.VK_E, new KeyMover(0f, 0.001f));
		d.addKeyAction(KeyEvent.VK_W, new KeyMover(0f, 0.01f));
		d.addKeyAction(KeyEvent.VK_Q, new KeyMover(0f, 0.1f));
		d.addKeyAction(KeyEvent.VK_D, new KeyMover(0f, -0.001f));
		d.addKeyAction(KeyEvent.VK_S, new KeyMover(0f, -0.01f));
		d.addKeyAction(KeyEvent.VK_A, new KeyMover(0f, -0.1f));
		
		
		d.addKeyAction(KeyEvent.VK_O, new KeyMover(0.001f ,0f));
		d.addKeyAction(KeyEvent.VK_I, new KeyMover(0.01f  ,0f));
		d.addKeyAction(KeyEvent.VK_U, new KeyMover(0.1f   ,0f));
		d.addKeyAction(KeyEvent.VK_L, new KeyMover(-0.001f,0f));
		d.addKeyAction(KeyEvent.VK_K, new KeyMover(-0.01f ,0f));
		d.addKeyAction(KeyEvent.VK_J, new KeyMover(-0.1f  ,0f));

//		d.addPanel(new DrawPanel(300, 300), "panel1", 0, 0, 1, 1);
//		d.addPanel(new DrawPanel(300, 300), "panel2", 1, 0, 1, 1);
//		d.addPanel(new DrawPanel(300, 300), "panel3", 0, 1, 1, 1);
//		d.addPanel(new DrawPanel(300, 300), "panel4", 1, 1, 1, 1);
//		d.addPanel(new DrawPanel(300, 300), "panel5", 0, 2, 1, 1);
//		d.addPanel(new DrawPanel(300, 300), "panel6", 1, 2, 1, 1);
//		d.addPanel(new DrawPanel(300, 300), "panel7", 0, 3, 1, 1);

		// DrawPanel panel2 = new DrawPanel();
		// panel1.setMinimumSize(new Dimension(300, 300));
		// panel1.setPreferredSize(new Dimension(300, 300));
		// panel1.setBackground(Color.red);

//		PolarDataDrawer qSoftMax = new PolarDataDrawer("Q softmax", modelAwake.softmax.probabilities);
//		PolarDataDrawer affordances = new PolarDataDrawer("Affordances", modelAwake.affordanceGateModule.gates);
//		PolarDataDrawer actionGating = new PolarDataDrawer("2 Actions Gate", modelAwake.twoActionsGateModule.gates);
//		resultProbabilities = new PolarDataDrawer("Resulting Probs", modelAwake.twoActionsGateModule.probabilities);
//		RuntimesDrawer runtimes = new RuntimesDrawer(Integer.parseInt(Globals.getInstance().get("learningEpisodes").toString()), 0, 800);
//		runtimes.doLines = false;
//		
//		
//		RobotDrawer rDrawer = new RobotDrawer((GlobalCameraUniverse)UniverseLoader.getUniverse());
//		
//		PCDrawer pcDrawer 		= new PCDrawer(modelAwake.placeCells.getCells(), modelAwake.placeCells.getActivationPort());
//		PCDrawer pcDrawerAsleep = new PCDrawer(modelAwake.getPlaceCells(), modelAsleep.placeCells.getActivationPort());
//		
//		pathDrawer = new PathDrawer((LocalizableRobot) lRobot);
//		pathDrawer.setColor(Color.red);
//		WallDrawer wallDrawer = new WallDrawer(VirtUniverse.getInstance(), 1);
//		wallDrawer.setColor(GuiUtils.getHSBAColor(0f, 0f, 0f, 1));
//
//		VDrawer vdrawer = new VDrawer(modelAwake.getPlaceCells(), VTable);
////		vdrawer.setRadius(10);
//
//		awakePlots.add(qSoftMax);
//		awakePlots.add(affordances);
//		awakePlots.add(actionGating);
//		awakePlots.add(resultProbabilities);
//		awakePlots.add(pcDrawer);
//
//		asleepPlots.add(pcDrawerAsleep);
//
//		// asleep plots
//		d.addDrawer("universe", "cycle info", new CycleDataDrawer());
//		
//
//		// awake plots
//		d.addDrawer("panel1", "softmax", qSoftMax);
//		d.addDrawer("panel2", "affordances", affordances);
//		d.addDrawer("panel3", "2 acation gate", actionGating);
//		d.addDrawer("panel4", "result", resultProbabilities);
//
//		// always plots
//		d.addDrawer("panel5", "wallsPanel5", wallDrawer);
//		d.addDrawer("panel5", "ValueF", vdrawer);
//		
//		
//		
//		d.addDrawer("panel6", "pcsAwake" , pcDrawer);
//		d.addDrawer("panel6", "pcsAsleep", pcDrawerAsleep);
//		d.addDrawer("panel6", "wallsPanel6", wallDrawer);
//		d.addDrawer("panel6", "paths", pathDrawer, 2);
//		
//		d.addDrawer("panel6", "p6 robot", rDrawer);
//		d.addDrawer("panel5", "p5 robot", rDrawer);
//		d.addDrawer("panel7", "runtimes", runtimes);
		
		
		
		// mapa con paredes + Q plot
		// mapa con paredes + W plot


	}

}
