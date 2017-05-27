package edu.usf.micronsl.plot;

import java.awt.Color;

import javax.swing.JPanel;

import edu.usf.micronsl.port.onedimensional.Int1dPort;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;

public class Int0dSeriesPlot extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2695029035531174298L;

	//TODO: 1d port?
	public Int0dSeriesPlot(Int1dPort aPort){
		Chart2D chart = new Chart2D();
        ITrace2D trace = new Trace2DSimple();
        
        TracePainterDisc discPainter = new TracePainterDisc();
        trace.setTracePainter(discPainter);
        trace.setColor(Color.BLACK);
        chart.addTrace(trace);
        // TODO: fix to autosize
        chart.setSize(100, 100);
        
        // TODO: why this here
        //Now to add the points to the trace, since it is static and a one dimensional 
        //Port, I take the current system time and use that as the x value for the plot
        //adding 20 ms each time
        double time = System.currentTimeMillis();
        for (int i = 0; i < aPort.getData().length; i++) 
        {
            trace.addPoint(time + i, aPort.getData()[i]);
        }
        
        add(chart);
	}
}
