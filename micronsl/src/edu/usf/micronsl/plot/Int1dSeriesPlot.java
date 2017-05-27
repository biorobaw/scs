package edu.usf.micronsl.plot;

import java.awt.Color;

import javax.swing.JPanel;

import edu.usf.micronsl.port.onedimensional.Int1dPort;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;

public class Int1dSeriesPlot extends JPanel {

	public Int1dSeriesPlot(Int1dPort port){
		Chart2D chart = new Chart2D();
        ITrace2D trace = new Trace2DLtd(port.getData().length);
        TracePainterDisc discPainter = new TracePainterDisc();
        trace.setTracePainter(discPainter);
        trace.setColor(Color.BLACK);
        chart.addTrace(trace);
        // TODO: Autosize
        chart.setSize(100, 100);
        
        // TODO: why this here?
        //Now to add the points to the trace, since it is static and a one dimensional 
        //Port, I take the current system time and use that as the x value for the plot
        //adding 20 ms each time
        double time = System.currentTimeMillis();
        for (int i = 0; i < port.getData().length; i++) 
        {
            trace.addPoint(time + i, port.getData()[i]);
        }
        
        add(chart);
	}
}
