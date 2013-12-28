package uk.ac.cam.oda22.core.robots;

import java.awt.geom.Point2D;

import uk.ac.cam.oda22.core.MathExtended;
import uk.ac.cam.oda22.core.tethers.Tether;

/**
 * @author Oliver
 *
 */
public class RectangularRobot extends Robot {

	public final double width;
	
	public final double height;
	
	/**
	 * @param args
	 */
	public RectangularRobot(Point2D position, double rotation, double rotationalSensitivity, Tether tether, double width, double height) {
		super(position, MathExtended.getRadius(width, height), rotation, rotationalSensitivity, tether);
		
		this.width = width;
		this.height = height;
	}

}
