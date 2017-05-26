/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arrowplot;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxisScalePolicy;
import info.monitorenter.gui.chart.IPointPainter;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.axis.AAxis;
import info.monitorenter.gui.chart.axis.AxisLinear;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterDate;
import info.monitorenter.gui.chart.pointpainters.PointPainterDisc;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterVerticalBar;
import info.monitorenter.gui.chart.views.ChartPanel;
import info.monitorenter.util.Range;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * Demonstrates advanced features of static charts in jchart2d.
 * <p>
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 * 
 */
public final class arrowPlot {

  /**
   * Application startup hook.
   * <p>
   * 
   * @param args
   *          ignored
   * 
   * @throws ParseException
   *           if something goes wrong.
   * 
   */
  public static void main(final String[] args) throws ParseException {
    // Create a chart:
    Chart2D chart = new Chart2D();
    
    // Obtain the basic default axes: 
    IAxis<?> axisX = chart.getAxisX();
    IAxis<?> axisY = chart.getAxisY();
    
    // Feature: Grids:
    chart.setGridColor(Color.LIGHT_GRAY);
    axisX.setPaintGrid(true);
    axisY.setPaintGrid(true);

    // Create an ITrace:
    ITrace2D trace = new Trace2DSimple();
    // Add the trace to the chart:
    chart.addTrace(trace);
    
    // Feature: trace painters: You are also able to specify multiple ones!
    trace.setTracePainter(new TracePainterVerticalBar(4, chart));
    
    // Feature: trace color. 
    trace.setColor(Color.ORANGE);
    
    // Feature: Axis title font. 
    Font titleFont = UIManager.getDefaults().getFont("Label.font").deriveFont(14f).deriveFont(
        Font.BOLD);
    IAxis.AxisTitle axisTitle = axisY.getAxisTitle();
    axisTitle.setTitleFont(titleFont);
    
    // Feature: axis title.
    axisTitle.setTitle("hoppelhase");
    
    // Feature: axis formatter.
    axisY.setFormatter(new LabelFormatterDate(new SimpleDateFormat()));
    
    // Feature: axis title (again).
    axisTitle = axisX.getAxisTitle();
    axisTitle.setTitle("emil");
    axisTitle.setTitleFont(titleFont);
    
    // Feature: range policy for axis. 
    axisX.setRangePolicy(new RangePolicyFixedViewport(new Range(0, 500)));
    
    // Feature: turn on tool tips (recommended for use in static mode only): 
    chart.setToolTipType(Chart2D.ToolTipType.VALUE_SNAP_TO_TRACEPOINTS);
    
    // Feature: turn on highlighting: Two steps enable it on the chart and set a highlighter for the trace: 
    Set<IPointPainter<?>> highlighters = trace.getPointHighlighters();
    highlighters.clear();
    trace.addPointHighlighter(new PointPainterDisc(20));
    chart.enablePointHighlighting(true);
    
    // Add all points, as it is static:
    double high = System.currentTimeMillis();
    for (double i = 0; i < 20; i++) {
      trace.addPoint(i * 10, high);
      high += 1000 * 50;

    }

    // Hack: Close the box by using empty axes:
    AAxis<IAxisScalePolicy> axisXTop = new AxisLinear<IAxisScalePolicy>(new LabelFormatterDate(new SimpleDateFormat("")));
    axisXTop.setPaintScale(false);
    AAxis<IAxisScalePolicy> axisYRight = new AxisLinear<IAxisScalePolicy>(new LabelFormatterDate(new SimpleDateFormat("")));
    axisYRight.setPaintScale(false);
    chart.setAxisXTop(axisXTop, 0);
    chart.setAxisYRight(axisYRight,0);
    
    // Make it visible:
    // Create a frame.
    JFrame frame = new JFrame("AdvancedStaticChart");
    // add the chart to the frame:
    frame.getContentPane().add(new ChartPanel(chart));
    frame.setSize(600, 600);
    // Enable the termination button [cross on the upper right edge]:
    frame.addWindowListener(new WindowAdapter() {
      /**
       * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
       */
      @Override
      public void windowClosing(final WindowEvent e) {
        System.exit(0);
      }
    });
    frame.setVisible(true);
  }

  /**
   * Utility constructor.
   * <p>
   */
  private arrowPlot() {
    super();
  }
}