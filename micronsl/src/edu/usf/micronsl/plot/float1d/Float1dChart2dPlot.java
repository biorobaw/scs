package edu.usf.micronsl.plot.float1d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import edu.usf.micronsl.port.onedimensional.Float1dPort;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITracePainter;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.painters.TracePainterFill;
import info.monitorenter.util.Range;

public abstract class Float1dChart2dPlot extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Trace2DLtd trace;
	private Float1dPort port;
	private double maxSoFar;
	private double minSoFar;
	private Chart2D chart;

	public Float1dChart2dPlot(Float1dPort port){
		chart = new Chart2D();
        trace = new Trace2DLtd(port.getSize());
        trace.setTracePainter(getPainter());
        trace.setColor(Color.BLACK);
        chart.addTrace(trace);
        chart.setSize(100, 100);
        
        this.port = port;
        
        maxSoFar = 0;
        minSoFar = 0;
        
        setLayout(new BorderLayout());
        add(chart);
	}
	
	

	@Override
	public void paint(Graphics g) {
		for (int i = 0; i < port.getData().length; i++) 
        {
            trace.addPoint(i, port.get(i));
        }
		
		double min = trace.getMinY();
		double max = trace.getMaxY();
		maxSoFar = Math.max(max, maxSoFar);
		minSoFar = Math.min(min, minSoFar);
		
		chart.getAxisY().setRangePolicy(new RangePolicyFixedViewport(new Range(maxSoFar, minSoFar)));
		
		super.paint(g);
	}

	public Dimension getMinimumSize(){
		return new Dimension(600,600);
	}

	public Chart2D getChart() {
		return chart;
	}

	public abstract ITracePainter<?> getPainter();
}

