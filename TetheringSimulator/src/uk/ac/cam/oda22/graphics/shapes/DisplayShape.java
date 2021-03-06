package uk.ac.cam.oda22.graphics.shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * @author Oliver
 * 
 */
public abstract class DisplayShape {

	public final Color colour;

	public final Stroke stroke;

	public DisplayShape(Color colour, Stroke stroke) {
		this.colour = colour;
		this.stroke = stroke;
	}

	public DisplayShape(Color colour, float thickness) {
		this.colour = colour;
		this.stroke = new BasicStroke(thickness);
	}

	public DisplayShape(float thickness) {
		this.colour = Color.black;
		this.stroke = new BasicStroke(thickness);
	}

	public DisplayShape() {
		this.colour = Color.black;
		this.stroke = new BasicStroke(1);
	}

	public static Stroke getDashedStroke(float thickness) {
		return new BasicStroke(thickness, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.0f }, 0.0f);
	}

	public abstract DisplayShape translate(double x, double y);

	public abstract DisplayShape stretch(double xScale, double yScale);

	public abstract DisplayShape flipY();

	public abstract Shape[] getShapes();

}
