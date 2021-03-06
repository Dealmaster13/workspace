package uk.ac.cam.oda22.core.environment;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.oda22.core.ListFunctions;
import uk.ac.cam.oda22.core.MathExtended;
import uk.ac.cam.oda22.core.Vector2D;
import uk.ac.cam.oda22.core.logging.Log;
import uk.ac.cam.oda22.pathplanning.Path;

/**
 * @author Oliver
 * 
 */
public class Obstacle {

	public final List<Point2D> points;

	public final List<Line2D> edges;

	public final boolean clockwise;

	public Obstacle(List<Point2D> points) {
		this.points = points;

		this.edges = new ArrayList<Line2D>();

		this.clockwise = isClockwise(this.points);

		for (int i = 0; i < points.size(); i++) {
			int j = (i + 1) % points.size();

			Line2D line = new Line2D.Double();
			line.setLine(this.points.get(i), this.points.get(j));

			// Fail if this line intersects with any of the existing edges.
			for (Line2D l : this.edges) {
				if (MathExtended.strictLineIntersectsLine(line, l)) {
					Log.error("Obstacle is malformed as two or more of its edges intersect one another.");

					return;
				}
			}

			this.edges.add(line);
		}
	}

	/**
	 * Gets the perimeter of the obstacle (polygon) as a path.
	 * 
	 * @return path
	 */
	public Path getPerimeter() {
		Path p = new Path();

		// Stop if there are no points.
		if (this.points.size() == 0) {
			return p;
		}

		for (int i = 0; i < points.size(); i++) {
			p.addPoint(this.points.get(i));
		}

		// Add the first point again if there are more than two points (i.e. the
		// path needs to be closed).
		if (this.points.size() > 2) {
			p.addPoint(this.points.get(0));
		}

		return p;
	}

	public Rectangle2D getBounds() {
		if (this.points.size() == 0) {
			return null;
		}

		double leftX = Double.POSITIVE_INFINITY;
		double rightX = Double.NEGATIVE_INFINITY;
		double bottomY = Double.POSITIVE_INFINITY;
		double topY = Double.NEGATIVE_INFINITY;

		for (Point2D p : this.points) {
			leftX = Math.min(p.getX(), leftX);
			rightX = Math.max(p.getX(), rightX);
			bottomY = Math.min(p.getY(), bottomY);
			topY = Math.max(p.getY(), topY);
		}

		double width = rightX - leftX;
		double height = topY - bottomY;

		return new Rectangle2D.Double(leftX, bottomY, width, height);
	}

	/**
	 * Gets the previous vertex in the obstacle, before v.
	 * 
	 * @param v
	 * @return previous vertex
	 */
	public Point2D getPreviousVertex(Point2D v) {
		int index = 0;

		int s = this.points.size();

		if (s == 1) {
			Log.warning("Obstacle only has one vertex.");
		}

		while (index < s) {
			if (v.equals(this.points.get(index))) {
				return this.points.get((index - 1 + s) % s);
			}

			index++;
		}

		Log.error("Vertex not found.");

		return null;
	}

	/**
	 * Gets the next vertex in the obstacle, after v.
	 * 
	 * @param v
	 * @return next vertex
	 */
	public Point2D getNextVertex(Point2D v) {
		int index = 0;

		int s = this.points.size();

		if (s == 1) {
			Log.warning("Obstacle only has one vertex.");
		}

		while (index < s) {
			if (v.equals(this.points.get(index))) {
				return this.points.get((index + 1) % s);
			}

			index++;
		}

		Log.error("Vertex not found.");

		return null;
	}

	/**
	 * Returns whether or not an obstacle intersects another (requires edge
	 * crossing).
	 * 
	 * @param o
	 * @return true for intersection, false otherwise
	 */
	public boolean strictIntersects(Obstacle o) {
		// TODO: Check if one obstacle lies within the other.

		Path p1 = new Path(this.points);
		Path p2 = new Path(o.points);

		return MathExtended.strictPathIntersectsPath(p1, p2);
	}

