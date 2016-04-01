package edu.usf.ratsim.nsl.modules.qlearning;
//package edu.usf.ratsim.nsl.modules.qlearning.update;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.util.LinkedList;
//
//import nslj.src.lang.NslDinFloat0;
//import nslj.src.lang.NslDinFloat1;
//import nslj.src.lang.NslDinInt0;
//import nslj.src.lang.NslDoutFloat2;
//import nslj.src.lang.NslModule;
//import edu.usf.experiment.subject.Subject;
//import edu.usf.experiment.universe.Universe;
//import edu.usf.ratsim.support.Configuration;
//
//public class MultiStateProportionalQLReplay extends NslModule implements
//		QLAlgorithm {
//
//	private static final String DUMP_FILENAME = "policy.txt";
//
//	private static final float EPS = 0.2f;
//
//	private static PrintWriter writer;
//	private NslDinFloat0 reward;
//	private NslDinInt0 takenAction;
//	private NslDoutFloat2 value;
//	private NslDinFloat1 statesBefore;
//
//	private float alpha;
//	private float discountFactor;
//	private NslDinFloat1 statesAfter;
//	private int numStates;
//
//	private NslDinFloat1 actionVotesAfter;
//
//	private NslDinFloat1 actionVotesBefore;
//
//	private boolean update;
//
//	private Subject subject;
//
//	private LinkedList<History> history;
//
//	public MultiStateProportionalQLReplay(String nslMain, NslModule nslParent,
//			Subject subject, int numStates, int numActions,
//			float discountFactor, float alpha, float initialValue) {
//		super(nslMain, nslParent);
//
//		this.discountFactor = discountFactor;
//		this.alpha = alpha;
//		this.numStates = numStates;
//		this.subject = subject;
//
//		takenAction = new NslDinInt0(this, "takenAction");
//		reward = new NslDinFloat0(this, "reward");
//		statesBefore = new NslDinFloat1(this, "statesBefore", numStates);
//		statesAfter = new NslDinFloat1(this, "statesAfter", numStates);
//
//		value = new NslDoutFloat2(this, "value", numStates, numActions);
//		File f = new File("policy.obj");
//		if (f.exists()
//				&& Configuration.getBoolean("Experiment.loadSavedPolicy")) {
//
//			try {
//				System.out.println("Reading saved policy...");
//				FileInputStream fin;
//				fin = new FileInputStream(f);
//				ObjectInputStream ois = new ObjectInputStream(fin);
//				value.set((float[][]) ois.readObject());
//			} catch (FileNotFoundException e) {
//				value.set(initialValue);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else {
//			value.set(initialValue);
//		}
//
//		actionVotesAfter = new NslDinFloat1(this, "actionVotesAfter",
//				numActions);
//		actionVotesBefore = new NslDinFloat1(this, "actionVotesBefore",
//				numActions);
//
//		history = new LinkedList<History>();
//
//		update = true;
//	}
//
//	public void simRun() {
//		// Gets the active state as computed at the beginning of the cycle
//		int a = takenAction.get();
//		float r = reward.get();
//		History h = new History(statesBefore.get(), statesAfter.get(),
//				actionVotesAfter.get(), a, r);
//		history.add(h);
//
//		// Updates may be disabled for data log reasons
//		if (update) {
//			// Update only on rewards
//			if (r > 0){
//				// Update whole history many times
//				for (int i = 0; i < 1; i++)
//					for (History hist : history)
//						update(hist);
//				history.clear();
//			}
//			
//		}
//	}
//
//	private void update(History h) {
//		float maxExpectedR = Float.NEGATIVE_INFINITY;
//		for (int action = 0; action < h.getActionsVotes().length; action++)
//			if (maxExpectedR < h.getActionsVotes()[action])
//				maxExpectedR = h.getActionsVotes()[action];
//
//		// Do the update once for each state
//		for (int stateBefore = 0; stateBefore < h.getStatesBefore().length; stateBefore++)
//			// Dont bother if the activation is to small
//			if (h.getStatesBefore()[stateBefore] > EPS)
//				updateLastAction(stateBefore, h.getStatesBefore()[stateBefore],
//						h.getAction(), h.getReward(), maxExpectedR);
//	}
//
//	private void updateLastAction(int sBefore, float sBeforeActivation, int a,
//			float reward, float maxERNextState) {
//		float val = value.get(sBefore, a);
//		float delta = alpha
//				* (reward + discountFactor * (maxERNextState) - val);
//		float newValue = sBeforeActivation * (val + delta)
//				+ (1 - sBeforeActivation) * val;
//
//		if (Float.isInfinite(newValue) || Float.isNaN(newValue))
//			System.out.println("Numeric Error");
//		value.set(sBefore, a, newValue);
//	}
//
//	public void setUpdatesEnabled(boolean enabled) {
//		update = enabled;
//	}
//
//	@Override
//	public void savePolicy() {
//		FileOutputStream fout;
//		try {
//			fout = new FileOutputStream("policy.obj");
//			ObjectOutputStream oos = new ObjectOutputStream(fout);
//			oos.writeObject(value._data);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	@Override
//	public void dumpPolicy(String trial, String groupName, String subName,
//			String rep, int numIntentions, Universe universe, Subject subject) {
//		// TODO Auto-generated method stub
//		
//	}
//
// }