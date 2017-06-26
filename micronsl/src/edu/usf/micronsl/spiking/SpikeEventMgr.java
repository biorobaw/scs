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
public class SpikeEventMgr {
    List<SpikeEvent> eventList;
    List<LeakyIntegratorSpikingNeuron> spikeLedger;
	private LinkedList<NetPlot> plots;
    private static SpikeEventMgr instance = null;
    
    public static SpikeEventMgr getInstance() {
        if (instance == null) instance = new SpikeEventMgr();
        return instance;
    }
    
    public void process(long time) {
        SpikeEvent cur;
        //iterate through eventList
        int i = 0;
        while (i < eventList.size()) {
            cur = eventList.get(i);
            if (cur.timeStamp == time) {
                //send voltage to neuron and remove from list
                cur.target.input(time, cur.source);
                eventList.remove(i);
                
                for (NetPlot plot : plots)
                	plot.addSpike(cur.source, time);
            }
            else {
                //check next index in list
                i++;
            }
        }
    }
    public void queueSpike(SpikingNeuron src, SpikingNeuron dst, long time) {
        SpikeEvent e0 = new SpikeEvent(src, dst, time);
        eventList.add(e0);
    }
    
    public void addToLedger(SpikingNeuron n) {
        if (n instanceof LeakyIntegratorSpikingNeuron) {
            LeakyIntegratorSpikingNeuron neuron = (LeakyIntegratorSpikingNeuron)n;
            spikeLedger.add(neuron);
        }
    }
    
    public List<LeakyIntegratorSpikingNeuron> getLedger() {
        List<LeakyIntegratorSpikingNeuron> l = new ArrayList();
        for (int i = 0; i < this.spikeLedger.size(); i++) {
            l.add(this.spikeLedger.get(i));
        }
        this.spikeLedger.clear();
        return l;
    }
    private SpikeEventMgr() {
        eventList = new ArrayList();
        spikeLedger = new ArrayList();
        plots = new LinkedList<NetPlot>();
    }

	public void registerPlot(NetPlot plot) {
		plots.add(plot);
	}
}

/*
current issue:
    spike event list handling:
        how to perform while maintaining good performance
        list implementation requires O(n) per time interval
            issue here: unknown how fast n grows
            possible solution: keep list sorted
                considerably more cost effective with a linked list
*/
