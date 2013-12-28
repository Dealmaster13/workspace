package uk.ac.cam.oda22.core.tethers;

import java.awt.geom.Point2D;
import java.util.List;

import uk.ac.cam.oda22.core.MathExtended;
import uk.ac.cam.oda22.core.logging.Log;
import uk.ac.cam.oda22.pathplanning.Path;

/**
 * @author Oliver
 *
 */
@TetherDetails (
		light = true,
		selfRetracting = true
		)
public class SimpleTether extends Tether {

	private Path path;

	/**
	 * 
	 * @param anchor
	 * @param length
	 * @param fixedPoints
	 * @throws Exception 
	 */
	public SimpleTether(Point2D anchor, double length, Path path) throws Exception {
		super(anchor, length);

		this.path = path;

		double usedLength = getUsedLength();

		// Check that the tether does not exceed its length restriction.
		if (usedLength > length && !MathExtended.ApproxEqual(length, usedLength, 0.001, 0)) {
			throw new Exception("Used length (" + usedLength + ") exceeds maximum length (" + length + ").");
		}
	}

	public List<Point2D> getFixedPoints() {
		return this.path.points;
	}

	@Override
	public double getUsedLength() {
		double totalLength = 0;

		Point2D currentPoint, previousPoint = anchor;

		List<Point2D> points = this.getFixedPoints();

		for (int i = 0; i < points.size(); i++) {
			currentPoint = points.get(i);

			totalLength += previousPoint.distance(currentPoint);

			previousPoint = currentPoint;
		}

		if (totalLength < 0) {
			Log.error("Tether length is negative.");
		}

		return totalLength;
	}

	@Override
	public Point2D getPositionByDistance(double w) {
		// Return null if the distance value is negative.
		if (w < 0) {
			return null;
		}

		double currentW = 0;

		Point2D currentPoint = this.anchor;

		List<Point2D> points = this.getFixedPoints();

		for (int i = 0; i < points.size(); i++) {
			Point2D nextPoint = points.get(i);

			double segmentLength = currentPoint.distance(nextPoint);

			// Use a 1.0001 multiplier in case the distances are not exact.
			if (w < (currentW + segmentLength) * 1.0001) {
				double ratio = (w - currentW) / segmentLength;

				double x = currentPoint.getX() + (nextPoint.getX() - currentPoint.getX()) * ratio;
				double y = currentPoint.getY() + (nextPoint.getY() - currentPoint.getY()) * ratio;

				return new Point2D.Double(x, y);
			}

			currentPoint = nextPoint;

			currentW += segmentLength;
		}

		// Return null if the distance is greater than the tether length.
		return null;
	}

	@Override
	public TetherSegment getTetherSegment(double startW, double endW) {
		if (startW > endW || startW < 0 || endW > this.length) {
			return null;
		}

		Path path = new Path();

		double currentW = 0;

		Point2D currentPoint = this.anchor;

		List<Point2D> points = this.getFixedPoints();

		boolean pointFound = false;

		int index = 1;

		// Find the start point.
		while (index < points.size() && !pointFound) {
			Point2D nextPoint = points.get(index);

			double segmentLength = currentPoint.distance(nextPoint);

			if (startW < (currentW + segmentLength)) {
				double ratio = (startW - currentW) / segmentLength;

				double x = currentPoint.getX() + (nextPoint.getX() - currentPoint.getX()) * ratio;
				double y = currentPoint.getY() + (nextPoint.getY() - currentPoint.getY()) * ratio;

				path.addPoint(new Point2D.Double(x, y));
				
				pointFound = true;
			}
			else {
				currentPoint = nextPoint;

				currentW += segmentLength;
				
				index ++;
			}
		}

		pointFound = false;
		
		// Find the end point.
		while (index < points.size() && !pointFound) {
			Point2D nextPoint = points.get(index);

			double segmentLength = currentPoint.distance(nextPoint);

			// Use a 1.0001 multiplier in case the distances are not exact.
			if (endW < (currentW + segmentLength) * 1.0001) {
				double ratio = (endW - currentW) / segmentLength;

				double x = currentPoint.getX() + (nextPoint.getX() - currentPoint.getX()) * ratio;
				double y = currentPoint.getY() + (nextPoint.getY() - currentPoint.getY()) * ratio;

				path.addPoint(new Point2D.Double(x, y));
				
				pointFound = true;
			}
			else {
				path.addPoint(nextPoint);
				
				currentPoint = nextPoint;

				currentW += segmentLength;
				
				index ++;
			}
		}

		return new SimpleTetherSegment(path);
	}

}
