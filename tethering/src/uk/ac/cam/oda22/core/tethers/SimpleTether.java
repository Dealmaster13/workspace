package uk.ac.cam.oda22.core.tethers;

import java.awt.geom.Point2D;
import java.util.List;

import uk.ac.cam.oda22.core.ListFunctions;
import uk.ac.cam.oda22.core.MathExtended;
import uk.ac.cam.oda22.core.logging.Log;
import uk.ac.cam.oda22.pathplanning.Path;

/**
 * @author Oliver
 * 
 */
@TetherDetails(light = true, selfRetracting = true)
public class SimpleTether extends Tether {

	/**
	 * 
	 * @param anchor
	 * @param length
	 * @param fixedPoints
	 * @throws Exception
	 */
	public SimpleTether(Point2D anchor, double length,
			TetherConfiguration configuration) throws Exception {
		super(anchor, length, configuration);
	}

	public List<Point2D> getFixedPoints() {
		return this.configuration.points;
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

				double x = currentPoint.getX()
						+ (nextPoint.getX() - currentPoint.getX()) * ratio;
				double y = currentPoint.getY()
						+ (nextPoint.getY() - currentPoint.getY()) * ratio;

				return new Point2D.Double(x, y);
			}

			currentPoint = nextPoint;

			currentW += segmentLength;
		}

		// Return null if the distance is greater than the tether length.
		return null;
	}

	@Override
	public Point2D getLastPoint() {
		if (configuration.points.size() > 0) {
			return ListFunctions.getLast(this.configuration.points);
		}

		return this.anchor;
	}

	@Override
	public ITetherSegment getTetherSegment(double startW, double endW) {
		// Fail if either of the distance parameters are out of invalid.
		if (startW > endW || startW < 0 || endW > this.length) {
			return null;
		}

		Path path = new Path();

		double currentW = 0;

		Point2D currentPoint = this.anchor;

		List<Point2D> points = this.getFixedPoints();

		boolean pointFound = false;

		// Note that the fixed points list does not include the anchor point.
		int index = 0;

		// Find the start point.
		while (index < points.size() && !pointFound) {
			Point2D nextPoint = points.get(index);

			double segmentLength = currentPoint.distance(nextPoint);

			if (startW < (currentW + segmentLength)) {
				double ratio = (startW - currentW) / segmentLength;

				double x = currentPoint.getX()
						+ (nextPoint.getX() - currentPoint.getX()) * ratio;
				double y = currentPoint.getY()
						+ (nextPoint.getY() - currentPoint.getY()) * ratio;

				path.addPoint(new Point2D.Double(x, y));

				pointFound = true;
			} else {
				currentPoint = nextPoint;

				currentW += segmentLength;

				// If the end of the tether has been reached then check if the
				// start distance is approximately equal to the tether's used
				// length.
				// If the start distance is at the end of the tether then simply
				// add that point to the path and return it.
				if (index == points.size() - 1
						&& MathExtended.approxEqual(startW, currentW, 0.0001,
								0.0001)) {
					double x = currentPoint.getX();
					double y = currentPoint.getY();

					path.addPoint(new Point2D.Double(x, y));

					// If the end distance is not at the end of the tether then
					// produce a warning.
					if (!MathExtended.approxEqual(endW, currentW, 0.0001,
							0.0001)) {
						Log.warning("The given end distance is greater than the tether's used length.");
					}

					return new SimpleTetherSegment(path);
				} else {
					index++;
				}
			}
		}

		// Check if the tether segment has no length, if which case we do not
		// need to search for the end point.
		if (startW == endW) {
			return new SimpleTetherSegment(path);
		}

		pointFound = false;

		// Find the end point.
		while (index < points.size() && !pointFound) {
			Point2D nextPoint = points.get(index);

			double segmentLength = currentPoint.distance(nextPoint);

			// Use a 1.0001 multiplier in case the distances are not exact.
			if (endW < (currentW + segmentLength) * 1.0001) {
				double ratio = (endW - currentW) / segmentLength;

				double x = currentPoint.getX()
						+ (nextPoint.getX() - currentPoint.getX()) * ratio;
				double y = currentPoint.getY()
						+ (nextPoint.getY() - currentPoint.getY()) * ratio;

				path.addPoint(new Point2D.Double(x, y));

				pointFound = true;
			} else {
				path.addPoint(nextPoint);

				currentPoint = nextPoint;

				currentW += segmentLength;

				index++;
			}
		}

		return new SimpleTetherSegment(path);
	}

}
