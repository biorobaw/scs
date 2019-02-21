package edu.usf.ratsim.model.taxi.discrete;


import edu.usf.experiment.display.Display;
import edu.usf.experiment.display.NoDisplay;
import edu.usf.experiment.display.PDFDisplay;
import edu.usf.experiment.display.drawer.WallDrawer;
import edu.usf.experiment.display.drawer.discrete.GridDrawer;
import edu.usf.ratsim.nsl.modules.cell.DiscretePlaceCell;
import edu.usf.ratsim.nsl.modules.cell.SizeNDiscretePlaceCell;
import edu.usf.vlwsim.discrete.DiscreteVirtualUniverse;
import edu.usf.vlwsim.discrete.FourNAbsDirDiscreteRobot;

public class SoftStateDraw {

	private static final int SIZE = 5;

	public static void main(String[] args){
		Display.setDisplay(new NoDisplay());
		// Must execute from the scs folder
		DiscreteVirtualUniverse dvu = new DiscreteVirtualUniverse(SIZE, SIZE, "logs/cellpaint/");
		FourNAbsDirDiscreteRobot robot = new FourNAbsDirDiscreteRobot(dvu);
		
		dvu.addWall(0,0, 0,SIZE);
		dvu.addWall(0,SIZE, SIZE,SIZE);
		dvu.addWall(SIZE,SIZE, SIZE,0);
		dvu.addWall(SIZE,0, 0,0);
		
		DiscretePlaceCell pc = new SizeNDiscretePlaceCell(2,2,0);
		
//		dvu.addWall(2,1,2,4);
		
		PDFDisplay pdfd = new PDFDisplay("logs/cellpaint/");
		pdfd.setupUniversePanel(dvu);
		
		pdfd.addDrawer("universe","DPCD",new DPCDrawer(dvu, pc, robot));
		pdfd.addDrawer("universe","grid",new GridDrawer(dvu));
		pdfd.addDrawer("universe","walls",new WallDrawer(dvu, 4));

		
		pdfd.repaint();
	}
}
