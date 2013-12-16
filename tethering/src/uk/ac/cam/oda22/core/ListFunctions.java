package uk.ac.cam.oda22.core;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * @author Oliver
 *
 */
public final class ListFunctions {

	public static boolean isPointInList(Point2D point, List<Point2D> list) {
		
		for (Point2D p : list) {
			if (point.equals(p)) {
				return true;
			}
		}
		
		return false;
	}
	
}
