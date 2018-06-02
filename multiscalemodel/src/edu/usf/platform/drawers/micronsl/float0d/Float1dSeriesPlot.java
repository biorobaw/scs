package edu.usf.platform.drawers.micronsl.float0d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import info.monitorenter.gui.chart.traces.painters.TracePainterLine;
import info.monitorenter.util.Range;

/** 
 * This plots the evolution of one particular value in a 1d float port.
 * @author martin
 *
 */
public class Float1dSeriesPlot extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2695029035531174298L;
	private Float1dPort port;
	private double maxSoFar;
	private double minSoFar;
	private Trace2DLtd trace;
	private int time;
	private Chart2D chart;
	private int index;

	public Float1dSeriesPlot(Float1dPort port, int index, String name){
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
        this.index = index;
        
        maxSoFar = 0;
        minSoFar = 0;
        time = 0;
        
        setLayout(new BorderLayout());
        add(chart);
	}
	
	@Override
	public void paint(Graphics g) {
		trace.addPoint(time++, port.get(index));
		
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
