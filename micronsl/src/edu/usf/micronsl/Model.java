package edu.usf.micronsl;

import edu.usf.micronsl.module.Module;

public abstract class Model {

	private ModuleSetRunner modulesPre;
	private ModuleSetRunner modulesPost;

	public Model(){
		modulesPre = new ModuleSetRunner();
		modulesPost = new ModuleSetRunner();
	}
	
	public void runPre() {
		modulesPre.simRun();
	}
	
	public void runPost() {
		modulesPost.simRun();
	}
	
	public void addModule(Module m){
		modulesPre.addModule(m);
	}
	
	public void addModulePre(Module m){
		modulesPre.addModule(m);
	}
	
	public void addModulePost(Module m){
		modulesPost.addModule(m);
	}
	
	public Module getModule(String name){
		Module m = modulesPre.getModule(name);
		return m != null ? m : modulesPost.getModule(name);
	}
	
	public void newEpisode(){
		modulesPre.newEpisode();
		modulesPost.newEpisode();
	}
	
	public void newTrial(){
		modulesPre.newTrial();
		modulesPost.newTrial();
	}

	public void endEpisode() {
	}
}
