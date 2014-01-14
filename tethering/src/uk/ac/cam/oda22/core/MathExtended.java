package uk.ac.cam.oda22.core;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

import uk.ac.cam.oda22.pathplanning.Path;

/**
 * @author Oliver
 *
 */
public final class MathExtended {

	public static boolean ApproxEqual(double m, double n, double fractionalError, double absoluteError) {
		if (Math.abs(n - m) <= absoluteError) {
			return true;
		}

		if (m != 0 && n != 0) {
			return Math.abs(1 - (m / n)) <= fractionalError;
		}

		return false;
	}

	public static boolean ApproxEqual(Point2D p, Point2D q, double fractionalError, double absoluteError) {
		return ApproxEqual(p.getX(), q.getX(), fractionalError, absoluteError)
				&& ApproxEqual(p.getY(), q.getY(), fractionalError, absoluteError);
	}

	public static boolean ApproxEqual(Line2D l, Line2D m, double fractionalError, double absoluteError) {
		if (ApproxEqual(l.getP1(), m.getP1(), fractionalError, absoluteError)
				&& ApproxEqual(l.getP2(), m.getP2(), fractionalError, absoluteError)) {
			return true;
		}

		if (ApproxEqual(l.getP1(), m.getP2(), fractionalError, absoluteError)
				&& ApproxEqual(l.getP2(), m.getP1(), fractionalError, absoluteError)) {
			return true;
		}

		return false;
	}

	public static double getRadius(double w, double h) {
		return Math.sqrt((w * w) + (h * h));
	}

	/**
	 * Gets the shortest angular change from one angle to another.
	 * 
	 * @param radsFrom
	 * @param radsTo
	 * @return angular change
	 */
	public static double getAngularChange(double radsFrom, double radsTo) {
		return normaliseAngle(radsTo - radsFrom);
	}

	/**
	 * Normalises an angle to take a value between -pi and +pi.
	 * 
	 * @param rads
	 * @return normalised angle
	 */
	public static double normaliseAngle(double rads) {
		double twoPi = 2 * Math.PI;

		double normalisedAngle = rads % twoPi;

		normalisedAngle = (normalisedAngle + twoPi) % twoPi;

		if (normalisedAngle > Math.PI) {
			normalisedAngle -= twoPi;
		}

		return normalisedAngle;
	}

	/**
	 * Checks if 'a' lies between r1 and r2, where r1 and r2 are unordered.
	 * 
	 * @param a
	 * @param r1
	 * @param r2
	 * @return true if 'a' lies between r1 and r2, and false otherwise
	 */
	public static boolean inRange(double a, double r1, double r2, boolean strictInequality) {
		double min = Math.min(r1, r2);
		double max = Math.max(r1, r2);

		if (strictInequality) {
			return a > min && a < max;
		}
		else {
			return a >= min && a <= max;
		}
	}

	public static PointOnLineResult pointOnLine(Point2D p, Line2D l) {
		// Check if the point does not lie on the line.
		if (!l.contains(p)) {
			return PointOnLineResult.NONE;
		}

		// Check if the point is at the first endpoint.
		if (p.equals(l.getP1())) {
			return PointOnLineResult.AT_ENDPOINT;
		}

		// Check if the point is at the second endpoint.
		if (p.equals(l.getP2())) {
			return PointOnLineResult.AT_ENDPOINT;
		}

		return PointOnLineResult.WITHIN_LINE;
	}
	
	public static PointOnLineResult pointOnPath(Point2D point, Path path) {
		List<Line2D> edges = path.getEdges();
		
		if (edges.size() == 0) {
			return PointOnLineResult.NONE;
		}

		Point2D startPoint = edges.get(0).getP1();
		Point2D endPoint = edges.get(edges.size() - 1).getP2();

		// Check if the point lies at one of the ends of the path.
		// Note that the point could still lie within the path.
		boolean pointAtEndPoint = point.equals(startPoint) || point.equals(endPoint);
		
		// For each segment of the path, check if the point lies on it.
		for (int i = 0; i < edges.size(); i++) {
			// Check if the point on the line.
			PointOnLineResult r = pointOnLine(point, edges.get(i));

			if (i == 0 || i == edges.size() - 1) {
				// Check if the point lies within the line if it is one of the end-lines.
				if (r == PointOnLineResult.WITHIN_LINE) {
					return PointOnLineResult.WITHIN_LINE;
				}
			}
			else {
				// Check if the point lies anywhere on the line if it is not one of the end-lines.
				if (r != PointOnLineResult.NONE) {
					return PointOnLineResult.WITHIN_LINE;
				}
			}
		}
		
		return pointAtEndPoint ? PointOnLineResult.AT_ENDPOINT : PointOnLineResult.NONE;
	}

	public static boolean loosePointOnLine(Point2D p, Line2D l) {
		return pointOnLine(p, l) != PointOnLineResult.NONE;
	}

