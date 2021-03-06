package uk.ac.cam.oda22.pathplanning;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import uk.ac.cam.oda22.core.MathExtended;
import uk.ac.cam.oda22.core.logging.Log;

/**
 * @author Oliver
 *
 */
public class Path {

	public final List<Point2D> points;
	
	public Path() {
		this.points = new LinkedList<Point2D>();
	}
	
	public Path(Point2D p) {
		this.points = new LinkedList<Point2D>();
		this.points.add(p);
	}
	
	public Path(List<Point2D> path) {
		this.points = path;
	}
	
	public void addPoint(Point2D p) {
		this.points.add(p);
	}
	
	public void addPoints(Collection<Point2D> ps) {
		this.points.addAll(ps);
	}
	
	public boolean removeLastPoint() {
		if (this.points.size() == 0) {
			Log.error("Cannot remove point from empty path.");
			
			return false;
		}
		
		this.points.remove(this.points.size() - 1);
		
		return true;
	}
	
	/**
	 * Returns a new path with the list of points reversed.
	 * 
	 * @return reversed path
	 */
	public Path reverse() {
		Path reversedPath = new Path();
		
		for (int i = this.points.size() - 1; i >= 0; i--) {
			reversedPath.addPoint(this.points.get(i));
		}
		
		return reversedPath;
	}
	
	public boolean contains(Point2D point) {
		for (Point2D p : this.points) {
			if (p.equals(point)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isEmpty() {
		return this.points.size() == 0;
	}
	
	/**
	 * Gets the length of the path.
	 * 
	 * @return length
	 */
	public double length() {
		if (this.points.size() == 0) {
			return 0;
		}
		
		double l = 0;
		
		Point2D currentPoint = this.points.get(0);
		
		for (int i = 1; i < this.points.size(); i++) {
			Point2D nextPoint = this.points.get(i);
			
			l += currentPoint.distance(nextPoint);
			
			currentPoint = nextPoint;
		}
		
		return l;
	}
	
	public boolean lengthExceeded(double maxLength, double fractionalError, double absoluteError) {
		double length = this.length();
		
		return length > maxLength && !MathExtended.approxEqual(length, maxLength, fractionalError, absoluteError);
	}
	
	public List<Line2D> getEdges() {
		List<Line2D> edges = new LinkedList<Line2D>();
		
		for (int i = 0; i < this.points.size() - 1; i++) {
			Point2D p1 = this.points.get(i);
			Point2D p2 = this.points.get(i + 1);
			
			edges.add(new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY()));
		}
		
		return edges;
	}
	
	/**
	 * Returns a section of the path.
	 * The index range is inclusive.
	 * 
	 * @return sub-path
	 */
	public Path getSubpath(int startIndex, int endIndex) {
		if (startIndex < 0 || endIndex >= this.points.size() || endIndex < startIndex) {
			Log.warning("Invalid subpath indices.");
			
			startIndex = Math.max(0, startIndex);
			endIndex = Math.min(this.points.size() - 1, Math.max(startIndex, endIndex));
		}
		
		Path p = new Path();
		
		for (int i = startIndex; i <= endIndex; i++) {
			p.addPoint(this.points.get(i));
		}
		
		return p;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		Path p = (Path) o;
		
		// Return false if the number of points do not match.
		if (this.points.size() != p.points.size()) {
			return false;
		}
		
		// Return false if any point does not match.
		// Note that we will not deal with path homotopy.
		for (int i = 0; i < this.points.size(); i++) {
			if (!this.points.get(i).equals(p.points.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
}
