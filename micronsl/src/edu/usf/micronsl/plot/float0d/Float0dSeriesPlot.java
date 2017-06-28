package edu.usf.micronsl.plot.float0d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import edu.usf.micronsl.port.singlevalue.Float0dPort;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.painters.TracePainterLine;
import info.monitorenter.util.Range;

public class Float0dSeriesPlot extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2695029035531174298L;
	private Float0dPort port;
	private double maxSoFar;
	private double minSoFar;
	private Trace2DLtd trace;
	private int time;
	private Chart2D chart;

	public Float0dSeriesPlot(Float0dPort port, String name){
		chart = new Chart2D();
        trace = new Trace2DLtd(20);
        
        TracePainterLine discPainter = new TracePainterLine();
        trace.setTracePainter(discPainter);
        trace.setColor(Color.BLACK);
        trace.setName(name);
        chart.setBackground(new Color(238,238,238));
        chart.addTrace(trace);
        // TODO: fix to autosize
        chart.setSize(100, 100);
        
        this.port = port;
        
        maxSoFar = 0;
        minSoFar = 0;
        time = 0;
        
        setLayout(new BorderLayout());
        add(chart);
	}
	
	@Override
	public void paint(Graphics g) {
		trace.addPoint(time++, port.get());
		
		double min = trace.getMinY();
		double max = trace.getMaxY();
		maxSoFar = Math.max(max, maxSoFar);
		minSoFar = Math.min(min, minSoFar);
		
		chart.getAxisY().setRangePolicy(new RangePolicyFixedViewport(new Range(maxSoFar, minSoFar)));
		
		super.paint(g);
	}

	@Override
	public Dimension getMinimumSize(){
		return new Dimension(500,200);
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(500,200);
	}
}
