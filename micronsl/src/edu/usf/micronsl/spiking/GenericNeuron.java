/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usf.micronsl.spiking;

/**
 *
 * @author ezuloaga
 */
public class GenericNeuron extends Neuron {
   
    int[] place;

    public GenericNeuron(
        long time,
        double s_t,
        long d,
        int layer_num,
        int neu_num
    ){
        super(time, s_t, d);
        place = new int[2];
        place[0] = layer_num;
        place[1] = neu_num;
    }

    public int[] getLocation() {
        return this.place;
    }
}
