package uk.ac.cam.oda22.graphics.shapes;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import uk.ac.cam.oda22.core.logging.Log;

/**
 * @author Oliver
 *
 */
public class Circle extends DisplayShape {

	public final Ellipse2D c;

	/**
	 * @param l
	 * @param colour
	 * @param thickness
	 */
	public Circle(Point2D position, double radius, Color colour, float thickness) {
		super(colour, thickness);
		
		this.c = new Ellipse2D.Double(position.getX() - radius, position.getY() - radius, radius * 2, radius * 2);
	}

	@Override
	public DisplayShape translate(double x, double y) {
		Point2D centre = new Point2D.Double(this.c.getCenterX() + x, this.c.getCenterY() + y);
		
		return new Circle(centre, this.c.getWidth() / 2, this.colour, this.thickness);
	}

	@Override
	public DisplayShape stretch(double xScale, double yScale) {
		if (xScale != yScale) {
			Log.warning("Scale parameters are not equal, so horizontal scaling factor will be used.");
		}
		
		Point2D centre = new Point2D.Double(this.c.getCenterX() * xScale, this.c.getCenterY() * xScale);
		
		return new Circle(centre, this.c.getWidth() * xScale, this.colour, this.thickness);
	}

}
