package uk.ac.cam.oda22.core.robots.actions;

import java.util.List;

import uk.ac.cam.oda22.core.ListFunctions;

/**
 * @author Oliver
 *
 */
public class MoveAction implements IRobotAction {

	public final double dist;

	public MoveAction(double dist) {
		this.dist = dist;
	}
	
	public void addAction(List<IRobotAction> l) {
		IRobotAction lastAction = l.size() == 0 ? null : ListFunctions.getLast(l);
		
		if (lastAction != null && lastAction instanceof MoveAction) {
			MoveAction newAction = new MoveAction(((MoveAction) lastAction).dist + this.dist);
			
			ListFunctions.removeLast(l);
			l.add(newAction);
		}
		else {
			l.add(this);
		}
	}
	
	@Override
	public String toString() {
		return "Move action: " + this.dist;
	}
	
}
