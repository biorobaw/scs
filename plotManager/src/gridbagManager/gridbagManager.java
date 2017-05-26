/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridbagManager;

/**
 *
 * @author Mitchell
 */
import edu.usf.micronsl.port.onedimensional.Int1dPort;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import info.monitorenter.gui.chart.traces.painters.TracePainterVerticalBar;
import info.monitorenter.util.Range;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.swing.JFrame;
 
public class gridbagManager 
{
    
    public  void addSeries0dPlot(Container pane, int x, int y, int width, int height,Int1dPort port) 
    {
        GridBagConstraints c = new GridBagConstraints();
        Chart2D chart = createSimpleChart(width,height,port);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.ipadx = width;
        c.ipady = height;
        c.gridx = x;
        c.gridy = y;
        pane.add(chart, c, -1);       
    }
    
    private static Chart2D createSimpleChart(int width, int height, Int1dPort aPort)
    {
        Chart2D chart = new Chart2D();
        ITrace2D trace = new Trace2DSimple();
        
        TracePainterDisc discPainter = new TracePainterDisc();
        trace.setTracePainter(discPainter);
        trace.setColor(Color.BLACK);
        chart.addTrace(trace);
        chart.setSize(width, height);
        
        //Now to add the points to the trace, since it is static and a one dimensional 
        //Port, I take the current system time and use that as the x value for the plot
        //adding 20 ms each time
        double time = System.currentTimeMillis();
        for (int i = 0; i < aPort.getData().length; i++) 
        {
            trace.addPoint(time + i, aPort.getData()[i]);
        }
        return chart;
    }
    
    public  void addSeries0dPlot(Container pane, int x, int y, int width, int height,Float1dPort port) 
    {
        GridBagConstraints c = new GridBagConstraints();
        Chart2D chart = createSimpleChart(width,height,port);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.ipadx = width;
        c.ipady = height;
        c.gridx = x;
        c.gridy = y;
        pane.add(chart, c, -1);       
    }
    
    private static Chart2D createSimpleChart(int width, int height, Float1dPort aPort)
    {
        Chart2D chart = new Chart2D();
        ITrace2D trace = new Trace2DSimple();
        TracePainterDisc discPainter = new TracePainterDisc();
        trace.setTracePainter(discPainter);
        trace.setColor(Color.BLACK);
        chart.addTrace(trace);
        chart.setSize(width, height);
        
        //Now to add the points to the trace, since it is static and a one dimensional 
        //Port, I take the current system time and use that as the x value for the plot
        //adding 20 ms each time
        double time = System.currentTimeMillis();
        for (int i = 0; i < aPort.getData().length; i++) 
        {
            trace.addPoint(time + i, aPort.getData()[i]);
        }
        return chart;
    }
    
    //Create Arrow Plot
    
    //Create Number Plot
    
    public  void addSeries1dPlot(Container pane, int x, int y, int width, int height,Int1dPort port) 
    {
        GridBagConstraints c = new GridBagConstraints();
        Chart2D chart = createDynamicChart(width,height,port);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.ipadx = width;
        c.ipady = height;
        c.gridx = x;
        c.gridy = y;
        pane.add(chart, c, -1);       
    }
    
    private static Chart2D createDynamicChart(int width, int height, Int1dPort aPort)
    {
        Chart2D chart = new Chart2D();
        ITrace2D trace = new Trace2DLtd(aPort.getData().length);
        TracePainterDisc discPainter = new TracePainterDisc();
        trace.setTracePainter(discPainter);
        trace.setColor(Color.BLACK);
        chart.addTrace(trace);
        chart.setSize(width, height);
        
        //Now to add the points to the trace, since it is static and a one dimensional 
        //Port, I take the current system time and use that as the x value for the plot
        //adding 20 ms each time
        double time = System.currentTimeMillis();
        for (int i = 0; i < aPort.getData().length; i++) 
        {
            trace.addPoint(time + i, aPort.getData()[i]);
        }
        return chart;
    }
    
