package com.github.biorobaw.scs.robot.modules;


public interface CameraModule {

	// TODO: choose an image format and change the return type
	/**
	 * Returns the image from the camera specified by the id
	 * @param id camera id
	 * @return 
	 */
	public abstract Object getImage(int id);
	
}