	/**
	 * Note that this does not deal with intersections within the polygon. This
	 * primitive implementation should be fine for visibility checking.
	 * 
	 * TODO: Calculate intersections within the polygon.
	 * 
	 * @param l
	 * @return intersection result
	 */
	public ObstacleLineIntersectionResult intersectsLine(Line2D l) {
		double fractionalError = 0.001, absoluteError = 0.001;

		// Check if the line is the same as any of the obstacle edges.
		for (Line2D edge : this.edges) {
			boolean b1 = MathExtended.approxEqual(edge.getP1(), l.getP1(),
					fractionalError, absoluteError)
					&& MathExtended.approxEqual(edge.getP2(), l.getP2(),
							fractionalError, absoluteError);

			boolean b2 = MathExtended.approxEqual(edge.getP1(), l.getP2(),
					fractionalError, absoluteError)
					&& MathExtended.approxEqual(edge.getP2(), l.getP1(),
							fractionalError, absoluteError);

			if (b1 || b2) {
				return ObstacleLineIntersectionResult.EQUAL_LINES;
			}
		}

		// Check if any obstacle edges intersect with the line.
		for (Line2D edge : this.edges) {
			if (MathExtended.strictLineIntersectsLine(l, edge)) {
				return ObstacleLineIntersectionResult.CROSSED;
			}
		}

		// Check if the edge formed spans between any two obstacle vertices.
		for (Point2D p1 : this.points) {
			if (p1.equals(l.getP1())) {
				for (Point2D p2 : this.points) {
					if (p2.equals(l.getP2())) {
						return ObstacleLineIntersectionResult.CROSSED;
					}
				}
			}
		}

		return ObstacleLineIntersectionResult.NONE;
	}

	public boolean touchesObstacle(Obstacle o) {
		// For each vertex of this obstacle, check if it touches one of o's
		// edges.
		for (Point2D p : this.points) {
			for (Line2D e : o.edges) {
				if (MathExtended.loosePointOnLine(p, e)) {
					return true;
				}
			}
		}

		// For each vertex of o, check if it touches one of this obstacle's
		// edges.
		for (Point2D p : o.points) {
			for (Line2D e : this.edges) {
				if (MathExtended.loosePointOnLine(p, e)) {
					return true;
				}
			}
		}

		return false;
	}

	public void addNewVerticesAtIntersectionPoints(Obstacle o) {
		Rectangle2D bounds = this.getBounds();
		Rectangle2D oBounds = o.getBounds();

		// Stop if the obstacles' bounds do not intersect.
		if (!bounds.intersects(oBounds)) {
			return;
		}

		// For each edge, check if it intersects with any of o's edges.
		// Note that we need to iterate through the list backwards as we are
		// adding new elements.
		for (int i = this.edges.size() - 1; i >= 0; i--) {
			Line2D edge = this.edges.get(i);

			// Store the fractions along the edge at which there are
			// intersection points.
			List<Double> intersectionPositions = new ArrayList<Double>();

			// For each of o's edges check if it intersects with edge.
			for (int j = 0; j < o.edges.size(); j++) {
				Line2D oEdge = o.edges.get(j);

				// Calculate the intersection point between the two edges if it
				// exists.
				Point2D intersectionPoint = MathExtended.getIntersectionPoint(
						edge, oEdge);

				if (intersectionPoint != null) {
					// Get the fraction along the edge at which the intersection
					// point lies.
					double fractionAlongLine = MathExtended
							.getFractionAlongLine(intersectionPoint, edge);

					// Add the intersection if it is within the edge.
					if (fractionAlongLine > 0 && fractionAlongLine < 1) {
						intersectionPositions.add(fractionAlongLine);
					}
				}
			}

			// Segment the edge appropriately.
			this.segmentEdge(intersectionPositions, i);
		}
	}

