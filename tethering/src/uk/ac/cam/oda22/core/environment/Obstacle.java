package uk.ac.cam.oda22.core.environment;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.oda22.core.MathExtended;

/**
 * @author Oliver
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
			
			this.edges.add(line);
		}
	}

	public boolean looseIntersectsLine(Line2D l) {
		double fractionalError = 0.001, absoluteError = 0.001;

		for (Line2D edge : this.edges) {
			if (l.intersectsLine(edge)
					&& !MathExtended.ApproxEqual(edge.getP1(), l.getP1(), fractionalError, absoluteError)
					&& !MathExtended.ApproxEqual(edge.getP1(), l.getP2(), fractionalError, absoluteError)
					&& !MathExtended.ApproxEqual(edge.getP2(), l.getP1(), fractionalError, absoluteError)
					&& !MathExtended.ApproxEqual(edge.getP2(), l.getP2(), fractionalError, absoluteError)) {
				return true;
			}
		}

		return false;
	}

	public boolean intersectsLine(Line2D l) {
		double fractionalError = 0.001, absoluteError = 0.001;

		for (Line2D edge : this.edges) {
			boolean b1 = MathExtended.ApproxEqual(edge.getP1(), l.getP1(), fractionalError, absoluteError)
					^ MathExtended.ApproxEqual(edge.getP2(), l.getP2(), fractionalError, absoluteError);
			
			boolean b2 = MathExtended.ApproxEqual(edge.getP1(), l.getP2(), fractionalError, absoluteError)
					^ MathExtended.ApproxEqual(edge.getP2(), l.getP1(), fractionalError, absoluteError);
			
			if (l.intersectsLine(edge) && !b1 && !b2) {
				return true;
			}
		}
		
		for (Point2D p1 : this.points) {
			if (p1.equals(l.getP1())) {
				for (Point2D p2 : this.points) {
					if (p2.equals(l.getP2())) {
						return true;
					}
				}
			}
		}

		return false;
	}
	
}
