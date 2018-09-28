package edu.usf.experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Random;

import edu.usf.experiment.display.Display;
import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.display.NoDisplay;
import edu.usf.experiment.display.SCSDisplay;
import edu.usf.experiment.log.Logger;
import edu.usf.experiment.log.LoggerLoader;
import edu.usf.experiment.plot.Plotter;
import edu.usf.experiment.plot.PlotterLoader;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.RobotLoader;
import edu.usf.experiment.subject.ModelLoader;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.task.TaskLoader;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.experiment.utils.XMLExperimentParser;
import edu.usf.micronsl.Model;

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
	
	
	
	public static void main(String[] args) {
		
		if (args.length < 4)
		{
			System.out.println("Usage: java edu.usf.experiment [-display]"
					+ "exprimentLayout logPath group individual [ARGNAME ARGVAL]*");			
			System.out.println();
			System.exit(0);
			
		}
		
		System.out.println("[+] Loading Globals");
		String experimentFile = loadGlobals(args);
		Experiment e = new Experiment(experimentFile);
		e.run();

		System.out.println("[+] Finished running");
		System.exit(0);
	}
	
	
	
	

	private List<Trial> trials;
	private List<Task> beforeTasks;
	private List<Task> afterTasks;
	private List<Logger> beforeLoggers;
	private List<Logger> afterLoggers;
	private List<Plotter> beforePlotters;
	private List<Plotter> afterPlotters;
	private Universe universe;
	private Subject subject;
	private boolean makePlots;
	private Robot robot;

	protected Experiment(){
		
	}
	
	/** 
	 * Loads experiment from experiment file, assumes globals have already been loaded
	 * @param experimentFile
	 */
	public Experiment(String experimentFile) {

		//create log folders and store experiment file, maze file and globals
		createAndInitLogFolder(experimentFile);
		
		//load experiment file
		ElementWrapper root = XMLExperimentParser.loadRoot(experimentFile);

		//load required globals
		Globals g = Globals.getInstance();
		String groupName   = (String)g.get("group");
		String subjectName = (String)g.get("subName");	
		String logPath 	   = (String)g.get("logPath");
		
		
		System.out.println("[+] Starting group " + groupName + " individual "+ subjectName + " in log " + logPath);
//		logPath = logPath ;

		
		//Load display
		loadDisplay();

		
		//load universe
		System.out.println("Loading universe...");
		universe = UniverseLoader.getInstance().load(root, logPath + "/");

		//load robot
		System.out.println("Loading robot...");
		robot = RobotLoader.getInstance().load(root, universe);
		robot.startRobot();
		universe.setRobot(robot);
		
		
		//set do plots (global plotting activator / deactivator
		if (root.getChild("plot") != null)
			makePlots = root.getChildBoolean("plot");
		else
			makePlots = true;
		
		
		//set seed
		setSeed(root);

		// Load model
		loadModel(root,groupName,subjectName);

		// Load trials that apply to the subject
		trials = XMLExperimentParser.loadTrials(root, logPath, subject,universe, makePlots);
		
		// Load tasks and plotters
		loadTasks(root);
		
		//Load episode (if loading)
		loadEpisode();
		
		
	}
	



	private ElementWrapper getGroupNode(ElementWrapper root, String groupName) {
		for(ElementWrapper g : root.getChildren("group"))
			if (g.getChildText("name").equals(groupName))
				return g;
		
		return null;
	}

	/***
	 * Runs the experiment for the especified subject. Just goes over trials and
	 * runs them all. It also executes tasks and plotters.
	 */
	public void run() {		
		// Do all before trial tasks
		for (Task task : beforeTasks)
			task.perform(this);

		for (Logger l : beforeLoggers){
			l.log(this);
			l.finalizeLog();
		}
		
		for (Plotter p : beforePlotters)
			p.plot();
		
		//Debug Sleep at start
		if (Debug.sleepBeforeStart) try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
		// Run each trial in order
		for (Trial t : trials)
			t.run();

		// Do all after experiment tasks
		for (Task task : afterTasks)
			task.perform(this);
		
		for (Logger l : afterLoggers){
			l.log(this);
			l.finalizeLog();
		}
		
		for (Plotter p : afterPlotters)
			p.plot();
		
		// Wait for threads
		Plotter.join();

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
	
	
	static public String loadGlobalsFromIndividual(String args[]) {
		
		String logPath = args[0];
		String xmlFile = logPath + File.separator + "experiment.xml";
		int individual = Integer.parseInt(args[1]);

		// Assumes pre-experiment put it in the right place
		ElementWrapper root = XMLExperimentParser.loadRoot(xmlFile);
		
		
		//find groupName and subName
		List<ElementWrapper> groupNodes = root.getChildren("group");
		// Look for the group of the individual to execute
		for (ElementWrapper gNode : groupNodes) {
			String groupName = gNode.getChildText("name");
			int numMembers = gNode.getChildInt("numMembers");
			
			for (int i = 0; i < numMembers; i++){
				// Decrease individual until we get to the one we want
				individual--;
				if (individual < 0){
					// Get the name and create the experiment and run
					String subName = new Integer(i).toString();
					return loadGlobals(new String[] {xmlFile,logPath,groupName, subName});
				}
			}
		}
		
		System.out.println("Error individual not found");
		System.exit(-1);
		return "";
		
	}
	
	static public String loadGlobals(String[] args ) {
		
		Globals g = Globals.getInstance();
		
		
		//check display
		int nextArg = 0;
		boolean display = args[nextArg].equals("-display");
		if (display){
			nextArg++;
		} 
		g.put("display",display );

		
		//get xml file and load globals from xml file
		String xml = args[nextArg++];
		

		//fix logpath if necessary
		String logPath = args[nextArg++];
		if (logPath.endsWith("/"))
			logPath = logPath.substring(0, logPath.length()-1);
		
		//store variables that are always present, 
		//init values might be overwritten by other parts of the code
		g.put("logPath",logPath);
		g.put("group", args[nextArg++]);
		g.put("subName", args[nextArg++]);
		g.put("maze.file",logPath + "/maze.xml");
		g.put("simulationSpeed",9); //init at full speed
		g.put("episode",0);
		
		
		//Load globals from experiment file
		ElementWrapper root = XMLExperimentParser.loadRoot(xml);
		ElementWrapper experimentGlobals = root.getChild("variables");
		if(experimentGlobals!=null)
			for(ElementWrapper child : experimentGlobals.getChildren() ) {
				String varName   = child.getChildText("name"); 
				String value 	 = child.getChildText("value");
				g.put(varName, value);
			}
		
		loadSimControls(root);
		
		
		//Load variables from command line
		for(int i =0; i < (args.length-4)/2;i++){
			g.put(args[nextArg], args[nextArg+1]);
			nextArg+=2;
		}

		
		
		
		return xml;
	}
	
	void loadTasks(ElementWrapper root) {
		String logPath = (String)Globals.getInstance().get("logPath");
		
		System.out.println("[+] Loading Experiment Tasks");
		beforeTasks = TaskLoader.getInstance().load(root.getChild("beforeExperimentTasks"));
		afterTasks = TaskLoader.getInstance().load(root.getChild("afterExperimentTasks"));		
		
		System.out.println("[+] creating loggers, logpath: " + logPath);
		beforeLoggers = LoggerLoader.getInstance().load(root.getChild("beforeExperimentLoggers"),logPath);
		afterLoggers = LoggerLoader.getInstance().load(root.getChild("afterExperimentLoggers"),logPath);
		
		beforePlotters = PlotterLoader.getInstance().load(root.getChild("beforeExperimentPlotters"),logPath);
		afterPlotters = PlotterLoader.getInstance().load(root.getChild("afterExperimentPlotters"),logPath);
	}
	
	void createAndInitLogFolder(String experimentFile) {
		Globals g = Globals.getInstance();
		String logPath = (String)g.get("logPath");
		
		//create log path directories
		System.out.println("[+] Creating directories");
		File file = new File(logPath +"/");
		file.mkdirs();
		
		
		//Copy experiment file
		IOUtils.copyFile(experimentFile, logPath + "/experiment.xml");	
		
		
		//Copy maze file
		ElementWrapper root = XMLExperimentParser.loadRoot(experimentFile);
		String mazeFile = root.getChild("universe").getChild("params").getChildText("maze");
		if (mazeFile != null) IOUtils.copyFile(mazeFile, logPath + "/maze.xml");
		
		
		//Save globals to a file:
		try {
			File f = new File(logPath+"/globals.txt");
			f.getParentFile().mkdirs();
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for(String key : g.global.keySet()){
				bw.write(key + " "  + g.get(key));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	void loadEpisode() {
		
		Globals g = Globals.getInstance();
		if(g.get("loadPath")==null) return;
				
		String t = g.get("loadTrial").toString();
		Integer e = Integer.parseInt(g.get("loadEpisode").toString());

		//Pop trials until we get the trial we want
		for(int i =0;;i++){
			System.out.println(trials.get(0).getName() + " "  + t);
			if(trials.get(0).getName().equals(t)) break;
			else trials.remove(0);
		}
		
		//pop episodes from the trial until we reach the episode being loaded
		for(int i=0;i<e;i++)
			trials.get(0).episodes.remove(0);
		g.put("episode",e);
		
		subject.getModel().load();
		
	}
	
	void loadDisplay() {
		Display displayer;
		if ((Boolean)Globals.getInstance().get("display")){
//			displayer = new PDFDisplay();
			displayer = new SCSDisplay();
		} else {
			displayer = new NoDisplay();
		}
		DisplaySingleton.setDisplay(displayer);
	}

	void setSeed(ElementWrapper root) {
		Long seed = (Long)Globals.getInstance().get("seed");
		if (seed != null) System.out.println("[+] Using seed from xml file");
		else seed = new Random().nextLong();

		RandomSingleton.getInstance().setSeed(seed);
		System.out.println("[+] Using seed " + seed);
	}
	
	void loadModel(ElementWrapper root , String groupName, String subjectName) {
		System.out.println("[+] Loading Model...");
		ElementWrapper modelParams = root.getChild("model");
		System.out.println("gName " + groupName);
		ElementWrapper groupParams = getGroupNode(root, groupName).getChild("params");
		modelParams.merge(root, groupParams);
		Model model = ModelLoader.getInstance().load(modelParams, robot);
		subject = new Subject(subjectName, groupName, model, robot);
	}
	
	static void loadSimControls(ElementWrapper root) {
		ElementWrapper controls = root.getChild("simulationControls");
		if(controls==null) return;
		
		Globals g = Globals.getInstance();		
		if(controls.hasChild("display")) g.put("display",controls.getChildBoolean("display") );
		if(controls.hasChild("simulationSpeed")) g.put("simulationSpeed",controls.getChildInt("simulationSpeed") );
		if(controls.hasChild("seed")) g.put("seed",controls.getChildLong("seed") );
		g.put("syncDisplay", !controls.hasChild("syncDisplay") || controls.getChildBoolean("syncDisplay"));
		g.put("collisionDetection", controls.getChildBoolean("collisionDetection"));
		
		if(controls.hasChild("load")){
			ElementWrapper load = controls.getChild("load");
			String p = load.getChildText("path");
			if (!p.endsWith("/")) p+="/";
			String e = load.getChildText("episode");
			String t = load.getChildText("trial");
						
			g.put("loadPath", p);
			g.put("loadEpisode",e );
			g.put("loadTrial", t);
			g.put("loadSnapshot", p+"g"+g.get("group")+"-s"+g.get("subName")+"-t"+t+"-e"+e+"-");
			
		}
		
		g.put("savePath",g.get("logPath") + "/snapshots/" + (controls.hasChild("saveFile") ? controls.getChildText("saveFile") : "default") + "/");
				
	}
}
