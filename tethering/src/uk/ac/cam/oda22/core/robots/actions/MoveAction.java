package uk.ac.cam.oda22.core.robots.actions;

/**
 * @author Oliver
 *
 */
public class MoveAction implements IRobotAction {

	public final double dist;

	public MoveAction(double dist) {
		this.dist = dist;
	}
	
}
