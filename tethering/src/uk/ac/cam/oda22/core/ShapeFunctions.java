package uk.ac.cam.oda22.core;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * @author Oliver
 *
 */
public class ShapeFunctions {

	public static Line2D[] getCross(Point2D centre, double radius) {
		Line2D l1 = new Line2D.Double(
				centre.getX() - radius,
				centre.getY() - radius,
				centre.getX() + radius,
				centre.getY() + radius);

		Line2D l2 = new Line2D.Double(
				centre.getX() - radius,
				centre.getY() + radius,
				centre.getX() + radius,
				centre.getY() - radius);

		Line2D[] lines = new Line2D[2];
		lines[0] = l1;
		lines[1] = l2;

		return lines;
	}

	public static Line2D translateShape(Line2D line, double x, double y) {
		return new Line2D.Double(
				line.getX1() + x,
				line.getY1() + y,
				line.getX2() + x,
				line.getY2() + y);
	}
	
	public static Line2D stretchShape(Line2D line, double xScale, double yScale) {
		return new Line2D.Double(
				line.getX1() * xScale,
				line.getY1() * yScale,
				line.getX2() * xScale,
				line.getY2() * yScale);
	}

}
