package edu.usf.experiment.universe;

import java.awt.geom.Rectangle2D;

public interface BoundedUniverse   {

	public Rectangle2D.Float getBoundingRect();

	public void setBoundingRect(Rectangle2D.Float boundingRect);

}
