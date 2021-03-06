package uk.ac.cam.oda22.core;

import java.util.List;

import uk.ac.cam.oda22.core.robots.actions.IRobotAction;
import uk.ac.cam.oda22.core.tethers.TetherConfiguration;
import uk.ac.cam.oda22.pathplanning.Path;

/**
 * @author Oliver
 * 
 */
public abstract class Result {

	public final List<IRobotAction> actions;

	public Result(List<IRobotAction> actions) {
		this.actions = actions;
	}
	
	public abstract Path getPath();
	
	public abstract TetherConfiguration getFinalTC();

}
