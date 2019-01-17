package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.util.List;
import java.util.Map;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.experiment.subject.interfaces.ActivityLoggerSubject;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import platform.simulatorVirtual.robots.PuckRobot;


public class TSPSubjectFrance extends Subject implements ActivityLoggerSubject {

	public float step;
	public float leftAngle;
	public float rightAngle;
	
	public TSPModelFrance model;
	
	public PuckRobot robot;

	static 
	{
		System.loadLibrary("vcomp120");
		System.loadLibrary("msvcr120");
		System.loadLibrary("msvcp120");


			System.loadLibrary("vcruntime140");
		System.loadLibrary("concrt140");
			System.loadLibrary("msvcp140");


		System.loadLibrary("vcomp140");

	
		System.loadLibrary("tbb");
		System.loadLibrary("tbbmalloc");

		System.loadLibrary("libimalloc");
		System.loadLibrary("libiomp5md");
		System.loadLibrary("mkl_core");
		System.loadLibrary("mkl_intel_thread");
		System.loadLibrary("mkl_sequential");
		System.loadLibrary("mkl_tbb_thread");
		System.loadLibrary("mkl_def");
		System.loadLibrary("mkl_avx");
		System.loadLibrary("mkl_avx2");
		System.loadLibrary("mkl_avx512");
		System.loadLibrary("mkl_avx512_mic");

		System.loadLibrary("mkl_mc");
		System.loadLibrary("mkl_vml_mc2");
		System.loadLibrary("mkl_mc3");
		System.loadLibrary("mkl_rt");
		System.loadLibrary("mkl_vml_cmpt");
		System.loadLibrary("mkl_vml_def");








		System.loadLibrary("hdf5");

		System.loadLibrary("icudt56");
		System.loadLibrary("icuin56");
		System.loadLibrary("icuio56");
		System.loadLibrary("icuuc56");
		System.loadLibrary("zlib1");
		System.loadLibrary("libexpat");


		System.loadLibrary("libmwfl");
		System.loadLibrary("libmwfoundation_usm");
		System.loadLibrary("libmwi18n");
		System.loadLibrary("libmwresource_core");

		System.loadLibrary("libut");
		System.loadLibrary("libmat");
		System.loadLibrary("libmx");

		System.loadLibrary("cudart64_100");
		System.loadLibrary("cublas64_100");

		System.loadLibrary("boost_chrono-vc140-mt-1_56");
		System.loadLibrary("boost_date_time-vc140-mt-1_56");
		System.loadLibrary("boost_filesystem-vc140-mt-1_56");
		System.loadLibrary("boost_log-vc140-mt-1_56");
		System.loadLibrary("boost_regex-vc140-mt-1_56");
		System.loadLibrary("boost_serialization-vc140-mt-1_56");
		System.loadLibrary("boost_signals-vc140-mt-1_56");
		System.loadLibrary("boost_thread-vc140-mt-1_56");
		System.loadLibrary("boost_system-vc140-mt-1_56");



		System.loadLibrary("boost_chrono-vc140-mt-1_62");
		System.loadLibrary("boost_date_time-vc140-mt-1_62");
		System.loadLibrary("boost_filesystem-vc140-mt-1_62");
		System.loadLibrary("boost_thread-vc140-mt-1_62");
		System.loadLibrary("boost_program_options-vc140-mt-1_62");
		System.loadLibrary("boost_serialization-vc140-mt-1_62");
		System.loadLibrary("boost_system-vc140-mt-1_62");
		System.loadLibrary("boost_zlib-vc140-mt-1_62");
		System.loadLibrary("boost_bzip2-vc140-mt-1_62");
		System.loadLibrary("boost_log-vc140-mt-1_62");
		System.loadLibrary("boost_regex-vc140-mt-1_62");
		System.loadLibrary("boost_log_setup-vc140-mt-1_62");
		System.loadLibrary("boost_iostreams-vc140-mt-1_62");
		System.loadLibrary("boost_mpi-vc140-mt-1_62");
		
		System.loadLibrary("Backend");
		System.loadLibrary("GPU");
		System.loadLibrary("CPU");

		System.loadLibrary("Helper");
		System.loadLibrary("Core");
		System.loadLibrary("Initializer");
		System.loadLibrary("Loop");
		System.loadLibrary("Measurement");
		System.loadLibrary("Mutator");
		System.loadLibrary("Reservoir");
		System.loadLibrary("Scheduler");
		System.loadLibrary("Simulator");
		System.loadLibrary("Decoder");
		System.loadLibrary("Encoder");
		System.loadLibrary("Model");

		System.loadLibrary("Network");

		System.loadLibrary("Engine");
		System.loadLibrary("Remote");
		System.loadLibrary("Distributed");
		System.loadLibrary("Local");
	
		System.loadLibrary("Presenter");
		System.loadLibrary("TRN4CPP");
		System.loadLibrary("TRN4JAVA");
	}	

	
	public TSPSubjectFrance(String name, String group,
			ElementWrapper params, Robot robot) {
		super(name, group, params, robot);

		this.robot = (PuckRobot)robot;
		
		
		model = new TSPModelFrance(params, this, this.robot);
	}
	
	@Override
	public void stepCycle() {
		model.simRun();
//		setHasEaten(false);
		VirtUniverse vu = VirtUniverse.getInstance();
		vu.render(true);
	}
	

	@Override
	public void newEpisode() {
		Globals.getInstance().put("done",false);
		model.newEpisode();
		this.clearTriedToEAt();
		this.setHasEaten(false);
	}

	@Override
	public void endEpisode() {
		// TODO Auto-generated method stub
		super.endEpisode();
		
		model.endEpisode();
		
	}
	
	@Override
	public void newTrial() {
		model.newTrial();
	}



	

	public List<PlaceCell> getPlaceCells() {
		return model.getPlaceCells();
	}


	public Map<Integer, Float> getPCActivity() {
		return model.getCellActivation();
	}

	

}
