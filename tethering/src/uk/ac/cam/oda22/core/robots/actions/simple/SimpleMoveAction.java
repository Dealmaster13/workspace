package uk.ac.cam.oda22.core.robots.actions.simple;

import java.util.List;

import uk.ac.cam.oda22.core.robots.actions.IRobotAction;

/**
 * @author Oliver
 *
 */
public class SimpleMoveAction implements IRobotAction {

	public final SimpleMoveDirection direction;
	
	public SimpleMoveAction(SimpleMoveDirection direction) {
		this.direction = direction;
	}

	@Override
	public void addAction(List<IRobotAction> l) {
		l.add(this);
	}
	
	@Override
	public String toString() {
		return "Move action: " + this.direction.toString();
	}
	
}