    public  void addSeries1dPlot(Container pane, int x, int y, int width, int height,Float1dPort port) 
    {
        GridBagConstraints c = new GridBagConstraints();
        Chart2D chart = createDynamicChart(width,height,port);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.ipadx = width;
        c.ipady = height;
        c.gridx = x;
        c.gridy = y;
        pane.add(chart, c, -1);       
    }
    
    private static Chart2D createDynamicChart(int width, int height, Float1dPort aPort)
    {
        Chart2D chart = new Chart2D();
        ITrace2D trace = new Trace2DLtd(100);
        TracePainterDisc discPainter = new TracePainterDisc();
        trace.setTracePainter(discPainter);
        trace.setColor(Color.BLACK);
        chart.addTrace(trace);
        chart.setSize(width, height);
        IAxis<?> axisY = chart.getAxisY();
        IAxis<?> axisX = chart.getAxisX();
        axisY.setRangePolicy(new RangePolicyFixedViewport(new Range(0, 1.0)));
        axisX.setRangePolicy(new RangePolicyFixedViewport(new Range(0,aPort.getData().length)));
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask()
        {
            
            
            @Override
            public void run() 
            {
                for(int i = 0; i < aPort.getData().length; i++)
                {
                    trace.addPoint(i,aPort.get(i));
                }
            }
        };
        // Every 20 milliseconds a new value is collected.
        timer.schedule(task, 100, 10);
        
        
        
        return chart;
    }
    
    //Create Polar Plot
    
    public  void addBoxPlot(Container pane, int x, int y, int width, int height,Int1dPort port) 
    {
        GridBagConstraints c = new GridBagConstraints();
        Chart2D chart = createSimpleBarChart(width,height,port);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.ipadx = width;
        c.ipady = height;
        c.gridx = x;
        c.gridy = y;
        pane.add(chart, c, -1);       
    }
    
    private static Chart2D createSimpleBarChart(int width, int height, Int1dPort aPort)
    {
        Chart2D chart = new Chart2D();
        ITrace2D trace = new Trace2DSimple();
        TracePainterVerticalBar barPainter = new TracePainterVerticalBar(chart);
        trace.setTracePainter(barPainter);
        trace.setColor(Color.BLACK);
        chart.addTrace(trace);
        chart.setSize(width, height);
        
        //Now to add the points to the trace, since it is static and a one dimensional 
        //Port, I take the current system time and use that as the x value for the plot
        //adding 20 ms each time
        double time = System.currentTimeMillis();
        for (int i = 0; i < aPort.getData().length; i++) 
        {
            trace.addPoint(time + i, aPort.getData()[i]);
        }
        return chart;
    }
    
    public  void addBoxPlot(Container pane, int x, int y, int width, int height,Float1dPort port) 
    {
        GridBagConstraints c = new GridBagConstraints();
        Chart2D chart = createSimpleBarChart(width,height,port);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.ipadx = width;
        c.ipady = height;
        c.gridx = x;
        c.gridy = y;
        pane.add(chart, c, -1);       
    }
    
    private static Chart2D createSimpleBarChart(int width, int height, Float1dPort aPort)
    {
        Chart2D chart = new Chart2D();
        ITrace2D trace = new Trace2DSimple();
        TracePainterVerticalBar barPainter = new TracePainterVerticalBar(chart);
        trace.setTracePainter(barPainter);
        trace.setColor(Color.BLACK);
        chart.addTrace(trace);
        chart.setSize(width, height);
        
        //Now to add the points to the trace, since it is static and a one dimensional 
        //Port, I take the current system time and use that as the x value for the plot
        //adding 20 ms each time
        double time = System.currentTimeMillis();
        for (int i = 0; i < aPort.getData().length; i++) 
        {
            trace.addPoint(time + i, aPort.getData()[i]);
        }
        return chart;
    }
    
    
}

         

