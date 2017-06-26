package edu.usf.micronsl.spiking;
import java.util.List;

import java.util.ArrayList;

/**
 *
 * @author eddie
 */

class SpikeEvent {
    public Neuron target;
    public long timeStamp;
    public double impulse;
    public SpikeEvent(Neuron t, long time, double i) {
        target = t;
        timeStamp = time;
        impulse = i;
    }
}

public class SpikeEventMgr {
    List<SpikeEvent> eventList;
    List<GenericNeuron> spikeLedger;
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
                cur.target.feedVoltage(time, cur.impulse, this);
                eventList.remove(i);
            }
            else {
                //check next index in list
                i++;
            }
        }
    }
    public void queueSpike(Neuron n, long time, double impulse) {
        SpikeEvent e0 = new SpikeEvent(n, time, impulse);
        eventList.add(e0);
    }
    
    public void addToLedger(Neuron n) {
        if (n instanceof GenericNeuron) {
            GenericNeuron neuron = (GenericNeuron)n;
            spikeLedger.add(neuron);
        }
    }
    
    public List<GenericNeuron> getLedger() {
        List<GenericNeuron> l = new ArrayList();
        for (int i = 0; i < this.spikeLedger.size(); i++) {
            l.add(this.spikeLedger.get(i));
        }
        this.spikeLedger.clear();
        return l;
    }
    private SpikeEventMgr() {
        eventList = new ArrayList();
        spikeLedger = new ArrayList();
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
