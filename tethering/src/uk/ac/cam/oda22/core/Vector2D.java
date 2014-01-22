package uk.ac.cam.oda22.core;

import java.awt.geom.Point2D;

/**
 * @author Oliver
 *
 */
public class Vector2D {

	public final double x;

	public final double y;

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2D(Point2D p, Point2D q) {
		this.x = q.getX() - p.getX();
		this.y = q.getY() - p.getY();
	}

	public static Vector2D getTangentVector(double x, double y, boolean leftSide) {
		double mag = getLength(x, y);

		if (leftSide) {
			return new Vector2D(-y / mag, x / mag);
		}
		else {
			return new Vector2D(y / mag, -x / mag);
		}
	}

	public static double getLength(double x, double y) {
		return Math.sqrt((x * x) + (y * y));
	}

	public Vector2D getTangentVector(boolean leftSide) {
		return getTangentVector(this.x, this.y, leftSide);
	}

	public double getAngle() {
		return Math.atan2(this.y, this.x);
	}

	public Point2D addPoint(Point2D p) {
		return new Point2D.Double(p.getX() + this.x, p.getY() + this.y);
	}

	public Vector2D scale(double xScale, double yScale) {
		return new Vector2D(this.x * xScale, this.y * yScale);
	}

	public double getLength() {
		return getLength(this.x, this.y);
	}

	public Vector2D setLength(double length) {
		double scale = length / this.getLength();

		return this.scale(scale, scale);
	}

}