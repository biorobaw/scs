package com.github.biorobaw.scs.gui.displays.java_fx.drawer.universe;


import com.github.biorobaw.scs.gui.displays.java_fx.PanelFX;
import com.github.biorobaw.scs.gui.displays.java_fx.drawer.DrawerFX;
import com.github.biorobaw.scs.simulation.object.RobotProxy;

import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class RobotDrawer extends DrawerFX {

	private static final float RADIUS = .075f; // in meters
	
	float[] position = new float[] {-1000000f,-1000000f};
	Float angle = 0f;
	RobotProxy robot;
		
	public RobotDrawer(RobotProxy robot) {
		this.robot = robot;
	}


	@Override
	public void updateData() {
		var pos = robot.getPosition();
		position = new float[] {(float)pos.getX(), (float)pos.getY()};
		angle 	 = robot.getOrientation2D();
		
	}

	
	
	@Override
	protected DrawerScene createGraphics(PanelFX panel) {
		return new DrawerGraphhics(panel);
	}

	
	class DrawerGraphhics extends DrawerScene {
		
		final Color default_color = Color.BLACK;
		


		RatGraphics rat = new RatGraphics();
		Tooltip tooltip = new Tooltip();
		
		
		public DrawerGraphhics(PanelFX panel) {
			super(panel);
			root.getChildren().add(rat.rat);
			
			Tooltip.install(rat.rat, tooltip);
			tooltip.setShowDelay(null);
			rat.rat.setOnMouseMoved((e)->{
				 tooltip.setText("(" + position[0] + " ; " + position[1] + " ; " + angle + ")");
			});
		}
		
		
		@Override
		public void update() {
			rat.pos.setX(position[0]);
			rat.pos.setY(position[1]);
			rat.orientation.setAngle(Math.toDegrees(angle));;
		}
		
		class RatGraphics {
			Circle circle = new Circle(0, 0, RADIUS);
			Line   line   = new Line(0, 0, RADIUS, 0);
			Group  rat 	  = new Group(circle, line);
			
			
			Translate pos = new Translate(0,0);
			Rotate orientation = new Rotate(0);
			
			// initialize data structure
			{
				circle.setFill(Color.TRANSPARENT);
				setStroke(default_color);
				setStrokeWidth(0.005);
				rat.getTransforms().addAll(pos, orientation);
			}
			
			void setStroke(Paint paint) {
				line.setStroke(paint);
				circle.setStroke(paint);
			}
			
			void setStrokeWidth(double width) {
				line.setStrokeWidth(width);
				circle.setStrokeWidth(width);
			}
			
		}
		
	}



}
