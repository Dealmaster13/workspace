package uk.ac.cam.oda22.graphics.shapes;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import uk.ac.cam.oda22.core.ShapeFunctions;
import uk.ac.cam.oda22.core.logging.Log;

/**
 * @author Oliver
 *
 */
public class Arrow extends DisplayShape {

	private final Point2D position;
	
	private final double rotation;
	
	private final double length;
	
	private final double headLength;
	
	public final Line2D[] ls;

	public Arrow(Point2D position, double rotation, double length, double headLength, Color colour, Stroke stroke) {
		super(colour, stroke);
		
		this.position = position;
		this.rotation = rotation;
		this.length = length;
		this.headLength = headLength;
		
		this.ls = ShapeFunctions.getArrow(position, rotation, length, headLength);
	}

	public Arrow(Point2D position, double rotation, double length, double headLength, Color colour, float thickness) {
		super(colour, thickness);
		
		this.position = position;
		this.rotation = rotation;
		this.length = length;
		this.headLength = headLength;
		
		this.ls = ShapeFunctions.getArrow(position, rotation, length, headLength);
	}

	/**
	 * @param l
	 * @param thickness
	 */
	public Arrow(Point2D position, double rotation, double length, double headLength, float thickness) {
		super(thickness);
		
		this.position = position;
		this.rotation = rotation;
		this.length = length;
		this.headLength = headLength;

		this.ls = ShapeFunctions.getArrow(position, rotation, length, headLength);
	}

	/**
	 * @param l
	 * @param thickness
	 */
	public Arrow(Point2D position, double rotation, double length, double headLength) {
		super();
		
		this.position = position;
		this.rotation = rotation;
		this.length = length;
		this.headLength = headLength;

		this.ls = ShapeFunctions.getArrow(position, rotation, length, headLength);
	}
	
	@Override
	public DisplayShape translate(double x, double y) {
		Point2D newPosition = new Point2D.Double(this.position.getX() + x, this.position.getY() + y);
		
		return new Arrow(newPosition, this.rotation, this.length, this.headLength, this.colour, this.stroke);
	}

	@Override
	public DisplayShape stretch(double xScale, double yScale) {
		if (xScale != yScale) {
			Log.warning("Scale parameters are not equal, so horizontal scaling factor will be used.");
		}
		
		return new Arrow(this.position, this.rotation, this.length * xScale, this.headLength * xScale, this.colour, this.stroke);
	}

	@Override
	public DisplayShape flipY() {
		Point2D newPosition = new Point2D.Double(this.position.getX(), -this.position.getY());
		
		return new Arrow(newPosition, -this.rotation, this.length, this.headLength, this.colour, this.stroke);
	}

	@Override
	public Shape[] getShapes() {
		return this.ls;
	}
	
}
