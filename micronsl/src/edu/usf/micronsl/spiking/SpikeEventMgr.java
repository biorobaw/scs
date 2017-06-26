package edu.usf.micronsl.spiking;
import java.util.List;

import edu.usf.micronsl.spiking.neuron.LeakyIntegratorSpikingNeuron;
import edu.usf.micronsl.spiking.neuron.SpikingNeuron;
import edu.usf.micronsl.spiking.plot.NetPlot;

import java.util.ArrayList;
import java.util.LinkedList;

/**
* @author Eduardo Zuloaga
*/
public class SpikeEventMgr implements SpikeListener {
   
	List<SpikeEvent> events;
    private static SpikeEventMgr instance = null;
    
    public static SpikeEventMgr getInstance() {
        if (instance == null) instance = new SpikeEventMgr();
        return instance;
    }
    
    // TODO: this list should be sorted by size
    public void process(long time) {
        SpikeEvent cur;
        //iterate through eventList
        int i = 0;
        while (i < events.size()) {
            cur = events.get(i);
            if (cur.timeStamp == time) {
                //send voltage to neuron and remove from list
                cur.target.input(time, cur.source);
                events.remove(i);
            }
            else {
                //check next index in list
                i++;
            }
        }
    }
    public void queueSpike(SpikingNeuron src, SpikingNeuron dst, long time) {
        SpikeEvent e0 = new SpikeEvent(src, dst, time);
        events.add(e0);
    }
    
    private SpikeEventMgr() {
        events = new ArrayList<SpikeEvent>();
    }

	@Override
	public void spikeEvent(SpikeEvent e) {
		for (SpikingNeuron sn : e.source.getOuputNeurons())
			events.add(new SpikeEvent(e.source, sn, e.timeStamp));
	}
}
