package com.github.biorobaw.scs.gui.utils;


public  class Window  {
	public float x,y,width,height;
	public Window(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	public Window(double x, double y, double w, double h) {
		this((float)x, (float)y, (float)w, (float)h);
	}
	
	public Window copy(){
		return new Window(x,y,width,height);
	}
	
	public void scale(float scale_x, float scale_y, float origin_x, float origin_y) {
		
		x = origin_x + (x-origin_x)*scale_x;
		y = origin_y + (y-origin_y)*scale_y;
		width *= scale_x;
		height *= scale_y;
		
	}
	
	public void scale(float scale, float[] origin) {
		x = origin[0] + (x-origin[0])*scale;
		y = origin[1] + (y-origin[1])*scale;
		width *= scale;
		height *= scale;
	}
	
	public void translate(float dx, float dy) {
		x += dx;
		y += dy;
	}
	
	public void translate(float[] delta) {
		x += delta[0];
		y += delta[1];
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y +"," + width + "," + height+")";
	}
	
}