	public Obstacle expandObstacle(double radius) {
		List<Point2D> l = new ArrayList<Point2D>();

		int s = this.points.size();

		if (s == 1) {
			/*
			 * Handle the one-vertex case.
			 */

			List<Point2D> arcPoints = MathExtended.approximateArc(8, 0,
					Math.PI * 2, radius);

			Point2D p = this.points.get(0);

			// Add all but the last arc point (duplicate).
			for (int i = 0; i < arcPoints.size() - 1; i++) {
				l.add(MathExtended.translate(p, arcPoints.get(i)));
			}
		} else if (s == 2) {
			/*
			 * Handle the two-vertex case.
			 */

			Point2D p1 = this.points.get(0);
			Point2D p2 = this.points.get(1);

			double x1 = p1.getX();
			double y1 = p1.getY();
			double x2 = p2.getX();
			double y2 = p2.getY();

			// Get the vector perpendicular to the line at the first endpoint.
			Vector2D t1 = Vector2D.getTangentVector(x1 - x2, y1 - y2, false);

			List<Point2D> arc1Points = MathExtended.approximateArc(4,
					t1.getAngle(), Math.PI, radius);

			// Add all of the arc points.
			for (int i = 0; i < arc1Points.size(); i++) {
				l.add(MathExtended.translate(p1, arc1Points.get(i)));
			}

			// Get the vector perpendicular to the line at the second endpoint.
			Vector2D t2 = Vector2D.getTangentVector(x2 - x1, y2 - y1, false);

			List<Point2D> arc2Points = MathExtended.approximateArc(4,
					t2.getAngle(), Math.PI, radius);

			// Add all of the arc points.
			for (int i = 0; i < arc2Points.size(); i++) {
				l.add(MathExtended.translate(p2, arc2Points.get(i)));
			}
		} else if (s >= 3) {
			/*
			 * Handle the many (>= 3) vertex case.
			 */

			for (int i = 0; i < s; i++) {
				Point2D p = this.points.get(i);
				Point2D prev = this.points.get((i - 1 + s) % s);
				Point2D next = this.points.get((i + 1) % s);

				// Get the vectors for each edge.
				Vector2D v1 = new Vector2D(prev, p);
				Vector2D v2 = new Vector2D(p, next);

				// Get the angular change between the edges.
				double angularChange = MathExtended.getAngularChange(v1, v2);

				// Skip the vertex if there is no angular change.
				if (angularChange != 0) {
					boolean vertexClockwise = angularChange < 0;

					// Get the vectors perpendicular to each edge.
					// If the obstacle is clockwise then the outer normal is on
					// the left, otherwise it is on the right.
					Vector2D t1 = v1.getTangentVector(this.clockwise);
					Vector2D t2 = v2.getTangentVector(this.clockwise);

					// If the obstacle is convex at the vertex, then add an
					// arced expansion, otherwise add a point expansion.
					// If the angular change matches the obstacle's rotational
					// orientation then it is convex at this point.
					if (vertexClockwise == this.clockwise) {
						/*
						 * Add an arced expansion.
						 */

						// Get the start rotation of the arc.
						double startRads = t1.getAngle();

						// Get the number of radians of rotation for the arc.
						// Note that this should be negative in the clockwise
						// case.
						double rads = MathExtended.getAngularChange(startRads,
								t2.getAngle());

						// Approximate an arc with one inner vertex for every 45
						// degrees.
						int numberOfInnerVertices = (int) Math.ceil(Math
								.abs(rads) / Math.PI * 4);

						List<Point2D> arcPoints = MathExtended.approximateArc(
								numberOfInnerVertices, startRads, rads, radius);

						// Add all of the arc points.
						for (int j = 0; j < arcPoints.size(); j++) {
							l.add(MathExtended.translate(p, arcPoints.get(j)));
						}
					} else {
						/*
						 * Add a point expansion
						 */

						// Get the earlier expanded edge parameters.
						Vector2D t3 = t1.setLength(radius);
						Point2D p3 = t3.addPoint(p);

						// Get the later expanded edge parameters.
						Vector2D t4 = t2.setLength(radius);
						Point2D p4 = t4.addPoint(p);

						// Get the intersection point between the two expanded
						// edges.
						Point2D intersectionPoint = MathExtended
								.getExtendedIntersectionPoint(p3, v1, p4, v2);

						if (intersectionPoint == null) {
							Log.warning("Invalid intersection point.");
						} else {
							// Add the intersection point as the sole expansion
							// vertex.
							l.add(intersectionPoint);
						}
					}
				}
			}
		}

		// TODO: Need to normalise the obstacle in case its edges overlap.

		return new Obstacle(l);
	}

	/**
	 * Compute whether or not a polygon, given by its vertex list, is clockwise
	 * or not. This can be determined using the following methodology: 1. Set
	 * angle accumulator n to 0 2. For each edge e: i. Increase n by the
	 * (signed) shortest number of degrees to rotate to the next vertex Take
	 * turning clockwise to be a negative rotation, and counter-clockwise to be
	 * a positive rotation 3. If n is negative then the polygon is clockwise,
	 * otherwise it is counter-clockwise n should be +/-360 by definition of the
	 * sum of the external angles of a polygon This algorithm assumes that the
	 * polygon has no crossing edges. Note that
	 * 
	 * @param l
	 * @return true if the polygon is clockwise, false otherwise
	 */
	private static boolean isClockwise(List<Point2D> l) {
		double n = 0;

		int s = l.size();

		// If the polygon has less than three vertices then it is clockwise.
		if (s < 3) {
			return true;
		}

		for (int i = 0; i < s; i++) {
			Point2D p = l.get(i);
			Point2D prev = l.get((i - 1 + s) % s);
			Point2D next = l.get((i + 1) % s);

			Vector2D v1 = new Vector2D(prev, p);
			Vector2D v2 = new Vector2D(p, next);

			n += MathExtended.getAngularChange(v1, v2);
		}

		if (!MathExtended.approxEqual(Math.abs(n), Math.PI * 2, 0.00001,
				0.00001)) {
			Log.warning("Polygon has invalid external angle.");
		}

		// If n is negative then the polygon is clockwise, otherwise it is
		// counter-clockwise
		return n < 0;
	}

