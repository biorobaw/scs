package edu.usf.micronsl.spiking.plot;

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

import edu.usf.micronsl.spiking.SpikeEvent;
import edu.usf.micronsl.spiking.SpikeListener;
import edu.usf.micronsl.spiking.neuron.SpikingNeuron;

/**
 * @author Eduardo Zuloaga
 */
public class NetPlot implements SpikeListener {

	JFreeChart chart;
	XYSeries spikes;
	int cutoffInterval;
	int limit_y;
	int size_x;
	String label;
	String x_label;
	String y_label;
	XYSeriesCollection series;

	private JFreeChart createChart() {
		JFreeChart chart = ChartFactory.createScatterPlot(label, x_label, y_label, series, PlotOrientation.VERTICAL,
				true, true, false);
		return chart;
	}

	public void addSpike(SpikingNeuron neuron, long time) {
		// Only add a spike if pertinent to this plot
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				spikes.add(time, neuron.id);
			}
		});

	}

	public NetPlot(String l, String x, String y, int cutoff, int ybound) {
		label = l;
		x_label = x;
		y_label = y;
		limit_y = ybound;
		spikes = new XYSeries("Spike");
		series = new XYSeriesCollection();
		series.addSeries(spikes);
		chart = createChart();
		cutoffInterval = cutoff;
	}

	public void plotSpikes(long time) {
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

	}

	public JPanel getPanel() {
		return new ChartPanel(chart);
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

}
