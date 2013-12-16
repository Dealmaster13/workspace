package uk.ac.cam.oda22.core.tethers;

import java.awt.geom.Point2D;
import java.util.List;

import uk.ac.cam.oda22.core.MathExtended;
import uk.ac.cam.oda22.core.logging.Log;

/**
 * @author Oliver
 *
 */
@TetherDetails (
	light = true,
	selfRetracting = true
)
public class SimpleTether extends Tether {

	private List<Point2D> fixedPoints;

	/**
	 * 
	 * @param anchor
	 * @param length
	 * @param fixedPoints
	 * @throws Exception 
	 */
	public SimpleTether(Point2D anchor, double length, List<Point2D> fixedPoints) throws Exception {
		super(anchor, length);
		
		this.fixedPoints = fixedPoints;
		
		double usedLength = getUsedLength();
		
		// Check that the tether does not exceed its length restriction.
		if (usedLength > length && !MathExtended.ApproxEqual(length, usedLength, 0.001, 0)) {
			throw new Exception("Used length (" + usedLength + ") exceeds maximum length (" + length + ").");
		}
	}
	
	public List<Point2D> getFixedPoints() {
		return fixedPoints;
	}
	
	@Override
	public double getUsedLength() {
		double totalLength = 0;
		
		Point2D currentPoint, previousPoint = anchor;
		
		for (int i = 0; i < fixedPoints.size(); i++) {
			currentPoint = fixedPoints.get(i);
			
			totalLength += Point2D.distance(previousPoint.getX(), previousPoint.getY(), currentPoint.getX(), currentPoint.getY());
			
			previousPoint = currentPoint;
		}
		
		if (totalLength < 0) {
			Log.error("Tether length is negative.");
		}
		
		return totalLength;
	}

	@Override
	public Point2D getPositionByDistance(double distance) {
		// Return null if the distance value is negative.
		if (distance < 0) {
			return null;
		}
		
		double currentDistance = 0;
		
		Point2D currentPoint = this.anchor;
		
		for (int i = 0; i < this.fixedPoints.size(); i++) {
			Point2D nextPoint = this.fixedPoints.get(i);
			
			double segmentDistance = currentPoint.distance(nextPoint);
			
			// Use a 1.0001 multiplier in case the distances are not exact.
			if (distance < (currentDistance + segmentDistance) * 1.0001) {
				double ratio = (distance - currentDistance) / segmentDistance;

				double x = currentPoint.getX() + (nextPoint.getX() - currentPoint.getX()) * ratio;
				double y = currentPoint.getY() + (nextPoint.getY() - currentPoint.getY()) * ratio;
				
				return new Point2D.Double(x, y);
			}
			
			currentPoint = nextPoint;
			
			currentDistance += segmentDistance;
		}
		
		// Return null if the distance is greater than the tether length.
		return null;
	}
	
}
