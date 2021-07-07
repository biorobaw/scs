package com.github.biorobaw.scs.simulation.object.maze_elements.walls;

import com.github.biorobaw.scs.simulation.object.SimulatedObject;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public abstract class AbstractWall extends SimulatedObject{

    public abstract boolean intersectsSegment(Vector3D pos1, Vector3D pos2);

}
