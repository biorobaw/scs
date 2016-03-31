package edu.usf.experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.RobotLoader;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.SubjectLoader;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.task.TaskLoader;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.experiment.utils.XMLExperimentParser;

/**
 * This holds a set of trials over a group of individuals. Group parameters, and
 * trial parameters are loaded from an xml file. See helloworld.xml for details.
 * 
 * When executed, this class only executes one subject's trials, loading from
 * the xml only the information needed for this subject.
 * 
 * @author gtejera,mllofriu
 * 
 */
public class Experiment implements Runnable {

	private List<Trial> trials;
	private List<Task> beforeTasks;
	private List<Task> afterTasks;
	private Universe universe;
	private Subject subject;

	protected Experiment(){
		
	}
	
	/** 
	 * This constructor performs all file operations in addition to experimental setup
	 * @param experimentFile
	 * @param logPath
	 * @param groupName
	 * @param subjectName
	 */
	public Experiment(String experimentFile, String logPath, String groupName,
			String subjectName) {
		logPath = logPath + "/";
		
		System.out.println("[+] Creating directories");
		File file = new File(logPath);
		file.mkdirs();
		
		IOUtils.copyFile(experimentFile, logPath + "/experiment.xml");
		ElementWrapper root = XMLExperimentParser.loadRoot(experimentFile);
		
		String mazeFile = root.getChild("universe").getChild("params")
				.getChildText("maze");
		IOUtils.copyFile(mazeFile, logPath + "/maze.xml");

		setup(root, logPath, groupName, subjectName);
	}
	
	/**
	 * This constructor assumes all files (experiment.xml, maze.xml, the folder) have been put
	 * into place and just setups the experiment.
	 * @param root
	 * @param logPath
	 * @param groupName
	 * @param subName
	 */
	public Experiment(ElementWrapper root, String logPath, String groupName,
			String subName) {
		setup(root, logPath, groupName, subName);
	}

	private void setup(ElementWrapper root, String logPath, String groupName,
			String subjectName) {
		//Get globals:
		Globals g = Globals.getInstace();
		
		// System.out.println(System.getProperty("java.class.path"));
		System.out.println("[+] Starting group " + groupName + " individual "
				+ " in log " + logPath);

		// logPath = logPath + File.separator + groupName + File.separator
		// + subjectName + File.separator;
		logPath = logPath + "/";

		PropertyHolder props = PropertyHolder.getInstance();
		props.setProperty("log.directory", logPath);
		props.setProperty("group", groupName);
		props.setProperty("subject", subjectName);
		
		props.setProperty("maze.file", logPath + "/maze.xml");

		universe = UniverseLoader.getInstance().load(root, logPath);

		Robot robot = RobotLoader.getInstance().load(root);

		long seed;
		if (root.getChildText("seed") != null) {
			seed = root.getChildLong("seed");
			System.out.println("[+] Using seed from xml file");
		} else {
			seed = new Random().nextLong();
		}
		RandomSingleton.getInstance().setSeed(seed);
		System.out.println("[+] Using seed " + seed);

		// Load the subject using reflection and assign name and group
		//previously, if feederOrder is defined, set the property in the xml:
		if (g.get("feederOrder")!=null)
				root.getChild("model").getChild("params").
					 getChild("feederOrder").setText((String)g.get("feederOrder"));
		subject = SubjectLoader.getInstance().load(subjectName, groupName,
				root.getChild("model"), robot);

		System.out.println("[+] Model created");

		// Load trials that apply to the subject
		trials = XMLExperimentParser.loadTrials(root, logPath, subject,
				universe);

		// Load tasks and plotters
		ElementWrapper params = root;
		beforeTasks = TaskLoader.getInstance().load(
				params.getChild("beforeExperimentTasks"));
		afterTasks = TaskLoader.getInstance().load(
				params.getChild("afterExperimentTasks"));
	}

	/***
	 * Runs the experiment for the especified subject. Just goes over trials and
	 * runs them all. It also executes tasks and plotters.
	 */
	public void run() {
		// Do all before trial tasks
		for (Task task : beforeTasks)
			task.perform(this);

		if (Debug.sleepBeforeStart)
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// Run each trial in order
		for (Trial t : trials)
			t.run();

		// Do all after trial tasks
		for (Task task : afterTasks)
			task.perform(this);
	}

	public static void main(String[] args) {
		if (args.length < 4)
		{
			System.out.println("Usage: java edu.usf.experiment "
					+ "exprimentLayout logPath group individual [ARGNAME ARGVAL]*");
			
			System.out.println();
			System.out.println("Possible ARGNAMES and ARGVALS:");
			System.out.println("feederFile file - file of `x y` coordinates indicating feeder position");
			System.out.println("nFoodStopCondition int - experiment end after eating 'int' times");
			System.out.println("feederOrder x1,x2,... - indicates order in which feeders are visited");
			System.out.println("startPosition x,y,theta - indicates rat's starting position");
			
		}

		//set global variables:
		Globals g = Globals.getInstace();
		g.put("logPath",args[1] + "/");
		g.put("groupName", args[2]);
		g.put("subName", args[3]);
		
		int nextVar = 4;
		for(int i =0; i < (args.length-4)/2;i++){
			g.put(args[nextVar], args[nextVar+1]);
			nextVar+=2;
		}
		
		//Save globals to a file:
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]+"/globals.txt"));
			for(String key : g.global.keySet()){
				bw.write(key + " "  + g.get(key));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		
		Experiment e = new Experiment(args[0],args[1] + "/" , args[2], args[3]);
		e.run();

		System.exit(0);
	}

	public Universe getUniverse() {
		return universe;
	}

	public Subject getSubject() {
		return subject;
	}
	
	public void setUniverse(Universe univ){
		this.universe = univ;
	}
}
