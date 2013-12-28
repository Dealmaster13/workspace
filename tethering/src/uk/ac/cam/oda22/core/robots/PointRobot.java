package uk.ac.cam.oda22.core.robots;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import uk.ac.cam.oda22.core.ShapeFunctions;
import uk.ac.cam.oda22.core.tethers.Tether;

/**
 * @author Oliver
 *
 */
public class PointRobot extends Robot {

	public PointRobot(Point2D position, double rotation, double rotationalSensitivity, Tether tether) {
		super(position, 0, rotation, rotationalSensitivity, tether);
		
		Line2D[] cross = ShapeFunctions.getCross(new Point2D.Double(0, 0), 5);
		
		RobotOutline outline = new RobotOutline(cross);
		
		this.setOutline(outline);
	}

}
