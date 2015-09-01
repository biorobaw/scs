//package edu.usf.ratsim.nsl.modules;
//
//import nslj.src.lang.NslDinFloat1;
//import nslj.src.lang.NslDoutFloat1;
//import nslj.src.lang.NslModule;
//
//public class JointStates extends Module {
//
//	public NslDinFloat1 state1;
//	public NslDinFloat1 state2;
//	public NslDoutFloat1 jointState;
//
//	public JointStates(String nslName, NslModule nslParent, int sizeState1,
//			int sizeState2) {
//		super(nslName, nslParent);
//
//		state1 = new NslDinFloat1(this, "state1", sizeState1);
//		state2 = new NslDinFloat1(this, "state2", sizeState2);
//		jointState = new NslDoutFloat1(this, "jointState", sizeState1
//				* sizeState2);
//
//		return;
//	}
//
//	public void simRun() {
//		jointState.set(0);
//		// Iterate over all states
//		int s2Size = state2.getSize();
//		for (int s1 = 0; s1 < state1.getSize(); s1++)
//			if (state1.get(s1) != 0)
//				for (int s2 = 0; s2 < state2.getSize(); s2++) {
//					// The joint state is just the multiplication of the two
//					jointState.set(s1 * s2Size + s2,
//							state1.get(s1) * state2.get(s2));
//				}
//	}
//
//	public int getSize() {
//		return jointState.getSize();
//	}
// }
