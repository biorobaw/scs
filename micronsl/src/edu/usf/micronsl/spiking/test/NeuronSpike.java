package edu.usf.micronsl.spiking.test;
import java.util.Timer;
import java.util.TimerTask;

import edu.usf.micronsl.spiking.SpikeEventMgr;

/**
* @author Eduardo Zuloaga
*/
class runSim extends TimerTask {
    NeuronNet network;
    public runSim(NeuronNet nn) {
        network = nn;
    }
    public void run() {
        network.incrementTime();
    }
}

public class NeuronSpike {

    /**
     * @param args the command line arguments
     */
    
    public static SpikeEventMgr sEM;
    
    //simulation properties
    static long period = 1;
    static long end_time = 30;
    static int oscillator_wavelength = 1;
    
    //network properties
    //first layer is fed voltage by oscillator
    //last layer is read as output
    static int[] layers = {10,100,100,20};
    static double[] connectivity = {0.1, 1, 0.1};
    
    //neuron properties
    public static double global_spike_threshold = 0.6;
    public static long global_delay = 1;
    public static double[] global_weight_range = {0, 0.2};
    
    public static void main(String[] args) {
        
        NeuronNet network = new NeuronNet(
            layers.length, 
            layers, 
            connectivity,
            global_spike_threshold,
            global_delay,
            global_weight_range,
            oscillator_wavelength
        );
        
        for (int i = 0; i < 10; i++)
        	network.addOscillator(0, i, 1.0f, 1, .2f);
        
        TimerTask task = new runSim(network);
        Timer timer = new Timer();
        
        timer.schedule(task, 1000, 5);

    }
    
}
