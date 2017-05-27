package edu.usf.micronsl.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Observable;

import javax.swing.JPanel;

import edu.usf.micronsl.port.onedimensional.Float1dPort;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;

public class Float1dSeriesPlot extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1421643658397983583L;
	
	private Trace2DLtd trace;
	private Float1dPort port;

	public Float1dSeriesPlot(Float1dPort port){
		Chart2D chart = new Chart2D();
        trace = new Trace2DLtd(port.getData().length); 
        TracePainterDisc discPainter = new TracePainterDisc();
        trace.setTracePainter(discPainter);
        trace.setColor(Color.BLACK);
        chart.addTrace(trace);
        // TODO: Autosize
        setSize(600, 600);
        
        this.port = port;
        
        setLayout(new BorderLayout());
        add(chart);
	}
	
	@Override
	public void paint(Graphics g) {
		for (int i = 0; i < port.getData().length; i++) 
        {
            trace.addPoint(i, port.get(i));
        }
		
		super.paint(g);
	}

	public Dimension getMinimumSize(){
		return new Dimension(600,600);
	}

}
