package com.github.biorobaw.scs.robot.modules.distance_sensing;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.robot.RobotModule;
import com.github.biorobaw.scs.simulation.object.maze_elements.walls.Wall;
import com.github.biorobaw.scs.utils.files.XML;

/**
 * A class that implements fake distance sensors.
 * They only wall objects.
 * @author bucef
 *
 */
public class SCSWallDistanceSensors extends RobotModule implements DistanceSensingModule {

	// distance sensor data
	public float[] sensor_angles;
	public int num_sensors;
	public float max_sensing_distance = 100f;
	
	
	// auxialiary variables for computing distances
	static final float pi2 = (float)(Math.PI*2);
	public float[] distances; // array to hold the distances of computed by the sensor
	public float[] current_angles; // angle of sensors relative to orientation
	public float[] cosines;	// cosine of current angles
	public float[] sines; // sines of current angles
	public float[] perpen_distance; // distance of current robot position measured perpendicularly to sensor direction
	public float[] parallel_distance; //  distance of current robot position measured in the direction of the sensor direction
	
	public SCSWallDistanceSensors(XML xml) {
		super(xml);
		if(xml.hasAttribute("max_sensing_distance"))
			max_sensing_distance = xml.getFloatAttribute("max_sensing_distance");
		if(xml.hasAttribute("num_sensors")) {
			num_sensors = xml.getIntAttribute("num_sensors");
			var d_tita = pi2/num_sensors;
			sensor_angles = new float[num_sensors];
			for(int i= 0; i<num_sensors ; i++) sensor_angles[i]= d_tita*i;
		} else {
			sensor_angles = xml.getFloatArrayAttribute("sensor_angles");
			num_sensors = sensor_angles.length;
		}
		
		// create auxiliary variables:
		distances   =new float[num_sensors];
		current_angles = new float[num_sensors];
		cosines = new float[num_sensors];
		sines = new float[num_sensors];
		perpen_distance = new float[num_sensors];
		parallel_distance = new float[num_sensors];
		
	}

	@Override
	public float getDistances(int id) {
		// get current position and the set of walls
		var walls = Experiment.get().getMaze().walls;
		var pos = proxy.getPosition();
		float tita = proxy.getOrientation2D();
		float x = (float)pos.getX();
		float y = (float)pos.getY();
		
		var distance = max_sensing_distance;
		
		var current_angle = sensor_angles[id] + tita;
		var cosine = (float)Math.cos(current_angle);
		var sine = (float)Math.sin(current_angle);
		
		var parallel_distance = x*cosine+y*sine; 
		var perpen_distance = y*cosine-x*sine;
		
		// for each wall update sensor readings
		for (var w_abstract : walls) {
			Wall wall = (Wall)w_abstract;

			// calculate signed distance from the wall line to the robot center
			var line_signed_distance = wall.signed_distance - wall.normal_x*x - wall.normal_y*y;
			
			
				
			// verify there's an intersection by checking both points lie on 
			// different sides of the semi-planes defined by the sensor line
			float perpen_distance_p1 = wall.y1*cosine - wall.x1*sine;
			float perpen_distance_p2 = wall.y2*cosine - wall.x2*sine;
			
			// check sensor intersects wall line
			if(perpen_distance_p1<= perpen_distance && perpen_distance<=perpen_distance_p2 ||
			   perpen_distance_p2<= perpen_distance && perpen_distance<=perpen_distance_p1) {
				
				// check if wall is aligned with sensor
				if(perpen_distance_p1 == perpen_distance_p2) {
					// if aligned, get closest point
					var d1 = wall.x1*cosine + wall.y1*sine - parallel_distance;
					var d2 = wall.x2*cosine + wall.y2*sine - parallel_distance;
					if(0 <= d1 && d1 < distance) distance = d1;
					if(0 <= d2 && d2 < distance) distance = d2;
					
				} else {
					// else, find distance to intersection
					float cos_normal_sensor = cosine*wall.normal_x + sine*wall.normal_y;
					var d = line_signed_distance / cos_normal_sensor;
					if(0 <= d && d < distance) distance = d;
					
				}
			}
		}
		
		
		return distance;
	}

	@Override
	public float[] getDistances() {		
		// get current position and the set of walls
		var walls = Experiment.get().getMaze().walls;
		var pos = proxy.getPosition();
		float tita = proxy.getOrientation2D();
		float x = (float)pos.getX();
		float y = (float)pos.getY();
		
		// init sensor angles and sensor distances:
		for(int i=0; i<num_sensors; i++) {
			distances[i] = max_sensing_distance;
			
			current_angles[i] = sensor_angles[i] + tita;
			cosines[i] = (float)Math.cos(current_angles[i]);
			sines[i] = (float)Math.sin(current_angles[i]);
			
			parallel_distance[i] = x*cosines[i]+y*sines[i]; 
			perpen_distance[i] = y*cosines[i]-x*sines[i];
		}
		
		
		// for each wall update sensor readings
		for (var w_abstract : walls) {
			Wall wall = (Wall)w_abstract;

			// calculate signed distance from the wall line to the robot center
			var line_signed_distance = wall.signed_distance - wall.normal_x*x - wall.normal_y*y;
			
			
			//  check sensor semi-line intersects wall segment
			// (distance from robot to object in the sensor's direction)
			for(int i=0; i <num_sensors; i++) {
				
				// verify there's an intersection by checking both points lie on 
				// different sides of the semi-planes defined by the sensor line
				float perpen_distance_p1 = wall.y1*cosines[i] - wall.x1*sines[i];
				float perpen_distance_p2 = wall.y2*cosines[i] - wall.x2*sines[i];
				
				// check sensor intersects wall line
				if(perpen_distance_p1<= perpen_distance[i] && perpen_distance[i]<=perpen_distance_p2 ||
				   perpen_distance_p2<= perpen_distance[i] && perpen_distance[i]<=perpen_distance_p1) {
					
					// check if wall is aligned with sensor
					if(perpen_distance_p1 == perpen_distance_p2) {
						// if aligned, get closest point
						var d1 = wall.x1*cosines[i] + wall.y1*sines[i] - parallel_distance[i];
						var d2 = wall.x2*cosines[i] + wall.y2*sines[i] - parallel_distance[i];
						if(0 <= d1 && d1 < distances[i]) distances[i] = d1;
						if(0 <= d2 && d2 < distances[i]) distances[i] = d2;
						
					} else {
						// else, find distance to intersection
						float cos_normal_sensor = cosines[i]*wall.normal_x + sines[i]*wall.normal_y;
						var d = line_signed_distance / cos_normal_sensor;
						if(0 <= d && d < distances[i]) distances[i] = d;
						
					}
					
					
				}
				

			}
		}
		
		return distances;
	}

	@Override
	public String getDefaultName() {
		return DistanceSensingModule.super.getDefaultName();
	}

}
