package edu.usf.experiment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import edu.usf.experiment.display.Display;
import edu.usf.experiment.display.NoDisplay;
import edu.usf.experiment.display.SCSDisplay;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
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
	
	private enum RunLevel  {PreExperiment,Experiment,PostExperiment,AllPhases};
	
	static Globals g = Globals.getInstance();
	
	
	

	private List<Trial> trials = new LinkedList<>();
	private List<Task> beforeTasks = new LinkedList<>();
	private List<Task> afterTasks = new LinkedList<>();
	
	private Universe universe;
	private Subject  subject;
	private Robot 	 robot;

//	protected Experiment(){
//		
//	}
	
	/** 
	 * Loads experiment from experiment file, assumes globals have already been loaded
	 * @param experimentFile
	 */
	public Experiment(ElementWrapper root) {	
		//Display log path
		String logPath 	   = (String)g.get("logPath");
		System.out.println("[+] Logpath: " + logPath);
		
		//get run level and create log folders if executing pre experiment
		RunLevel runLevel = (RunLevel) g.get("runLevel");
		if(runLevel == RunLevel.PreExperiment || runLevel == RunLevel.AllPhases)
				createAndInitLogFolder();
		
		//set seed
		setSeed(root);
		
		//	Load display - must load before universe
		loadDisplay();
		
		//load universe
		System.out.println("Loading universe...");
		universe = Universe.load(root, logPath + "/");
		
		//load robot
		System.out.println("Loading robot...");
		robot = Robot.load(root, universe);
		robot.startRobot();
		universe.setRobot(robot);
		
		// Load experiment tasks
		System.out.println("[+] Loading Experiment Tasks");
		if(runLevel == RunLevel.PreExperiment || runLevel == RunLevel.AllPhases)
			beforeTasks = Task.loadTask(root.getChild("beforeExperimentTasks"));
		if(runLevel == RunLevel.PostExperiment || runLevel == RunLevel.AllPhases)
			afterTasks = Task.loadTask(root.getChild("afterExperimentTasks"));	;
		

		//load information specific to the execution of the rat
		if(runLevel==RunLevel.Experiment || runLevel==RunLevel.AllPhases) {
			
			//print group and subject
			String groupName   = (String)g.get("group");
			String subjectName = (String)g.get("subName");	
			System.out.println("[+] Starting group " + groupName + " individual "+ subjectName + " in log " + logPath);
			

			// Load model
			loadModel(root,groupName,subjectName);
			
			// Load trials that apply to the subject
			trials = XMLExperimentParser.loadTrials(root, logPath, subject,universe);

			//Load episode (if loading)
			loadEpisode();
		}
		
	}
	



	private ElementWrapper getGroupNode(ElementWrapper root, String groupName) {
		for(ElementWrapper group : root.getChildren("group"))
			if (group.getChildText("name").equals(groupName))
				return group;
		
		return null;
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
	
	
	
	
	

	
	void createAndInitLogFolder() {
		String logPath = (String)g.get("logPath");
		
		//create log path directories
		System.out.println("[+] Creating directories");
		
		File file = new File(logPath +"/");
		file.mkdirs();
		
		
		//Copy experiment file
		String experimentFile = g.get("experiment").toString();
		IOUtils.copyFile(experimentFile, logPath + "/experiment.xml");	
		
		//Copy configuration file
		String configFile = g.get("configFile").toString();
		IOUtils.copyFile(configFile, logPath + "/config.txt");	
		
		//Copy maze file
		ElementWrapper root = XMLExperimentParser.loadRoot(experimentFile);
		String mazeFile = root.getChild("universe").getChild("params").getChildText("maze");
		if (mazeFile != null) IOUtils.copyFile(mazeFile, logPath + "/maze.xml");
		
		
//		//Save globals to a file:
//		try {
//			File f = new File(logPath+"/globals.txt");
//			f.getParentFile().mkdirs();
//			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
//			for(String key : g.global.keySet()){
//				bw.write(key + " "  + g.get(key));
//				bw.newLine();
//			}
//			bw.close();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
	}

	void loadEpisode() {
		
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
		trials.get(0).startingEpisode = e;
//		g.put("episode",e);
		
		subject.getModel().load();
		
	}
	
	void loadDisplay() {
		Display displayer;
		if ((Boolean)g.get("display")){
//			displayer = new PDFDisplay();
			displayer = new SCSDisplay();
		} else {
			displayer = new NoDisplay();
		}
		Display.setDisplay(displayer);
	}

	void setSeed(ElementWrapper root) {
		Long seed = (Long)g.get("seed");
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
		Model model = Subject.load(modelParams, robot);
		subject = new Subject(subjectName, groupName, model, robot);
	}
	
	static void loadSimControls(ElementWrapper root) {
		ElementWrapper controls = root.getChild("simulationControls");
		if(controls==null) return;
		
		if(controls.hasChild("display")) g.put("display",controls.getChildBoolean("display") );
		if(controls.hasChild("simulationSpeed")) SimulationControl.setSimulationSpeed(controls.getChildInt("simulationSpeed")); 
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
	

	/***
	 * Runs the experiment for the especified subject. Just goes over trials and
	 * runs them all. It also executes tasks and plotters.
	 */
	public void run() {					
		
		//perform before experiment tasks
		for (Task task : beforeTasks) task.perform(Universe.getUniverse(),this.getSubject());

		//Debug Sleep at start
		if (Debug.sleepBeforeStart) try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		// Run each trial in order
		for (Trial t : trials) t.run();

		//perform after experiment tasks
		for (Task task : afterTasks) task.perform(Universe.getUniverse(),this.getSubject());
		
		//signal end experiment
		for (Task task : beforeTasks) task.endEpisode();
		for (Task task : afterTasks) task.endExperiment();

	}
	
	
	
	
	
	public static void main(String[] args) {
	
		System.out.println("[+] Loading Globals");
		Experiment e = new Experiment(loadGlobals(processCommandInput(args),
										g.get("baseLogPath").toString()));		
		e.run();
		
		System.out.println("[+] Finished running");
		System.exit(0);
	}
	
	
	static public String[] processCommandInput(String args[]) {
		if(args.length > 3) return args;
		
		//Get configFile from the arguments, and the configId
		String configFile = args[0];
		int configId = Integer.parseInt(args[1]);
		String baseLogPath = args[2];
		
		//trim logpath
		if (baseLogPath.endsWith("/"))
			baseLogPath = baseLogPath.substring(0, baseLogPath.length()-1);
		
		//store command arguments in global
		g.put("baseLogPath", baseLogPath);
		g.put("configId", configId);
		g.put("configFile", configFile);
		
		//get specified configuration from the config set
		try (Stream<String> lines = Files.lines(Paths.get(configFile))) {

			//get lines iterator:
			var iterator = lines.iterator();
			
			//get the columns
			String columns = iterator.next();
			
			//skip to the line
			for(int i=0;i<configId;i++) iterator.next();
			
			//get config values:
			String values = iterator.next();
		    		    
		    //get tokens and trim them:
		    String[] valueTokens  = values.split("\t");	
		    String[] columnTokens = columns.split("\t");
		    
		    if(valueTokens.length != columnTokens.length) {
		    	System.err.println("ERROR: The number of values in the config does not match the number of columns");
		    	System.exit(-1);
		    }
		    
		    String[] tokens = new String[2*valueTokens.length];
		    for(int i=0;i<valueTokens.length;i++) {
		    	
		    	tokens[2*i] = columnTokens[i].trim();
		    	tokens[2*i+1] = valueTokens[i].trim();		    	
		    }
		    for(int i=0;i<tokens.length;i++)
		    	if(tokens[i].charAt(0)=='"') tokens[i] = tokens[i].substring(1, tokens[i].length()-1);
		    
		    return tokens;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
	    return null;
		
	}
	
	
	static public ElementWrapper loadGlobals(String[] args, String configLogPath ) {
		
	    for(int i=0; i+1<args.length; i=i+2) 
	    	g.put(args[i], args[i+1]);
	    
	    //check required fields were included:
	    
	    //check if run level was defined
	    String runLevelAux = (String)g.get("runLevel");
	    if(runLevelAux==null) {
	    	System.err.println("Run level was not specified for the configuration");
	    	System.exit(-1);
	    } 
	    RunLevel runLevel = RunLevel.values()[Integer.parseInt(runLevelAux)];
	    g.put("runLevel", runLevel);
	    
	    //check if config was defined:
	    if(g.get("config")==null) {
	    	System.err.println("config was not specified for the configuration");
	    	System.exit(-1);
	    }
	    
	    //check if experiment file was defined
	    if(g.get("experiment")==null) {
	    	System.err.println("experiment was not specified for the configuration");
	    	System.exit(-1);
	    }
	    
	    
	    //set logPath:
	    g.put("logPath", configLogPath +"/" +g.get("config"));
	    
	    
	    //check if running a rat or only executing pre or post experiment tasks
	    if(runLevel == RunLevel.Experiment || runLevel == RunLevel.AllPhases) {
	    	//check if group was defined
		    if(g.get("group")==null) {
		    	System.err.println("group was not specified for the configuration");
		    	System.exit(-1);
		    }
		    
		  //check if subName was defined
		    if(g.get("subName")==null) {
		    	System.err.println("subName was not specified for the configuration");
		    	System.exit(-1);
		    }
	    	
	    }

	    //check if display was defined:
	    String displayAux = (String)g.get("display");
	    if(displayAux!=null) g.put("display",Boolean.parseBoolean(displayAux));
	    
	    //define other globals
	    g.put("maze.file",g.get("logPath") + "/maze.xml");
	    g.put("episode",-1);		    
	    
	    
		//Load globals from experiment file
		ElementWrapper root = XMLExperimentParser.loadRoot(g.get("experiment").toString());
		ElementWrapper variables = root.getChild("variables");
		if(variables!=null)
			for(ElementWrapper child : variables.getChildren() ) {
				String varName   = child.getName(); 
				String value 	 = child.getText();
				g.put(varName, value);
			}
	    
		loadSimControls(root);
		    
		return root;
		


	}
	
	
	
	
}
