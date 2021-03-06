package uk.ac.cam.oda22.pathplanning;

import java.awt.geom.Point2D;
import java.util.List;

import uk.ac.cam.oda22.core.tethers.TetherPoint;

/**
 * @author Oliver
 *
 */
public class TetherPointVisibility {

	/**
	 * The start distance from the anchor point.
	 */
	public final double startW;

	/**
	 * The end distance from the anchor point.
	 */
	public final double endW;
	
	/**
	 * The tether points.
	 */
	public final List<TetherPoint> tetherPoints;
	
	public final List<Point2D> visibleVertices;

	/**
	 * @param w
	 * @param visibleVertices
	 */
	public TetherPointVisibility(double startW, double endW, List<TetherPoint> points, List<Point2D> visibleVertices) {
		this.startW = startW;
		this.endW = endW;
		this.tetherPoints = points;
		this.visibleVertices = visibleVertices;
	}
	
	/**
	 * Check if two visibility sets are equal in the points that they contain.
	 * 
	 * @param l
	 * @return true if the sets are equal, false otherwise
	 */
	public boolean isVisibilitySetEqual(List<Point2D> l) {
		if (this.visibleVertices.size() != l.size()) {
			return false;
		}
		
		for (Point2D p : l) {
			int index = 0;
			
			boolean found = false;
			
			while (!found && index < this.visibleVertices.size()) {
				if (p.equals(this.visibleVertices.get(index))) {
					found = true;
				}
				
				index ++;
			}
			
			if (!found) {
				return false;
			}
		}
		
		return true;
	}
	
}
