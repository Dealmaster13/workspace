package uk.ac.cam.oda22.graphics.shapes;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
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
	 * @param position
	 * @param radius
	 * @param colour
	 * @param thickness
	 */
	public Circle(Point2D position, double radius, Color colour, Stroke stroke) {
		super(colour, stroke);

		this.c = getEllipse(position, radius);
	}

	/**
	 * @param position
	 * @param radius
	 * @param colour
	 * @param thickness
	 */
	public Circle(Point2D position, double radius, Color colour, float thickness) {
		super(colour, thickness);

		this.c = getEllipse(position, radius);
	}

	/**
	 * @param position
	 * @param radius
	 * @param thickness
	 */
	public Circle(Point2D position, double radius, float thickness) {
		super(thickness);

		this.c = getEllipse(position, radius);
	}

	/**
	 * @param position
	 * @param radius
	 * @param thickness
	 */
	public Circle(Point2D position, double radius) {
		super();

		this.c = getEllipse(position, radius);
	}

	@Override
	public DisplayShape translate(double x, double y) {
		Point2D centre = new Point2D.Double(this.c.getCenterX() + x,
				this.c.getCenterY() + y);

		return new Circle(centre, this.c.getWidth() / 2, this.colour,
				this.stroke);
	}

	@Override
	public DisplayShape stretch(double xScale, double yScale) {
		if (xScale != yScale) {
			Log.warning("Scale parameters are not equal, so horizontal scaling factor will be used.");
		}

		Point2D centre = new Point2D.Double(this.c.getCenterX() * xScale,
				this.c.getCenterY() * xScale);

		return new Circle(centre, this.c.getWidth() * xScale, this.colour,
				this.stroke);
	}

	@Override
	public DisplayShape flipY() {
		Point2D centre = new Point2D.Double(this.c.getCenterX(),
				-this.c.getCenterY());

		return new Circle(centre, this.c.getWidth(), this.colour, this.stroke);
	}

	@Override
	public Shape[] getShapes() {
		return new Shape[] { this.c };
	}

	private static Ellipse2D getEllipse(Point2D position, double radius) {
		return new Ellipse2D.Double(position.getX() - radius, position.getY()
				- radius, radius * 2, radius * 2);
	}

}
