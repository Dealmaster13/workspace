package uk.ac.cam.oda22.coverage.sweeping;

import java.awt.geom.Point2D;

import uk.ac.cam.oda22.core.MathExtended;

/**
 * @author Oliver
 *
 */
public class CurvedSweepSegment implements ISweepSegment {

	public final Point2D centre;
	
	public final double radius;
	
	public final double startRads;
	
	public final double rads;

	/**
	 * Creates a sweep segment which is a segment of a circle with the appropriate parameters.
	 * 
	 * @param centre
	 * @param radius
	 * @param startRads
	 * @param rads
	 */
	public CurvedSweepSegment(Point2D centre, double radius, double startRads, double rads) {
		this.centre = centre;
		this.radius = radius;
		this.startRads = startRads;
		this.rads = rads;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		CurvedSweepSegment s = (CurvedSweepSegment) o;
		
		return this.centre.equals(s.centre)
				&& this.radius == s.radius
				&& this.startRads == s.startRads
				&& this.rads == s.rads;
	}

	@Override
	public Point2D getStartPoint() {
		return MathExtended.translate(centre, MathExtended.getCartesian(this.radius, this.startRads));
	}

	@Override
	public Point2D getEndPoint() {
		return MathExtended.translate(centre, MathExtended.getCartesian(this.radius, this.startRads + this.rads));
	}
	
}
