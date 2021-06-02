package com.github.biorobaw.scs.gui.displays.java_fx.drawer.plot;


import java.text.DecimalFormat;

import com.github.biorobaw.scs.gui.displays.java_fx.PanelFX;
import com.github.biorobaw.scs.gui.displays.java_fx.drawer.DrawerFX;
import com.github.biorobaw.scs.gui.utils.Scaler;
import com.github.biorobaw.scs.gui.utils.Window;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;


public abstract class Plot extends DrawerFX {
	
	public Window view_coordinates = new Window(-1+0.05,-1+0.05,2-0.1,2-0.1); // leave small margin
	public Window plot_coordinates;
	public Scaler plot_to_view;
	public Scaler view_to_plot;
	
	protected double minx, miny, maxx, maxy;
	
	boolean x_in_range;
	boolean y_in_range;
	
	String title_x = "x";
	String title_y = "y";

	
	public Plot() {
		resize_plot(0, 0, 1, 1);
	}
	
	public Plot(double minx, double miny, double maxx, double maxy) {
		resize_plot(minx, miny, maxx, maxy);
	}
	
	public void resize_plot(double minx, double miny, double maxx, double maxy) {
		double w = maxx - minx;
		double h = maxy - miny;
		// add 5% margin to window on each side (left, right, top, bottom)
		plot_coordinates = new Window(minx - w*0.05, miny - h*0.05, w*1.1, h*1.1);
		
		// obs, by default scaler flips y coordinates since world and panel y coordinates are inverted
		// thus we need to flip them back
		plot_to_view = new Scaler(plot_coordinates, view_coordinates, false)
				.flip_y_output(view_coordinates.y, view_coordinates.y + view_coordinates.height);

		
		
		view_to_plot = plot_to_view.inverse();
				
		x_in_range = minx <= 0 && 0 <= maxx;
		y_in_range = miny <= 0 && 0 <= maxy;
		this.minx = minx;
		this.miny = miny;
		this.maxx = maxx;
		this.maxy = maxy;
	}

	
	
	public abstract class PlotScene extends DrawerScene {

		protected float sminx = plot_to_view.scaleX(minx);
		protected float smaxx = plot_to_view.scaleX(maxx);
		protected float sminy = plot_to_view.scaleY(miny);
		protected float smaxy = plot_to_view.scaleY(maxy);
		
		protected double x_axis_y_coord = y_in_range ? plot_to_view.scaleY(0) : sminy;
		protected double y_axis_x_coord = x_in_range ? plot_to_view.scaleX(0) : sminx;
		protected Line line_x_axis = new Line(sminx, x_axis_y_coord, smaxx, x_axis_y_coord);
		protected Line line_y_axis = new Line(y_axis_x_coord, sminy, y_axis_x_coord, smaxy);
		
		
		protected Label label_x_axis = new Label(title_x);
		protected Label label_y_axis = new Label(title_y);
		
		DecimalFormat format = new DecimalFormat("0.##");
		protected Label label_min_x = new Label(format.format(minx));
		protected Label label_max_x = new Label(format.format(maxx));
		protected Label label_min_y = new Label(format.format(miny));
		protected Label label_max_y = new Label(format.format(maxy));
		
		protected Label[] all_labels = new Label[] {label_x_axis, label_y_axis, 
				label_min_x, label_max_x, label_min_y, label_max_y
		};
		protected Label[] x_labels = new Label[] { label_x_axis, label_min_x, label_max_x };
		protected Label[] y_labels = new Label[] { label_y_axis, label_min_y, label_max_y };
		
		public Pane axes = new Pane(line_x_axis, line_y_axis);
		public Pane data = new Pane();
		public Pane labels = new Pane(all_labels);
		
		protected double pixel_x = view_to_plot.scaleDistanceX(0.005f); // approximation of a pixel's x dimension 
		protected double pixel_y = view_to_plot.scaleDistanceY(0.005f); // approximation of a pixel's y dimension 
		
		public double font_size = 0.11;
		
		public PlotScene(PanelFX panel) {
			super(panel);
			setUnits(axes, Units.LOCAL);
			setUnits(data, Units.LOCAL);
			setUnits(labels, Units.LOCAL);
			data.getTransforms().add(PanelFX.getAffine(plot_to_view));

			root = new StackPane(axes, data , labels);

			
			// set fonts and label positions
			for(var l : all_labels) l.setFont(new Font(font_size));
			set_label_position();
			
			// set line width
			line_x_axis.setStrokeWidth(0.005);
			line_y_axis.setStrokeWidth(0.005);
			line_x_axis.setStroke(Color.BLUE);
			line_y_axis.setStroke(Color.BLUE);
			 
			
		}

		
		void set_label_position() {
			// first invert y coordinates (since plot and panel y coordinates are inverted)
			// and center them around coordinate 0,0
			for(var l : all_labels) {
				l.setScaleY(-1);
				l.translateXProperty().bind(l.widthProperty().divide(-2));
			}
			
			// NOTE: by this time text is oriented like this (centered over y coordinate, on top of x):
			//              0 coordinate
			//	  	 text in| center
			//   ------------------------- 0 coordinate
			
			// rotate y labels:
			for( var l : y_labels) l.setRotate(90); // after this y labels centered in x axis with bottom of text on y axis
			
			// translate x labels to their respective positions:
			var buffer = 0.03 ;
			var t = font_size*1 + buffer;
			label_x_axis.setLayoutX((sminx+smaxx)/2);
			label_min_x.setLayoutX(sminx);
			label_max_x.setLayoutX(smaxx);
			
			label_x_axis.setLayoutY(sminy - t);
			label_min_x.setLayoutY(sminy - t);
			label_max_x.setLayoutY(sminy - t);
			
			// translate y labels to their respective positions:
			t = buffer;
			label_y_axis.setLayoutX(sminx - t);
			label_min_y.setLayoutX(sminx - t);
			label_max_y.setLayoutX(sminx - t);
			
			label_y_axis.setLayoutY((sminy+smaxy)/2);
			label_min_y.setLayoutY(sminy);
			label_max_y.setLayoutY(smaxy);	
			
		}
		
	}


	


		
	
}
