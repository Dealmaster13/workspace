package uk.ac.cam.oda22.core.robots;

import java.awt.geom.Point2D;

import lejos.nxt.Motor;
import uk.ac.cam.oda22.core.tethers.Tether;

/**
 * @author Oliver
 * 
 * This LEGO Mindstorms robot uses the NXT brick in a square formation using tracks with two motors.
 */
public class NXT1 extends RectangularRobot implements ITracksDriven {

	/**
	 * The robot's wheel diameter in metres, noting that the tracks are attached to wheels.
	 */
	private double tracksHeight;
	
	public NXT1(Point2D position, double rotation, Tether tether, double width, double height) throws Exception {
		super(position, rotation, Math.PI / 180, tether, width, height);
		
		this.tracksHeight = 0.045;

		// Set the robot speed to 0.4m/s.
		int revs = (int) (360 * 0.4 / (this.tracksHeight * Math.PI));
		Motor.A.setSpeed(revs);
		Motor.B.setSpeed(revs);
	}

	@Override
	protected void moveMotors(double dist) {
		int angle = (int) (360 * dist / (this.tracksHeight * Math.PI));

		Motor.A.rotate(angle);
		Motor.B.rotate(angle);
	}
	
	@Override
	// TODO: Incorrect implementation.
	protected void rotateMotors(double revs) {
		Motor.A.rotate((int) (360 * revs));
		Motor.B.rotate((int) (360 * -revs));
	}

	@Override
	public double getTracksHeight() {
		return this.tracksHeight;
	}

}
