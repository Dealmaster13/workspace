package uk.ac.cam.oda22.core.tethers;

import java.awt.geom.Point2D;

/**
 * @author Oliver
 *
 */
public abstract class Tether {
	
	protected Point2D anchor;
	
	protected double length;
	
	public Tether(Point2D anchor, double length) {
		this.anchor = anchor;
		this.length = length;
	}
	
	public Point2D getAnchor() {
		return this.anchor;
	}
	
	public abstract double getUsedLength();
	
	public abstract Point2D getPositionByDistance(double distance);

}
