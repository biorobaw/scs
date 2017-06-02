package edu.usf.ratsim.model.taxi.discrete;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point3f;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dPortSparseConcatenate;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.ratsim.nsl.modules.celllayer.DiscretePlaceCellLayer;
import edu.usf.ratsim.nsl.modules.celllayer.PlaceCellLayer;

/**
 * Concatenates DiscretePlaceCellLayer modules. The normal output is
 * concatenated by a Float1dSparseConcat port. This class also provides the
 * getActive functionality used for debuging.
 * 
 * @author martin
 *
 */
public class DiscretePlaceCellLayerCombination extends Module implements PlaceCellLayer {

	private Float1dPortSparseConcatenate port;
	private List<DiscretePlaceCellLayer> layers;

	public DiscretePlaceCellLayerCombination(String name, List<DiscretePlaceCellLayer> layers) {
		super(name);

		// A float sparse port takes care of concatenating the individual porst
		List<Float1dSparsePortMap> ports = new LinkedList<Float1dSparsePortMap>();
		for (DiscretePlaceCellLayer layer : layers)
			ports.add((Float1dSparsePortMap) layer.getOutPort("output"));
		port = new Float1dPortSparseConcatenate(this, ports);
		addOutPort("output", port);
		
		// Add dependencies to the layers
		for (DiscretePlaceCellLayer layer : layers)
			addPreReq(layer);

		this.layers = layers;
	}

	@Override
	public Map<Integer, Float> getActive(Point3f position) {
		Map<Integer, Float> active = new HashMap<Integer, Float>();
		int sizeOffset = 0;
		for (DiscretePlaceCellLayer layer : layers) {
			Map<Integer, Float> layerActive = layer.getActive(position);
			for (Integer s : layerActive.keySet()) {
				active.put(s + sizeOffset, layerActive.get(s));
			}
			sizeOffset += layer.getCells().size();
		}
		return active;
	}

	@Override
	public void run() {
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	@Override
	public Float1dPort getActivationPort() {
		return port;
	}

	@Override
	public float getMaxActivation() {
		float sum = 0;
		for (DiscretePlaceCellLayer l : layers)
			sum += l.getMaxActivation();
		return sum;
	}
	
}
