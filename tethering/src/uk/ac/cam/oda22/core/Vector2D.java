package uk.ac.cam.oda22.core;

import java.awt.geom.Line2D;
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

	public Vector2D(Line2D l) {
		this.x = l.getX2() - l.getX1();
		this.y = l.getY2() - l.getY1();
	}
	
	public Point2D getPoint() {
		return new Point2D.Double(this.x, this.y);
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

	public static double getAngle(Point2D p) {
		return Math.atan2(p.getY(), p.getX());
	}

	public static double getAngle(Point2D p, Point2D q) {
		return Math.atan2(q.getY() - p.getY(), q.getX() - p.getX());
	}

	public Vector2D getTangentVector(boolean leftSide) {
		return getTangentVector(this.x, this.y, leftSide);
	}

	public double getAngle() {
		return (this.x != 0 || this.y != 0) ? Math.atan2(this.y, this.x) : Double.NaN;
	}

	public Point2D addPoint(Point2D p) {
		return new Point2D.Double(p.getX() + this.x, p.getY() + this.y);
	}

	public Vector2D scale(double xScale, double yScale) {
		return new Vector2D(this.x * xScale, this.y * yScale);
	}

	public Vector2D scale(double scale) {
		return new Vector2D(this.x * scale, this.y * scale);
	}
	
	public Vector2D negate() {
		return new Vector2D(-x, -y);
	}

	public double getLength() {
		return getLength(this.x, this.y);
	}
	
	public boolean isZeroVector() {
		return this.x == 0 && this.y == 0;
	}

	public Vector2D setLength(double length) {
		double scale = length / this.getLength();

		return this.scale(scale, scale);
	}

}