	/**
	 * Divides the edge up into a number of segments.
	 * 
	 * @param fs
	 * @param edgeIndex
	 */
	private void segmentEdge(List<Double> fs, int edgeIndex) {
		if (fs.size() == 0) {
			return;
		}

		// Fail if the point and edge lists do not correlate with each other.
		if (!this.assertPointAndEdgesValid()) {
			return;
		}

		int sE = this.edges.size();

		// Fail if the edge index is invalid.
		if (edgeIndex < 0 || edgeIndex >= sE) {
			Log.error("Edge index is invalid.");

			return;
		}

		/*
		 * Note that the edge index is the same as the point-list index of the
		 * first point in the edge.
		 */

		// Get the edge vector.
		Vector2D v = new Vector2D(this.edges.get(edgeIndex));

		// Get the second point of the edge.
		Point2D lastPoint = this.points.get((edgeIndex + 1) % sE);

		// Remove the existing edge.
		this.edges.remove(edgeIndex);

		List<Double> fsS = ListFunctions.getSortedList(fs);

		// Store the index offset for adding new points and edges.
		int indexOffset = 0;

		double previousFraction = 0;
		Point2D previousPoint = this.points.get(edgeIndex);

		for (int i = 0; i < fsS.size(); i++) {
			double currentFraction = fsS.get(i);

			// Check if the next segment has no length or is invalid.
			if (currentFraction <= previousFraction || currentFraction >= 1) {
				Log.warning("Segment has no length or is invalid.");
			} else {
				// Create the new point along the edge, and add it to the point
				// list.
				Vector2D vF = v.scale(currentFraction);
				Point2D p = vF.addPoint(this.points.get(edgeIndex));
				this.points.add(edgeIndex + indexOffset + 1, p);

				// Create the new edge segment, and add it to the edge list.
				Line2D lF = new Line2D.Double(previousPoint.getX(),
						previousPoint.getY(), p.getX(), p.getY());
				this.edges.add(edgeIndex + indexOffset, lF);

				indexOffset++;

				previousFraction = currentFraction;
				previousPoint = p;
			}
		}

		// Add the last segment.
		Line2D lF = new Line2D.Double(previousPoint.getX(),
				previousPoint.getY(), lastPoint.getX(), lastPoint.getY());
		this.edges.add(edgeIndex + indexOffset, lF);

		// Assert that the point and edge lists correlate with each other.
		this.assertPointAndEdgesValid();
	}

	/**
	 * Checks if the point and edge lists are consistent with each other.
	 */
	private boolean assertPointAndEdgesValid() {
		int sP = this.points.size();
		int sE = this.edges.size();

		// Fail if the point and edge list sizes do not correlate.
		if (sP != sE) {
			Log.error("Point and edge list sizes do not correlate.");

			return false;
		}

		for (int i = 0; i < sP; i++) {
			Point2D currentPoint = this.points.get(i);
			Point2D nextPoint = this.points.get((i + 1) % sP);

			Point2D previousEdgeP2 = this.edges.get((i - 1 + sP) % sP).getP2();
			Point2D currentEdgeP1 = this.edges.get(i).getP1();
			Point2D currentEdgeP2 = this.edges.get(i).getP2();
			Point2D nextEdgeP1 = this.edges.get((i + 1) % sP).getP1();

			// Check if the points match the edge endpoints.
			if (!currentPoint.equals(previousEdgeP2)
					|| !currentPoint.equals(currentEdgeP1)
					|| !nextPoint.equals(currentEdgeP2)
					|| !nextPoint.equals(nextEdgeP1)) {
				Log.error("Point list and edge list are inconsistent.");

				return false;
			}
		}

		return true;
	}
}
