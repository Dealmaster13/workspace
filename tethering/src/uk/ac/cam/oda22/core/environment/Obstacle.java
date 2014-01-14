package uk.ac.cam.oda22.core.environment;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.oda22.core.MathExtended;
import uk.ac.cam.oda22.core.logging.Log;

/**
 * @author Oliver
 *
 * TODO: Implement expansion of an obstacle for non-point robots.
 *
 */
public class Obstacle {
	
	public final List<Point2D> points;
	
	public final List<Line2D> edges;

	public Obstacle(List<Point2D> points) {
		this.points = points;
		
		this.edges = new ArrayList<Line2D>();
		
		for (int i = 0; i < points.size(); i++) {
			int j = (i + 1) % points.size();
			
			Line2D line = new Line2D.Double();
			line.setLine(this.points.get(i), this.points.get(j));
			
			// Fail if this line intersects with any of the existing edges.
			for (Line2D l : this.edges) {
				if (MathExtended.strictIntersectsLine(line, l)) {
					Log.error("Obstacle is malformed as two or more of its edges intersect one another.");
					
					return;
				}
			}
			
			this.edges.add(line);
		}
	}

	/**
	 * Note that this does not deal with intersections within the polygon.
	 * This primitive implementation should be fine for visibility checking.
	 * 
	 * TODO: Calculate intersections within the polygon.
	 * 
	 * @param l
	 * @return
	 */
	public ObstacleLineIntersectionResult intersectsLine(Line2D l) {
		double fractionalError = 0.001, absoluteError = 0.001;

		// Check if the line is the same as any of the obstacle edges.
		for (Line2D edge : this.edges) {
			boolean b1 = MathExtended.ApproxEqual(edge.getP1(), l.getP1(), fractionalError, absoluteError)
					&& MathExtended.ApproxEqual(edge.getP2(), l.getP2(), fractionalError, absoluteError);
			
			boolean b2 = MathExtended.ApproxEqual(edge.getP1(), l.getP2(), fractionalError, absoluteError)
					&& MathExtended.ApproxEqual(edge.getP2(), l.getP1(), fractionalError, absoluteError);
			
			if (b1 || b2) {
				return ObstacleLineIntersectionResult.EQUAL_LINES;
			}
		}

		// Check if any obstacle edges intersect with the line.
		for (Line2D edge : this.edges) {
			if (MathExtended.strictIntersectsLine(l, edge)) {
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
		// For each vertex of this obstacle, check if it touches one of o's edges.
		for (Point2D p : this.points) {
			for (Line2D e : o.edges) {
				if (MathExtended.loosePointOnLine(p, e)) {
					return true;
				}
			}
		}

		// For each vertex of o, check if it touches one of this obstacle's edges.
		for (Point2D p : o.points) {
			for (Line2D e : this.edges) {
				if (MathExtended.loosePointOnLine(p, e)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public Obstacle mergeObstacles(Obstacle o) {
		// TODO: Implement.
		return null;
	}
	
}
