package edu.usf.micronsl.spiking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.usf.micronsl.spiking.neuron.InputSpikingNeuron;

/**
 * @author Eduardo Zuloaga
 */
public class SpikeEventMgr implements SpikeListener {

	Map<Long, Set<SpikeEvent>> events;
	private static SpikeEventMgr instance = null;

	public static SpikeEventMgr getInstance() {
		if (instance == null)
			instance = new SpikeEventMgr();
		return instance;
	}

	public void process(long time) {
		// iterate through eventList
		if (events.containsKey(time)){
			for (SpikeEvent e : events.get(time)) {
				// send voltage to neuron and remove from list
				e.target.input(time, e.source);
			}
			events.remove(time);
		}
		
	}

	private SpikeEventMgr() {
		events = new HashMap<Long, Set<SpikeEvent>>();
	}

	@Override
	public synchronized void spikeEvent(SpikeEvent e) {
		long arrivalTime = e.time + e.source.delay;

		if (!events.containsKey(arrivalTime))
			events.put(arrivalTime, new HashSet<SpikeEvent>());

		for (InputSpikingNeuron dest : e.source.getOuputNeurons())
			events.get(arrivalTime).add(new SpikeEvent(e.source, dest, arrivalTime));
	}
}
