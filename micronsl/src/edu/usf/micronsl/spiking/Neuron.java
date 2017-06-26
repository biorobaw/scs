package edu.usf.micronsl.spiking;
import java.util.List;

import java.util.ArrayList;

/**
 *
 * @author eddie
 */
public abstract class Neuron {
   List<Neuron> outputNeurons;
   List<Float> weights;
   double volts;
   double spike_threshold;
   long lastUpdated;
   long delay;
   long last_spike;
   
   public String  metadata[] = new String[]{};
   
   public Neuron(long time, double s_t, long d) {
       //neuron classes:
       outputNeurons = new ArrayList<>();
       weights = new ArrayList<>();
       volts = 0;
       spike_threshold = s_t;
       delay = d;
       lastUpdated = time;
   }
   public void attachNeuron(Neuron n, double w) {
       outputNeurons.add(n);
       weights.add(new Float(w));
   }
   private void neuronDecay(long time, double lambda) {
       //should be called when pertinent. e.g., neuron receiving voltage
       //lambda must be < 1.0
       if (time == lastUpdated) {
           return;
       }
       if (volts <= 0.001) {
           volts = 0.0;
           return;
       }
       double deltaT = time - lastUpdated;
       double v1 = volts * Math.pow(lambda, deltaT);
       volts = v1;
       lastUpdated = time;
   }
   public void feedVoltage(long time, double inVolts, SpikeEventMgr sEM) {
       //simple voltage feed. public but should mainly be used only by event mgr
       neuronDecay(time, 0.8);
       lastUpdated = time;
       volts += inVolts;
       
       //check for spiking behavior
       //if currently spiking, do nothing to it
       if (last_spike < time) {
            if (volts >= spike_threshold) {
                last_spike = time;
                sEM.addToLedger(this);
                volts = 0.0;
                //queue voltage signals to all outputs
                for (int i = 0; i < outputNeurons.size(); i++) {
                     sEM.queueSpike(outputNeurons.get(i),
                         time + outputNeurons.get(i).delay,
                         weights.get(i));
                }
            }
       }
       else {
           volts = 0.0;
       }
       return;
   }


}
