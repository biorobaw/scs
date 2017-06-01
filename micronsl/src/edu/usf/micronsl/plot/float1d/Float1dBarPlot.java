package edu.usf.micronsl.plot.float1d;

import edu.usf.micronsl.port.onedimensional.Float1dPort;
import info.monitorenter.gui.chart.ITracePainter;
import info.monitorenter.gui.chart.traces.painters.TracePainterVerticalBar;

public class Float1dBarPlot extends Float1dChart2dPlot {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Float1dBarPlot(Float1dPort port){
		super(port);
	}

	@Override
	public ITracePainter<?> getPainter() {
		return new TracePainterVerticalBar(getChart());
	}
	
}