	public static boolean colinear(Line2D l1, Line2D l2) {
		int i = 0;

		if (pointOnLine(l1.getP1(), l2) != PointOnLineResult.NONE) {
			i ++;
		}

		if (pointOnLine(l1.getP2(), l2) != PointOnLineResult.NONE) {
			i ++;
		}

		if (pointOnLine(l2.getP1(), l1) != PointOnLineResult.NONE) {
			i ++;
		}

		if (pointOnLine(l2.getP2(), l1) != PointOnLineResult.NONE) {
			i ++;
		}

		// Return true if at least two endpoints lie on the other line.
		return i >= 2;
	}

	public static LineIntersectionResult intersectsLine(Line2D l1, Line2D l2) {
		// Check if the lines do not intersect.
		if (!l1.intersectsLine(l2)) {
			return LineIntersectionResult.NONE;
		}

		// Check if the lines are colinear.
		if (colinear(l1, l2)) {
			return LineIntersectionResult.COLINEAR;
		}

		// Check if the an endpoint of one line lies on the other line.
		PointOnLineResult r1 = pointOnLine(l1.getP1(), l2);
		PointOnLineResult r2 = pointOnLine(l1.getP2(), l2);
		PointOnLineResult r3 = pointOnLine(l2.getP1(), l1);
		PointOnLineResult r4 = pointOnLine(l2.getP2(), l1);

		if (r1 == PointOnLineResult.AT_ENDPOINT
				|| r2 == PointOnLineResult.AT_ENDPOINT
				|| r3 == PointOnLineResult.AT_ENDPOINT
				|| r4 == PointOnLineResult.AT_ENDPOINT) {
			return LineIntersectionResult.JOINT;
		}
		else if (r1 == PointOnLineResult.WITHIN_LINE
				|| r2 == PointOnLineResult.WITHIN_LINE
				|| r3 == PointOnLineResult.WITHIN_LINE
				|| r4 == PointOnLineResult.WITHIN_LINE) {
			return LineIntersectionResult.A_TOUCHES_B;
		}

		return LineIntersectionResult.CROSS;
	}

	public static boolean strictIntersectsLine(Line2D l1, Line2D l2) {
		return intersectsLine(l1, l2) == LineIntersectionResult.CROSS;
	}

	public static boolean strictLineIntersectsPath(Line2D l, Path p) {
		List<Line2D> pathEdges = p.getEdges();

		// For each path segment, check if it intersects with the line. 
		for (int i = 0; i < pathEdges.size(); i++) {
			Line2D edge = pathEdges.get(i);
			
			if (strictIntersectsLine(edge, l)) {
				return true;
			}

			// Check if the line crosses a joint in the path.
			if (i != pathEdges.size() - 1) {
				// Get the second endpoint of the current path segment.
				Point2D endpoint = edge.getP2();

				// Check if the second endpoint of the current path segment lies within the line.
				PointOnLineResult r = pointOnLine(endpoint, l);

				if (r == PointOnLineResult.WITHIN_LINE) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean strictPathIntersectsPath(Path p1, Path p2) {
		List<Line2D> p1Edges = p1.getEdges();

		// For each segment of the first path, check if it intersects with the second path. 
		for (int i = 0; i < p1Edges.size(); i++) {
			Line2D edge = p1Edges.get(i);
			
			if (strictLineIntersectsPath(edge, p2)) {
				return true;
			}
			
			// Check if a joint in the first path intersects with a joint in the second path.
			if (i != p1Edges.size() - 1) {
				// Get the second endpoint of the current path segment.
				Point2D endpoint = edge.getP2();

				// Check if the endpoint lies within the second path.
				if (pointOnPath(endpoint, p2) == PointOnLineResult.WITHIN_LINE) {
					return true;
				}
			}
		}

		return false;
	}
	
	public static boolean strictPathIntersectsItself(Path p) {
		List<Line2D> edges = p.getEdges();
		
		for (int i = 1; i < p.points.size() - 1; i++) {
			// Get the subpath with indices 0 and i.
			Path subpath = p.getSubpath(0, i);
			
			// Get the next edge in the path.
			Line2D edge = edges.get(i);
			
			// Check if the edge intersects the subpath.
			if (strictLineIntersectsPath(edge, subpath)) {
				return true;
			}
			
			// Check if a joint in the path intersects the subpath.
			if (i != edges.size() - 1) {
				// Get the second endpoint of the current path segment.
				Point2D endpoint = edge.getP2();

				// Check if the endpoint lies within the second path.
				// Note that this also checks for overlap in the path segments.
				if (pointOnPath(endpoint, subpath) == PointOnLineResult.WITHIN_LINE) {
					return true;
				}
			}
		}
		
		return false;
	}

	public static Point2D getCartesian(double radius, double rads) {
		double x = radius * Math.cos(rads);
		double y = radius * Math.sin(rads);

		return new Point2D.Double(x, y);
	}

	/**
	 * Translates point p by position vector t, returning a new point.
	 * 
	 * @param p
	 * @param t
	 * @return translated point
	 */
	public static Point2D translate(Point2D p, Point2D t) {
		return new Point2D.Double(p.getX() + t.getX(), p.getY() + t.getY());
	}

}
