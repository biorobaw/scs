package edu.usf.micronsl.plot;

import java.awt.Color;

import javax.swing.JPanel;

import edu.usf.micronsl.port.onedimensional.Float1dPort;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterVerticalBar;

public class Float1dBarPlot extends JPanel {

	public Float1dBarPlot(Float1dPort port){
		Chart2D chart = new Chart2D();
        ITrace2D trace = new Trace2DSimple();
        TracePainterVerticalBar barPainter = new TracePainterVerticalBar(chart);
        trace.setTracePainter(barPainter);
        trace.setColor(Color.BLACK);
        chart.addTrace(trace);
        chart.setSize(100, 100);
        
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
