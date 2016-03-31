package edu.usf.micronsl.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.usf.micronsl.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.cartesian.Float1dPortCartesian;

public class Float1dPortCartesianTest {

	private static final float EPS = 1e-6f;

	public static void main(String args[]){
		Random r = new Random();
		
		boolean correct = true;
		for (int t = 0; t < 100 && correct; t++){
			Module dummy = new DummyModule("Dummy module");
			
			// Create 5 1-20 item data
			List<Float1dPort> ports = new LinkedList<Float1dPort>();
			for (int i = 0; i < 5; i++){
				int size = r.nextInt(20) + 1;
				float data[] = new float[size];
				for (int j = 0; j < size; j++)
					// 10% zeros
					if (r.nextFloat() < .9)
						data[j] = r.nextFloat();
					else
						data[j] = 0;
				ports.add(new Float1dPortArray(dummy, data));
			}
			
			Float1dPortCartesian cart = new Float1dPortCartesian(dummy, ports, 0, r.nextBoolean());
			
			float[] getData = cart.getData();
			for (int i = 0; i < cart.getSize() && correct; i++){
				// Compute result according to definition
				float computed = 1;
				for (int j = 0; j < ports.size(); j++){
					// Get the index for the j port
					int sizePrevious = 1;
					for (int k = 0; k < j; k++)
						sizePrevious *= ports.get(k).getSize();
					int indexJ;
					if (sizePrevious > 0)
						indexJ = (i / sizePrevious) % ports.get(j).getSize();
					else
						indexJ = i % ports.get(j).getSize();
					
					computed *= ports.get(j).get(indexJ);
				}
				
				float fromCart = cart.get(i);
				float fromGetData = getData[i];
				// Compare
				correct = correct && (Math.abs(computed - fromCart) < EPS) && Math.abs(computed - fromGetData) < EPS;
			}
		}
		
		if (correct)
			System.out.println("All items were the same");
		else
			System.out.println("There was an error in calculations");
	}
}
