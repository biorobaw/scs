package edu.usf.micronsl;

import edu.usf.micronsl.module.Module;

public class Model {

	private ModuleSetRunner modules;
	
	private InitialModule initialModule = new InitialModule();
	private FinalModule finalModule = new FinalModule();

	public Model(){
		modules = new ModuleSetRunner();
		modules.addModule(initialModule);
		modules.addModule(finalModule);
		finalModule.addPreReq(initialModule);
	}
	
	public void run() {
		modules.simRun();
	}
	
	
	public void addModule(Module m){
		m.addPreReq(initialModule);
		finalModule.addPreReq(m);
		modules.addModule(m);
	}
	
	
	
	public Module getModule(String name){
		Module m = modules.getModule(name);
		return m;
	}
	
	public void newEpisode(){
		modules.newEpisode();
	}
	
	public void newTrial(){
		modules.newTrial();
	}

	public void endTrial() {
	}
	
	public void endEpisode() {
	}
	
	public void initialTask(){};
	public void finalTask(){};
	
	private class InitialModule extends Module{
		
		public InitialModule() {
			super("InitialTask");
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			initialTask();
			
		}

		@Override
		public boolean usesRandom() {
			return true;
		}
		
	}
	
	private class FinalModule extends Module{
		
		public FinalModule() {
			super("FinalTask");
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			finalTask();
			
		}

		@Override
		public boolean usesRandom() {
			return true;
		}
		
	}
	
	public void save(){
		
	}
	
	public void load(){
		
	}
}
