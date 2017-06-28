package edu.usf.micronsl.spiking.plot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.usf.micronsl.NSLSimulation;
import edu.usf.micronsl.spiking.SpikeEvent;
import edu.usf.micronsl.spiking.SpikeListener;
import edu.usf.micronsl.spiking.neuron.SpikingNeuron;

/**
 * @author Eduardo Zuloaga
 */
public class NetPlot extends JPanel implements SpikeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int MIN_WIDTH = 300;
	private static final int MIN_HEIGHT = 100;

	JFreeChart chart;
	XYSeries spikes;
	int cutoffInterval;
	private int limit_y;
	int size_x;
	String label;
	String xLabel;
	String yLabel;
	XYSeriesCollection series;

	public void addSpike(SpikingNeuron neuron, long time) {
		// Only add a spike if pertinent to this plot
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				spikes.add(time, neuron.id);
			}
		});

	}

	public NetPlot(String label, String xLabel, String yLabel, int cutoff, Set<SpikingNeuron> neurons) {
		super();
		
		this.label = label;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		
		spikes = new XYSeries("Spike");
		series = new XYSeriesCollection();
		series.addSeries(spikes);
		cutoffInterval = cutoff;
		
		limit_y = 0;
		for (SpikingNeuron sn : neurons){
			sn.addSpikeListener(this);
			limit_y = Math.max(limit_y, sn.id);
		}
		limit_y++;
		
		chart = ChartFactory.createScatterPlot(label, xLabel, xLabel, series, PlotOrientation.VERTICAL,
				true, true, false);
		
		ChartPanel cP = new ChartPanel(chart);
		setLayout(new BorderLayout());
		add(cP);
	}

	@Override
	public void paint(Graphics g) {
		long time = NSLSimulation.getInstance().getSimTime();
		XYPlot xyPlot = chart.getXYPlot();
		ValueAxis domain = xyPlot.getDomainAxis();
		domain.setRange(Math.max(time - cutoffInterval, 0), Math.max(time, cutoffInterval));
		domain = xyPlot.getRangeAxis();
		domain.setRange(-1, limit_y);
		

		// for (int i = 0; i < spikeList.size(); i++) {
		// addPoint(cursor, spikeList.get(i).getLocation()[1]);
		// }
		// Remove too old points
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				while (!spikes.isEmpty() && spikes.getDataItem(0).getX().doubleValue() < time - cutoffInterval)
					spikes.remove(0);
			}
		});

		super.paint(g);
	}

	@Override
	public void spikeEvent(SpikeEvent e) {
		// Only add a spike if pertinent to this plot
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				spikes.add(e.time, e.source.id);
			}
		});

	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(MIN_WIDTH, MIN_HEIGHT);
	}

	
}
