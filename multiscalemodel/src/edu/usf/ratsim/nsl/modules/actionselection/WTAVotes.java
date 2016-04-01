package edu.usf.ratsim.nsl.modules.actionselection;
//package edu.usf.ratsim.nsl.modules.qlearning.actionselection;
//
//import edu.usf.ratsim.micronsl.FloatArrayPort;
//import edu.usf.ratsim.micronsl.Module;
//import edu.usf.ratsim.nsl.modules.Voter;
//
//public class WTAVotes extends Module implements Voter {
//
//	public float[] actionVote;
//	private FloatArrayPort states;
//	private FloatMatrixPort value;
//
//	public WTAVotes(FloatArrayPort states, FloatMatrixPort value, int numActions) {
//
//		actionVote = new float[numAction];
//		addPort(new FloatArrayPort("votes", actionVote));
//
//		this.states = states;
//		this.value = value;
//	}
//
//	public void simRun() {
//		int s = getActiveState();
//
//		setVotes(s);
//	}
//
//	private void setVotes(int state) {
//		float[] values = new float[numActions];
//		for (int action = 0; action < numActions; action++){
//			values[action] = value.get(state, action);
////			System.out.print(value.get(state, action));
//		}
////		System.out.println();
//		
//		actionVote.set(values);
//	}
//
//	private int getActiveState() {
//		// Winner take all within the layer
//		float maxVal = Float.NEGATIVE_INFINITY;
//		int activeState = -1;
//		for (int i = 0; i < states.getSize(); i++)
//			if (states.get(i) > maxVal) {
//				activeState = i;
//				maxVal = states.get(i);
//			}
//
//		return activeState;
//	}
//
//	@Override
//	public NslDoutFloat1 getVotes() {
//		return actionVote;
//	}
//
// }
